package com.sam.exclusion.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NycRentControlledPropertiesResponse {
    private String ucbblNumber;

    private String borough;

    private String block;

    private String lot;

    private String status1;

    private String status2;

    private String status3;

    private String buildingNumber;

    private String street;

    private String stateSuffix;

    private String city;

    private String county;

    private String zip;

    private String dataConsolidationYear;

    private String uc;

    private String est;

    private String dhcr;

    private String abat;

    private String cd;

    private String ct;

    private String cb;

    private String council;

    private String unitAddress;

    private String ownerName;

    private String numberOfBuildings;

    private String numOfFloors;

    private String unitRes;

    private String unitTotal;

    private Long yearBuilt;

    private String condoNumber;

    private String longitude;

    private String latitude;
}
