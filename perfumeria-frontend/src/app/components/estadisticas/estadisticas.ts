import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartData, ChartType } from 'chart.js';
import { VentaService, EstadisticasDTO } from '../../services/venta'; // Ajusta la ruta si es necesario

@Component({
  selector: 'app-estadisticas',
  standalone: true,
  imports: [CommonModule, BaseChartDirective],
  templateUrl: './estadisticas.html',
  styleUrls: ['./estadisticas.css']
})
export class EstadisticasComponent implements OnInit {

  // Variables para controlar si hay datos
  hayVentas: boolean = false;
  cargando: boolean = true;

  // Configuración del Gráfico de Barras (Ingresos por Día)
  public barChartType: ChartType = 'bar';
  public barChartData: ChartData<'bar'> = {
    labels: [],
    datasets: [
      { 
        data: [], 
        label: 'Ingresos S/', 
        backgroundColor: '#7b1fa2', // Morado elegante
        borderRadius: 4
      }
    ]
  };
  public barChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { display: false },
      title: { display: true, text: 'Ingresos de los últimos días' }
    }
  };

  // Configuración del Gráfico Circular (Top 3 Perfumes)
  public pieChartType: ChartType = 'pie';
  public pieChartData: ChartData<'pie'> = {
    labels: [],
    datasets: [
      { 
        data: [],
        backgroundColor: ['#4a148c', '#ab47bc', '#e1bee7'], // Paleta de morados
      }
    ]
  };
  public pieChartOptions: ChartConfiguration['options'] = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: { position: 'right' },
      title: { display: true, text: 'Top Perfumes Vendidos' }
    }
  };

  constructor(private ventaService: VentaService) {}

  ngOnInit(): void {
    this.cargarDatos();
  }

  cargarDatos(): void {
    this.ventaService.obtenerEstadisticas().subscribe({
      next: (datos: EstadisticasDTO) => {
        if (datos.totales.length > 0) {
          this.hayVentas = true;
          
          // Llenamos el gráfico de barras
          this.barChartData.labels = datos.fechas;
          this.barChartData.datasets[0].data = datos.totales;

          // Llenamos el gráfico circular
          this.pieChartData.labels = datos.perfumes;
          this.pieChartData.datasets[0].data = datos.cantidades;
        }
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar estadísticas', err);
        this.cargando = false;
      }
    });
  }
}