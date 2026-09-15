import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common'; 
import { FormsModule } from '@angular/forms'; 
import { PerfumeService, Perfume } from './perfume'; 

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, FormsModule], 
  templateUrl: './app.html', 
  styleUrls: ['./app.css']
})
export class App implements OnInit {
  perfumes: Perfume[] = [];
  
  nuevoPerfume: Perfume = {
    nombre: '',
    marca: '',
    presentacion: '',
    stock: null as any,
    precioCompra: null as any,
    precioVenta: null as any
  };

  terminoBusqueda: string = '';

  constructor(private perfumeService: PerfumeService) {}

  ngOnInit(): void {
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

  cargarParaEditar(perfume: Perfume): void {
    this.nuevoPerfume = { ...perfume };
  }

  guardarPerfume(): void {
    if (this.nuevoPerfume.id) {
      this.perfumeService.actualizarPerfume(this.nuevoPerfume.id, this.nuevoPerfume).subscribe(perfumeActualizado => {
        const index = this.perfumes.findIndex(p => p.id === perfumeActualizado.id);
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
            this.perfumes = this.perfumes.filter(p => p.id !== id);
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
      stock: null as any,
      precioCompra: null as any,
      precioVenta: null as any
    };
  }
}