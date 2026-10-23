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
  perfumesPaginados: any[] = []; 
  perfumesMostrados: any[] = []; 
  
  nuevoPerfume: any = { 
    nombre: '',
    marca: '',
    categoria: '', // <-- AÑADIDO
    presentacion: '',
    stock: null,
    precioCompra: null,
    precioVenta: null
  };

  terminoBusqueda: string = '';
  
  paginaActual: number = 0;
  tamanioPagina: number = 5; 
  totalPaginas: number = 0;
  totalElementos: number = 0;
  rolUsuario: string = '';

  constructor(
    private perfumeService: PerfumeService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.rolUsuario = localStorage.getItem('rol') || '';
    this.cargarPerfumes();
  }

  get esAdmin(): boolean {
    return this.rolUsuario === 'ROLE_ADMIN' || this.rolUsuario === 'ADMIN'; 
  }

  logout(): void {
    this.authService.cerrarSesion();
    localStorage.removeItem('rol');
    this.router.navigate(['/login']);
  }

  cargarPerfumes(): void {
    this.perfumeService.obtenerPerfumes().subscribe(datos => {
      this.perfumes = datos;
    });
    this.cargarPaginaInventario(0); 
  }

  cargarPaginaInventario(pageIndex: number): void {
    if (pageIndex < 0 || (this.totalPaginas > 0 && pageIndex >= this.totalPaginas)) return;
    
    this.perfumeService.obtenerPerfumesPaginados(pageIndex, this.tamanioPagina).subscribe({
      next: (response) => {
        this.perfumesPaginados = response.content; 
        this.paginaActual = response.number;
        this.totalPaginas = response.totalPages;
        this.totalElementos = response.totalElements;
        
        this.aplicarFiltro(); 
      },
      error: (err) => console.error('Error al cargar inventario paginado', err)
    });
  }

  aplicarFiltro(): void {
    if (!this.terminoBusqueda) {
      this.perfumesMostrados = [...this.perfumesPaginados];
    } else {
      const termino = this.terminoBusqueda.toLowerCase();
      this.perfumesMostrados = this.perfumesPaginados.filter(perfume => 
        perfume.nombre.toLowerCase().includes(termino) ||
        perfume.marca.toLowerCase().includes(termino) ||
        // <-- AÑADIDO: Ahora busca también por categoría
        (perfume.categoria && perfume.categoria.toLowerCase().includes(termino)) 
      );
    }
  }

  paginaSiguiente(event?: Event): void {
    if (event) event.preventDefault();
    this.cargarPaginaInventario(this.paginaActual + 1);
  }

  paginaAnterior(event?: Event): void {
    if (event) event.preventDefault();
    this.cargarPaginaInventario(this.paginaActual - 1);
  }

  cargarParaEditar(perfume: any): void {
    this.nuevoPerfume = { ...perfume };
  }

  guardarPerfume(): void {
    if (this.nuevoPerfume.id) {
      this.perfumeService.actualizarPerfume(this.nuevoPerfume.id, this.nuevoPerfume).subscribe(() => {
        this.cargarPaginaInventario(this.paginaActual); 
        this.limpiarFormulario();
      });
    } else {
      this.perfumeService.crearPerfume(this.nuevoPerfume).subscribe(() => {
        this.cargarPaginaInventario(0); 
        this.limpiarFormulario();
      });
    }
  }

  eliminarPerfume(id: number | undefined): void {
    if (id !== undefined && confirm('¿Estás seguro de eliminar este perfume?')) {
      this.perfumeService.eliminarPerfume(id).subscribe({
        next: () => {
          this.cargarPaginaInventario(this.paginaActual); 
        },
        error: (err) => {
          console.error('Error al eliminar:', err);
          alert('Error al eliminar');
        }
      });
    }
  }

  limpiarFormulario(): void {
    this.nuevoPerfume = {
      nombre: '',
      marca: '',
      categoria: '', // <-- AÑADIDO
      presentacion: '',
      stock: null,
      precioCompra: null,
      precioVenta: null
    };
  }
}