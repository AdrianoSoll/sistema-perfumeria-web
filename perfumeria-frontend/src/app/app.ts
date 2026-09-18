import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterOutlet } from '@angular/router';
import { PosComponent } from './components/pos/pos';
// Importamos tu servicio de perfumes
import { PerfumeService } from './perfume';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, PosComponent, CommonModule, FormsModule],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class AppComponent implements OnInit {
  title = 'perfumeria-frontend';
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

  // Inyectamos el servicio
  constructor(private perfumeService: PerfumeService) {}

  ngOnInit(): void {
    this.cargarPerfumes();
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
      if(confirm('¿Estás seguro de eliminar este perfume?')) {
        this.perfumeService.eliminarPerfume(id).subscribe({
          next: () => {
            this.perfumes = this.perfumes.filter((p: any) => p.id !== id);
          },
          error: (err) => {
            console.error("Error completo:", err);
            alert("Error al eliminar: " + err.message);
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