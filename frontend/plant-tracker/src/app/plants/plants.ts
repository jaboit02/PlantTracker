import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Plant } from '../models/plant.model';
import { PlantService } from '../services/plant.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatExpansionModule } from '@angular/material/expansion';

/**
 * UI Component for viewing and managing plants.
 * 
 * @note The initial basic setup and Angular Material imports were generated 
 *       by an LLM (Gemini) in summer 2026. The logic, filtering, and color rules 
 *       were adjusted and customized manually where necessary.
 */

@Component({
  selector: 'app-plants',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,
    // Material Modules
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    MatExpansionModule
  ],
  templateUrl: './plants.html',
  styleUrl: './plants.css',
})
export class Plants implements OnInit {

  allPlants: Plant[] = [];
  plants: Plant[] = []; //Only Alive Plants
  deceasedPlants: Plant[] = [];
  selectedPlant?: Plant;

  showCreateForm = false;
  showDeceased = false;

  newPlant: Plant = this.getEmptyPlant();

  constructor(private service: PlantService, private cdr: ChangeDetectorRef) {}

  async ngOnInit() {
    await this.loadPlants();
  }

  // LOAD
  async loadPlants() {
    const data = await this.service.getAll();
    this.allPlants = data;

    this.plants = data
      .filter(p => p.plantAlive !== false)
      .sort((a, b) =>
        new Date(a.waterNext ?? '').getTime() -
        new Date(b.waterNext ?? '').getTime()
    );

    this.deceasedPlants = data.filter(p => p.plantAlive === false);

    // Force Reoload of UI after Data is retrieved
    this.cdr.detectChanges(); 
  }

  // CREATE
  async createPlant() {
    const plantToSend = { ...this.newPlant };
    if (!plantToSend.lastWatered) delete plantToSend.lastWatered;
    if (!plantToSend.lastRepotted) delete plantToSend.lastRepotted;
    if (!plantToSend.lastFertilized) delete plantToSend.lastFertilized;

    await this.service.create(plantToSend);

    this.newPlant = this.getEmptyPlant();
    this.showCreateForm = false;

    await this.loadPlants();
  }

  // DELETE
  async deletePlant(id: number) {
    await this.service.delete(id);
    await this.loadPlants();
    this.selectedPlant = undefined;
  }

  // PLANT DECEASED
  async deceasePlant(plant: Plant) {
    if (!plant.plantId) return;
    
    const confirmDecompose = confirm(`Are you sure "${plant.plantName}" has met its demise?`);
    if (!confirmDecompose) return;

    await this.service.decease(plant.plantId); 
    
    this.selectedPlant = undefined;
    await this.loadPlants();
  }

  // WATERED PLANT
  async waterPlant(id: number) {
    await this.service.waterPlant(id);
    await this.refreshSelected(id);
  }

  // REPOTTED PLANT
  async repotPlant(id: number) {
    await this.service.repotPlant(id);
    await this.refreshSelected(id);
  }

  // FERTILIZED PLANT
  async fertilizePlant(id: number) {
    await this.service.fertilizePlant(id);
    await this.refreshSelected(id);
  }

  // RENAME PLANT
  async updateName(id: number, currentName: string) {
    const newName = prompt('Enter new plant name:', currentName);
    if (newName && newName.trim() !== '') {
      await this.service.renamePlant(id, newName.trim());
      await this.refreshSelected(id);
    }
  }

  // RELOCATE PLANT
  async updateLocation(id: number, event: any) {
    const location = event.value;

    const plant = this.selectedPlant;
    if (!plant) return;

    await this.service.plantWasMoved(
      id,
      location,
      plant.plantOutdoor ?? false,
    );

    await this.refreshSelected(id);
  }

  // MOVE PLANT OUTDOORS
  async updateOutdoor(id: number, outdoor: boolean) {
    const plant = this.selectedPlant;
    if (!plant) return;

    await this.service.plantWasMoved(
      id,
      plant.plantLocation ?? 'Indoor',
      outdoor
    );

    await this.refreshSelected(id);
  }

  // CHANGE PLANT TYPE
  async updateType(id: number, event: any) {
    await this.service.changeType(id, event.value);
    await this.refreshSelected(id);
  }

  // CHANGE WATER CONSUMPTION
  async updateWaterNeeded(id: number, event: any) {
    await this.service.changeWaterNeeded(id, Number(event.value));
    await this.refreshSelected(id);
  }

  // CHANGE LIGHT REQUIREMENTS
  async updateLightNeeded(id: number, event: any) {
    await this.service.changeLightNeeded(id, Number(event.value));
    await this.refreshSelected(id);
  }

  // RELOAD TO KEEP DATA UP TP DATE
  private async refreshSelected(id: number) {
    await this.loadPlants();
    this.selectedPlant = this.allPlants.find(p => p.plantId === id);
    this.cdr.detectChanges();
  }

  // SELECT 
  selectPlant(plant: Plant) {
    this.selectedPlant = plant;
  }

  // helper: EMPTY OBJECT
  private getEmptyPlant(): Plant {
    return {
      plantName: '',
      plantLocation: 'INDOOR', 
      plantType: 'GREEN', 
      plantAlive: true,     
      plantOutdoor: false,
      lightNeeded: 3,
      waterNeeded: 4,
      lastWatered: undefined,
      lastRepotted: undefined,
      lastFertilized: undefined
    } as Plant;
  }

  // COLOR LOGIC
  getColor(plant: Plant): string {
    if (!plant.waterNext) return 'green'; // Fallback in case there is no date for waterNext because there is no lastWatered

    const today = new Date();
    // Set time to 00:00:00 to only compare dates
    today.setHours(0, 0, 0, 0); 
    
    const next = new Date(plant.waterNext);
    next.setHours(0, 0, 0, 0);

    const diff = Math.ceil(
      (next.getTime() - today.getTime()) / (1000 * 60 * 60 * 24)
    );

    if (diff <= 0) return 'red';      // due immediately
    if (diff === 1) return 'yellow';  // due tomorrow
    return 'green';                   
  }
}