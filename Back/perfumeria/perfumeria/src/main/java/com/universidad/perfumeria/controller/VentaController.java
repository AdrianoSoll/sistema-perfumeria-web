package com.universidad.perfumeria.controller;

import com.universidad.perfumeria.controller.dto.EstadisticasDTO; // <-- IMPORTACIÓN NUEVA
import com.universidad.perfumeria.service.VentaPdfService;
import com.universidad.perfumeria.service.VentaService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import com.universidad.perfumeria.model.Venta;
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
    private VentaService ventaService;

    @Autowired
    private VentaPdfService ventaPdfService;

    @GetMapping
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @GetMapping("/fechas")
    public ResponseEntity<List<Venta>> obtenerVentasPorFecha(
            @RequestParam("inicio") LocalDateTime inicio,
            @RequestParam("fin") LocalDateTime fin) {
        List<Venta> ventas = ventaRepository.findByFechaBetween(inicio, fin);
        return ResponseEntity.ok(ventas);
    }

    @GetMapping("/metodo")
    public ResponseEntity<List<Venta>> obtenerVentasPorMetodo(@RequestParam("tipo") String tipo) {
        List<Venta> ventas = ventaRepository.findByMetodoPago(tipo);
        return ResponseEntity.ok(ventas);
    }

    // --- NUEVO ENDPOINT PARA GRÁFICOS DEL DASHBOARD ---
    @GetMapping("/estadisticas")
    public ResponseEntity<EstadisticasDTO> obtenerEstadisticas() {
        return ResponseEntity.ok(ventaService.obtenerEstadisticasDashboard());
    }

    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody Venta venta) {
        try {
            Venta ventaGuardada = ventaService.registrarVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando la venta: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/pdf")
    public void descargarBoletaPdf(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Optional<Venta> ventaOpt = ventaRepository.findById(id);
        
        if (ventaOpt.isPresent()) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=boleta_" + id + ".pdf");
            ventaPdfService.generarBoletaPdf(ventaOpt.get(), response);
        } else {
            throw new IllegalArgumentException("La boleta con ID " + id + " no existe en el sistema.");
        }
    }
}