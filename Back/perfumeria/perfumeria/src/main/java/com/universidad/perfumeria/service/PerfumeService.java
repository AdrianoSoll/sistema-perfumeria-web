package com.universidad.perfumeria.service;

import com.universidad.perfumeria.model.Perfume;
import com.universidad.perfumeria.repository.PerfumeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PerfumeService {

    @Autowired
    private PerfumeRepository perfumeRepository;

    // Obtener todos los perfumes (Gracias a @Where, esto SOLO traerá los activos)
    public List<Perfume> obtenerTodosActivos() {
        return perfumeRepository.findAll();
    }

    public Optional<Perfume> obtenerPorId(Long id) {
        return perfumeRepository.findById(id);
    }

    @Transactional
    public Perfume guardar(Perfume perfume) {
        // Asegurarnos de que si es nuevo, nazca como activo
        if (perfume.getId() == null) {
            perfume.setActivo(true);
        }
        return perfumeRepository.save(perfume);
    }

    @Transactional
    public void eliminar(Long id) {
        // Al ejecutar deleteById, Spring Data JPA verá la anotación @SQLDelete 
        // de la entidad y automáticamente ejecutará el UPDATE activo = false.
        perfumeRepository.deleteById(id);
    }
}