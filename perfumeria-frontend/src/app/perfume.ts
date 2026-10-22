import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Perfume {
  id?: number;
  nombre: string;
  marca: string;
  presentacion: string;
  stock: number;
  precioCompra: number;
  precioVenta: number;
  activo?: boolean; // Añadido para el soft delete (opcional en el front)
}

// Nueva interfaz para mapear la respuesta paginada de Spring Boot
export interface PerfumePage {
  content: Perfume[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number; // Página actual (0-indexed)
}

@Injectable({
  providedIn: 'root'
})
export class PerfumeService {
  private apiUrl = 'http://localhost:8080/api/perfumes';

  constructor(private http: HttpClient) { }

  // Método original para el POS (Sin paginación, trae todos los activos)
  obtenerPerfumes(): Observable<Perfume[]> {
    return this.http.get<Perfume[]>(this.apiUrl);
  }

  // NUEVO MÉTODO: Obtener perfumes con paginación
  obtenerPerfumesPaginados(page: number, size: number): Observable<PerfumePage> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PerfumePage>(`${this.apiUrl}/page`, { params });
  }

  crearPerfume(perfume: Perfume): Observable<Perfume> {
    return this.http.post<Perfume>(this.apiUrl, perfume);
  }

  eliminarPerfume(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  actualizarPerfume(id: number, perfume: Perfume): Observable<Perfume> {
    return this.http.put<Perfume>(`${this.apiUrl}/${id}`, perfume);
  }
}