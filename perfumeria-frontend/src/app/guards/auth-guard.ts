import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.obtenerToken();

  if (token) {
    return true; // Token válido presente, permite el acceso
  } else {
    router.navigate(['/login']); // Redirige al login si no hay token
    return false;
  }
};