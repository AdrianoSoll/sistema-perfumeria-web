package com.universidad.perfumeria.controller;

import com.universidad.perfumeria.model.Perfume;
import com.universidad.perfumeria.service.PerfumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfumes")
@CrossOrigin(origins = "http://localhost:4200")
public class PerfumeController {

    @Autowired
    private PerfumeService perfumeService;

    // Endpoint original (Usado por el Punto de Venta)
    @GetMapping
    public List<Perfume> obtenerPerfumes() {
        return perfumeService.obtenerTodosActivos();
    }

    // NUEVO ENDPOINT: Paginado (Usado por la tabla de Inventario)
    // Ejemplo de uso: /api/perfumes/page?page=0&size=5
    @GetMapping("/page")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<Perfume> obtenerPerfumesPaginados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return perfumeService.obtenerTodosPaginados(page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Perfume crearPerfume(@RequestBody Perfume perfume) {
        return perfumeService.guardar(perfume);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Perfume> actualizarPerfume(@PathVariable Long id, @RequestBody Perfume detallesPerfume) {
        return perfumeService.obtenerPorId(id)
                .map(perfumeExistente -> {
                    perfumeExistente.setNombre(detallesPerfume.getNombre());
                    perfumeExistente.setMarca(detallesPerfume.getMarca());
                    perfumeExistente.setCategoria(detallesPerfume.getCategoria()); // <-- AÑADIDO: Actualiza la categoría
                    perfumeExistente.setPresentacion(detallesPerfume.getPresentacion());
                    perfumeExistente.setStock(detallesPerfume.getStock());
                    perfumeExistente.setPrecioCompra(detallesPerfume.getPrecioCompra());
                    perfumeExistente.setPrecioVenta(detallesPerfume.getPrecioVenta());
                    Perfume actualizado = perfumeService.guardar(perfumeExistente);
                    return ResponseEntity.ok(actualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarPerfume(@PathVariable Long id) {
        return perfumeService.obtenerPorId(id)
                .map(perfume -> {
                    perfumeService.eliminar(id);
                    return ResponseEntity.ok().<Void>build();
                }).orElse(ResponseEntity.notFound().build());
    }
}