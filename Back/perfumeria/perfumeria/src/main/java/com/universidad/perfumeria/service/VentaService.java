package com.universidad.perfumeria.service;

import com.universidad.perfumeria.model.DetalleVenta;
import com.universidad.perfumeria.model.Perfume;
import com.universidad.perfumeria.model.Venta;
import com.universidad.perfumeria.repository.PerfumeRepository;
import com.universidad.perfumeria.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private PerfumeRepository perfumeRepository;

    // La anotación @Transactional garantiza que la operación sea atómica.
    // Si se lanza cualquier excepción, se hace un ROLLBACK automático.
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
                    // Al lanzar una excepción aquí, @Transactional anula cualquier guardado previo
                    throw new IllegalArgumentException("Stock insuficiente para '" + perfumeReal.getNombre() + "'. Stock actual: " + perfumeReal.getStock());
                }

                // Descontar stock
                perfumeReal.setStock(perfumeReal.getStock() - detalle.getCantidad());
                perfumeRepository.save(perfumeReal);

                // Calcular subtotales
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
}