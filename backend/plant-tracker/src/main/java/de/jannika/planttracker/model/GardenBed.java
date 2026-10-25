package de.jannika.planttracker.model;

import java.time.LocalDate;

import de.jannika.planttracker.enums.GardenBedLocation;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GardenBed {
    private Integer gardenBedId;
    private String gardenBedName;
    private GardenBedLocation gardenBedLocation;
    private Boolean roofIsPresent;
    private Integer sunlightAvailable;
    private LocalDate lastWatered;
    
}
