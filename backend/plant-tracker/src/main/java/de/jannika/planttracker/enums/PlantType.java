package de.jannika.planttracker.enums;

/** Plant Type
 * Enumeration representing the functional classification of a Plant.
 * Provides mapping methods to resolve database strings and user inputs into enum instances.
 * 
 * @note The enum constants and core structure were authored manually, while the utility 
 *       methods for safe string-to-enum conversions ({@code valueFromDbToEnum} and 
 *       {@code fromStringToPlantType}) were generated using an LLM (Gemini/ChatGPT) in summer 2026.
 */

public enum PlantType {
    GREEN("Green"), 
    FLOWER("Flower"),
    HERB("Herb"),
    FRUIT("Fruit"),
    VEGETABLE("Vegetable");

    private final String value;

    PlantType(String string) {
        this.value = string;
    }

    public String getValue() {
        return value;
    }

    // LLM GENERATED: Convert Values from DB to Enum Type
    public static PlantType valueFromDbToEnum(String value) {
        for (PlantType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PlantType: " + value);
    }

    // LLM GENERATED: Convert String to Enum 
    public static PlantType fromStringToPlantType(String value) {
        for (PlantType plantType : values()) {
            if (plantType.value.equalsIgnoreCase(value)
                    || plantType.name().equalsIgnoreCase(value)) {
                return plantType;
            }
        }
        throw new IllegalArgumentException("Unknown PlantType: " + value);
    }
}
