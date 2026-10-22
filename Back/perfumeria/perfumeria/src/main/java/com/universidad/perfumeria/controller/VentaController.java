package com.universidad.perfumeria.controller;

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

    // Ya no inyectamos el PerfumeRepository aquí, el servicio se encarga de eso.
    
    // Inyectamos nuestro nuevo servicio transaccional
    @Autowired
    private VentaService ventaService;

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

    // EL REGISTRO DE VENTA AHORA ES TRANSACCIONAL A TRAVÉS DEL SERVICIO
    @PostMapping
    public ResponseEntity<?> registrarVenta(@RequestBody Venta venta) {
        try {
            // El controlador ahora solo delega la responsabilidad al servicio seguro
            Venta ventaGuardada = ventaService.registrarVenta(venta);
            return ResponseEntity.status(HttpStatus.CREATED).body(ventaGuardada);
        } catch (IllegalArgumentException e) {
            // Si el servicio lanza el error de "Stock insuficiente" o "Perfume no existe"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            // Cualquier otro error imprevisto (Base de datos caída, etc.)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando la venta: " + e.getMessage());
        }
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