package com.universidad.perfumeria.service;

import com.universidad.perfumeria.model.Perfume;
import com.universidad.perfumeria.repository.PerfumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PerfumeService {

    @Autowired
    private PerfumeRepository perfumeRepository;

    // Método original para el POS (trae todos los activos)
    public List<Perfume> obtenerTodosActivos() {
        return perfumeRepository.findAll();
    }

    // NUEVO MÉTODO: Paginación para la tabla de Inventario
    public Page<Perfume> obtenerTodosPaginados(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return perfumeRepository.findAll(pageable);
    }

    public Optional<Perfume> obtenerPorId(Long id) {
        return perfumeRepository.findById(id);
    }

    @Transactional
    public Perfume guardar(Perfume perfume) {
        if (perfume.getId() == null) {
            perfume.setActivo(true);
        }
        return perfumeRepository.save(perfume);
    }

    @Transactional
    public void eliminar(Long id) {
        perfumeRepository.deleteById(id);
    }
}