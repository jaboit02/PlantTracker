import { GardenBed } from "./gardenbed.model";
import { Plant } from "./plant.model";

export interface OutdoorPlant {
    plant: Plant;
    gardenBed: GardenBed;
}