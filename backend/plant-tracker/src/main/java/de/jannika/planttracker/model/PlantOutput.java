package de.jannika.planttracker.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlantOutput {
    private Integer plantOutputId;
    private Integer plantId;
    private LocalDateTime outputDate;
    private Integer outputAmount;
}
