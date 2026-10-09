import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth'; 

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent {
  credenciales = {
    username: '',
    password: ''
  };
  mensajeError: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  iniciarSesion(): void {
    console.log('Botón presionado. Credenciales:', this.credenciales); 
    
    this.authService.login(this.credenciales.username, this.credenciales.password).subscribe({
      next: (respuesta) => {
        // Guarda el token
        this.authService.guardarToken(respuesta.token);
        
        // NUEVO: Guarda el rol del usuario (tomamos el primero de la lista enviada por Spring Boot)
        if (respuesta.roles && respuesta.roles.length > 0) {
          localStorage.setItem('rol', respuesta.roles[0]); 
        }

        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.mensajeError = 'Usuario o contraseña incorrectos';
        console.error('Error de login:', err);
      }
    });
  }
}