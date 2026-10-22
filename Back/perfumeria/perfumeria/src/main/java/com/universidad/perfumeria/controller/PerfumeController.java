package com.universidad.perfumeria.controller;

import com.universidad.perfumeria.model.Perfume;
import com.universidad.perfumeria.service.PerfumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfumes")
@CrossOrigin(origins = "http://localhost:4200")
public class PerfumeController {

    // Ya no inyectamos el Repository, inyectamos el Service (Arquitectura N-Capas)
    @Autowired
    private PerfumeService perfumeService;

    // Cualquier usuario logueado puede ver el catálogo (Cajero o Admin)
    @GetMapping
    public List<Perfume> obtenerPerfumes() {
        return perfumeService.obtenerTodosActivos();
    }

    // Solo el ADMIN puede crear perfumes
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Perfume crearPerfume(@RequestBody Perfume perfume) {
        return perfumeService.guardar(perfume);
    }

    // Solo el ADMIN puede editar perfumes
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Perfume> actualizarPerfume(@PathVariable Long id, @RequestBody Perfume detallesPerfume) {
        return perfumeService.obtenerPorId(id)
                .map(perfumeExistente -> {
                    perfumeExistente.setNombre(detallesPerfume.getNombre());
                    perfumeExistente.setMarca(detallesPerfume.getMarca());
                    perfumeExistente.setPresentacion(detallesPerfume.getPresentacion());
                    perfumeExistente.setStock(detallesPerfume.getStock());
                    perfumeExistente.setPrecioCompra(detallesPerfume.getPrecioCompra());
                    perfumeExistente.setPrecioVenta(detallesPerfume.getPrecioVenta());
                    // El estado 'activo' se mantiene igual
                    Perfume actualizado = perfumeService.guardar(perfumeExistente);
                    return ResponseEntity.ok(actualizado);
                }).orElse(ResponseEntity.notFound().build());
    }

    // Solo el ADMIN puede aplicar el Soft Delete
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