package com.nyc.properties.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ny_rent_stblzd_property_addr", schema = "NYC-RCU")
public class NyRentStabilizedPropertyAddress {

    private static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ny_rent_stblzd_property_addr_seq")
    @SequenceGenerator(name = "ny_rent_stblzd_property_addr_seq", sequenceName = "\"ny_rent_stblzd_property_addr_seq\"", allocationSize = 1, schema = "NYC-RCU")
    @Column(name = "addrid")
    private Long addressId;

    @Column(name = "ucbbl_number")
    private String ucbblNumber;

    @Column(name = "bldng_number")
    private String buildingNumber;

    @Column(name = "street")
    private String street;

    @Column(name = "stsufx")
    private String stateSuffix;

    @Column(name = "city")
    private String city;

    @Column(name = "county")
    private String county;

    @Column(name = "block")
    private String block;

    @Column(name = "lot")
    private String lot;

    @Column(name = "borough")
    private String borough;

    @Column(name = "zip")
    private String zip;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
