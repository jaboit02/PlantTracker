import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Plant } from '../models/plant.model';
import { GardenBed } from '../models/gardenbed.model';

/**
 * Service handling HTTP communication with the backend for GardenBed entities.
 * 
 * @note This service was authored manually by following the established blueprint 
 *       of the PlantService. An LLM was subsequently utilized as a code review assistant 
 *       to verify the logic, check for syntax errors, and refine the TypeScript implementation.
 */

@Injectable({ providedIn: 'root' })
export class GardenBedService {

  private api = 'http://localhost:8080/gardenbeds';

  constructor(private http: HttpClient) {}

  // GET ALL GARDENBEDS
  async getAll(): Promise<GardenBed[]> {
    const res = await firstValueFrom(
      this.http.get<GardenBed[]>(this.api)
    );
    console.log('SERVICE RESPONSE:', res);
    return res;
  }

  // GET GARDENBED
  async getOne(id: number): Promise<GardenBed> {
    return await firstValueFrom(
      this.http.get<GardenBed>(`${this.api}/${id}`)
    );
  }

  // CREATE GARDENBED
  async create(gardenbed: GardenBed): Promise<void> {
    return await firstValueFrom(
      this.http.post<void>(this.api, gardenbed)
    );
  }

  // RENAME GARDENBED
  async renameGardenBed(id: number, name: string): Promise<void> {
    const params = new HttpParams().set('name', name);
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/rename`, {}, { params })
    );
  }

  // MOVE GARDENBED
  async moveGardenBed(id: number, location: string): Promise<void> {
    const params = new HttpParams().set('location', location)
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/move`, {}, { params })
    );
  }

  // CHANGE WHETHER GARDENBED IS UNDER ROOF
  async roofAboveGardenBed(id: number, roof: boolean): Promise<void> {
    const params = new HttpParams().set('roof', roof)
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/roof`, {}, { params })
    );
  }

  // CHANGE HOW MUCH SUNLIGHT IS AVAILABLE TO GARDENBED
  async sunlightAboveGardenBed(id: number, sunlight: number): Promise<void> {
    const params = new HttpParams().set('sunlight', sunlight)
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/sunlight`, {}, { params })
    );
  }

  // WATER GARDENBED
  async waterGardenBed(id: number): Promise<void> {
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/watered`,{},{})
    );
  }

  // DELETE GARDENBED
  async deleteGardenBed(id: number): Promise<void> {
    await firstValueFrom(
      this.http.delete<void>(`${this.api}/${id}`)
    );
  }
}
