import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { VentaService, VentaDTO, DetalleVentaDTO } from '../../services/venta';
import { PerfumeService } from '../../perfume'; 


@Component({
  selector: 'app-pos',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pos.html',
  styleUrls: ['./pos.css']
})
export class PosComponent implements OnInit {
  
  perfumesDisponibles: any[] = []; 
  carrito: any[] = []; 
  metodoPagoSeleccionado: string = 'Efectivo';
  metodosPago: string[] = ['Efectivo', 'Yape', 'Plin', 'Transferencia', 'Tarjeta'];
  totalCalculado: number = 0;

  constructor(
    private ventaService: VentaService,
    private perfumeService: PerfumeService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarCatalogo();
  }

  cargarCatalogo(): void {
    this.perfumeService.obtenerPerfumes().subscribe({
      next: (datos) => {
        console.log("Perfumes recibidos en el POS:", datos);
        this.perfumesDisponibles = datos;
        this.cdr.detectChanges(); // Forzar la detección de cambios
      },
      error: (err) => {
        console.error('Error al conectar POS con la base de datos', err);
      }
    });
  }

  agregarAlCarrito(perfume: any): void {
    const itemExistente = this.carrito.find(item => item.perfume.id === perfume.id);
    
    if (itemExistente) {
      if (itemExistente.cantidad < perfume.stock) {
        itemExistente.cantidad++;
        itemExistente.subtotal = itemExistente.cantidad * perfume.precioVenta;
      } else {
        alert('No hay suficiente stock disponible');
      }
    } else {
      this.carrito.push({
        perfume: perfume,
        cantidad: 1,
        precioUnitario: perfume.precioVenta,
        subtotal: perfume.precioVenta
      });
    }
    this.actualizarTotal();
  }

  eliminarDelCarrito(index: number): void {
    this.carrito.splice(index, 1);
    this.actualizarTotal();
  }

  actualizarTotal(): void {
    this.totalCalculado = this.carrito.reduce((acc, item) => acc + item.subtotal, 0);
  }

  procesarVenta(): void {
    if (this.carrito.length === 0) {
      alert('El carrito está vacío');
      return;
    }

    const detallesVenta: DetalleVentaDTO[] = this.carrito.map(item => ({
      perfume: { id: item.perfume.id },
      cantidad: item.cantidad
    }));

    const nuevaVenta: VentaDTO = {
      metodoPago: this.metodoPagoSeleccionado,
      detalles: detallesVenta
    };

    this.ventaService.registrarVenta(nuevaVenta).subscribe({
      next: (respuesta) => {
        alert(`Venta registrada con éxito. N° Boleta: ${respuesta.id}`);
        this.carrito = [];
        this.actualizarTotal();
        this.cargarCatalogo(); // Recarga el stock actualizado
      },
      error: (err) => {
        alert('Error al procesar la venta');
      }
    });
  }
}