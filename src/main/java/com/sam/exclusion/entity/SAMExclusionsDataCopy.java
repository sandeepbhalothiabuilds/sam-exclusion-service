package com.sam.exclusion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sam_exclusions_data", schema = "sam")
public class SAMExclusionsDataCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sam_exclusion_data_copy_id_seq")
    @SequenceGenerator(name = "sam_exclusion_data_copy_id_seq", sequenceName = "\"sam_exclusion_data_copy_id_seq\"", allocationSize = 1, schema = "sam")
    @Column(name = "sam_exclusion_data_id")
    private Long samExclusionDataId;

    @Column(name = "classification")
    private String classification;

    @Column(name = "name")
    private String name;

    @Column(name = "prefix_name")
    private String prefixName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "suffix_name")
    private String suffixName;

    @Column(name = "address1")
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(name = "address3")
    private String address3;

    @Column(name = "address4")
    private String address4;

    @Column(name = "city")
    private String city;

    @Column(name = "state_or_province")
    private String stateOrProvince;

    @Column(name = "country")
    private String country;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "open_data_flag")
    private String openDataFlag;

    @Column(name = "unique_entity_id")
    private String uniqueEntityId;

    @Column(name = "exclusion_program")
    private String exclusionProgram;

    @Column(name = "excluding_agency")
    private String excludingAgency;

    @Column(name = "ct_code")
    private String ctCode;

    @Column(name = "exclusion_type")
    private String exclusionType;

    @Column(name = "additional_comments")
    private String additionalComments;

    @Column(name = "active_date")
    private String activeDate;

    @Column(name = "termination_date")
    private String terminationDate;

    @Column(name = "record_status")
    private String recordStatus;

    @Column(name = "cross_reference")
    private String crossReference;

    @Column(name = "sam_number")
    private String samNumber;

    @Column(name = "cage")
    private String cage;

    @Column(name = "npi")
    private String npi;

    @Column(name = "creation_date")
    private String creationDate;

    @Column(name = "full_address")
    private String fullAddress;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "alias")
    private String alias;

    @OneToMany(mappedBy = "samExclusionsData", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SAMExclusionsAlias> samExclusionsAlias = new ArrayList<>();

}
