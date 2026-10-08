// ======================================================
// 📁 PACKAGE
// ======================================================
package com.ecomarket.controller;

// ======================================================
// 📚 IMPORTS - MODELOS
// ======================================================
import com.ecomarket.model.Cliente;
import com.ecomarket.model.DetalleVenta;
import com.ecomarket.model.Factura;
import com.ecomarket.model.Venta;

// ======================================================
// 📚 IMPORTS - REPOSITORIES
// ======================================================
import com.ecomarket.repository.ClienteRepository;
import com.ecomarket.repository.DetalleVentaRepository;
import com.ecomarket.repository.FacturaRepository;
import com.ecomarket.repository.VentaRepository;

// ======================================================
// 📚 IMPORTS - DTO / SERVICE
// ======================================================
import com.ecomarket.dto.FacturaDTO;
import com.ecomarket.service.VentaService;

// ======================================================
// 🔐 IMPORTS - SEGURIDAD
// ======================================================
import com.ecomarket.security.CustomUserDetails;

// ======================================================
// 📚 IMPORTS - iTEXT PDF
// ======================================================
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

// ======================================================
// 📚 IMPORTS - SPRING
// ======================================================
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// ======================================================
// 📚 IMPORTS - JAVA
// ======================================================
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

// ======================================================
// 🚀 CONTROLADOR FACTURAS - MARAV
// ======================================================

/**
 * ======================================================
 * 🧾 CONTROLADOR DE FACTURAS
 * ======================================================
 *
 * Funcionalidades:
 *
 * ✔ Listar facturas
 * ✔ Buscar facturas
 * ✔ Ver detalle de factura
 * ✔ Descargar factura PDF
 * ✔ Descargar tirilla PDF
 * ✔ Mostrar cliente
 * ✔ Mostrar productos
 * ✔ Mantener roles ADMIN / EMPLEADO
 * ✔ Aislamiento por negocio
 *
 * ======================================================
 *
 * 🔐 SEGURIDAD MULTI-NEGOCIO
 *
 * El negocio SIEMPRE se obtiene desde:
 *
 * Authentication
 *       ↓
 * CustomUserDetails
 *       ↓
 * getNegocioId()
 *
 * Nunca se recibe negocioId desde el navegador.
 *
 * ======================================================
 */
@Controller
@RequestMapping("/facturas")
public class FacturaController {

    // =========================================================
    // 🔗 REPOSITORIOS
    // =========================================================

    private final FacturaRepository facturaRepo;

    private final DetalleVentaRepository detalleRepo;

    private final ClienteRepository clienteRepo;

    private final VentaRepository ventaRepo;

    private final VentaService ventaService;

    // =========================================================
    // 🏗 CONSTRUCTOR
    // =========================================================

    public FacturaController(
            FacturaRepository facturaRepo,
            DetalleVentaRepository detalleRepo,
            ClienteRepository clienteRepo,
            VentaRepository ventaRepo,
            VentaService ventaService
    ) {

        this.facturaRepo = facturaRepo;
        this.detalleRepo = detalleRepo;
        this.clienteRepo = clienteRepo;
        this.ventaRepo = ventaRepo;
        this.ventaService = ventaService;
    }

    // =========================================================
    // 👤 CARGAR DATOS DEL USUARIO
    // =========================================================

    private void cargarDatosUsuario(
            Model model,
            Authentication authentication
    ) {

        if (
                authentication == null ||
                        !authentication.isAuthenticated()
        ) {

            model.addAttribute(
                    "usuario",
                    "Usuario"
            );

            model.addAttribute(
                    "rolUsuario",
                    "USUARIO"
            );

            return;
        }

        Object principal =
                authentication.getPrincipal();

        // =====================================================
        // 👤 CUSTOM USER DETAILS
        // =====================================================

        if (
                principal instanceof CustomUserDetails user
        ) {

            model.addAttribute(
                    "usuario",
                    user.getNombre()
            );

            String rol =
                    authentication
                            .getAuthorities()
                            .stream()
                            .map(
                                    GrantedAuthority::getAuthority
                            )
                            .findFirst()
                            .orElse("USUARIO");

            if (
                    rol.startsWith("ROLE_")
            ) {

                rol =
                        rol.substring(5);
            }

            model.addAttribute(
                    "rolUsuario",
                    rol
            );

            model.addAttribute(
                    "negocioId",
                    user.getNegocioId()
            );

            return;
        }

        // =====================================================
        // 🔄 RESPALDO
        // =====================================================

        model.addAttribute(
                "usuario",
                authentication.getName()
        );

        String rol =
                authentication
                        .getAuthorities()
                        .stream()
                        .map(
                                GrantedAuthority::getAuthority
                        )
                        .findFirst()
                        .orElse("USUARIO");

        if (
                rol.startsWith("ROLE_")
        ) {

            rol =
                    rol.substring(5);
        }

        model.addAttribute(
                "rolUsuario",
                rol
        );
    }

    // =========================================================
    // 📋 LISTAR FACTURAS
    // =========================================================

    @GetMapping
    public String listarFacturas(
            Model model,
            Authentication authentication
    ) {

        Integer negocioId =
                obtenerNegocioId(authentication);

        List<Factura> facturas =
                facturaRepo
                        .findAllByNegocioIdOrderByFechaDesc(
                                negocioId
                        );

        model.addAttribute(
                "facturas",
                facturas
        );

        model.addAttribute(
                "menuActivo",
                "facturas"
        );

        cargarDatosUsuario(
                model,
                authentication
        );

        return "facturas";
    }

    // =========================================================
    // 🔍 BUSCAR FACTURAS
    // =========================================================

    @GetMapping("/buscar")
    public String buscarFacturas(
            @RequestParam(
                    defaultValue = ""
            )
            String numeroFactura,

            Model model,

            Authentication authentication
    ) {

        Integer negocioId =
                obtenerNegocioId(authentication);

        String numeroBusqueda =
                numeroFactura == null
                        ? ""
                        : numeroFactura.trim();

        List<Factura> facturas =
                facturaRepo
                        .findByNumeroFacturaContainingIgnoreCaseAndNegocioId(
                                numeroBusqueda,
                                negocioId
                        );

        model.addAttribute(
                "facturas",
                facturas
        );

        model.addAttribute(
                "menuActivo",
                "facturas"
        );

        cargarDatosUsuario(
                model,
                authentication
        );

        return "facturas";
    }

    // =========================================================
    // 🔎 OBTENER VENTA SEGURA
    // =========================================================

    private Venta obtenerVentaDelNegocio(
            Integer ventaId,
            Integer negocioId
    ) {

        if (
                ventaId == null
        ) {

            throw new IllegalStateException(
                    "La factura no tiene una venta asociada."
            );
        }

        return ventaRepo
                .findByIdAndNegocioId(
                        ventaId,
                        negocioId
                )
                .orElseThrow(() ->
                        new IllegalStateException(
                                "La venta no pertenece al negocio autenticado."
                        )
                );
    }

    // =========================================================
    // 🧾 OBTENER FACTURA SEGURA
    // =========================================================

    private Factura obtenerFacturaDelNegocio(
            Integer facturaId,
            Integer negocioId
    ) {

        Factura factura =
                facturaRepo
                        .findById(facturaId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Factura no encontrada."
                                )
                        );

        obtenerVentaDelNegocio(
                factura.getVentaId(),
                negocioId
        );

        return factura;
    }

    // =========================================================
    // 📄 VER DETALLE DE FACTURA
    // =========================================================

    @GetMapping("/{id}")
    public String verFactura(
            @PathVariable Integer id,
            Model model,
            Authentication authentication
    ) {

        Integer negocioId =
                obtenerNegocioId(authentication);

        Factura facturaEntity =
                obtenerFacturaDelNegocio(
                        id,
                        negocioId
                );

        FacturaDTO factura =
                ventaService.obtenerFactura(
                        facturaEntity.getVentaId(),
                        negocioId
                );

        factura.setId(
                facturaEntity.getId()
        );

        Venta venta =
                obtenerVentaDelNegocio(
                        facturaEntity.getVentaId(),
                        negocioId
                );

        if (
                venta.getIdCliente() != null
        ) {

            clienteRepo
                    .findByIdAndNegocioId(
                            venta.getIdCliente(),
                            negocioId
                    )
                    .ifPresent(cliente ->
                            model.addAttribute(
                                    "cliente",
                                    cliente
                            )
                    );
        }

        model.addAttribute(
                "factura",
                factura
        );

        model.addAttribute(
                "detalles",
                detalleRepo.findByIdVentaAndNegocioId(
                        facturaEntity.getVentaId(),
                        negocioId
                )
        );

        model.addAttribute(
                "menuActivo",
                "facturas"
        );

        cargarDatosUsuario(
                model,
                authentication
        );

        return "factura-detalle";
    }

    // =========================================================
    // 📥 DESCARGAR FACTURA PDF
    // =========================================================

    @GetMapping("/pdf/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> descargarPdf(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        Document document = null;

        try {

            System.out.println(
                    "[MARAV PDF] Iniciando generación. Factura ID: "
                            + id
            );

            // -------------------------------------------------
            // 🔐 NEGOCIO
            // -------------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(authentication);

            System.out.println(
                    "[MARAV PDF] Negocio autenticado: "
                            + negocioId
            );

            // -------------------------------------------------
            // 🧾 FACTURA SEGURA
            // -------------------------------------------------

            Factura factura =
                    obtenerFacturaDelNegocio(
                            id,
                            negocioId
                    );

            System.out.println(
                    "[MARAV PDF] Factura encontrada: "
                            + factura.getNumeroFactura()
            );

            // -------------------------------------------------
            // 📦 DETALLES
            // -------------------------------------------------

            List<DetalleVenta> detalles =
                    detalleRepo.findByIdVentaAndNegocioId(
                            factura.getVentaId(),
                            negocioId
                    );

            System.out.println(
                    "[MARAV PDF] Detalles encontrados: "
                            + detalles.size()
            );

            // -------------------------------------------------
            // 🧾 VENTA
            // -------------------------------------------------

            Venta venta =
                    obtenerVentaDelNegocio(
                            factura.getVentaId(),
                            negocioId
                    );

            // -------------------------------------------------
            // 👤 CLIENTE
            // -------------------------------------------------

            String clienteNombre =
                    "Cliente de Contado";

            String documentoCliente =
                    "";

            String telefonoCliente =
                    "";

            String direccionCliente =
                    "";

            String metodoPago =
                    venta.getMetodoPago() == null
                            ? ""
                            : venta.getMetodoPago();

            if (
                    venta.getIdCliente() != null
            ) {

                Cliente cliente =
                        clienteRepo
                                .findByIdAndNegocioId(
                                        venta.getIdCliente(),
                                        negocioId
                                )
                                .orElse(null);

                if (
                        cliente != null
                ) {

                    clienteNombre =
                            valorSeguro(
                                    cliente.getNombre()
                            );

                    documentoCliente =
                            valorSeguro(
                                    cliente.getNumeroIdentificacion()
                            );

                    telefonoCliente =
                            valorSeguro(
                                    cliente.getTelefono()
                            );

                    direccionCliente =
                            valorSeguro(
                                    cliente.getDireccion()
                            );
                }
            }

            // =================================================
            // 📄 CREAR PDF EN MEMORIA
            // =================================================

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            document =
                    new Document();

            PdfWriter.getInstance(
                    document,
                    baos
            );

            document.open();

            // =================================================
            // 🖼 LOGO SEGURO
            // =================================================

            agregarLogo(
                    document,
                    120,
                    120
            );

            // =================================================
            // 🔤 FUENTES
            // =================================================

            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            18
                    );

            Font subtitulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12
                    );

            // =================================================
            // 🏪 EMPRESA
            // =================================================

            Paragraph empresa =
                    new Paragraph(
                            "Sistema de ventas",
                            titulo
                    );

            empresa.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(
                    empresa
            );

            // =================================================
            // 🧾 FACTURA
            // =================================================

            document.add(
                    new Paragraph(
                            "Factura: "
                                    + valorSeguro(
                                    factura.getNumeroFactura()
                            )
                    )
            );

            document.add(
                    new Paragraph(
                            "Fecha: "
                                    + valorSeguro(
                                    factura.getFecha()
                                            == null
                                            ? ""
                                            : factura.getFecha().toString()
                            )
                    )
            );

            document.add(
                    new Paragraph(
                            "Estado: "
                                    + valorSeguro(
                                    factura.getEstado()
                            )
                    )
            );

            document.add(
                    new Paragraph(" ")
            );

            // =================================================
            // 👤 CLIENTE
            // =================================================

            document.add(
                    new Paragraph(
                            "Cliente: "
                                    + clienteNombre
                    )
            );

            document.add(
                    new Paragraph(
                            "Documento: "
                                    + documentoCliente
                    )
            );

            document.add(
                    new Paragraph(
                            "Telefono: "
                                    + telefonoCliente
                    )
            );

            document.add(
                    new Paragraph(
                            "Direccion: "
                                    + direccionCliente
                    )
            );

            document.add(
                    new Paragraph(
                            "Metodo de Pago: "
                                    + metodoPago
                    );

            document.add(
                    new Paragraph(" ")
            );

            // =================================================
            // 📦 TABLA PRODUCTOS
            // =================================================

            PdfPTable tabla =
                    new PdfPTable(4);

            tabla.setWidthPercentage(
                    100
            );

            tabla.addCell(
                    "Producto"
            );

            tabla.addCell(
                    "Cantidad"
            );

            tabla.addCell(
                    "Precio"
            );

            tabla.addCell(
                    "Subtotal"
            );

            for (
                    DetalleVenta d :
                    detalles
            ) {

                String nombreProducto =
                        valorSeguro(
                                d.getNombreProducto()
                        );

                tabla.addCell(
                        nombreProducto
                );

                tabla.addCell(
                        String.valueOf(
                                d.getCantidad()
                        )
                );

                tabla.addCell(
                        "$ "
                                + formatearNumero(
                                d.getPrecio()
                        )
                );

                tabla.addCell(
                        "$ "
                                + formatearNumero(
                                d.getSubtotal()
                        )
                );
            }

            document.add(
                    tabla
            );

            document.add(
                    new Paragraph(" ")
            );

            // =================================================
            // 💰 TOTALES
            // =================================================

            document.add(
                    new Paragraph(
                            "Subtotal: $ "
                                    + formatearNumero(
                                    factura.getSubtotal()
                            ),
                            subtitulo
                    )
            );

            document.add(
                    new Paragraph(
                            "IVA: $ "
                                    + formatearNumero(
                                    factura.getIva()
                            ),
                            subtitulo
                    )
            );

            document.add(
                    new Paragraph(
                            "TOTAL: $ "
                                    + formatearNumero(
                                    factura.getTotal()
                            ),
                            titulo
                    )
            );

            document.add(
                    new Paragraph(
                            "\nGracias por comprar en MARAV",
                            subtitulo
                    )
            );

            System.out.println(
                    "[MARAV PDF] Cerrando documento..."
            );

            return construirRespuestaPdf(
                    document,
                    baos,
                    "Factura_"
                            + limpiarNombreArchivo(
                            factura.getNumeroFactura()
                    )
                            + ".pdf"
            );

        } catch (Exception e) {

            System.err.println(
                    "[MARAV PDF ERROR] Factura ID: "
                            + id
            );

            e.printStackTrace();

            throw new RuntimeException(
                    "Error generando PDF de factura.",
                    e
            );

        } finally {

            if (
                    document != null &&
                            document.isOpen()
            ) {

                try {

                    document.close();

                } catch (Exception e) {

                    System.err.println(
                            "[MARAV PDF] Error cerrando documento."
                    );

                    e.printStackTrace();
                }
            }
        }
    }

    // =========================================================
    // 🧾 DESCARGAR TIRILLA
    // =========================================================

    @GetMapping("/pdf-tirilla/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> descargarTirilla(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        Document document = null;

        try {

            System.out.println(
                    "[MARAV TIRILLA] Iniciando generación. Factura ID: "
                            + id
            );

            Integer negocioId =
                    obtenerNegocioId(authentication);

            Factura factura =
                    obtenerFacturaDelNegocio(
                            id,
                            negocioId
                    );

            List<DetalleVenta> detalles =
                    detalleRepo.findByIdVentaAndNegocioId(
                            factura.getVentaId(),
                            negocioId
                    );

            Venta venta =
                    obtenerVentaDelNegocio(
                            factura.getVentaId(),
                            negocioId
                    );

            String clienteNombre =
                    "Cliente de Contado";

            String metodoPago =
                    venta.getMetodoPago() == null
                            ? ""
                            : venta.getMetodoPago();

            if (
                    venta.getIdCliente() != null
            ) {

                Cliente cliente =
                        clienteRepo
                                .findByIdAndNegocioId(
                                        venta.getIdCliente(),
                                        negocioId
                                )
                                .orElse(null);

                if (
                        cliente != null
                ) {

                    clienteNombre =
                            valorSeguro(
                                    cliente.getNombre()
                            );
                }
            }

            // =================================================
            // 📄 TAMAÑO TIRILLA
            // =================================================

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();

            Rectangle ticket =
                    new Rectangle(
                            226f,
                            1200f
                    );

            document =
                    new Document(
                            ticket,
                            10,
                            10,
                            10,
                            10
                    );

            PdfWriter.getInstance(
                    document,
                    baos
            );

            document.open();

            // =================================================
            // 🖼 LOGO
            // =================================================

            agregarLogo(
                    document,
                    70,
                    70
            );

            // =================================================
            // 🔤 FUENTES
            // =================================================

            Font titulo =
                    FontFactory.getFont(
                            FontFactory.HELVETICA_BOLD,
                            12
                    );

            Font normal =
                    FontFactory.getFont(
                            FontFactory.HELVETICA,
                            9
                    );

            // =================================================
            // 🏪 EMPRESA
            // =================================================

            Paragraph empresa =
                    new Paragraph(
                            "Sistema de ventas\n",
                            titulo
                    );

            empresa.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(
                    empresa
            );

            // =================================================
            // 🧾 DATOS FACTURA
            // =================================================

            document.add(
                    new Paragraph(
                            "Factura: "
                                    + valorSeguro(
                                    factura.getNumeroFactura()
                            ),
                            normal
                    )
            );

            document.add(
                    new Paragraph(
                            "Cliente: "
                                    + clienteNombre,
                            normal
                    )
            );

            document.add(
                    new Paragraph(
                            "Pago: "
                                    + metodoPago,
                            normal
                    )
            );

            document.add(
                    new Paragraph(
                            "--------------------------------",
                            normal
                    )
            );

            // =================================================
            // 📦 PRODUCTOS
            // =================================================

            for (
                    DetalleVenta d :
                    detalles
            ) {

                document.add(
                        new Paragraph(
                                valorSeguro(
                                        d.getNombreProducto()
                                ),
                                normal
                        )
                );

                document.add(
                        new Paragraph(
                                d.getCantidad()
                                        + " x $ "
                                        + formatearNumero(
                                        d.getPrecio()
                                ),
                                normal
                        )
                );

                document.add(
                        new Paragraph(
                                "$ "
                                        + formatearNumero(
                                        d.getSubtotal()
                                ),
                                normal
                        )
                );

                document.add(
                        new Paragraph(
                                "--------------------------------",
                                normal
                        )
                );
            }

            // =================================================
            // 💰 TOTALES
            // =================================================

            document.add(
                    new Paragraph(
                            "Subtotal: $ "
                                    + formatearNumero(
                                    factura.getSubtotal()
                            ),
                            normal
                    )
            );

            document.add(
                    new Paragraph(
                            "IVA: $ "
                                    + formatearNumero(
                                    factura.getIva()
                            ),
                            normal
                    )
            );

            Paragraph total =
                    new Paragraph(
                            "\nTOTAL: $ "
                                    + formatearNumero(
                                    factura.getTotal()
                            ),
                            titulo
                    );

            total.setAlignment(
                    Element.ALIGN_CENTER
            );

            document.add(
                    total
            );

            document.add(
                    new Paragraph(
                            "\nGracias por su compra",
                            normal
                    )
            );

            return construirRespuestaPdf(
                    document,
                    baos,
                    "Tirilla_"
                            + limpiarNombreArchivo(
                            factura.getNumeroFactura()
                    )
                            + ".pdf"
            );

        } catch (Exception e) {

            System.err.println(
                    "[MARAV TIRILLA ERROR] Factura ID: "
                            + id
            );

            e.printStackTrace();

            throw new RuntimeException(
                    "Error generando tirilla.",
                    e
            );

        } finally {

            if (
                    document != null &&
                            document.isOpen()
            ) {

                try {

                    document.close();

                } catch (Exception e) {

                    System.err.println(
                            "[MARAV TIRILLA] Error cerrando documento."
                    );

                    e.printStackTrace();
                }
            }
        }
    }

    // =========================================================
    // 🖼 CARGAR LOGO DE FORMA SEGURA
    // =========================================================

    private void agregarLogo(
            Document document,
            float ancho,
            float alto
    ) {

        try {

            ClassPathResource resource =
                    new ClassPathResource(
                            "static/images/logo.png"
                    );

            if (
                    !resource.exists()
            ) {

                System.err.println(
                        "[MARAV PDF] Logo no encontrado en classpath."
                );

                return;
            }

            try (
                    InputStream inputStream =
                            resource.getInputStream()
            ) {

                byte[] logoBytes =
                        inputStream.readAllBytes();

                Image logo =
                        Image.getInstance(
                                logoBytes
                        );

                logo.scaleToFit(
                        ancho,
                        alto
                );

                logo.setAlignment(
                        Element.ALIGN_CENTER
                );

                document.add(
                        logo
                );
            }

        } catch (Exception e) {

            // =================================================
            // ⚠️ EL LOGO NUNCA DEBE IMPEDIR EL PDF
            // =================================================

            System.err.println(
                    "[MARAV PDF] No se pudo cargar el logo. "
                            + "El PDF continuará sin logo."
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // 📤 CONSTRUIR RESPUESTA PDF
    // =========================================================

    private ResponseEntity<byte[]> construirRespuestaPdf(
            Document document,
            ByteArrayOutputStream baos,
            String nombreArchivo
    ) {

        // -----------------------------------------------------
        // 🔒 CERRAR PDF ANTES DE DEVOLVER BYTES
        // -----------------------------------------------------

        if (
                document != null &&
                        document.isOpen()
        ) {

            document.close();
        }

        byte[] pdf =
                baos.toByteArray();

        System.out.println(
                "[MARAV PDF] PDF generado correctamente. "
                        + "Tamaño: "
                        + pdf.length
                        + " bytes"
        );

        HttpHeaders headers =
                new HttpHeaders();

        headers.add(
                "Content-Disposition",
                "attachment; filename=\""
                        + nombreArchivo
                        + "\""
        );

        headers.setContentLength(
                pdf.length
        );

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(
                        MediaType.APPLICATION_PDF
                )
                .body(
                        pdf
                );
    }

    // =========================================================
    // 🔤 VALOR SEGURO
    // =========================================================

    private String valorSeguro(
            Object valor
    ) {

        if (
                valor == null
        ) {

            return "";
        }

        return String.valueOf(
                valor
        );
    }

    // =========================================================
    // 💰 FORMATEAR NÚMEROS
    // =========================================================

    private String formatearNumero(
            Number numero
    ) {

        if (
                numero == null
        ) {

            return "0";
        }

        return String.format(
                "%,.0f",
                numero.doubleValue()
        );
    }

    // =========================================================
    // 🧹 LIMPIAR NOMBRE ARCHIVO
    // =========================================================

    private String limpiarNombreArchivo(
            Object nombre
    ) {

        String texto =
                valorSeguro(
                        nombre
                );

        if (
                texto.isBlank()
        ) {

            return "Factura";
        }

        return texto
                .replaceAll(
                        "[\\\\/:*?\"<>|]",
                        "_"
                )
                .trim();
    }

    // =========================================================
    // 🔐 OBTENER NEGOCIO DEL USUARIO
    // =========================================================

    private Integer obtenerNegocioId(
            Authentication authentication
    ) {

        if (
                authentication == null ||
                        !authentication.isAuthenticated()
        ) {

            throw new IllegalStateException(
                    "Usuario no autenticado."
            );
        }

        Object principal =
                authentication.getPrincipal();

        if (
                !(principal instanceof CustomUserDetails user)
        ) {

            throw new IllegalStateException(
                    "No se pudo obtener la información del usuario autenticado."
            );
        }

        Integer negocioId =
                user.getNegocioId();

        if (
                negocioId == null
        ) {

            throw new IllegalStateException(
                    "El usuario no tiene un negocio asociado."
            );
        }

        return negocioId;
    }
}