package com.universidad.perfumeria.controller;

import com.universidad.perfumeria.service.VentaPdfService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.universidad.perfumeria.model.DetalleVenta;
import com.universidad.perfumeria.model.Perfume;
import com.universidad.perfumeria.model.Venta;
import com.universidad.perfumeria.repository.PerfumeRepository;
import com.universidad.perfumeria.repository.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private PerfumeRepository perfumeRepository; // Puente para acceder a los datos de los perfumes

    @Autowired
    private VentaPdfService ventaPdfService;

    @GetMapping
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    // Endpoint para filtrar por rango de fechas
    // Ejemplo de URL: /api/ventas/fechas?inicio=2026-09-17T00:00:00&fin=2026-09-17T23:59:59
    @GetMapping("/fechas")
    public ResponseEntity<List<Venta>> obtenerVentasPorFecha(
            @RequestParam("inicio") LocalDateTime inicio,
            @RequestParam("fin") LocalDateTime fin) {
        List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);
        return ResponseEntity.ok(ventas);
    }

    // Endpoint para filtrar por método de pago
    // Ejemplo de URL: /api/ventas/metodo?tipo=Yape
    @GetMapping("/metodo")
    public ResponseEntity<List<Venta>> obtenerVentasPorMetodo(@RequestParam("tipo") String tipo) {
        List<Venta> ventas = ventaRepository.findByMetodoPago(tipo);
        return ResponseEntity.ok(ventas);
    }

    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody Venta venta) {
        venta.setFecha(LocalDateTime.now());
        double totalVenta = 0.0;

        if (venta.getDetalles() != null) {
            for (DetalleVenta detalle : venta.getDetalles()) {
                // 1. Buscar el perfume real en la base de datos
                Optional<Perfume> perfumeOpt = perfumeRepository.findById(detalle.getPerfume().getId());
                
                if (!perfumeOpt.isPresent()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Error: El perfume con ID " + detalle.getPerfume().getId() + " no existe.");
                }

                Perfume perfumeReal = perfumeOpt.get();

                // 2. Validar que haya suficiente stock
                if (perfumeReal.getStock() < detalle.getCantidad()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Error: Stock insuficiente para '" + perfumeReal.getNombre() + 
                                  "'. Stock actual: " + perfumeReal.getStock());
                }

                // 3. Descontar el stock y guardar la actualización del perfume
                perfumeReal.setStock(perfumeReal.getStock() - detalle.getCantidad());
                perfumeRepository.save(perfumeReal);

                // 4. Calcular precios automáticamente desde el backend
                detalle.setPrecioUnitario(perfumeReal.getPrecioVenta());
                double subtotal = detalle.getCantidad() * perfumeReal.getPrecioVenta();
                detalle.setSubtotal(subtotal);

                // 5. Acumular al total de la boleta y enlazar el detalle con la venta
                totalVenta += subtotal;
                detalle.setVenta(venta);
            }
        }

        // 6. Asignar el total calculado y guardar la venta
        venta.setTotal(totalVenta);
        Venta ventaGuardada = ventaRepository.save(venta);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
    }

// Generar y descargar Boleta en PDF
    @GetMapping("/{id}/pdf")
    public void descargarBoletaPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<Venta> ventaOpt = ventaRepository.findById(id);
        
        if (ventaOpt.isPresent()) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=boleta_" + id + ".pdf");
            ventaPdfService.generarBoletaPdf(ventaOpt.get(), response);
        } else {
            // ¡Aquí lanzamos el error que será capturado por el GlobalExceptionHandler!
            throw new IllegalArgumentException("La boleta con ID " + id + " no existe en el sistema.");
        }
    }
}