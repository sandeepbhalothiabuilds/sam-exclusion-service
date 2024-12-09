package com.sam.exclusion.model;

import java.util.ArrayList;
import java.util.List;

import com.sam.exclusion.entity.SAMExclusionsData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SAMExclusionsSearchResponse {
    private int primaryIDListSize;
    private List<SAMExclusionsData> primaryData = new ArrayList<>();
    private List<SAMExclusionsData> secondaryData = new ArrayList<>();
}
