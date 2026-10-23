package com.universidad.perfumeria.controller.dto;

import java.util.List;

public class EstadisticasDTO {
    private List<String> fechas;
    private List<Double> totales;
    private List<String> perfumes;
    private List<Long> cantidades;

    public EstadisticasDTO(List<String> fechas, List<Double> totales, List<String> perfumes, List<Long> cantidades) {
        this.fechas = fechas;
        this.totales = totales;
        this.perfumes = perfumes;
        this.cantidades = cantidades;
    }

    public List<String> getFechas() { return fechas; }
    public List<Double> getTotales() { return totales; }
    public List<String> getPerfumes() { return perfumes; }
    public List<Long> getCantidades() { return cantidades; }
}