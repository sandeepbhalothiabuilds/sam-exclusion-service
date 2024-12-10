package com.sam.exclusion.service.impl;

import com.sam.exclusion.entity.SAMExclusionsData;
import com.sam.exclusion.model.SAMExclusionsDataResponse;
import com.sam.exclusion.model.SAMExclusionsSearchRequest;
import com.sam.exclusion.model.SAMExclusionsSearchResponse;
import com.sam.exclusion.repository.SAMExclusionsAliasRepository;
import com.sam.exclusion.repository.SAMExclusionsDataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SAMExclusionsSearchService {

    @Autowired
    private SAMExclusionsDataRepository dataRepository;
    @Autowired
    private SAMExclusionsAliasRepository aliasRepository;

    @PersistenceContext
    EntityManager entityManager;


    public SAMExclusionsSearchResponse searchSAMExclusionsData(SAMExclusionsSearchRequest samExclusionsSearchRequest) {
        SAMExclusionsSearchResponse searchResponse = new SAMExclusionsSearchResponse();
        List<SAMExclusionsDataResponse> primaryDestinationAddressList = new ArrayList<>();
        List<SAMExclusionsDataResponse> secondaryDestinationAddressList = new ArrayList<>();

        List<SAMExclusionsDataResponse> primaryDestinationNameList = new ArrayList<>();
        List<SAMExclusionsDataResponse> secondaryDestinationNameList = new ArrayList<>();

        if (null != samExclusionsSearchRequest.getName() && !samExclusionsSearchRequest.getName().isEmpty()) {
            //Getting Data for the primary data
            List<Long> primaryIDs = aliasRepository.findDistinctIDByAliasNameIgnoreCaseContaining(samExclusionsSearchRequest.getName());
            List<String> samNumberList = dataRepository.findSamNumberByIDList(primaryIDs);
            List<String> uniqueEntityIDList = dataRepository.findUniqueEntityIDByIDList(primaryIDs);
            List<SAMExclusionsData> primaryList = dataRepository.findBySamNumberListAndUniqueEntityIDListAndName(samNumberList, uniqueEntityIDList, samExclusionsSearchRequest.getName());

            //Getting Data for the secondary data
            List<Long> secondaryIDs = aliasRepository.findSecondaryDataByName(samExclusionsSearchRequest.getName());
            List<String> secondarySAMNumberList = dataRepository.findSamNumberByIDList(secondaryIDs);
            List<String> secondaryUniqueEntityIDList = dataRepository.findUniqueEntityIDByIDList(secondaryIDs);
            List<SAMExclusionsData> secondaryList = dataRepository.findBySamNumberListAndUniqueEntityIDListAndName(secondarySAMNumberList, secondaryUniqueEntityIDList, samExclusionsSearchRequest.getName());

            ModelMapper modelMapper = new ModelMapper();

            primaryDestinationNameList = modelMapper.map(primaryList, new TypeToken<List<SAMExclusionsDataResponse>>() {
            }.getType());
            secondaryDestinationNameList = modelMapper.map(secondaryList, new TypeToken<List<SAMExclusionsDataResponse>>() {
            }.getType());

            filterForClassificationAndAgency(primaryDestinationNameList, secondaryDestinationNameList, searchResponse, samExclusionsSearchRequest);

        }
        if (null != samExclusionsSearchRequest.getAddress() && !samExclusionsSearchRequest.getAddress().isEmpty()) {
            //Getting Data for the primary data
            List<SAMExclusionsData> primarySAMExclusionsDataList = dataRepository.findRecordsByAddress(samExclusionsSearchRequest.getAddress().replaceAll(" ", "%"), samExclusionsSearchRequest.getAddress());
            List<String> samNumberList = dataRepository.findSamNumberByIDList(primarySAMExclusionsDataList.stream().map(SAMExclusionsData::getSamExclusionDataId).toList());
            List<String> uniqueEntityIDList = dataRepository.findUniqueEntityIDByIDList(primarySAMExclusionsDataList.stream().map(SAMExclusionsData::getSamExclusionDataId).toList());
            List<SAMExclusionsData> primaryList = dataRepository.findBySamNumberListAndUniqueEntityIDListAndAddress(samNumberList, uniqueEntityIDList, samExclusionsSearchRequest.getAddress());

            List<SAMExclusionsData> mergedPrimaryList = mergeTwoListWithoutDuplicates(primarySAMExclusionsDataList, primaryList);


            //Getting Data for the secondary data
            List<String> aliasList = Arrays.asList(mergedPrimaryList.stream().map(SAMExclusionsData::getAlias)
                    .collect(Collectors.joining(",")).split(","));

            List<Long> secondaryIDs = aliasRepository.findSecondaryDataIdsByProvidingAliasList(aliasList, samExclusionsSearchRequest.getAddress().replaceAll(" ", "%"));
            List<String> secondarySAMNumberList = dataRepository.findSamNumberByIDList(secondaryIDs);
            List<String> secondaryUniqueEntityIDList = dataRepository.findUniqueEntityIDByIDList(secondaryIDs);
            List<SAMExclusionsData> mergedSecondaryList = dataRepository.findBySamNumberListAndUniqueEntityIDListAndAddress(secondarySAMNumberList, secondaryUniqueEntityIDList, samExclusionsSearchRequest.getAddress());

            ModelMapper modelMapper = new ModelMapper();

            primaryDestinationAddressList = modelMapper.map(mergedPrimaryList, new TypeToken<List<SAMExclusionsDataResponse>>() {
            }.getType());
            secondaryDestinationAddressList = modelMapper.map(mergedSecondaryList, new TypeToken<List<SAMExclusionsDataResponse>>() {
            }.getType());

            filterForClassificationAndAgency(primaryDestinationAddressList, secondaryDestinationAddressList, searchResponse, samExclusionsSearchRequest);
        }

        if (null != samExclusionsSearchRequest.getName() && !samExclusionsSearchRequest.getName().isEmpty() && null != samExclusionsSearchRequest.getAddress() && !samExclusionsSearchRequest.getAddress().isEmpty()) {
            primaryDestinationAddressList.retainAll(primaryDestinationNameList);
            searchResponse.setPrimaryData(primaryDestinationAddressList);

            secondaryDestinationAddressList.retainAll(secondaryDestinationNameList);
            searchResponse.setSecondaryData(secondaryDestinationAddressList);
        }

        removeItemFromSecondListIfPresentInFirstList(searchResponse);
        return searchResponse;
    }

    private void removeItemFromSecondListIfPresentInFirstList(SAMExclusionsSearchResponse searchResponse) {
        List<SAMExclusionsDataResponse> newSecondaryList = new ArrayList<SAMExclusionsDataResponse>(searchResponse.getSecondaryData());
        newSecondaryList.removeAll(searchResponse.getPrimaryData());
        searchResponse.setSecondaryData(newSecondaryList);
    }

    private void filterForClassificationAndAgency(List<SAMExclusionsDataResponse> primaryDestinationList, List<SAMExclusionsDataResponse> secondaryDestinationList, SAMExclusionsSearchResponse searchResponse, SAMExclusionsSearchRequest samExclusionsSearchRequest) {
        if (null != samExclusionsSearchRequest.getClassification() && !samExclusionsSearchRequest.getClassification().isEmpty()) {
            primaryDestinationList = primaryDestinationList.stream().filter(s -> s.getClassification().equalsIgnoreCase(samExclusionsSearchRequest.getClassification())).toList();
            secondaryDestinationList = secondaryDestinationList.stream().filter(s -> s.getClassification().equalsIgnoreCase(samExclusionsSearchRequest.getClassification())).toList();
        }

        if (null != samExclusionsSearchRequest.getExcludingAgency() && !samExclusionsSearchRequest.getExcludingAgency().isEmpty()) {
            primaryDestinationList = primaryDestinationList.stream().filter(s -> s.getExcludingAgency().equalsIgnoreCase(samExclusionsSearchRequest.getExcludingAgency())).toList();
            secondaryDestinationList = secondaryDestinationList.stream().filter(s -> s.getExcludingAgency().equalsIgnoreCase(samExclusionsSearchRequest.getExcludingAgency())).toList();
        }

        searchResponse.setPrimaryData(primaryDestinationList);
        searchResponse.setSecondaryData(secondaryDestinationList);
    }

    private List<SAMExclusionsData> mergeTwoListWithoutDuplicates(List<SAMExclusionsData> list1, List<SAMExclusionsData> list2) {
        Set<SAMExclusionsData> set = new HashSet<>();
        set.addAll(list1);
        set.addAll(list2);
        return new ArrayList<>(set);
    }

}
