package com.universidad.perfumeria.model;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "perfumes")
// Intercepta el delete nativo y hace un update
@SQLDelete(sql = "UPDATE perfumes SET activo = false WHERE id = ?")
// Filtra automáticamente: Solo traerá los que tengan activo en true
@SQLRestriction("activo = true")
public class Perfume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String marca;
    
    // NUEVO CAMPO AÑADIDO
    @Column(name = "categoria")
    private String categoria;

    private String presentacion;
    private Integer stock;
    private Double precioCompra;
    private Double precioVenta;

    @Column(nullable = false)
    private Boolean activo = true;

    // Constructor vacío requerido por JPA
    public Perfume() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    
    // GETTERS Y SETTERS PARA LA NUEVA PROPIEDAD
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getPresentacion() { return presentacion; }
    public void setPresentacion(String presentacion) { this.presentacion = presentacion; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public Double getPrecioCompra() { return precioCompra; }
    public void setPrecioCompra(Double precioCompra) { this.precioCompra = precioCompra; }
    
    public Double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(Double precioVenta) { this.precioVenta = precioVenta; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}