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
    private List<SAMExclusionsDataResponse> primaryData = new ArrayList<>();
    private List<SAMExclusionsDataResponse> secondaryData = new ArrayList<>();
}
