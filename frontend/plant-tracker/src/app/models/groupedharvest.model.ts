import { PlantHarvest } from "./plantharvest.model";

export interface GroupedHarvest {
  plantId: number;
  plantName: string;
  plantType: string;
  plantLocation: string;
  totalAmount: number;
  records: PlantHarvest[];
}