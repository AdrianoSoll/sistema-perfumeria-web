import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { VentaService } from '../../services/venta';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './reportes.html',
  styleUrls: ['./reportes.css']
})
export class ReportesComponent implements OnInit {
  ventas: any[] = [];
  totalRecaudado: number = 0;

  // Cierre de caja desglosado
  totalEfectivo: number = 0;
  totalYapePlin: number = 0;
  totalTarjeta: number = 0;

  // Filtros de fecha
  fechaInicio: string = '';
  fechaFin: string = '';

  constructor(private ventaService: VentaService) {}

  ngOnInit(): void {
    // Por defecto, carga la fecha de HOY
    const hoy = new Date();
    // Formato YYYY-MM-DD
    const fechaFormat = hoy.toISOString().split('T')[0];
    this.fechaInicio = fechaFormat;
    this.fechaFin = fechaFormat;

    this.filtrarPorFechas();
  }

  filtrarPorFechas(): void {
    if (!this.fechaInicio || !this.fechaFin) {
      alert('Por favor selecciona ambas fechas.');
      return;
    }

    // Spring Boot espera LocalDateTime con hora: YYYY-MM-DDTHH:mm:ss
    const inicio = `${this.fechaInicio}T00:00:00`;
    const fin = `${this.fechaFin}T23:59:59`;

    this.ventaService.obtenerVentasPorFecha(inicio, fin).subscribe({
      next: (datos) => {
        this.ventas = datos;
        this.calcularTotales();
      },
      error: (err) => console.error('Error al filtrar ventas', err)
    });
  }

  cargarTodas(): void {
    this.fechaInicio = '';
    this.fechaFin = '';
    
    this.ventaService.obtenerVentas().subscribe({
      next: (datos) => {
        this.ventas = datos;
        this.calcularTotales();
      },
      error: (err) => console.error('Error al cargar historial', err)
    });
  }

calcularTotales(): void {
    this.totalRecaudado = 0;
    this.totalEfectivo = 0;
    this.totalYapePlin = 0;
    this.totalTarjeta = 0;

    this.ventas.forEach(venta => {
      this.totalRecaudado += venta.total;
      const metodo = venta.metodoPago?.toUpperCase();

      if (metodo === 'EFECTIVO') {
        this.totalEfectivo += venta.total;
      } else if (metodo === 'YAPE' || metodo === 'PLIN') {
        // Agrupamos todo el dinero bancario/digital aquí
        this.totalYapePlin += venta.total;
      } else if (metodo === 'TARJETA' || metodo === 'TRANSFERENCIA') {
        this.totalTarjeta += venta.total;
      }
    });
  }

  descargarComprobante(id: number): void {
    this.ventaService.descargarTicketPdf(id).subscribe({
      next: (pdfBlob) => {
        const url = window.URL.createObjectURL(pdfBlob);
        const enlace = document.createElement('a');
        enlace.href = url;
        enlace.download = `Boleta_EsenciaFina_N${id}.pdf`;
        enlace.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => console.error('Error al descargar el PDF', err)
    });
  }
}