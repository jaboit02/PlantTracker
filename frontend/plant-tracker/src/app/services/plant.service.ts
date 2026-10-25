import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { Plant } from '../models/plant.model';

/**
 * Service handling HTTP communication with the backend for Plant entities.
 * 
 * @note This service was initially generated via an LLM (Gemini) in summer 2026 to serve 
 *       as a learning reference and structural blueprint for Angular services, 
 *       demonstrating HttpClient integration and async/await conversion via RxJS.
 */

@Injectable({ providedIn: 'root' })
export class PlantService {

  private api = 'http://localhost:8080/plants';

  constructor(private http: HttpClient) {}

  // GET ALL PLANTS
  async getAll(): Promise<Plant[]> {
    const res = await firstValueFrom(
      this.http.get<Plant[]>(this.api)
    );
    console.log('SERVICE RESPONSE:', res);
    return res;
  }

  // GET PLANT
  async getOne(id: number): Promise<Plant> {
    return await firstValueFrom(
      this.http.get<Plant>(`${this.api}/${id}`)
    );
  }

  // CREATE PLANT
  async create(plant: Plant): Promise<void> {
    return await firstValueFrom(
      this.http.post<void>(this.api, plant)
    );
  }

  // DELETE PLANT
  async delete(id: number): Promise<void> {
    return await firstValueFrom(
      this.http.delete<void>(`${this.api}/${id}`)
    );
  }

  // RENAME PLANT
  async renamePlant(id: number, name: string): Promise<void> {
    const params = new HttpParams().set('name', name);
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/rename`, {}, { params })
    );
  }

  // DECEASE PLANT
  async decease(id: number): Promise<void> {
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/deceased`, {})
    );
  }

  // CHANGE TYPE
  async changeType(id: number, type: string): Promise<void> {
    const params = new HttpParams().set('type', type);
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/type`, {}, { params })
    );
  }

  // CHANGE LOCATION
  async plantWasMoved(
    id: number,
    location: string,
    outdoor: boolean
  ): Promise<void> {
    const params = new HttpParams()
      .set('location', location)
      .set('outdoor', outdoor.toString());

    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/moved`, {}, { params })
    );
  }

  // CHANGE LIGHT NEEDED
  async changeLightNeeded(id: number, lightNeeded: number): Promise<void> {
    const params = new HttpParams().set('lightNeeded', lightNeeded.toString());
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/light-needed`, {}, { params })
    );
  }

  // CHANGE WATER NEEDED
  async changeWaterNeeded(id: number, waterNeeded: number): Promise<void> {
    const params = new HttpParams().set('waterNeeded', waterNeeded.toString());
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/water-needed`, {}, { params })
    );
  }

   // WATERED
  async waterPlant(id: number): Promise<void> {
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/watered`, {})
    );
  }

  // REPOTTED
  async repotPlant(id: number): Promise<void> {
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/repotted`, {})
    );
  }

  // FERTILIZED
  async fertilizePlant(id: number): Promise<void> {
    await firstValueFrom(
      this.http.put<void>(`${this.api}/${id}/fertilized`, {})
    );
  }
}