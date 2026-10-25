package de.jannika.planttracker.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutdoorPlantComplete {
    Plant plant;
    GardenBed gardenBed;
}
