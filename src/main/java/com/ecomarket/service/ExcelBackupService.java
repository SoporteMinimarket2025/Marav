package com.ecomarket.service;

import com.ecomarket.model.Producto;
import com.ecomarket.model.Cliente;
import com.ecomarket.model.Venta;
import com.ecomarket.model.Inventario;
import com.ecomarket.model.Factura;

import com.ecomarket.repository.ProductoRepository;
import com.ecomarket.repository.ClienteRepository;
import com.ecomarket.repository.VentaRepository;
import com.ecomarket.repository.InventarioRepository;
import com.ecomarket.repository.FacturaRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExcelBackupService {

    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final VentaRepository ventaRepository;
    private final InventarioRepository inventarioRepository;
    private final FacturaRepository facturaRepository;

    public ExcelBackupService(
            ProductoRepository productoRepository,
            ClienteRepository clienteRepository,
            VentaRepository ventaRepository,
            InventarioRepository inventarioRepository,
            FacturaRepository facturaRepository) {

        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.ventaRepository = ventaRepository;
        this.inventarioRepository = inventarioRepository;
        this.facturaRepository = facturaRepository;
    }

    // ======================================================
    // GENERAR EXCEL DEL NEGOCIO AUTENTICADO
    // ======================================================

    public byte[] generarExcel(Integer negocioId) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            // Estilo de encabezados
            CellStyle estiloEncabezado = workbook.createCellStyle();

            Font fuente = workbook.createFont();
            fuente.setBold(true);
            fuente.setColor(IndexedColors.WHITE.getIndex());

            estiloEncabezado.setFont(fuente);
            estiloEncabezado.setFillForegroundColor(
                    IndexedColors.DARK_GREEN.getIndex()
            );
            estiloEncabezado.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND
            );

            // Obtener exclusivamente los registros del negocio
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

            crearHojaProductos(workbook, estiloEncabezado, productos);
            crearHojaClientes(workbook, estiloEncabezado, clientes);
            crearHojaInventario(workbook, estiloEncabezado, inventarios);
            crearHojaVentas(workbook, estiloEncabezado, ventas);
            crearHojaFacturas(workbook, estiloEncabezado, facturas);

            workbook.write(salida);

            return salida.toByteArray();
        }
    }

    // ======================================================
    // HOJA 1: PRODUCTOS
    // ======================================================

    private void crearHojaProductos(
            Workbook workbook,
            CellStyle estilo,
            List<Producto> productos) {

        Sheet hoja = workbook.createSheet("Productos");

        String[] columnas = {
                "ID", "Nombre", "Descripción",
                "Precio", "Estado", "Fecha de creación"
        };

        crearEncabezado(hoja, columnas, estilo);

        int fila = 1;

        for (Producto p : productos) {

            Row row = hoja.createRow(fila++);

            escribirNumero(row, 0, p.getId());
            escribirTexto(row, 1, p.getNombre());
            escribirTexto(row, 2, p.getDescripcion());
            escribirNumero(row, 3, p.getPrecio());
            escribirTexto(row, 4, estadoTexto(p.getEstado()));
            escribirTexto(row, 5, fechaTexto(p.getFechaCreacion()));
        }

        ajustarColumnas(hoja, columnas.length);
    }

    // ======================================================
    // HOJA 2: CLIENTES
    // ======================================================

    private void crearHojaClientes(
            Workbook workbook,
            CellStyle estilo,
            List<Cliente> clientes) {

        Sheet hoja = workbook.createSheet("Clientes");

        String[] columnas = {
                "ID", "Nombre", "Tipo de identificación",
                "Número de identificación", "Teléfono",
                "Dirección", "Deuda", "Estado", "Fecha de creación"
        };

        crearEncabezado(hoja, columnas, estilo);

        int fila = 1;

        for (Cliente c : clientes) {

            Row row = hoja.createRow(fila++);

            escribirNumero(row, 0, c.getId());
            escribirTexto(row, 1, c.getNombre());
            escribirTexto(row, 2, c.getTipoIdentificacion());
            escribirTexto(row, 3, c.getNumeroIdentificacion());
            escribirTexto(row, 4, c.getTelefono());
            escribirTexto(row, 5, c.getDireccion());
            escribirNumero(row, 6, c.getDeuda());
            escribirTexto(row, 7, estadoTexto(c.getEstado()));
            escribirTexto(row, 8, fechaTexto(c.getFechaCreacion()));
        }

        ajustarColumnas(hoja, columnas.length);
    }

    // ======================================================
    // HOJA 3: INVENTARIO
    // ======================================================

    private void crearHojaInventario(
            Workbook workbook,
            CellStyle estilo,
            List<Inventario> inventarios) {

        Sheet hoja = workbook.createSheet("Inventario");

        String[] columnas = {
                "ID", "ID del producto", "Stock actual",
                "Stock mínimo", "Ubicación", "Lote",
                "Fecha de vencimiento", "Fecha de actualización"
        };

        crearEncabezado(hoja, columnas, estilo);

        int fila = 1;

        for (Inventario i : inventarios) {

            Row row = hoja.createRow(fila++);

            escribirNumero(row, 0, i.getId());
            escribirNumero(row, 1, i.getProductoId());
            escribirNumero(row, 2, i.getStockActual());
            escribirNumero(row, 3, i.getStockMinimo());
            escribirTexto(row, 4, i.getUbicacion());
            escribirTexto(row, 5, i.getLote());
            escribirTexto(row, 6, fechaTexto(i.getFechaVencimiento()));
            escribirTexto(row, 7, fechaTexto(i.getFechaActualizacion()));
        }

        ajustarColumnas(hoja, columnas.length);
    }

    // ======================================================
    // HOJA 4: VENTAS
    // ======================================================

    private void crearHojaVentas(
            Workbook workbook,
            CellStyle estilo,
            List<Venta> ventas) {

        Sheet hoja = workbook.createSheet("Ventas");

        String[] columnas = {
                "ID", "Número de factura", "Fecha",
                "ID del usuario", "ID del cliente",
                "Método de pago", "Total",
                "Efectivo recibido", "Cambio", "Estado"
        };

        crearEncabezado(hoja, columnas, estilo);

        int fila = 1;

        for (Venta v : ventas) {

            Row row = hoja.createRow(fila++);

            escribirNumero(row, 0, v.getId());
            escribirTexto(row, 1, v.getNumeroFactura());
            escribirTexto(row, 2, fechaTexto(v.getFecha()));
            escribirNumero(row, 3, v.getIdUsuario());
            escribirNumero(row, 4, v.getIdCliente());
            escribirTexto(row, 5, v.getMetodoPago());
            escribirNumero(row, 6, v.getTotal());
            escribirNumero(row, 7, v.getEfectivoRecibido());
            escribirNumero(row, 8, v.getCambio());
            escribirTexto(row, 9, v.getEstado());
        }

        ajustarColumnas(hoja, columnas.length);
    }

    // ======================================================
    // HOJA 5: FACTURAS
    // ======================================================

    private void crearHojaFacturas(
            Workbook workbook,
            CellStyle estilo,
            List<Factura> facturas) {

        Sheet hoja = workbook.createSheet("Facturas");

        String[] columnas = {
                "ID", "ID de venta", "Número de factura",
                "Subtotal", "IVA", "Aplica IVA",
                "Total", "Fecha", "Estado", "Observación"
        };

        crearEncabezado(hoja, columnas, estilo);

        int fila = 1;

        for (Factura f : facturas) {

            Row row = hoja.createRow(fila++);

            escribirNumero(row, 0, f.getId());
            escribirNumero(row, 1, f.getVentaId());
            escribirTexto(row, 2, f.getNumeroFactura());
            escribirNumero(row, 3, f.getSubtotal());
            escribirNumero(row, 4, f.getIva());
            escribirTexto(row, 5, f.getAplicarIva() != null
                    && f.getAplicarIva() ? "Sí" : "No");
            escribirNumero(row, 6, f.getTotal());
            escribirTexto(row, 7, fechaTexto(f.getFecha()));
            escribirTexto(row, 8, f.getEstado());
            escribirTexto(row, 9, f.getObservacion());
        }

        ajustarColumnas(hoja, columnas.length);
    }

    // ======================================================
    // MÉTODOS AUXILIARES
    // ======================================================

    private void crearEncabezado(
            Sheet hoja,
            String[] columnas,
            CellStyle estilo) {

        Row encabezado = hoja.createRow(0);

        for (int i = 0; i < columnas.length; i++) {

            Cell celda = encabezado.createCell(i);
            celda.setCellValue(columnas[i]);
            celda.setCellStyle(estilo);
        }

        hoja.createFreezePane(0, 1);
        hoja.setAutoFilter(
                new org.apache.poi.ss.util.CellRangeAddress(
                        0, 0, 0, columnas.length - 1
                )
        );
    }

    private void escribirTexto(Row fila, int columna, String valor) {

        Cell celda = fila.createCell(columna);
        celda.setCellValue(valor != null ? valor : "");
    }

    private void escribirNumero(Row fila, int columna, Number valor) {

        Cell celda = fila.createCell(columna);

        if (valor != null) {
            celda.setCellValue(valor.doubleValue());
        } else {
            celda.setBlank();
        }
    }

    private String estadoTexto(Boolean estado) {

        if (estado == null) {
            return "Sin definir";
        }

        return estado ? "Activo" : "Inactivo";
    }

    private String fechaTexto(Object fecha) {

        return fecha != null ? fecha.toString() : "";
    }

    private void ajustarColumnas(Sheet hoja, int cantidad) {

        for (int i = 0; i < cantidad; i++) {
            hoja.autoSizeColumn(i);
        }
    }
}