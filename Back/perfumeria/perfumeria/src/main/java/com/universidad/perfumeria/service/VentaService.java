package com.universidad.perfumeria.service;

import com.universidad.perfumeria.controller.dto.EstadisticasDTO;
import com.universidad.perfumeria.model.DetalleVenta;
import com.universidad.perfumeria.model.Perfume;
import com.universidad.perfumeria.model.Venta;
import com.universidad.perfumeria.repository.PerfumeRepository;
import com.universidad.perfumeria.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    @Transactional(rollbackFor = Exception.class)
    public Venta registrarVenta(Venta venta) {
        venta.setFecha(LocalDateTime.now());
        double totalVenta = 0.0;

        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                Optional<Perfume> perfumeOpt = perfumeRepository.findById(detalle.getPerfume().getId());

                if (!perfumeOpt.isPresent()) {
                    throw new IllegalArgumentException("El perfume con ID " + detalle.getPerfume().getId() + " no existe.");
                }

                Perfume perfumeReal = perfumeOpt.get();

                if (perfumeReal.getStock() < detalle.getCantidad()) {
                    throw new IllegalArgumentException("Stock insuficiente para '" + perfumeReal.getNombre() + "'.");
                }

                perfumeReal.setStock(perfumeReal.getStock() - detalle.getCantidad());
                perfumeRepository.save(perfumeReal);

                detalle.setPrecioUnitario(perfumeReal.getPrecioVenta());
                double subtotal = detalle.getCantidad() * perfumeReal.getPrecioVenta();
                detalle.setSubtotal(subtotal);

                totalVenta += subtotal;
                detalle.setVenta(venta);
            }
        }
        venta.setTotal(totalVenta);
        return ventaRepository.save(venta);
    }

    // NUEVO MÉTODO PARA EL DASHBOARD
    public EstadisticasDTO obtenerEstadisticasDashboard() {
        // Obtenemos últimos 7 registros de días
        List<Object[]> ventas = ventaRepository.obtenerVentasPorFecha(PageRequest.of(0, 7));
        List<String> fechas = new ArrayList<>();
        List<Double> totales = new ArrayList<>();
        
        for (Object[] row : ventas) {
            fechas.add(row[0].toString());
            totales.add(Double.valueOf(row[1].toString()));
        }

        // Obtenemos los 3 perfumes más vendidos
        List<Object[]> topPerfumes = ventaRepository.obtenerTopPerfumes(PageRequest.of(0, 3));
        List<String> perfumes = new ArrayList<>();
        List<Long> cantidades = new ArrayList<>();
        
        for (Object[] row : topPerfumes) {
            perfumes.add(row[0].toString());
            cantidades.add(Long.valueOf(row[1].toString()));
        }

        return new EstadisticasDTO(fechas, totales, perfumes, cantidades);
    }
}