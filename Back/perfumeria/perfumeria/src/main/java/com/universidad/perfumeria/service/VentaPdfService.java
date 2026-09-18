package com.universidad.perfumeria.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.universidad.perfumeria.model.DetalleVenta;
import com.universidad.perfumeria.model.Venta;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.IOException;

@Service
public class VentaPdfService {

    public void generarBoletaPdf(Venta venta, HttpServletResponse response) throws IOException {
        // Inicializar documento A4
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());
        document.open();

        // Título de la Boleta
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, Color.BLACK);
        Paragraph titulo = new Paragraph("BOLETA DE VENTA - ESENCIA FINA", fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        document.add(titulo);
        document.add(new Paragraph(" ")); // Espacio en blanco

        // Datos Generales
        Font fontDatos = FontFactory.getFont(FontFactory.HELVETICA, 12, Color.DARK_GRAY);
        document.add(new Paragraph("N° de Boleta: " + venta.getId(), fontDatos));
        document.add(new Paragraph("Fecha: " + venta.getFecha().toString(), fontDatos));
        document.add(new Paragraph("Método de Pago: " + venta.getMetodoPago(), fontDatos));
        document.add(new Paragraph(" "));

        // Tabla de Productos
        PdfPTable tabla = new PdfPTable(4); // 4 columnas
        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{1.5f, 4f, 2f, 2f});

        // Cabeceras de la tabla
        String[] cabeceras = {"Cant.", "Descripción (Perfume)", "P. Unitario", "Subtotal"};
        for (String cabecera : cabeceras) {
            PdfPCell celda = new PdfPCell(new Phrase(cabecera, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            celda.setBackgroundColor(Color.LIGHT_GRAY);
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabla.addCell(celda);
        }

        // Llenar datos de los perfumes vendidos
        for (DetalleVenta detalle : venta.getDetalles()) {
            tabla.addCell(String.valueOf(detalle.getCantidad()));
            tabla.addCell(detalle.getPerfume().getNombre() + " - " + detalle.getPerfume().getMarca());
            tabla.addCell("S/ " + detalle.getPrecioUnitario());
            tabla.addCell("S/ " + detalle.getSubtotal());
        }
        document.add(tabla);
        document.add(new Paragraph(" "));

        // Total
        Font fontTotal = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Color.RED);
        Paragraph total = new Paragraph("TOTAL A PAGAR: S/ " + venta.getTotal(), fontTotal);
        total.setAlignment(Element.ALIGN_RIGHT);
        document.add(total);

        document.close();
    }
}