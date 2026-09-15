package com.universidad.perfumeria.controller;

import com.universidad.perfumeria.model.Perfume;
import com.universidad.perfumeria.repository.PerfumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfumes")
@CrossOrigin(origins = "*") // Permite que Angular se conecte sin bloqueos de seguridad CORS
public class PerfumeController {

    @Autowired
    private PerfumeRepository repository;

    // Método GET: Devuelve todos los perfumes
    @GetMapping
    public List<Perfume> listarTodos() {
        return repository.findAll();
    }

    // Método POST: Guarda un nuevo perfume
    @PostMapping
    public Perfume crearPerfume(@RequestBody Perfume perfume) {
        return repository.save(perfume);
    }

    // Método PUT: Actualiza un perfume existente (NUEVO)
    @PutMapping("/{id}")
    public Perfume actualizarPerfume(@PathVariable Long id, @RequestBody Perfume perfumeActualizado) {
        return repository.findById(id)
                .map(perfume -> {
                    perfume.setNombre(perfumeActualizado.getNombre());
                    perfume.setMarca(perfumeActualizado.getMarca());
                    perfume.setPresentacion(perfumeActualizado.getPresentacion());
                    perfume.setStock(perfumeActualizado.getStock());
                    perfume.setPrecioCompra(perfumeActualizado.getPrecioCompra());
                    perfume.setPrecioVenta(perfumeActualizado.getPrecioVenta());
                    return repository.save(perfume);
                })
                .orElseThrow(() -> new RuntimeException("Perfume no encontrado"));
    }

    // Método DELETE: Elimina un perfume
    @DeleteMapping("/{id}")
    public void eliminarPerfume(@PathVariable Long id) {
        repository.deleteById(id);
    }
}