package de.jannika.planttracker.enums;

/**
 * Enumeration representing the physical placement or environment of a Plant.
 * Provides mapping methods to resolve database strings and user inputs into enum instances.
 * 
 * @note The enum constants and core structure were authored manually, while the utility 
 *       methods for safe string-to-enum conversions ({@code valueFromDbToEnum} and 
 *       {@code fromStringToPlantLocation}) were generated using an LLM (Gemini/ChatGPT) in summer 2026.
 */

public enum PlantLocation {
    GARDEN("Garden"), 
    BALCONY("Balcony"),
    INDOOR("Indoor");

    private final String value;

    PlantLocation(String string) {
        this.value = string;
    }

    public String getValue() {
        return value;
    }

    // LLM GENERATED: Convert Values from DB to Enum Type
    public static PlantLocation valueFromDbToEnum(String value) {
        for (PlantLocation location : values()) {
            if (location.value.equalsIgnoreCase(value)) {
                return location;
            }
        }
        throw new IllegalArgumentException("Unknown PlantLocation: " + value);
    }

    // LLM GENERATED: Convert String to Enum 
    public static PlantLocation fromStringToPlantLocation(String value) {
        for (PlantLocation location : values()) {
            if (location.value.equalsIgnoreCase(value)
                    || location.name().equalsIgnoreCase(value)) {
                return location;
            }
        }
        throw new IllegalArgumentException("Unknown PlantLocation: " + value);
    }
}
