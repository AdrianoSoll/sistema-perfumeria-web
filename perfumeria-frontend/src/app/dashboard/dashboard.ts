import { ReportesComponent } from '../components/reportes/reportes';
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PosComponent } from '../components/pos/pos';
import { PerfumeService } from '../perfume';
import { AuthService } from '../services/auth';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [PosComponent, ReportesComponent, CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.css']
})
export class DashboardComponent implements OnInit {
  vistaActual: string = 'pos'; 
  perfumes: any[] = []; 
  
  nuevoPerfume: any = { 
    nombre: '',
    marca: '',
    presentacion: '',
    stock: null,
    precioCompra: null,
    precioVenta: null
  };

  terminoBusqueda: string = '';
  
  // NUEVO: Variable para almacenar el rol del usuario actual
  rolUsuario: string = '';

  constructor(
    private perfumeService: PerfumeService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // NUEVO: Leemos el rol guardado en el login
    this.rolUsuario = localStorage.getItem('rol') || '';
    
    this.cargarPerfumes();
  }

  // NUEVO: Función para determinar si el usuario es administrador
  get esAdmin(): boolean {
    return this.rolUsuario === 'ROLE_ADMIN' || this.rolUsuario === 'ADMIN'; 
  }

  logout(): void {
    this.authService.cerrarSesion();
    localStorage.removeItem('rol'); // NUEVO: Limpiamos el rol al salir
    this.router.navigate(['/login']);
  }

  cargarPerfumes(): void {
    this.perfumeService.obtenerPerfumes().subscribe(datos => {
      this.perfumes = datos;
    });
  }

  get perfumesFiltrados() {
    if (!this.terminoBusqueda) {
      return this.perfumes;
    }
    return this.perfumes.filter(perfume => 
      perfume.nombre.toLowerCase().includes(this.terminoBusqueda.toLowerCase()) ||
      perfume.marca.toLowerCase().includes(this.terminoBusqueda.toLowerCase())
    );
  }

  cargarParaEditar(perfume: any): void {
    this.nuevoPerfume = { ...perfume };
  }

  guardarPerfume(): void {
    if (this.nuevoPerfume.id) {
      this.perfumeService.actualizarPerfume(this.nuevoPerfume.id, this.nuevoPerfume).subscribe(perfumeActualizado => {
        const index = this.perfumes.findIndex((p: any) => p.id === perfumeActualizado.id);
        if (index !== -1) {
          this.perfumes[index] = perfumeActualizado;
        }
        this.limpiarFormulario();
      });
    } else {
      this.perfumeService.crearPerfume(this.nuevoPerfume).subscribe(perfumeGuardado => {
        this.perfumes.push(perfumeGuardado);
        this.limpiarFormulario();
      });
    }
  }

  eliminarPerfume(id: number | undefined): void {
    if (id !== undefined) {
      if (confirm('¿Estás seguro de eliminar este perfume?')) {
        this.perfumeService.eliminarPerfume(id).subscribe({
          next: () => {
            this.perfumes = this.perfumes.filter((p: any) => p.id !== id);
          },
          error: (err) => {
            console.error('Error completo:', err);
            alert('Error al eliminar: ' + err.message);
          }
        });
      }
    }
  }

  limpiarFormulario(): void {
    this.nuevoPerfume = {
      nombre: '',
      marca: '',
      presentacion: '',
      stock: null,
      precioCompra: null,
      precioVenta: null
    };
  }
}