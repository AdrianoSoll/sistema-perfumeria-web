import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Perfume {
  id?: number;
  nombre: string;
  marca: string;
  presentacion: string;
  stock: number;
  precioCompra: number;
  precioVenta: number;
}

@Injectable({
  providedIn: 'root'
})
export class PerfumeService {
  private apiUrl = 'http://localhost:8080/api/perfumes';

  constructor(private http: HttpClient) { }

  obtenerPerfumes(): Observable<Perfume[]> {
    return this.http.get<Perfume[]>(this.apiUrl);
  }

  // Método para enviar datos mediante POST (Crear)
  crearPerfume(perfume: Perfume): Observable<Perfume> {
    return this.http.post<Perfume>(this.apiUrl, perfume);
  }

  // Método para enviar la petición DELETE (Eliminar)
  eliminarPerfume(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // NUEVO: Método para enviar la petición PUT (Actualizar/Editar)
  actualizarPerfume(id: number, perfume: Perfume): Observable<Perfume> {
    return this.http.put<Perfume>(`${this.apiUrl}/${id}`, perfume);
  }
}