package com.sam.exclusion.model;

import java.util.List;

import com.sam.exclusion.entity.SAMExclusionsData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@AllArgsConstructor
@NoArgsConstructor
public class SAMExclusionsSearchResponse {
    private List<SAMExclusionsData> primaryData;
    private List<SAMExclusionsData> secondaryData;
}
