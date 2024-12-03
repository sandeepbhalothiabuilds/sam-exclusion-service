package com.sam.exclusion.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.sam.exclusion.repository.NycStblzdPropertyDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sam.exclusion.entity.NycStblzdPropertyData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

@Service
public class CSVParser {

    @Autowired
    NycStblzdPropertyDataRepository nycStblzdPropertyDataRepository;

    String line = "";
    Integer indexOfdataYear = null;

    public void extractExcelData(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            ArrayList<NycStblzdPropertyData> propertiesList = new ArrayList<>();
            String dataYear = null;
            while ((line = br.readLine()) != null) {
                try {
                    NycStblzdPropertyData pro = new NycStblzdPropertyData();
                    String[] property = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);// use comma as separator

                    if (("borough".equals(property[0])) && (dataYear == null)) {
                        dataYear = getDataConYer(property);
                    }

                    if (!"borough".equals(property[0])) {
                        pro.setBorough(property[0]);
                        pro.setUcbblNumber(property[1]);
                        pro.setDataConsolidationYear(dataYear);
                        pro.setUc(property[indexOfdataYear - 4]);
                        pro.setEst(property[indexOfdataYear - 3]);
                        pro.setDhcr(property[indexOfdataYear - 2]);
                        pro.setAbat(property[indexOfdataYear - 1]);
                        pro.setCd(property[46]);
                        pro.setCt(property[47]);
                        pro.setCb(property[48]);
                        pro.setCouncil(property[49]);
                        pro.setZip(property[50]);
                        pro.setUnitAddress(property[51]);
                        pro.setOwnerName(property[52]);
                        pro.setNumberOfBuildings(property[53]);
                        pro.setNumOfFloors(property[54]);
                        pro.setUnitRes(property[55]);
                        pro.setUnitTotal(property[56]);
                        pro.setCondoNumber(property[58]);
                        pro.setLongitude(property[59]);
                        pro.setLatitude(property[60]);
                        pro.setCreatedBy("postgres");
                        pro.setUpdatedBy("postgres");

                        if ("".equals(property[0])) {
                            pro.setYearBuilt(null);
                            if (null != property[1].substring(1, 2)) switch (property[1].substring(0, 2)) {
                                case "10" -> pro.setBorough("MN");
                                case "20" -> pro.setBorough("BX");
                                case "30" -> pro.setBorough("BK");
                                case "40" -> pro.setBorough("QN");
                                case "50" -> pro.setBorough("SI");
                                default -> {
                                }
                            }
                        } else {
                            pro.setYearBuilt(Long.valueOf(property[57]));
                        }

                        if (property[indexOfdataYear - 1].contains("\"")) {
                            pro.setAbat(property[indexOfdataYear - 1].substring(1, property[indexOfdataYear - 1].length() - 1));
                        }

                        propertiesList.add(pro);
                        ObjectMapper mapper = new ObjectMapper();
                        String jsonString = mapper.writeValueAsString(property);
                        pro.setContent(jsonString);
                    }
                } catch (Exception e) {
                    System.out.println("Error Occurred when building data to save in data in rent_stblzd_property_units table: " + e);
                }
            }
            CompletableFuture.runAsync(() -> saveProperties(propertiesList));
        } catch (IOException e) {
            System.out.println("Error while calling Async for git hub data: " + e);
        }
    }

    public void saveProperties(List<NycStblzdPropertyData> propertiesList) {
        try {
            nycStblzdPropertyDataRepository.deleteAllRecords();
            List<List<NycStblzdPropertyData>> listOfPropertiesList = Lists.partition(propertiesList, 500);
            listOfPropertiesList.parallelStream().forEach(list -> nycStblzdPropertyDataRepository.saveAll(list));
        } catch (Exception e) {
            System.out.println("Error while saving the github data: " + e);
        }
    }

    public String getDataConYer(String[] property) {
        Integer pos = Arrays.asList(property).indexOf("cd");
        indexOfdataYear = pos;
        String dataConYear = property[pos - 1];

        return dataConYear.substring(0, 4);
    }
}
