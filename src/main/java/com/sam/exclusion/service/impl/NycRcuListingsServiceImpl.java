package com.sam.exclusion.service.impl;

import com.sam.exclusion.entity.*;
import com.sam.exclusion.repository.NycRcuListingsAddressRepository;
import com.sam.exclusion.repository.NycRcuListingsRepository;
import com.sam.exclusion.service.NycRcuListingsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class NycRcuListingsServiceImpl implements NycRcuListingsService {

    @Autowired
    private NycRcuListingsRepository nycRcuListingsRepository;

    @Autowired
    private NycRcuListingsAddressRepository nycRcuListingsAddressRepository;

    ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;

    @Override
    public void persistNycRcuRecord(List<Table> tables, Map<String, String> boroughAndIdMap) {
        String boroughName = boroughAndIdMap.keySet().iterator().next();
        String boroughId = boroughAndIdMap.get(boroughName);
        nycRcuListingsRepository.deleteRecordsByBorough(boroughName);

        CompletableFuture.runAsync(() -> tables.parallelStream().forEach(table -> {
            try {
                insertRcuRecords(table, boroughName, boroughId);
            } catch (JsonProcessingException e) {
                System.out.println("Error Occurred while saving-: " + e);
            }
        }));
    }

    private void insertRcuRecords(Table table, String boroughName, String boroughId) throws JsonProcessingException {
        List<NyRentStabilizedProperty> nyRentStabilizedPropertyList = new ArrayList<>();
        List<NyRentStabilizedPropertyAddress> nyRentStabilizedPropertyAddressesList = new ArrayList<>();
        boolean headerRow = true;
        for (TableRow tableRow : table.getRows()) {
            try {
                if (!headerRow) {
                    buildRentStabilizedPropertyList(nyRentStabilizedPropertyList, tableRow, table.getRows().get(0), boroughName, boroughId);

                    //Address Records
                    buildRentStabilizedPropertyAddressList(nyRentStabilizedPropertyAddressesList, tableRow, table.getRows().get(0), boroughName, boroughId);

                }
            } catch (Exception e) {
                System.out.println("Error Occurred when processing data for row, so skipping it: " + tableRow);
            }
            headerRow = false;


        }
        nycRcuListingsRepository.saveAll(nyRentStabilizedPropertyList);
        nycRcuListingsAddressRepository.saveAll(nyRentStabilizedPropertyAddressesList);
        System.out.println("Page Records Saved for Page# " + table.getPageIdx());
    }

    private void buildRentStabilizedPropertyAddressList(List<NyRentStabilizedPropertyAddress> nyRentStabilizedPropertyAddressesList, TableRow tableRow, TableRow headerRow, String boroughName, String boroughId) {
        if (tableRow.getCells().get(1).getContent().contains("TO")) {
            List<String> listOfBuildings = buildListOfBuildings(tableRow.getCells().get(1).getContent());
            listOfBuildings.forEach(buildingNumber -> {
                nyRentStabilizedPropertyAddressesList.add(buildAddressObject(buildingNumber, tableRow, tableRow.getCells().get(2), tableRow.getCells().get(3), boroughName, boroughId));
            });
        } else {
            nyRentStabilizedPropertyAddressesList.add(buildAddressObject(tableRow.getCells().get(1).getContent(), tableRow, tableRow.getCells().get(2), tableRow.getCells().get(3), boroughName, boroughId));
        }
        if (!tableRow.getCells().get(4).getContent().isEmpty()) {
            if (tableRow.getCells().get(4).getContent().contains("TO")) {
                List<String> listOfBuildings = buildListOfBuildings(tableRow.getCells().get(4).getContent());
                listOfBuildings.forEach(buildingNumber -> {
                    nyRentStabilizedPropertyAddressesList.add(buildAddressObject(buildingNumber, tableRow, tableRow.getCells().get(5), tableRow.getCells().get(6), boroughName, boroughId));
                });
            } else {
                nyRentStabilizedPropertyAddressesList.add(buildAddressObject(tableRow.getCells().get(4).getContent(), tableRow, tableRow.getCells().get(5), tableRow.getCells().get(6), boroughName, boroughId));
            }
        }
    }

    private NyRentStabilizedPropertyAddress buildAddressObject(String buildingNumber, TableRow tableRow, TableCell street, TableCell stateSuffix, String boroughName, String boroughId) {
        NyRentStabilizedPropertyAddress nyRentStabilizedPropertyAddress = new NyRentStabilizedPropertyAddress();
        String blockNumber = buildBlockNumber(tableRow);
        String lotNumber = buildLotNumber(tableRow);
        nyRentStabilizedPropertyAddress.setUcbblNumber(buildUcbblNumber(tableRow, boroughId, blockNumber, lotNumber));
        nyRentStabilizedPropertyAddress.setBuildingNumber(buildingNumber);
        nyRentStabilizedPropertyAddress.setStreet(street.getContent());
        nyRentStabilizedPropertyAddress.setStateSuffix(stateSuffix.getContent());
        nyRentStabilizedPropertyAddress.setCity(tableRow.getCells().get(7).getContent());
        nyRentStabilizedPropertyAddress.setCounty(tableRow.getCells().get(8).getContent());
        nyRentStabilizedPropertyAddress.setBorough(boroughName);
        nyRentStabilizedPropertyAddress.setZip(tableRow.getCells().get(0).getContent());
        nyRentStabilizedPropertyAddress.setLot(lotNumber);
        nyRentStabilizedPropertyAddress.setBlock(blockNumber);
        nyRentStabilizedPropertyAddress.setCreatedBy("postgres");
        nyRentStabilizedPropertyAddress.setCreatedAt(LocalDateTime.now());
        nyRentStabilizedPropertyAddress.setUpdatedBy("postgres");
        nyRentStabilizedPropertyAddress.setUpdatedAt(LocalDateTime.now());

        return nyRentStabilizedPropertyAddress;
    }

    private List<String> buildListOfBuildings(String buildingRange) {
        List<String> buildingList = new ArrayList<>();
        String[] buildings = buildingRange.split("TO");
        int startRange = Integer.parseInt(buildings[0].replaceAll("[^\\d]", ""));
        int endRange = Integer.parseInt(buildings[1].replaceAll("[^\\d]", ""));
        for (int buildingNumber = startRange; buildingNumber <= endRange; ) {
            buildingList.add(String.valueOf(buildingNumber));
            buildingNumber = buildingNumber + 2;
        }
        return buildingList;
    }

    private void buildRentStabilizedPropertyList(List<NyRentStabilizedProperty> nyRentStabilizedPropertyList, TableRow tableRow, TableRow tableHeader, String boroughName, String boroughId) throws JsonProcessingException {
        NyRentStabilizedProperty nyRentStabilizedProperty = new NyRentStabilizedProperty();
        String blockNumber = buildBlockNumber(tableRow);
        String lotNumber = buildLotNumber(tableRow);
        nyRentStabilizedProperty.setUcbblNumber(buildUcbblNumber(tableRow, boroughId, blockNumber, lotNumber));
        nyRentStabilizedProperty.setJsonRawData(convertRowDataToJson(tableHeader, tableRow));
        nyRentStabilizedProperty.setBorough(boroughName);
        nyRentStabilizedProperty.setLot(lotNumber);
        nyRentStabilizedProperty.setBlock(blockNumber);
        nyRentStabilizedProperty.setStatus1(tableRow.getCells().size() >= 10 ? tableRow.getCells().get(9).getContent() : null);
        nyRentStabilizedProperty.setStatus2(tableRow.getCells().size() >= 11 ? tableRow.getCells().get(10).getContent() : null);
        nyRentStabilizedProperty.setStatus3(tableRow.getCells().size() >= 12 ? tableRow.getCells().get(11).getContent() : null);
        nyRentStabilizedProperty.setCreatedBy("postgres");
        nyRentStabilizedProperty.setCreatedAt(LocalDateTime.now());
        nyRentStabilizedProperty.setUpdatedBy("postgres");
        nyRentStabilizedProperty.setUpdatedAt(LocalDateTime.now());
        nyRentStabilizedPropertyList.add(nyRentStabilizedProperty);
    }

    private String buildLotNumber(TableRow tableRow) {
        String lotNumber = "0000";
        if (tableRow.getCells().size() >= 14) {
            lotNumber = tableRow.getCells().get(13).getContent();
            lotNumber = String.format("%04d", Integer.parseInt(lotNumber));
        }
        return lotNumber;
    }

    private String buildBlockNumber(TableRow tableRow) {
        String blockNumber = "0000";
        if (tableRow.getCells().size() >= 13) {
            blockNumber = tableRow.getCells().get(12).getContent();
            blockNumber = String.format("%04d", Integer.parseInt(blockNumber));
        }
        return blockNumber;
    }

    private String buildUcbblNumber(TableRow tableRow, String boroughId, String blockNumber, String lotNumber) {
        return boroughId + blockNumber + lotNumber;
    }

    private String convertRowDataToJson(TableRow headerRow, TableRow dataRow) throws JsonProcessingException {
        Map<String, String> dataMap = new HashMap<>();

        for (int i = 0; i < dataRow.getCells().size(); i++) {
            dataMap.put(headerRow.getCells().get(i).getContent(), dataRow.getCells().get(i).getContent());
        }
        return mapper.writeValueAsString(dataMap);

    }
}
