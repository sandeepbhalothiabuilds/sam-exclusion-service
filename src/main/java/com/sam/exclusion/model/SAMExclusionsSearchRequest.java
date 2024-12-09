package com.sam.exclusion.model;

import com.sam.exclusion.entity.SAMExclusionsData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@AllArgsConstructor
@NoArgsConstructor
public class SAMExclusionsSearchRequest {

    String name;
    String classification;
    String address;
    String excludingAgency;
}
