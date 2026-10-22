package com.universidad.perfumeria.repository;

import com.universidad.perfumeria.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Consulta JPQL explícita para buscar ventas que ocurrieron entre dos fechas específicas
    @Query("SELECT v FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin")
    List<Venta> findByFechaBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);

    // Consulta JPQL explícita para buscar ventas filtradas por Yape, Efectivo, etc.
    @Query("SELECT v FROM Venta v WHERE v.metodoPago = :metodoPago")
    List<Venta> findByMetodoPago(@Param("metodoPago") String metodoPago);
}