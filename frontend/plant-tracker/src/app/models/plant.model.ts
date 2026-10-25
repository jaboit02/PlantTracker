// AI GENERATED based on backend model
export interface Plant {
  plantId?: number;
  
  plantName: string;
  plantCreatedAt?: string;
  plantAlive?: boolean;

  plantType?: string;
  plantOutdoor?: boolean;
  plantLocation?: string;

  lightNeeded?: number;
  waterNeeded?: number;

  lastWatered?: string;
  lastRepotted?: string;
  lastFertilized?: string;
  waterNext?: string;
}