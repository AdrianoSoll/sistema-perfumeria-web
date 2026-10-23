import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface DetalleVentaDTO {
  perfume: { id: number };
  cantidad: number;
}

export interface VentaDTO {
  metodoPago: string;
  detalles: DetalleVentaDTO[];
}

// NUEVO: Interfaz para recibir los datos de los gráficos
export interface EstadisticasDTO {
  fechas: string[];
  totales: number[];
  perfumes: string[];
  cantidades: number[];
}

@Injectable({
  providedIn: 'root'
})
export class VentaService {
  private apiUrl = 'http://localhost:8080/api/ventas';

  constructor(private http: HttpClient) { }

  registrarVenta(venta: VentaDTO): Observable<any> {
    return this.http.post<any>(this.apiUrl, venta);
  }

  obtenerVentas(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  descargarTicketPdf(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/pdf`, { responseType: 'blob' });
  }
  
  obtenerVentasPorFecha(inicio: string, fin: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/fechas?inicio=${inicio}&fin=${fin}`);
  }

  // NUEVO MÉTODO: Para pedir las estadísticas de los gráficos
  obtenerEstadisticas(): Observable<EstadisticasDTO> {
    return this.http.get<EstadisticasDTO>(`${this.apiUrl}/estadisticas`);
  }
}