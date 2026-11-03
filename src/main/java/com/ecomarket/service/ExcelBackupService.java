
package com.ecomarket.service;

import com.ecomarket.model.Producto;
import com.ecomarket.model.Cliente;
import com.ecomarket.model.Inventario;
import com.ecomarket.model.Venta;
import com.ecomarket.model.Factura;

import com.ecomarket.repository.ProductoRepository;
import com.ecomarket.repository.ClienteRepository;
import com.ecomarket.repository.InventarioRepository;
import com.ecomarket.repository.VentaRepository;
import com.ecomarket.repository.FacturaRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelBackupService {

    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final InventarioRepository inventarioRepository;
    private final VentaRepository ventaRepository;
    private final FacturaRepository facturaRepository;

    public ExcelBackupService(
            ProductoRepository productoRepository,
            ClienteRepository clienteRepository,
            InventarioRepository inventarioRepository,
            VentaRepository ventaRepository,
            FacturaRepository facturaRepository) {

        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.inventarioRepository = inventarioRepository;
        this.ventaRepository = ventaRepository;
        this.facturaRepository = facturaRepository;
    }

    public byte[] generarExcel(Integer negocioId) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            List<Producto> productos =
                    productoRepository.findByNegocioId(negocioId);

            List<Cliente> clientes =
                    clienteRepository.findByNegocioId(negocioId);

            List<Inventario> inventarios =
                    inventarioRepository.findAllByNegocioId(negocioId);

            List<Venta> ventas =
                    ventaRepository.findByNegocioIdOrderByFechaDesc(negocioId);

            List<Factura> facturas =
                    facturaRepository.findAllByNegocioIdOrderByFechaDesc(negocioId);

            EstilosExcel estilos = new EstilosExcel(workbook);

            crearHojaResumen(
                    workbook, estilos, negocioId,
                    productos.size(), clientes.size(),
                    inventarios.size(), ventas.size(), facturas.size()
            );

            crearHojaProductos(workbook, estilos, productos);
            crearHojaClientes(workbook, estilos, clientes);
            crearHojaInventario(workbook, estilos, inventarios);
            crearHojaVentas(workbook, estilos, ventas);
            crearHojaFacturas(workbook, estilos, facturas);

            workbook.write(salida);

            return salida.toByteArray();
        }
    }

    // =====================================================
    // RESUMEN GENERAL
    // =====================================================

    private void crearHojaResumen(
            Workbook workbook,
            EstilosExcel estilos,
            Integer negocioId,
            int totalProductos,
            int totalClientes,
            int totalInventarios,
            int totalVentas,
            int totalFacturas) {

        Sheet hoja = workbook.createSheet("Resumen");

        hoja.setColumnWidth(0, 8500);
        hoja.setColumnWidth(1, 5500);

        hoja.createFreezePane(0, 3);

        Row titulo = hoja.createRow(0);
        titulo.setHeightInPoints(35);

        Cell celdaTitulo = titulo.createCell(0);
        celdaTitulo.setCellValue("MARAV | RESPALDO GENERAL");
        celdaTitulo.setCellStyle(estilos.titulo);

        hoja.addMergedRegion(
                new org.apache.poi.ss.util.CellRangeAddress(0, 0, 0, 1)
        );

        Row subtitulo = hoja.createRow(1);
        Cell celdaSubtitulo = subtitulo.createCell(0);

        celdaSubtitulo.setCellValue(
                "Copia de seguridad de la información del negocio"
        );

        celdaSubtitulo.setCellStyle(estilos.subtitulo);

        hoja.addMergedRegion(
                new org.apache.poi.ss.util.CellRangeAddress(1, 1, 0, 1)
        );

        crearFilaResumen(hoja, 3, "INFORMACIÓN", "DETALLE", estilos.encabezado);

        crearFilaResumen(hoja, 4, "Identificador del negocio",
                negocioId, estilos.celda);

        crearFilaResumen(hoja, 5, "Fecha del respaldo",
                LocalDateTime.now().format(
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
                ), estilos.celda);

        crearFilaResumen(hoja, 7, "MÓDULO", "REGISTROS", estilos.encabezado);

        crearFilaResumen(hoja, 8, "Productos", totalProductos, estilos.celda);
        crearFilaResumen(hoja, 9, "Clientes", totalClientes, estilos.celda);
        crearFilaResumen(hoja, 10, "Inventario", totalInventarios, estilos.celda);
        crearFilaResumen(hoja, 11, "Ventas", totalVentas, estilos.celda);
        crearFilaResumen(hoja, 12, "Facturas", totalFacturas, estilos.celda);

        Row pie = hoja.createRow(14);
        Cell celdaPie = pie.createCell(0);

        celdaPie.setCellValue(
                "MARAV - Sistema de gestión para pequeños negocios"
        );

        celdaPie.setCellStyle(estilos.subtitulo);

        hoja.addMergedRegion(
                new org.apache.poi.ss.util.CellRangeAddress(14, 14, 0, 1)
        );
    }

    private void crearFilaResumen(
            Sheet hoja,
            int numeroFila,
            String texto1,
            Object texto2,
            CellStyle estilo) {

        Row fila = hoja.createRow(numeroFila);

        Cell celda1 = fila.createCell(0);
        celda1.setCellValue(texto1);
        celda1.setCellStyle(estilo);

        Cell celda2 = fila.createCell(1);

        if (texto2 instanceof Number) {
            celda2.setCellValue(((Number) texto2).doubleValue());
        } else {
            celda2.setCellValue(String.valueOf(texto2));
        }

        celda2.setCellStyle(estilo);
    }

    // =====================================================
    // PRODUCTOS
    // =====================================================

    private void crearHojaProductos(
            Workbook workbook,
            EstilosExcel estilos,
            List<Producto> productos) {

        String[] columnas = {
                "ID", "Nombre", "Descripción",
                "Precio", "Estado", "Fecha de creación"
        };

        crearHojaDatos(
                workbook, estilos, "Productos",
                columnas, productos.size(), (fila, indice) -> {

                    Producto p = productos.get(indice);

                    escribir(fila, 0, p.getId(), estilos.celda);
                    escribir(fila, 1, p.getNombre(), estilos.celda);
                    escribir(fila, 2, p.getDescripcion(), estilos.celda);
                    escribir(fila, 3, p.getPrecio(), estilos.moneda);
                    escribir(fila, 4, p.getEstado(), estilos.celda);
                    escribir(fila, 5, p.getFechaCreacion(), estilos.celda);
                }
        );
    }

    // =====================================================
    // CLIENTES
    // =====================================================

    private void crearHojaClientes(
            Workbook workbook,
            EstilosExcel estilos,
            List<Cliente> clientes) {

        String[] columnas = {
                "ID", "Nombre", "Tipo identificación",
                "Número identificación", "Teléfono",
                "Dirección", "Deuda", "Estado", "Fecha creación"
        };

        crearHojaDatos(
                workbook, estilos, "Clientes",
                columnas, clientes.size(), (fila, indice) -> {

                    Cliente c = clientes.get(indice);

                    escribir(fila, 0, c.getId(), estilos.celda);
                    escribir(fila, 1, c.getNombre(), estilos.celda);
                    escribir(fila, 2, c.getTipoIdentificacion(), estilos.celda);
                    escribir(fila, 3, c.getNumeroIdentificacion(), estilos.celda);
                    escribir(fila, 4, c.getTelefono(), estilos.celda);
                    escribir(fila, 5, c.getDireccion(), estilos.celda);
                    escribir(fila, 6, c.getDeuda(), estilos.moneda);
                    escribir(fila, 7, c.getEstado(), estilos.celda);
                    escribir(fila, 8, c.getFechaCreacion(), estilos.celda);
                }
        );
    }

    // =====================================================
    // INVENTARIO
    // =====================================================

    private void crearHojaInventario(
            Workbook workbook,
            EstilosExcel estilos,
            List<Inventario> inventarios) {

        String[] columnas = {
                "ID", "Producto ID", "Stock actual",
                "Stock mínimo", "Ubicación", "Lote",
                "Fecha vencimiento", "Fecha actualización"
        };

        crearHojaDatos(
                workbook, estilos, "Inventario",
                columnas, inventarios.size(), (fila, indice) -> {

                    Inventario i = inventarios.get(indice);

                    escribir(fila, 0, i.getId(), estilos.celda);
                    escribir(fila, 1, i.getProductoId(), estilos.celda);
                    escribir(fila, 2, i.getStockActual(), estilos.celda);
                    escribir(fila, 3, i.getStockMinimo(), estilos.celda);
                    escribir(fila, 4, i.getUbicacion(), estilos.celda);
                    escribir(fila, 5, i.getLote(), estilos.celda);
                    escribir(fila, 6, i.getFechaVencimiento(), estilos.celda);
                    escribir(fila, 7, i.getFechaActualizacion(), estilos.celda);
                }
        );
    }

    // =====================================================
    // VENTAS
    // =====================================================

    private void crearHojaVentas(
            Workbook workbook,
            EstilosExcel estilos,
            List<Venta> ventas) {

        String[] columnas = {
                "ID", "Número factura", "Fecha",
                "Usuario ID", "Cliente ID", "Método de pago",
                "Total", "Efectivo recibido", "Cambio", "Estado"
        };

        crearHojaDatos(
                workbook, estilos, "Ventas",
                columnas, ventas.size(), (fila, indice) -> {

                    Venta v = ventas.get(indice);

                    escribir(fila, 0, v.getId(), estilos.celda);
                    escribir(fila, 1, v.getNumeroFactura(), estilos.celda);
                    escribir(fila, 2, v.getFecha(), estilos.celda);
                    escribir(fila, 3, v.getIdUsuario(), estilos.celda);
                    escribir(fila, 4, v.getIdCliente(), estilos.celda);
                    escribir(fila, 5, v.getMetodoPago(), estilos.celda);
                    escribir(fila, 6, v.getTotal(), estilos.moneda);
                    escribir(fila, 7, v.getEfectivoRecibido(), estilos.moneda);
                    escribir(fila, 8, v.getCambio(), estilos.moneda);
                    escribir(fila, 9, v.getEstado(), estilos.celda);
                }
        );
    }

    // =====================================================
    // FACTURAS
    // =====================================================

    private void crearHojaFacturas(
            Workbook workbook,
            EstilosExcel estilos,
            List<Factura> facturas) {

        String[] columnas = {
                "ID", "Venta ID", "Número factura",
                "Subtotal", "IVA", "Aplica IVA",
                "Total", "Fecha", "Estado", "Observación"
        };

        crearHojaDatos(
                workbook, estilos, "Facturas",
                columnas, facturas.size(), (fila, indice) -> {

                    Factura f = facturas.get(indice);

                    escribir(fila, 0, f.getId(), estilos.celda);
                    escribir(fila, 1, f.getVentaId(), estilos.celda);
                    escribir(fila, 2, f.getNumeroFactura(), estilos.celda);
                    escribir(fila, 3, f.getSubtotal(), estilos.moneda);
                    escribir(fila, 4, f.getIva(), estilos.moneda);
                    escribir(fila, 5, f.getAplicarIva(), estilos.celda);
                    escribir(fila, 6, f.getTotal(), estilos.moneda);
                    escribir(fila, 7, f.getFecha(), estilos.celda);
                    escribir(fila, 8, f.getEstado(), estilos.celda);
                    escribir(fila, 9, f.getObservacion(), estilos.celda);
                }
        );
    }

    // =====================================================
    // MÉTODO GENERAL PARA CREAR HOJAS
    // =====================================================

    private void crearHojaDatos(
            Workbook workbook,
            EstilosExcel estilos,
            String nombreHoja,
            String[] columnas,
            int totalRegistros,
            FilaExcel escritor) {

        Sheet hoja = workbook.createSheet(nombreHoja);

        hoja.createFreezePane(0, 3);

        Row titulo = hoja.createRow(0);
        titulo.setHeightInPoints(32);

        Cell celdaTitulo = titulo.createCell(0);
        celdaTitulo.setCellValue("MARAV | " + nombreHoja.toUpperCase());
        celdaTitulo.setCellStyle(estilos.titulo);

        if (columnas.length > 1) {
            hoja.addMergedRegion(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            0, 0, 0, columnas.length - 1
                    )
            );
        }

        Row subtitulo = hoja.createRow(1);
        Cell celdaSubtitulo = subtitulo.createCell(0);

        celdaSubtitulo.setCellValue(
                "Respaldo de información | Total de registros: " + totalRegistros
        );

        celdaSubtitulo.setCellStyle(estilos.subtitulo);

        if (columnas.length > 1) {
            hoja.addMergedRegion(
                    new org.apache.poi.ss.util.CellRangeAddress(
                            1, 1, 0, columnas.length - 1
                    )
            );
        }

        Row encabezado = hoja.createRow(2);
        encabezado.setHeightInPoints(25);

        for (int i = 0; i < columnas.length; i++) {

            Cell celda = encabezado.createCell(i);
            celda.setCellValue(columnas[i]);
            celda.setCellStyle(estilos.encabezado);
        }

        for (int i = 0; i < totalRegistros; i++) {

            Row fila = hoja.createRow(i + 3);

            CellStyle estiloFila = (i % 2 == 0)
                    ? estilos.celda
                    : estilos.celdaAlterna;

            escritor.escribir(fila, i);

            for (int j = 0; j < columnas.length; j++) {

                Cell celda = fila.getCell(j);

                if (celda == null) {
                    celda = fila.createCell(j);
                    celda.setCellStyle(estiloFila);
                }
            }
        }

        hoja.setAutoFilter(
                new org.apache.poi.ss.util.CellRangeAddress(
                        2,
                        Math.max(2, totalRegistros + 2),
                        0,
                        columnas.length - 1
                )
        );

        for (int i = 0; i < columnas.length; i++) {
            hoja.autoSizeColumn(i);

            int ancho = hoja.getColumnWidth(i);

            if (ancho > 14000) {
                hoja.setColumnWidth(i, 14000);
            }

            if (hoja.getColumnWidth(i) < 3500) {
                hoja.setColumnWidth(i, 3500);
            }
        }

        hoja.setDisplayGridlines(false);

        hoja.getPrintSetup().setLandscape(true);
        hoja.getPrintSetup().setFitWidth((short) 1);
        hoja.setFitToPage(true);
    }

    // =====================================================
    // ESCRITURA SEGURA DE CELDAS
    // =====================================================

    private void escribir(
            Row fila,
            int columna,
            Object valor,
            CellStyle estilo) {

        Cell celda = fila.createCell(columna);

        if (valor == null) {
            celda.setCellValue("");
        } else if (valor instanceof Number) {
            celda.setCellValue(((Number) valor).doubleValue());
        } else if (valor instanceof Boolean) {
            celda.setCellValue((Boolean) valor);
        } else {
            celda.setCellValue(valor.toString());
        }

        celda.setCellStyle(estilo);
    }

    @FunctionalInterface
    private interface FilaExcel {
        void escribir(Row fila, int indice);
    }

    // =====================================================
    // ESTILOS PROFESIONALES MARAV
    // =====================================================

    private static class EstilosExcel {

        private final CellStyle titulo;
        private final CellStyle subtitulo;
        private final CellStyle encabezado;
        private final CellStyle celda;
        private final CellStyle celdaAlterna;
        private final CellStyle moneda;

        EstilosExcel(Workbook workbook) {

            Font fuenteTitulo = workbook.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 16);
            fuenteTitulo.setColor(IndexedColors.WHITE.getIndex());

            titulo = workbook.createCellStyle();
            titulo.setFont(fuenteTitulo);
            titulo.setFillForegroundColor(
                    IndexedColors.DARK_GREEN.getIndex()
            );
            titulo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titulo.setAlignment(HorizontalAlignment.CENTER);
            titulo.setVerticalAlignment(VerticalAlignment.CENTER);

            Font fuenteSubtitulo = workbook.createFont();
            fuenteSubtitulo.setItalic(true);
            fuenteSubtitulo.setColor(IndexedColors.GREY_50_PERCENT.getIndex());
            fuenteSubtitulo.setFontHeightInPoints((short) 10);

            subtitulo = workbook.createCellStyle();
            subtitulo.setFont(fuenteSubtitulo);
            subtitulo.setAlignment(HorizontalAlignment.LEFT);
            subtitulo.setVerticalAlignment(VerticalAlignment.CENTER);

            Font fuenteEncabezado = workbook.createFont();
            fuenteEncabezado.setBold(true);
            fuenteEncabezado.setColor(IndexedColors.WHITE.getIndex());
            fuenteEncabezado.setFontHeightInPoints((short) 10);

            encabezado = workbook.createCellStyle();
            encabezado.setFont(fuenteEncabezado);
            encabezado.setFillForegroundColor(
                    IndexedColors.GREEN.getIndex()
            );
            encabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            encabezado.setAlignment(HorizontalAlignment.CENTER);
            encabezado.setVerticalAlignment(VerticalAlignment.CENTER);
            encabezado.setWrapText(true);
            encabezado.setBorderBottom(BorderStyle.THIN);
            encabezado.setBorderTop(BorderStyle.THIN);
            encabezado.setBorderLeft(BorderStyle.THIN);
            encabezado.setBorderRight(BorderStyle.THIN);

            Font fuenteCelda = workbook.createFont();
            fuenteCelda.setFontHeightInPoints((short) 10);
            fuenteCelda.setColor(IndexedColors.BLACK.getIndex());

            celda = workbook.createCellStyle();
            celda.setFont(fuenteCelda);
            celda.setVerticalAlignment(VerticalAlignment.CENTER);
            celda.setBorderBottom(BorderStyle.HAIR);
            celda.setBorderLeft(BorderStyle.HAIR);
            celda.setBorderRight(BorderStyle.HAIR);

            celdaAlterna = workbook.createCellStyle();
            celdaAlterna.cloneStyleFrom(celda);
            celdaAlterna.setFillForegroundColor(
                    IndexedColors.LIGHT_GREEN.getIndex()
            );
            celdaAlterna.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            moneda = workbook.createCellStyle();
            moneda.cloneStyleFrom(celda);
            moneda.setDataFormat(
                    workbook.createDataFormat().getFormat(
                            "$ #,##0.00"
                    )
            );
        }
    }
}
