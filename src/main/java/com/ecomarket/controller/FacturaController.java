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
import com.itextpdf.text.*;
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
import java.util.List;

// ======================================================
// 🚀 CONTROLADOR FACTURAS - ECO MARKET PRO
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
 * ✔ Mostrar datos del usuario
 * ✔ Mantener rol ADMIN / EMPLEADO
 * ✔ Aislamiento por negocio
 *
 * ======================================================
 *
 * 🔐 REGLA MULTI-NEGOCIO
 *
 * Una factura pertenece a una venta.
 *
 * La venta pertenece a un negocio mediante:
 *
 * Factura
 *    ↓
 * ventaId
 *    ↓
 * Venta
 *    ↓
 * negocioId
 *
 * Por eso NO confiamos en ningún negocioId enviado
 * desde el navegador.
 *
 * El negocio se obtiene exclusivamente desde:
 *
 * Authentication
 *       ↓
 * CustomUserDetails
 *       ↓
 * getNegocioId()
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

        this.facturaRepo =
                facturaRepo;

        this.detalleRepo =
                detalleRepo;

        this.clienteRepo =
                clienteRepo;

        this.ventaRepo =
                ventaRepo;

        this.ventaService =
                ventaService;
    }


    // =========================================================
    // 👤 CARGAR DATOS DEL USUARIO
    // =========================================================

    /**
     * Carga en el Model:
     *
     * ✔ nombre del usuario
     * ✔ rol
     * ✔ negocioId
     *
     * Estos datos son utilizados principalmente por
     * el TOPBAR y el menú de la aplicación.
     */
    private void cargarDatosUsuario(
            Model model,
            Authentication authentication
    ) {

        // -----------------------------------------------------
        // ❌ NO AUTENTICADO
        // -----------------------------------------------------

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


        // -----------------------------------------------------
        // 🔐 OBTENER PRINCIPAL
        // -----------------------------------------------------

        Object principal =
                authentication.getPrincipal();


        // =====================================================
        // 👤 CUSTOM USER DETAILS
        // =====================================================

        if (
                principal instanceof CustomUserDetails user
        ) {

            // -------------------------------------------------
            // 👤 NOMBRE
            // -------------------------------------------------

            model.addAttribute(
                    "usuario",
                    user.getNombre()
            );


            // -------------------------------------------------
            // 🔐 ROL
            // -------------------------------------------------

            String rol =
                    authentication
                            .getAuthorities()
                            .stream()
                            .map(
                                    GrantedAuthority::getAuthority
                            )
                            .findFirst()
                            .orElse("USUARIO");


            // -------------------------------------------------
            // 🔄 QUITAR ROLE_
            // -------------------------------------------------

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


            // -------------------------------------------------
            // 🏪 NEGOCIO
            // -------------------------------------------------

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

    /**
     * ======================================================
     * GET /facturas
     * ======================================================
     *
     * Muestra únicamente las facturas del negocio
     * autenticado.
     *
     * ❌ Ya NO se utiliza:
     *
     * facturaRepo.findAll()
     *
     * ======================================================
     */
    @GetMapping
    public String listarFacturas(
            Model model,
            Authentication authentication
    ) {

        // -----------------------------------------------------
        // 🔐 OBTENER NEGOCIO
        // -----------------------------------------------------

        Integer negocioId =
                obtenerNegocioId(authentication);


        // -----------------------------------------------------
        // 📋 CONSULTAR FACTURAS DEL NEGOCIO
        // -----------------------------------------------------

        List<Factura> facturas =
                facturaRepo
                        .findAllByNegocioIdOrderByFechaDesc(
                                negocioId
                        );


        // -----------------------------------------------------
        // 📦 ENVIAR AL HTML
        // -----------------------------------------------------

        model.addAttribute(
                "facturas",
                facturas
        );


        model.addAttribute(
                "menuActivo",
                "facturas"
        );


        // -----------------------------------------------------
        // 👤 USUARIO
        // -----------------------------------------------------

        cargarDatosUsuario(
                model,
                authentication
        );


        return "facturas";
    }


    // =========================================================
    // 🔍 BUSCAR FACTURAS
    // =========================================================

    /**
     * ======================================================
     * GET /facturas/buscar
     * ======================================================
     *
     * Busca por número de factura pero únicamente dentro
     * del negocio autenticado.
     *
     * Ejemplo:
     *
     * /facturas/buscar?numeroFactura=FAC-001
     *
     * ======================================================
     */
    @GetMapping("/buscar")
    public String buscarFacturas(
            @RequestParam(
                    defaultValue = ""
            )
            String numeroFactura,

            Model model,

            Authentication authentication
    ) {

        // -----------------------------------------------------
        // 🔐 OBTENER NEGOCIO
        // -----------------------------------------------------

        Integer negocioId =
                obtenerNegocioId(authentication);


        // -----------------------------------------------------
        // 🧹 LIMPIAR BÚSQUEDA
        // -----------------------------------------------------

        String numeroBusqueda =
                numeroFactura == null
                        ? ""
                        : numeroFactura.trim();


        // -----------------------------------------------------
        // 🔍 BUSCAR
        // -----------------------------------------------------

        List<Factura> facturas =
                facturaRepo
                        .findByNumeroFacturaContainingIgnoreCaseAndNegocioId(
                                numeroBusqueda,
                                negocioId
                        );


        // -----------------------------------------------------
        // 📦 ENVIAR RESULTADO
        // -----------------------------------------------------

        model.addAttribute(
                "facturas",
                facturas
        );


        model.addAttribute(
                "menuActivo",
                "facturas"
        );


        // -----------------------------------------------------
        // 👤 USUARIO
        // -----------------------------------------------------

        cargarDatosUsuario(
                model,
                authentication
        );


        return "facturas";
    }


    // =========================================================
    // 🔎 OBTENER VENTA SEGURA
    // =========================================================

    /**
     * Busca una venta únicamente si pertenece al negocio
     * autenticado.
     *
     * Este método es importante porque:
     *
     * Factura → Venta → negocioId
     *
     * De esta manera podemos validar la pertenencia de una
     * factura antes de mostrarla o generar un PDF.
     */
    private Venta obtenerVentaDelNegocio(
            Integer ventaId,
            Integer negocioId
    ) {

        if (ventaId == null) {

            throw new RuntimeException(
                    "La factura no tiene una venta asociada."
            );
        }


        return ventaRepo
                .findByIdAndNegocioId(
                        ventaId,
                        negocioId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Factura no encontrada."
                        )
                );
    }


    // =========================================================
    // 🧾 OBTENER FACTURA SEGURA
    // =========================================================

    /**
     * Busca una factura por ID validando primero que la venta
     * asociada pertenezca al negocio autenticado.
     *
     * Esto evita que un usuario pueda acceder manualmente
     * a una factura de otro negocio modificando la URL.
     */
    private Factura obtenerFacturaDelNegocio(
            Integer facturaId,
            Integer negocioId
    ) {

        // -----------------------------------------------------
        // 🔍 BUSCAR FACTURA
        // -----------------------------------------------------

        Factura factura =
                facturaRepo
                        .findById(facturaId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Factura no encontrada."
                                )
                        );


        // -----------------------------------------------------
        // 🔐 VALIDAR VENTA
        // -----------------------------------------------------

        obtenerVentaDelNegocio(
                factura.getVentaId(),
                negocioId
        );


        return factura;
    }


    // =========================================================
    // 📄 VER DETALLE DE FACTURA
    // =========================================================

    /**
     * ======================================================
     * GET /facturas/{id}
     * ======================================================
     *
     * Muestra el detalle de una factura.
     *
     * 🔐 La factura debe pertenecer al negocio autenticado.
     */
    @GetMapping("/{id}")
    public String verFactura(
            @PathVariable Integer id,
            Model model,
            Authentication authentication
    ) {

        // -----------------------------------------------------
        // 🔐 NEGOCIO AUTENTICADO
        // -----------------------------------------------------

        Integer negocioId =
                obtenerNegocioId(authentication);


        // -----------------------------------------------------
        // 🧾 FACTURA SEGURA
        // -----------------------------------------------------

        Factura facturaEntity =
                obtenerFacturaDelNegocio(
                        id,
                        negocioId
                );


        // -----------------------------------------------------
        // 🔐 OBTENER FACTURA COMPLETA
        // -----------------------------------------------------
        //
        // IMPORTANTE:
        // Se utiliza la versión del servicio que recibe
        // negocioId.
        //

        FacturaDTO factura =
                ventaService.obtenerFactura(
                        facturaEntity.getVentaId(),
                        negocioId
                );


        // -----------------------------------------------------
        // 🆔 ID FACTURA
        // -----------------------------------------------------

        factura.setId(
                facturaEntity.getId()
        );


        // -----------------------------------------------------
        // 🔍 OBTENER VENTA
        // -----------------------------------------------------

        Venta venta =
                obtenerVentaDelNegocio(
                        facturaEntity.getVentaId(),
                        negocioId
                );


        // =====================================================
        // 👤 CLIENTE
        // =====================================================

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


        // -----------------------------------------------------
        // 📦 DATOS FACTURA
        // -----------------------------------------------------

        model.addAttribute(
                "factura",
                factura
        );


        // -----------------------------------------------------
        // 📦 DETALLES SEGUROS
        // -----------------------------------------------------

        model.addAttribute(
                "detalles",
                detalleRepo.findByIdVentaAndNegocioId(
                        facturaEntity.getVentaId(),
                        negocioId
                )
        );


        // -----------------------------------------------------
        // 📌 MENÚ
        // -----------------------------------------------------

        model.addAttribute(
                "menuActivo",
                "facturas"
        );


        // -----------------------------------------------------
        // 👤 USUARIO
        // -----------------------------------------------------

        cargarDatosUsuario(
                model,
                authentication
        );


        return "factura-detalle";
    }


    // =========================================================
    // 📥 DESCARGAR FACTURA PDF
    // =========================================================

    /**
     * ======================================================
     * GET /facturas/pdf/{id}
     * ======================================================
     *
     * Genera el PDF únicamente si la factura pertenece al
     * negocio del usuario autenticado.
     */
    @GetMapping("/pdf/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> descargarPdf(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        try {

            // -------------------------------------------------
            // 🔐 NEGOCIO
            // -------------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(authentication);


            // -------------------------------------------------
            // 🧾 FACTURA SEGURA
            // -------------------------------------------------

            Factura factura =
                    obtenerFacturaDelNegocio(
                            id,
                            negocioId
                    );


            // -------------------------------------------------
            // 📦 DETALLES DEL NEGOCIO
            // -------------------------------------------------

            List<DetalleVenta> detalles =
                    detalleRepo.findByIdVentaAndNegocioId(
                            factura.getVentaId(),
                            negocioId
                    );


            // -------------------------------------------------
            // 👤 DATOS CLIENTE
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
                    "";


            // -------------------------------------------------
            // 🧾 VENTA SEGURA
            // -------------------------------------------------

            Venta venta =
                    obtenerVentaDelNegocio(
                            factura.getVentaId(),
                            negocioId
                    );


            // -------------------------------------------------
            // 💳 MÉTODO DE PAGO
            // -------------------------------------------------

            if (
                    venta.getMetodoPago() != null
            ) {

                metodoPago =
                        venta.getMetodoPago();
            }


            // -------------------------------------------------
            // 👤 CLIENTE
            // -------------------------------------------------

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
                            cliente.getNombre();

                    documentoCliente =
                            cliente.getNumeroIdentificacion();

                    telefonoCliente =
                            cliente.getTelefono();

                    direccionCliente =
                            cliente.getDireccion();
                }
            }


            // =================================================
            // 📄 CREAR PDF
            // =================================================

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();


            Document document =
                    new Document();


            PdfWriter.getInstance(
                    document,
                    baos
            );


            document.open();


            // =================================================
            // 🖼 LOGO
            // =================================================

            try {

                ClassPathResource resource =
                        new ClassPathResource(
                                "static/images/logo.png"
                        );


                Image logo =
                        Image.getInstance(
                                resource.getURL()
                        );


                logo.scaleToFit(
                        120,
                        120
                );


                logo.setAlignment(
                        Image.ALIGN_CENTER
                );


                document.add(
                        logo
                );

            } catch (Exception e) {

                System.out.println(
                        "No se pudo cargar el logo."
                );
            }


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
            // 🧾 INFORMACIÓN FACTURA
            // =================================================

            document.add(
                    new Paragraph(
                            "Factura: "
                                    + factura.getNumeroFactura()
                    )
            );


            document.add(
                    new Paragraph(
                            "Fecha: "
                                    + factura.getFecha()
                    )
            );


            document.add(
                    new Paragraph(
                            "Estado: "
                                    + factura.getEstado()
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
                    )
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

                tabla.addCell(
                        d.getNombreProducto()
                );


                tabla.addCell(
                        String.valueOf(
                                d.getCantidad()
                        )
                );


                tabla.addCell(
                        "$ "
                                + String.format(
                                "%,.0f",
                                d.getPrecio()
                        )
                );


                tabla.addCell(
                        "$ "
                                + String.format(
                                "%,.0f",
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
                                    + String.format(
                                    "%,.0f",
                                    factura.getSubtotal()
                            ),
                            subtitulo
                    )
            );


            document.add(
                    new Paragraph(
                            "IVA: $ "
                                    + String.format(
                                    "%,.0f",
                                    factura.getIva()
                            ),
                            subtitulo
                    )
            );


            document.add(
                    new Paragraph(
                            "TOTAL: $ "
                                    + String.format(
                                    "%,.0f",
                                    factura.getTotal()
                            ),
                            titulo
                    )
            );


            document.add(
                    new Paragraph(
                            "\nGracias por comprar en EcoMarket PRO",
                            subtitulo
                    )
            );


            // =================================================
            // 🔒 CERRAR PDF
            // =================================================

            document.close();


            // =================================================
            // 📤 RESPUESTA
            // =================================================

            HttpHeaders headers =
                    new HttpHeaders();


            headers.add(
                    "Content-Disposition",
                    "attachment; filename=Factura_"
                            + factura.getNumeroFactura()
                            + ".pdf"
            );


            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .body(
                            baos.toByteArray()
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generando PDF",
                    e
            );
        }
    }


    // =========================================================
    // 🧾 DESCARGAR TIRILLA
    // =========================================================

    /**
     * ======================================================
     * GET /facturas/pdf-tirilla/{id}
     * ======================================================
     *
     * Genera una tirilla térmica en PDF.
     *
     * También está protegida por negocio.
     */
    @GetMapping("/pdf-tirilla/{id}")
    @ResponseBody
    public ResponseEntity<byte[]> descargarTirilla(
            @PathVariable Integer id,
            Authentication authentication
    ) {

        try {

            // -------------------------------------------------
            // 🔐 NEGOCIO
            // -------------------------------------------------

            Integer negocioId =
                    obtenerNegocioId(authentication);


            // -------------------------------------------------
            // 🧾 FACTURA SEGURA
            // -------------------------------------------------

            Factura factura =
                    obtenerFacturaDelNegocio(
                            id,
                            negocioId
                    );


            // -------------------------------------------------
            // 📦 DETALLES SEGUROS
            // -------------------------------------------------

            List<DetalleVenta> detalles =
                    detalleRepo.findByIdVentaAndNegocioId(
                            factura.getVentaId(),
                            negocioId
                    );


            // -------------------------------------------------
            // 👤 CLIENTE
            // -------------------------------------------------

            String clienteNombre =
                    "Cliente de Contado";


            String metodoPago =
                    "";


            // -------------------------------------------------
            // 🧾 VENTA SEGURA
            // -------------------------------------------------

            Venta venta =
                    obtenerVentaDelNegocio(
                            factura.getVentaId(),
                            negocioId
                    );


            // -------------------------------------------------
            // 💳 MÉTODO DE PAGO
            // -------------------------------------------------

            if (
                    venta.getMetodoPago() != null
            ) {

                metodoPago =
                        venta.getMetodoPago();
            }


            // -------------------------------------------------
            // 👤 CLIENTE
            // -------------------------------------------------

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
                            cliente.getNombre();
                }
            }


            // =================================================
            // 📄 CREAR TIRILLA
            // =================================================

            ByteArrayOutputStream baos =
                    new ByteArrayOutputStream();


            Rectangle ticket =
                    new Rectangle(
                            226f,
                            1200f
                    );


            Document document =
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

            try {

                ClassPathResource resource =
                        new ClassPathResource(
                                "static/images/logo.png"
                        );


                Image logo =
                        Image.getInstance(
                                resource.getURL()
                        );


                logo.scaleToFit(
                        70,
                        70
                );


                logo.setAlignment(
                        Element.ALIGN_CENTER
                );


                document.add(
                        logo
                );

            } catch (Exception e) {

                System.out.println(
                        "Logo no encontrado"
                );
            }


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
                                    + factura.getNumeroFactura(),
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
                            "--------------------------------"
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
                                d.getNombreProducto(),
                                normal
                        )
                );


                document.add(
                        new Paragraph(
                                d.getCantidad()
                                        + " x $ "
                                        + String.format(
                                        "%,.0f",
                                        d.getPrecio()
                                ),
                                normal
                        )
                );


                document.add(
                        new Paragraph(
                                "$ "
                                        + String.format(
                                        "%,.0f",
                                        d.getSubtotal()
                                ),
                                normal
                        )
                );


                document.add(
                        new Paragraph(
                                "--------------------------------"
                        )
                );
            }


            // =================================================
            // 💰 TOTALES
            // =================================================

            document.add(
                    new Paragraph(
                            "Subtotal: $ "
                                    + String.format(
                                    "%,.0f",
                                    factura.getSubtotal()
                            ),
                            normal
                    )
            );


            document.add(
                    new Paragraph(
                            "IVA: $ "
                                    + String.format(
                                    "%,.0f",
                                    factura.getIva()
                            ),
                            normal
                    )
            );


            Paragraph total =
                    new Paragraph(
                            "\nTOTAL: $ "
                                    + String.format(
                                    "%,.0f",
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


            // =================================================
            // 🔒 CERRAR DOCUMENTO
            // =================================================

            document.close();


            // =================================================
            // 📤 RESPUESTA
            // =================================================

            HttpHeaders headers =
                    new HttpHeaders();


            headers.add(
                    "Content-Disposition",
                    "attachment; filename=Tirilla_"
                            + factura.getNumeroFactura()
                            + ".pdf"
            );


            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .body(
                            baos.toByteArray()
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Error generando tirilla",
                    e
            );
        }
    }


    // =========================================================
    // 🔐 OBTENER NEGOCIO DEL USUARIO
    // =========================================================

    /**
     * Obtiene el negocio directamente desde el usuario
     * autenticado.
     *
     * 🚨 IMPORTANTE:
     *
     * Nunca se recibe negocioId desde:
     *
     * - URL
     * - RequestParam
     * - RequestBody
     * - JavaScript
     *
     * El negocio se obtiene exclusivamente desde
     * CustomUserDetails.
     */
    private Integer obtenerNegocioId(
            Authentication authentication
    ) {

        // -----------------------------------------------------
        // ❌ VALIDAR AUTENTICACIÓN
        // -----------------------------------------------------

        if (
                authentication == null ||
                        !authentication.isAuthenticated()
        ) {

            throw new IllegalStateException(
                    "Usuario no autenticado."
            );
        }


        // -----------------------------------------------------
        // 🔐 VALIDAR PRINCIPAL
        // -----------------------------------------------------

        Object principal =
                authentication.getPrincipal();


        if (
                !(principal instanceof CustomUserDetails user)
        ) {

            throw new IllegalStateException(
                    "No se pudo obtener la información del usuario autenticado."
            );
        }


        // -----------------------------------------------------
        // 🏪 NEGOCIO
        // -----------------------------------------------------

        Integer negocioId =
                user.getNegocioId();


        // -----------------------------------------------------
        // ❌ SIN NEGOCIO
        // -----------------------------------------------------

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

