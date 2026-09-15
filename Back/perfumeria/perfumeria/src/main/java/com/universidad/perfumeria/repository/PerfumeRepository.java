package com.universidad.perfumeria.repository;

import com.universidad.perfumeria.model.Perfume; // ¡Ahora sí se usará esta importación!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerfumeRepository extends JpaRepository<Perfume, Long> {
    // Fíjate en el <Perfume, Long> al final. Esa es la clave.
}