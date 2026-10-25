package de.jannika.planttracker.model;

import java.time.LocalDate;

import de.jannika.planttracker.enums.GardenBedLocation;
import de.jannika.planttracker.enums.PlantLocation;
import de.jannika.planttracker.enums.PlantType;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Harvest {
    private Integer plantId;
    private String plantName;
    private PlantType plantType;
    private PlantLocation plantLocation;

    private Integer gardenBedId;
    private String gardenBedName;
    private GardenBedLocation gardenBedLocation;

    private Integer plantOutputId;
    private LocalDate harvestDate;
    private Integer harvestAmount;
}
