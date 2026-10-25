import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatNativeDateModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { Plant } from '../models/plant.model';
import { PlantOutput } from '../models/plantoutput.model';
import { HarvestService } from '../services/harvest.service';
import { PlantService } from '../services/plant.service';
import { GroupedHarvest } from '../models/groupedharvest.model';
import { PlantHarvest } from '../models/plantharvest.model';

/**
 * UI Component for viewing, tracking, and grouping plant harvest yields.
 * 
 * @note This component layout and the data grouping logic were generated 
 *       by an LLM (Gemini) in June 2026 as a late-stage feature addition. 
 */

@Component({
  selector: 'app-harvest',
  imports: [
    CommonModule,
    FormsModule,
    MatCardModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  templateUrl: './harvest.html',
  styleUrl: './harvest.css',
})
export class Harvest implements OnInit {
  allHarvests: PlantHarvest[] = [];
  groupedHarvests: GroupedHarvest[] = [];
  allPlants: Plant[] = [];
  selectedGroup?: GroupedHarvest;

  showCreateForm = false;

  // Binding model for the new harvest form
  newOutput: PlantOutput = {
    plantOutputId: 0,
    plantId: 0,
    outputDate: new Date().toISOString(),
    outputAmount: 1
  };

  // Raw date tracker for the material datepicker
  pickerDate: Date = new Date();

  constructor(
    private harvestService: HarvestService,
    private plantService: PlantService,
    private cdr: ChangeDetectorRef
  ) {}

  async ngOnInit() {
    await this.loadInitialData();
  }

  // LOAD DATA & GROUP IT
  async loadInitialData() {
    try {
      this.allHarvests = await this.harvestService.getAll();
      this.allPlants = await this.plantService.getAll();
      this.groupHarvestData();
    } catch (error) {
      console.error('Failed to load harvest data:', error);
    }
  }

  // GROUP HARVEST DATA 
  private groupHarvestData() {
    const groupsMap = new Map<number, GroupedHarvest>();

    this.allHarvests.forEach(h => {
      if (!groupsMap.has(h.plantId)) {
        groupsMap.set(h.plantId, {
          plantId: h.plantId,
          plantName: h.plantName,
          plantType: h.plantType,
          plantLocation: h.plantLocation,
          totalAmount: 0,
          records: []
        });
      }
      
      const currentGroup = groupsMap.get(h.plantId)!;
      currentGroup.records.push(h);
      currentGroup.totalAmount += h.harvestAmount;
    });

    // Sort groups alphabetically by plant name
    this.groupedHarvests = Array.from(groupsMap.values()).sort((a, b) => 
      a.plantName.localeCompare(b.plantName)
    );

    // Keep active selection updated if it still exists
    if (this.selectedGroup) {
      this.selectedGroup = this.groupedHarvests.find(g => g.plantId === this.selectedGroup?.plantId);
    }
    
    this.cdr.detectChanges();
  }

  // CREATE HARVEST ENTRY
  async createHarvest() {
    if (this.newOutput.plantId === 0 || !this.newOutput.outputAmount) {
      alert('Please fill out all required fields.');
      return;
    }

    // Set the chosen date from material picker into ISO format
    this.newOutput.outputDate = this.pickerDate.toISOString();

    try {
      await this.harvestService.create(this.newOutput);
      this.showCreateForm = false;
      this.newOutput = { plantOutputId: 0, plantId: 0, outputDate: new Date().toISOString(), outputAmount: 1 };
      await this.loadInitialData();
    } catch (error) {
      console.error('Error creating harvest log:', error);
    }
  }

  // UPDATE ENTRY AMOUNT INLINE
  async editAmount(record: PlantHarvest) {
    const newAmountStr = prompt(`Enter new amount for ${record.plantName} on ${record.harvestDate}:`, record.harvestAmount.toString());
    if (newAmountStr === null) return;
    
    const newAmount = parseInt(newAmountStr, 10);
    if (isNaN(newAmount) || newAmount <= 0) {
      alert('Please enter a valid number greater than 0.');
      return;
    }

    // AI FIX: Clean up the date string to remove milliseconds and timezone markers (e.g. '.792Z')
    let safeDate = record.harvestDate || '';
    if (safeDate.includes('.')) {
      safeDate = safeDate.split('.')[0]; // Leaves you with clean "yyyy-MM-ddTHH:mm:ss"
    } else if (safeDate && !safeDate.includes('T')) {
      safeDate += 'T00:00:00';
    }

    const payload: PlantOutput = {
      plantOutputId: record.plantOutputId,
      plantId: record.plantId,
      outputDate: safeDate, // Maps back to the date key identifier
      outputAmount: newAmount
    };

    try {
      await this.harvestService.updateAmount(payload);
      await this.loadInitialData();
    } catch (error) {
      console.error('Error updating amount:', error);
    }
  }

  // DELETE HARVEST RECORD
  async deleteRecord(record: PlantHarvest) {
    const confirmDelete = confirm(`Permanently delete this entry (${record.harvestAmount}x) from ${record.harvestDate}?`);
    if (!confirmDelete) return;

    try {
      await this.harvestService.delete(record.plantOutputId);
      await this.loadInitialData();
    } catch (error) {
      console.error('Error deleting harvest log:', error);
    }
  }

  // SELECT PLANT GROUP
  selectGroup(group: GroupedHarvest) {
    this.selectedGroup = group;
  }

  // Handful vs Pieces
  getUnit(plantType: string): string {
    if (!plantType) return 'Stück';
    const type = plantType.toUpperCase();
    return (type === 'FRUIT' || type === 'BERRY' || type === 'HERB') ? 'Handvoll' : 'Stück';
  }
}
