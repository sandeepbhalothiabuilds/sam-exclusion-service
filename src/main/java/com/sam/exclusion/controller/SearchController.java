package com.sam.exclusion.controller;


import com.sam.exclusion.model.SAMExclusionsSearchRequest;
import com.sam.exclusion.model.SAMExclusionsSearchResponse;
import com.sam.exclusion.service.impl.SAMExclusionsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class SearchController {
    @Autowired
    private SAMExclusionsSearchService samExclusionsSearchService;

    @PostMapping("/search")
    public SAMExclusionsSearchResponse searchSAMExclusions(@RequestBody SAMExclusionsSearchRequest samExclusionsSearchRequest) {
        
        return samExclusionsSearchService.searchSAMExclusionsData(samExclusionsSearchRequest);
    }

}
