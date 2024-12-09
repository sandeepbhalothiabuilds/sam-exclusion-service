package com.sam.exclusion.service;

import com.google.common.collect.Lists;
import com.sam.exclusion.entity.SAMExclusionsAlias;
import com.sam.exclusion.entity.SAMExclusionsData;
import com.sam.exclusion.repository.SAMExclusionsAliasRepository;
import com.sam.exclusion.repository.SAMExclusionsDataRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class CSVParser {

    @Autowired
    SAMExclusionsDataRepository samExclusionsDataRepository;

  //  @Autowired
   // Scheduler scheduler;

    @Autowired
    SAMExclusionsAliasRepository samExclusionsAliasRepository;

    String line = "";

    public void extractExcelData(File file) throws IOException {

     //   scheduler.downloadFile();

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            ArrayList<SAMExclusionsData> samExclusionsDataList = new ArrayList<>();
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                try {
                    SAMExclusionsData entity = new SAMExclusionsData();
                    String[] samExclusionRecord = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);// use comma as separator

                    entity.setClassification(samExclusionRecord[0].replaceAll("\"", ""));
                    entity.setName(samExclusionRecord[1].replaceAll("\"", ""));
                    entity.setPrefixName(samExclusionRecord[2].replaceAll("\"", ""));
                    entity.setFirstName(samExclusionRecord[3].replaceAll("\"", ""));
                    entity.setMiddleName(samExclusionRecord[4].replaceAll("\"", ""));
                    entity.setLastName(samExclusionRecord[5].replaceAll("\"", ""));
                    entity.setSuffixName(samExclusionRecord[6].replaceAll("\"", ""));
                    entity.setAddress1(samExclusionRecord[7].replaceAll("\"", ""));
                    entity.setAddress2(samExclusionRecord[8].replaceAll("\"", ""));
                    entity.setAddress3(samExclusionRecord[9].replaceAll("\"", ""));
                    entity.setAddress4(samExclusionRecord[10].replaceAll("\"", ""));
                    entity.setCity(samExclusionRecord[11].replaceAll("\"", ""));
                    entity.setStateOrProvince(samExclusionRecord[12].replaceAll("\"", ""));
                    entity.setCountry(samExclusionRecord[13].replaceAll("\"", ""));
                    entity.setZipCode(samExclusionRecord[14].replaceAll("\"", ""));
                    entity.setOpenDataFlag(samExclusionRecord[15].replaceAll("\"", ""));
                    entity.setUniqueEntityId(samExclusionRecord[17].replaceAll("\"", ""));
                    entity.setExclusionProgram(samExclusionRecord[18].replaceAll("\"", ""));
                    entity.setExcludingAgency(samExclusionRecord[19].replaceAll("\"", ""));
                    entity.setCtCode(samExclusionRecord[20].replaceAll("\"", ""));
                    entity.setExclusionType(samExclusionRecord[21].replaceAll("\"", ""));
                    entity.setAdditionalComments(samExclusionRecord[22].replaceAll("\"", ""));
                    entity.setActiveDate(samExclusionRecord[23].replaceAll("\"", ""));
                    entity.setTerminationDate(samExclusionRecord[24].replaceAll("\"", ""));
                    entity.setRecordStatus(samExclusionRecord[25].replaceAll("\"", ""));
                    entity.setCrossReference(samExclusionRecord[26].replaceAll("\"", ""));
                    entity.setSamNumber(samExclusionRecord[27].replaceAll("\"", ""));
                    entity.setCage(samExclusionRecord[28].replaceAll("\"", ""));
                    entity.setNpi(samExclusionRecord[29].replaceAll("\"", ""));
                    entity.setCreationDate(samExclusionRecord[30].replaceAll("\"", ""));
                    entity.setFullName(getFullName(samExclusionRecord));
                    entity.setFullAddress(getFullAddress(samExclusionRecord));
                    entity.setAlias(getAliasNames(entity.getFullName(), samExclusionRecord[26]));
                    entity.setSamExclusionsAlias(getAliases(entity.getAlias(), entity));

                    samExclusionsDataList.add(entity);

                } catch (Exception e) {
                    System.out.println("Error Occurred when building data to save in data in sam_exclusions_data table: " + e);
                }
            }
            CompletableFuture.runAsync(() -> saveSamExclusions(samExclusionsDataList));
        } catch (IOException e) {
            System.out.println("Error while calling Async for SAM Exclusions data: " + e);
        }
    }



    private String getAliasNames(String fullName, String aliases) {
        aliases = StringUtils.replaceEach(aliases, Constants.searchList, Constants.replacementList);
        Set<String> aliasSet = new HashSet<>();
        aliasSet.add(fullName.trim());
        if (null != aliases && !aliases.isBlank()) {
            String[] arr = aliases.replace("(also", "").replace(")", "").replaceAll("'", "").replaceAll("\"", "").split(",");
            for (String s : arr) {
                aliasSet.add(s.trim());
            }
        }
        return StringUtils.removeStart(String.join(",", aliasSet), ",");
    }

    private List<SAMExclusionsAlias> getAliases(String aliases, SAMExclusionsData entity) {
        List<SAMExclusionsAlias> samExclusionsAliasList = new ArrayList<>();
        SAMExclusionsAlias samExclusionsAlias;
        if (null != aliases && !aliases.isBlank()) {
            String[] arr = aliases.split(",");
            for (String s : arr) {
                samExclusionsAlias = new SAMExclusionsAlias();
                samExclusionsAlias.setAliasName(s.trim());
                samExclusionsAlias.setSamExclusionsData(entity);
                samExclusionsAliasList.add(samExclusionsAlias);
            }
        }
        return samExclusionsAliasList;

    }

    private String getFullAddress(String[] samExclusionRecord) {
        String fAddress = samExclusionRecord[7] + " " + samExclusionRecord[8] + " " + samExclusionRecord[9] + " " + samExclusionRecord[10] + "\n" + samExclusionRecord[11] + " " + samExclusionRecord[12] + " " + samExclusionRecord[13] + " " + samExclusionRecord[14];
        fAddress = fAddress.replaceAll(" +", " ");
        return fAddress.replaceAll("\"", "").trim();
    }

    private String getFullName(String[] samExclusionRecord) {
        String fName = samExclusionRecord[1] + " " + samExclusionRecord[2] + " " + samExclusionRecord[3] + " " + samExclusionRecord[4] + " " + samExclusionRecord[5] + " " + samExclusionRecord[6];
        fName = fName.replaceAll(" +", " ").replaceAll("\"", "").replaceAll(",", "");
        return fName.trim();
    }

    public void saveSamExclusions(List<SAMExclusionsData> propertiesList) {
        try {
            samExclusionsDataRepository.deleteAllRecords();
            samExclusionsAliasRepository.deleteAllRecords();
            List<List<SAMExclusionsData>> listOfPropertiesList = Lists.partition(propertiesList, 500);
            listOfPropertiesList.parallelStream().forEach(list -> samExclusionsDataRepository.saveAll(list));
        } catch (Exception e) {
            System.out.println("Error while saving the data: " + e);
        }
    }
}