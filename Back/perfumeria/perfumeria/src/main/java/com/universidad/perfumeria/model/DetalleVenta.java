package com.universidad.perfumeria.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "detalle_ventas")
public class DetalleVenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación Muchos a 1: Muchos detalles pertenecen a una sola Venta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id")
    @JsonIgnore // Evita bucles infinitos en Postman/Angular
    private Venta venta;

    // Relación Muchos a 1: Muchos detalles pueden referenciar al mismo Perfume
    @ManyToOne
    @JoinColumn(name = "perfume_id")
    private Perfume perfume;

    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;

    public DetalleVenta() {
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Venta getVenta() { return venta; }
    public void setVenta(Venta venta) { this.venta = venta; }

    public Perfume getPerfume() { return perfume; }
    public void setPerfume(Perfume perfume) { this.perfume = perfume; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    public Double getSubtotal() { return subtotal; }
    public void setSubtotal(Double subtotal) { this.subtotal = subtotal; }
}