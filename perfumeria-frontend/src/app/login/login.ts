import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  // IMPORTANTE: Ahora usamos ReactiveFormsModule
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;
  mensajeError: string = '';
  cargando: boolean = false;
  mostrarPassword: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // Inicializamos el formulario con validaciones estrictas
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.pattern(/^\S*$/)]], // No permite espacios
      password: ['', Validators.required]
    });
  }

  // Getter para facilitar el acceso a los campos en el HTML
  get f() { return this.loginForm.controls; }

  iniciarSesion(): void {
    // Si el formulario es inválido, marcamos los campos para que salte el error visual
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.mensajeError = '';
    this.cargando = true;

    // Extraemos los valores ya validados
    const { username, password } = this.loginForm.value;

    this.authService.login(username, password).subscribe({
      next: (respuesta: any) => {
        this.authService.guardarToken(respuesta.token);

        if (respuesta.roles && respuesta.roles.length > 0) {
          localStorage.setItem('rol', respuesta.roles[0]);
        }

        this.cargando = false;
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.cargando = false;
        this.mensajeError =
          err.status === 0
            ? 'No se pudo conectar con el servidor. Inténtalo de nuevo en unos minutos.'
            : 'Usuario o contraseña incorrectos.';
        console.error('Error de login:', err);
      }
    });
  }
}