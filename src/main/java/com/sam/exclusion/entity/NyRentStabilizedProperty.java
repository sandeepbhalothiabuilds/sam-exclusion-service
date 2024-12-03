package com.sam.exclusion.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ny_rent_stblzd_property", schema = "NYC-RCU")
public class NyRentStabilizedProperty {

    private static long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ny_rent_stblzd_property_seq")
    @SequenceGenerator(name = "ny_rent_stblzd_property_seq", sequenceName = "\"ny_rent_stblzd_property_seq\"", allocationSize = 1, schema = "NYC-RCU")
    @Column(name = "ny_rent_stblzd_property_id")
    private Long nyRentStabilizedPropertyId;

    @Column(name = "ucbbl_number")
    private String ucbblNumber;

    @Column(name = "status1")
    private String status1;

    @Column(name = "status2")
    private String status2;

    @Column(name = "status3")
    private String status3;

    @Column(name = "block")
    private String block;

    @Column(name = "lot")
    private String lot;

    @Column(name = "borough")
    private String borough;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "json_raw_data")
    @JsonIgnore
    private String jsonRawData;

}
