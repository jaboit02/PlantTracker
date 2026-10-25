package de.jannika.planttracker.enums;

/**
 * Enumeration representing the placement or environment of a GardenBed.
 * Provides mapping methods to resolve database strings and user inputs into enum instances.
 * 
 * @note The enum constants and core structure were authored manually, while the utility 
 *       methods for safe string-to-enum conversions ({@code valueFromDbToEnum} and 
 *       {@code fromStringToGardenBedLocation}) were generated using an LLM (Gemini/ChatGPT) in summer 2026.
 */

public enum GardenBedLocation {
    GARDEN("Garden"), 
    BALCONY("Balcony");

    private final String value;

    GardenBedLocation(String string) {
        this.value = string;
    }

    public String getValue() {
        return value;
    }

    // LLM GENERATED: Convert Values from DB to Enum Type
    public static GardenBedLocation valueFromDbToEnum(String value) {
        for (GardenBedLocation location : values()) {
            if (location.value.equalsIgnoreCase(value)) {
                return location;
            }
        }
        throw new IllegalArgumentException("Unknown PlantLocation: " + value);
    }

    // LLM GENERATED: Convert String to Enum 
    public static GardenBedLocation fromStringToGardenBedLocation(String value) {
        for (GardenBedLocation location : values()) {
            if (location.value.equalsIgnoreCase(value)
                    || location.name().equalsIgnoreCase(value)) {
                return location;
            }
        }
        throw new IllegalArgumentException("Unknown GardenBedLocation: " + value);
    }
}
