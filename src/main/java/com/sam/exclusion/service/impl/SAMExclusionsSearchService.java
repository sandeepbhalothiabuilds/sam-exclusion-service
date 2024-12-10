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

import java.util.List;

@Service
public class SAMExclusionsSearchService {

    @Autowired
    private SAMExclusionsDataRepository dataRepository;
    @Autowired
    private SAMExclusionsAliasRepository aliasRepository;

    @PersistenceContext
    EntityManager entityManager;


    public SAMExclusionsSearchResponse searchSAMExclusionsData(SAMExclusionsSearchRequest samExclusionsSearchRequest){

        SAMExclusionsSearchResponse searchResponse = new SAMExclusionsSearchResponse();
        
        //Getting Data for the primary data
        List<Long> primaryIDs = aliasRepository.findDistinctIDByAliasNameIgnoreCaseContaining(samExclusionsSearchRequest.getName()); 
        List<String> samNumberList = dataRepository.findSamNumberByIDList(primaryIDs);
        List<String> uniqueEntityIDList = dataRepository.findUniqueEntityIDByIDList(primaryIDs);
        List<SAMExclusionsData> primaryList = dataRepository.findBySamNumberListAndUniqueEntityIDList(samNumberList,uniqueEntityIDList);
       
        //Getting Data for the secondary data
        List<Long> secondaryIDs = aliasRepository.findSecondaryDataByName(samExclusionsSearchRequest.getName());
        List<String> secondarySAMNumberList = dataRepository.findSamNumberByIDList(secondaryIDs);
        List<String> secondaryUniqueEntityIDList = dataRepository.findUniqueEntityIDByIDList(secondaryIDs);
        List<SAMExclusionsData> secondaryList = dataRepository.findBySamNumberListAndUniqueEntityIDList(secondarySAMNumberList,secondaryUniqueEntityIDList);

        ModelMapper modelMapper = new ModelMapper();

        List<SAMExclusionsDataResponse> destinationList = modelMapper.map(primaryList, new TypeToken<List<SAMExclusionsDataResponse>>() {}.getType());
        List<SAMExclusionsDataResponse> secondaryDestinationList = modelMapper.map(secondaryList, new TypeToken<List<SAMExclusionsDataResponse>>() {}.getType());

        searchResponse.setPrimaryData(destinationList);
        searchResponse.setSecondaryData(secondaryDestinationList);
        return searchResponse;
    }

}
