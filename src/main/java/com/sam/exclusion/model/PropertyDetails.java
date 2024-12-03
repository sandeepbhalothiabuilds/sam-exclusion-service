package com.sam.exclusion.model;

import com.sam.exclusion.entity.NyRentStabilizedProperty;
import com.sam.exclusion.entity.NyRentStabilizedPropertyAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // This annotation combines @Getter, @Setter, @EqualsAndHashCode, @ToString
@AllArgsConstructor
@NoArgsConstructor
public class PropertyDetails {
    private NyRentStabilizedProperty property;
    private NyRentStabilizedPropertyAddress addresses;
}
