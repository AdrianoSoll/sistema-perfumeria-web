package com.universidad.perfumeria.repository;

import com.universidad.perfumeria.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Busca las ventas que ocurrieron entre dos fechas/horas específicas
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    // Busca las ventas filtradas por Yape, Efectivo, etc.
    List<Venta> findByMetodoPago(String metodoPago);
}