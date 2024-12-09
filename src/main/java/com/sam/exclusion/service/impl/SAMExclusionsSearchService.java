package com.sam.exclusion.service.impl;

import com.sam.exclusion.entity.SAMExclusionsAlias;
import com.sam.exclusion.entity.SAMExclusionsData;
import com.sam.exclusion.model.SAMExclusionsSearchRequest;
import com.sam.exclusion.model.SAMExclusionsSearchResponse;
import com.sam.exclusion.repository.SAMExclusionsAliasRepository;
import com.sam.exclusion.repository.SAMExclusionsDataRepository;

import jakarta.persistence.criteria.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
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
        
        List<Long> primaryIDs = aliasRepository.findDistinctIDByAliasNameIgnoreCaseContaining(samExclusionsSearchRequest.getName());
        searchResponse.setPrimaryIDListSize(primaryIDs.size());
        List<SAMExclusionsData> primaryList = dataRepository.findExclusionDataByIDList(primaryIDs);
        searchResponse.setPrimaryData(primaryList);

        return searchResponse;
    }

    public Long getPropertyDetailsCount() {

        return null;
    }

}
