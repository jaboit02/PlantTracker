import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Plant } from '../models/plant.model';
import { GardenBed } from '../models/gardenbed.model';
import { OutdoorPlant } from '../models/outdoorPlant.model';

/**
 * Service handling HTTP communication with the backend for OutdoorPlant entities.
 * 
 * @note This service was authored manually by following the established blueprint 
 *       of the PlantService. An LLM was subsequently utilized as a code review assistant 
 *       to verify the logic, check for syntax errors, and refine the TypeScript implementation.
 */

@Injectable({ providedIn: 'root' })
export class OutdoorPlantService {

  private api = 'http://localhost:8080/outdoorplants';

  constructor(private http: HttpClient) {}

  // GET ALL OutdoorPlants
  async getAll(): Promise<OutdoorPlant[]> {
    const res = await firstValueFrom(
      this.http.get<OutdoorPlant[]>(this.api)
    );
    console.log('SERVICE RESPONSE:', res);
    return res;
  }

  // GET OUTDOORPLANT BASED ON PLANTID
  async getOne(plantId: number): Promise<OutdoorPlant> {
    return await firstValueFrom(
      this.http.get<OutdoorPlant>(`${this.api}/plant/${plantId}`)
    );
  }

  // GET OUTDOORPLANTS BASED ON GARDENBEDID
  async getAllBasedInGardenBedId(gardenBedId: number): Promise<OutdoorPlant[]> {
    return await firstValueFrom(
      this.http.get<OutdoorPlant[]>(`${this.api}/gardenBed/${gardenBedId}`)
    );
  }

  // GET ALL PLANTS WITHIN A GARDENBED
  async getAllPlantsInGardenBed (gardenBedId: number): Promise<Plant[]> {
    return await firstValueFrom(
      this.http.get<Plant[]>(`${this.api}/plantsInGardenBed/${gardenBedId}`)
    );
  }

  // CREATE OUTDOORPLANT
  async create(outdoorPlant: OutdoorPlant): Promise<void> {
    const body = {
        plantId: outdoorPlant.plant.plantId,
        gardenBedId: outdoorPlant.gardenBed.gardenBedId
    };
    return await firstValueFrom(
      this.http.post<void>(this.api, body)
    );
  }

  // DELETE OUTDOORPLANT
  async delete(outdoorPlant: OutdoorPlant): Promise<void> {
    await firstValueFrom(
      this.http.delete<void>(`${this.api}/${outdoorPlant.plant.plantId}/${outdoorPlant.gardenBed.gardenBedId}`)
    );
  }
}
