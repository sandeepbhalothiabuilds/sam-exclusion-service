package com.sam.exclusion.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sam_exclusions_alias_copy", schema = "sam")
public class SAMExclusionsAliasCopy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sam_exclusion_alias_copy_id_seq")
    @SequenceGenerator(name = "sam_exclusion_alias_copy_id_seq", sequenceName = "\"sam_exclusion_alias_copy_id_seq\"", allocationSize = 1, schema = "sam")
    @Column(name = "sam_exclusion_alias_id")
    private Long samExclusionAliasId;

    @Column(name = "alias_name")
    private String aliasName;

    @ManyToOne
    @JoinColumn(name = "sam_exclusion_data_id", nullable = false)
    private SAMExclusionsData samExclusionsData;

}
