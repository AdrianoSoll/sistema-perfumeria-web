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
}