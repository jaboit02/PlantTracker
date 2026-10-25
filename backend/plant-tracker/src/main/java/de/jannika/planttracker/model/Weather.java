package de.jannika.planttracker.model;

import java.time.LocalDateTime;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Weather {
    private LocalDateTime weatherDate;
    private float temperature;
    private float precipitation;
    private Integer cloudCover;
    private Boolean isRainy;
    private Boolean isSunny;
}
