package com.nyc.properties.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rent_stblzd_property_units", schema = "NYC-RCU")
public class NycStblzdPropertyData {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rent_stblzd_property_units_seq")
    @SequenceGenerator(name = "rent_stblzd_property_units_seq", sequenceName = "\"rent_stblzd_property_units_seq\"", allocationSize = 1, schema = "NYC-RCU")
    @Column(name = "rent_stblzd_property_units_id")
    private Long rentStabilizedPropertyUnitsId;

    @Column(name = "borough")
    private  String borough;

    @Column(name = "ucbbl_number")
    private String ucbblNumber;

    @Column(name = "data_consolidation_year")
    private String dataConsolidationYear;

    @Column(name = "uc")
    private String uc;

    @Column(name = "est")
    private String est;

    @Column(name = "dhcr")
    private String dhcr;

    @Column(name = "abat")
    private String abat;

    @Column(name = "cd")
    private String cd;

    @Column(name = "ct")
    private String ct;

    @Column(name = "cb")
    private String cb;

    @Column(name = "council")
    private String council;

    @Column(name = "zip")
    private String zip;

    @Column(name = "unit_address")
    private String unitAddress;

    @Column(name = "ownername")
    private String ownerName;

    @Column(name = "number_of_buildings")
    private String numberOfBuildings;

    @Column(name = "numberOfFloors")
    private String numOfFloors;

    @Column(name = "units_res")
    private String unitRes;

    @Column(name = "units_total")
    private String unitTotal;

    @Column(name = "year_built")
    private Long yearBuilt;

    @Column(name = "condo_number")
    private String condoNumber;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String  latitude;

    @JdbcTypeCode(SqlTypes.JSON)
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "json_raw_data")
    @JsonIgnore
    private String content;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}
