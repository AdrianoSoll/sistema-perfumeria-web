import { Routes } from '@angular/router';
import { LoginComponent } from './login/login';
import { DashboardComponent } from './dashboard/dashboard';
import { authGuard } from './guards/auth-guard'; // Apunta al archivo auth-guard.ts

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [authGuard] }, // Ruta protegida
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];