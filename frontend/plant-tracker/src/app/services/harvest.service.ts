import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { firstValueFrom } from "rxjs";
import { PlantHarvest } from "../models/plantharvest.model";
import { PlantOutput } from "../models/plantoutput.model";

/**
 * Service handling HTTP communication with the backend for Harvest and Yield entities.
 * 
 * @note This optional feature service was fully generated via an LLM (Gemini) in June 2026 
 *       to rapidly implement a secondary harvest-tracking feature shortly before project completion.
 */

@Injectable({ providedIn: 'root' })
export class HarvestService {

  private api = 'http://localhost:8080/harvest'; // Assuming the context path

  constructor(private http: HttpClient) {}

  // GET ALL HARVESTS
  // Matches: @GetMapping()
  async getAll(): Promise<PlantHarvest[]> {
    return await firstValueFrom(
      this.http.get<PlantHarvest[]>(this.api)
    );
  }

  // GET ALL HARVESTS FOR A SPECIFIC PLANT
  // Matches: @GetMapping("/plant/{plantId}")
  async getAllForPlant(plantId: number): Promise<PlantHarvest[]> {
    return await firstValueFrom(
      this.http.get<PlantHarvest[]>(`${this.api}/plant/${plantId}`)
    );
  }

  // CREATE NEW HARVEST ENTRY
  // Matches: @PostMapping()
  async create(plantOutput: PlantOutput): Promise<void> {
    return await firstValueFrom(
      this.http.post<void>(this.api, plantOutput)
    );
  }

  // UPDATE HARVEST AMOUNT
  // Matches: @PutMapping()
  async updateAmount(plantOutput: PlantOutput): Promise<void> {
    return await firstValueFrom(
      this.http.put<void>(this.api, plantOutput)
    );
  }

  // DELETE HARVEST ENTRY
  // Matches: @DeleteMapping("/{plantOutputId}")
  async delete(plantOutputId: number): Promise<void> {
    return await firstValueFrom(
      this.http.delete<void>(`${this.api}/${plantOutputId}`)
    );
  }
}