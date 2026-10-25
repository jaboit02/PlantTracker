package de.jannika.planttracker.model;

import java.time.LocalDate;

import de.jannika.planttracker.enums.PlantLocation;
import de.jannika.planttracker.enums.PlantType;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Plant {
    private Integer plantId;
    private String plantName;
    private LocalDate plantCreatedAt;
    private Boolean plantAlive;
    private PlantType plantType;
    private Boolean plantOutdoor;
    private PlantLocation plantLocation;
    private Integer lightNeeded;
    private Integer waterNeeded;
    private LocalDate lastWatered;
    private LocalDate lastRepotted;
    private LocalDate lastFertilized;
    private LocalDate waterNext;
}
