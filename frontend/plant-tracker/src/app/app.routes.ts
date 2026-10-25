import { Routes } from '@angular/router';
import { Plants } from './plants/plants';
import { Gardenbeds } from './gardenbeds/gardenbeds';
import { Harvest } from './harvest/harvest';

export const routes: Routes = [
    { path: '', redirectTo: 'plants', pathMatch: 'full' }, // Landing Page = Redirects to Plants
    { path: 'plants', component: Plants },
    { path: 'gardenbeds', component: Gardenbeds },
    { path: 'harvest', component: Harvest }
];
