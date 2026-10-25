package de.jannika.planttracker.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutdoorPlant {
    private Integer plantId;
    private Integer gardenBedId;
    
}
