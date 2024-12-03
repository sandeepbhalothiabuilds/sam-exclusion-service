package com.nyc.properties.controller;

import com.nyc.properties.model.PropertyDetails;
import com.nyc.properties.service.impl.NYRentStbLzdPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin
public class SearchController {
    @Autowired
    private NYRentStbLzdPropertyService nyRentStbLzdPropertyService;

    @GetMapping("/details/{offset}")
    public List<PropertyDetails> getPropertyDetails(@PathVariable int offset) {
        return nyRentStbLzdPropertyService.getPropertyDetails(offset);
    }

    @GetMapping("/details/")
    public List<PropertyDetails> getPropertyDetails() {
        return nyRentStbLzdPropertyService.getPropertyDetails();
    }
    @GetMapping("/detailsCount")
    public long getPropertyDetailsCount() {
        return nyRentStbLzdPropertyService.getPropertyDetailsCount();
    }


    @GetMapping("/criteria")
    public List<PropertyDetails> getProperties(@RequestParam(required = false) String zipcode, @RequestParam(required = false) String borough
            ,@RequestParam(required = false) String buildingNumber,
                                               @RequestParam(required = false) String street,
                                               @RequestParam(required = false) String stateSuffix,
                                               @RequestParam(required = true) int offset) {
        return nyRentStbLzdPropertyService.findAllByCriteria(zipcode, borough, buildingNumber, street, stateSuffix, offset);
    }

    @GetMapping("/criteriaCount")
    public Long getPropertiesCount(@RequestParam(required = false) String zipcode, @RequestParam(required = false) String borough
            , @RequestParam(required = false) String buildingNumber,
                                   @RequestParam(required = false) String street,
                                   @RequestParam(required = false) String stateSuffix) {
        return nyRentStbLzdPropertyService.countByCriteria(zipcode, borough, buildingNumber, street, stateSuffix);
    }
}
