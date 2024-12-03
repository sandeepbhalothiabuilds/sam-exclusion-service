package com.nyc.properties.model;

import com.nyc.properties.entity.NyRentStabilizedProperty;
import com.nyc.properties.entity.NyRentStabilizedPropertyAddress;
import com.nyc.properties.entity.NycStblzdPropertyData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // This annotation combines @Getter, @Setter, @EqualsAndHashCode, @ToString
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDetails {
    private NyRentStabilizedProperty property;
    private NycStblzdPropertyData units;
    private NyRentStabilizedPropertyAddress addresses;
}
