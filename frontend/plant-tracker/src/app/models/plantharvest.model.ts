export interface PlantHarvest {
    plantId: number;
    plantName: string;
    plantType: string;
    plantLocation: string;

    gardenBedId?: number;
    gardenBedName?: string;
    gardenBedLocation?: string;

    plantOutputId: number;
    harvestDate?: string;
    harvestAmount: number;
}