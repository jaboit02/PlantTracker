import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// Angular Material Imports
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';

// Data Models & Services
import { GardenBed } from '../models/gardenbed.model';
import { Plant } from '../models/plant.model';
import { GardenBedService } from '../services/gardenbed.service';
import { OutdoorPlantService } from '../services/outdoorplant.service';
import { PlantService } from '../services/plant.service';


/**
 * UI Component for viewing and managing garden beds and their plant assignments.
 * 
 * @note The initial basic setup and Angular Material imports were generated 
 *       by an LLM (Gemini) in summer 2026. The logic, complex relational mappings 
 *       (assigning and removing plants), and state filtering rules were adjusted 
 *       and customized manually where necessary.
 */

@Component({
  selector: 'app-gardenbeds',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatCheckboxModule
  ],
  templateUrl: './gardenbeds.html',
  styleUrl: './gardenbeds.css' 
})
export class Gardenbeds implements OnInit {

  beds: GardenBed[] = [];
  selectedBed?: GardenBed;
  plantsInSelectedBed: Plant[] = [];

  allAvailablePlants: Plant[] = []; // Stores all plants to show in the dropdown
  selectedPlantIdForAssignment?: number; // Stores the currently chosen plant ID


  showCreateForm = false;
  
  // Empty model for the form
  newBed: GardenBed = this.getEmptyBed();

  constructor(
    private bedService: GardenBedService,
    private outdoorPlantService: OutdoorPlantService,
    private plantService: PlantService,
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit() {
    await this.loadGardenBeds();
  }

  // LOAD ALL BEDS
  async loadGardenBeds() {
    // Fetch all garden Beds
    this.beds = await this.bedService.getAll();
    // Fetch all plants for the assignment dropdown
    const allPlants = await this.plantService.getAll();

    // UX IMPROVEMENT: Only allow plants that are alive and located outdoors (GARDEN or BALCONY)
    this.allAvailablePlants = allPlants.filter(p => 
      p.plantAlive !== false && 
      (p.plantLocation === 'GARDEN' || p.plantLocation === 'BALCONY')
    );

    this.cdr.detectChanges();
  }

  // CREATE NEW BED
  async createBed() {
    await this.bedService.create(this.newBed);
    this.newBed = this.getEmptyBed();
    this.showCreateForm = false;
    await this.loadGardenBeds();
  }

  // ADD PLANT TO GARDENBED
  async addPlantToBed() {
    if (!this.selectedBed?.gardenBedId || !this.selectedPlantIdForAssignment) {
      alert('Please select a valid plant first!');
      return;
    }

    // Find the selected plant object from list
    const chosenPlant = this.allAvailablePlants.find(p => p.plantId === this.selectedPlantIdForAssignment);
    
    if (!chosenPlant) return;

    // Build the OutdoorPlant structure the service expects
    const newOutdoorAssignment = {
      plant: { plantId: chosenPlant.plantId },
      gardenBed: { gardenBedId: this.selectedBed.gardenBedId }
    } as any;

    try {
      await this.outdoorPlantService.create(newOutdoorAssignment);
      this.selectedPlantIdForAssignment = undefined; // Reset dropdown
      
      // Refresh the inhabitants list immediately
      await this.selectBed(this.selectedBed); 
    } catch (error) {
      console.error('Failed to assign plant to bed:', error);
      alert('Could not assign plant. Is it already in this bed?');
    }
  }

  // DELETE BED
  async deleteBed(id: number) {
    const confirmDelete = confirm('Are you sure you want to permanently delete this garden bed?');
    if (!confirmDelete) return;

    await this.bedService.deleteGardenBed(id);
    this.selectedBed = undefined;
    this.plantsInSelectedBed = [];
    await this.loadGardenBeds();
  }

  // REMOVE PLANT ONLY FROM THIS BED (Plant survives)
  async removePlantFromBed(plantId: number) {
    if (!this.selectedBed?.gardenBedId) return;

    const confirmRemove = confirm('Do you want to remove this plant from the bed? (The plant will not be deleted)');
    if (!confirmRemove) return;

    // Build the OutdoorPlant object expected by the OutdoorPlantService.delete
    const assignmentToRemove = {
      plant: { plantId: plantId },
      gardenBed: { gardenBedId: this.selectedBed.gardenBedId }
    } as any;

    try {
      await this.outdoorPlantService.delete(assignmentToRemove);
      // Refresh the list of inhabitants immediately
      await this.selectBed(this.selectedBed);
    } catch (error) {
      console.error('Failed to remove plant from bed:', error);
    }
  }

  // MARK PLANT AS DECEASED (Moves it to the cemetery)
  async markPlantAsDeceased(plantId: number, plantName: string) {
    const confirmDecease = confirm(`Are you sure "${plantName}" has met its demise? This will move it to the cemetery.`);
    if (!confirmDecease) return;

    try {
      // Calls the existing PlantService method to set plantAlive = false
      await this.plantService.decease(plantId);
      
      // Refresh the inhabitants list immediately
      await this.selectBed(this.selectedBed!);
    } catch (error) {
      console.error('Failed to mark plant as deceased:', error);
    }
  }

  // QUICK ACTION: WATER ENTIRE BED
  async waterBed(id: number) {
    await this.bedService.waterGardenBed(id);
    await this.refreshSelected(id);
  }

  // INLINE UPDATES (Via Material-compatible change triggers)
  async updateName(id: number, currentName: string) {
    const newName = prompt('Enter new garden bed name:', currentName);
    if (newName && newName.trim() !== '') {
      await this.bedService.renameGardenBed(id, newName.trim());
      await this.refreshSelected(id);
    }
  }

  async updateLocation(id: number, event: any) {
    await this.bedService.moveGardenBed(id, event.value);
    await this.refreshSelected(id);
  }

  async updateSunlight(id: number, event: any) {
    await this.bedService.sunlightAboveGardenBed(id, Number(event.value));
    await this.refreshSelected(id);
  }

  async toggleRoof(id: number, checked: boolean) {
    await this.bedService.roofAboveGardenBed(id, checked);
    await this.refreshSelected(id);
  }

  // SELECT BED & FETCH ITS PLANTS
  async selectBed(bed: GardenBed) {
    this.selectedBed = bed;
    this.plantsInSelectedBed = []; // Reset list first
    
    if (bed.gardenBedId) {
      try {
        // Fetches all Plant objects inside this specific bed via OutdoorPlantService
        this.plantsInSelectedBed = await this.outdoorPlantService.getAllPlantsInGardenBed(bed.gardenBedId);
      } catch (error) {
        console.error('Failed to load plants for bed:', error);
        this.plantsInSelectedBed = [];
      }
    }
    this.cdr.detectChanges();
  }

  // Helper to reload data and keep state updated
  private async refreshSelected(id: number) {
    await this.loadGardenBeds();
    this.selectedBed = this.beds.find(b => b.gardenBedId === id);
    if (this.selectedBed?.gardenBedId) {
      this.plantsInSelectedBed = await this.outdoorPlantService.getAllPlantsInGardenBed(this.selectedBed.gardenBedId);
    }
    this.cdr.detectChanges();
  }

  // Helper: blank initializer object
  private getEmptyBed(): GardenBed {
    return {
      gardenBedName: '',
      gardenBedLocation: 'GARDEN',
      roofIsPresent: false,
      sunlightAvailable: 2,
      lastWatered: undefined
    };
  }

  // COLOR LOGIC FOR BEDS (Based on sunlight levels or watering status)
  getBedColor(bed: GardenBed): string {
    if (!bed.lastWatered) return 'red'; // Needs immediate attention
    
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const last = new Date(bed.lastWatered);
    last.setHours(0, 0, 0, 0);

    const diff = Math.ceil((today.getTime() - last.getTime()) / (1000 * 60 * 60 * 24));
    
    if (diff >= 3) return 'red';     // Dry for 3+ days
    if (diff === 2) return 'yellow';  // Dry for 2 days
    return 'green';                  // Freshly watered
  }
}
