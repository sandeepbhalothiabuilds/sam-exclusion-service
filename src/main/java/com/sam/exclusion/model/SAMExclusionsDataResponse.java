package com.sam.exclusion.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SAMExclusionsDataResponse {

    private Long samExclusionDataId;

    private String classification;

    private String name;

    private String prefixName;

    private String firstName;

    private String middleName;

    private String lastName;

    private String suffixName;

    private String address1;

    private String address2;

    private String address3;

    private String address4;

    private String city;

    private String stateOrProvince;

    private String country;

    private String zipCode;

    private String openDataFlag;

    private String uniqueEntityId;

    private String exclusionProgram;

    private String excludingAgency;

    private String ctCode;

    private String exclusionType;

    private String additionalComments;

    private String activeDate;

    private String terminationDate;

    private String recordStatus;

    private String crossReference;

    private String samNumber;

    private String cage;

    private String npi;

    private String creationDate;

    private String fullAddress;

    private String fullName;

    private String alias;

}
