
// ======================================================
// 📦 VARIABLES GLOBALES
// ======================================================

let devolucionesGlobal = [];
let ventasGlobal = [];
let detallesVenta = [];

let clienteSeleccionado = null;
let detalleSeleccionado = null;
let devolucionEditando = null;


// ======================================================
// 🔒 CONTROLES DE OPERACIÓN
// ======================================================

let guardandoDevolucion = false;
let eliminandoDevolucion = false;
let cargandoFactura = false;


// ======================================================
// ⏱️ CONFIGURACIÓN
// ======================================================

const TIMEOUT_PETICION = 20000;


// ======================================================
// 🔐 VALIDAR ADMIN
// ======================================================

function esAdmin() {

    return String(
        typeof ROL_USUARIO !== "undefined"
            ? ROL_USUARIO
            : ""
    )
        .toUpperCase()
        .trim() === "ADMIN";
}


// ======================================================
// 🚀 INICIAR MODULO
// ======================================================

document.addEventListener(
    "DOMContentLoaded",
    async function () {

        console.log(
            "↩️ Iniciando módulo Devoluciones..."
        );

        aplicarPermisos();

        configurarBuscadorTopbar();

        configurarEventos();

        await cargarVentas();

        await cargarDevoluciones();

        console.log(
            "✅ Módulo Devoluciones cargado correctamente."
        );

    }
);


// ======================================================
// 🔐 PERMISOS
// ======================================================

function aplicarPermisos() {

    if (!esAdmin()) {

        console.log(
            "👤 Usuario EMPLEADO: modo consulta."
        );

    } else {

        console.log(
            "🛡️ Usuario ADMIN: permisos de gestión."
        );

    }

}


// ======================================================
// 🔎 BUSCADOR TOPBAR
// ======================================================

function configurarBuscadorTopbar() {

    const buscarTopbar =
        document.getElementById(
            "buscarTopbar"
        );

    if (!buscarTopbar) {

        console.warn(
            "⚠️ No se encontró #buscarTopbar."
        );

        return;
    }

    buscarTopbar.placeholder =
        "Buscar devoluciones...";

    buscarTopbar.value = "";

    buscarTopbar.addEventListener(
        "input",
        function () {

            filtrarDevoluciones(
                this.value
            );

        }
    );

}


// ======================================================
// 🔎 NORMALIZAR TEXTO
// ======================================================

function normalizarTexto(texto) {

    if (
        texto === null ||
        texto === undefined
    ) {

        return "";

    }

    return String(texto)
        .toLowerCase()
        .normalize("NFD")
        .replace(
            /[\u0300-\u036f]/g,
            ""
        )
        .trim();

}


// ======================================================
// 🔎 FILTRAR DEVOLUCIONES
// ======================================================

function filtrarDevoluciones(texto) {

    const busqueda =
        normalizarTexto(texto);

    if (!busqueda) {

        actualizarKPIs(
            devolucionesGlobal
        );

        renderTabla(
            devolucionesGlobal
        );

        return;

    }


    const filtradas =
        devolucionesGlobal.filter(
            function (devolucion) {

                const factura =
                    normalizarTexto(

                        devolucion.venta?.numeroFactura

                        ||

                        devolucion.numeroFactura

                        ||

                        (
                            "Venta #" +
                            (
                                devolucion.venta?.id
                                ||
                                devolucion.ventaId
                                ||
                                ""
                            )
                        )

                    );


                const cliente =
                    normalizarTexto(

                        devolucion.cliente?.nombre

                        ||

                        devolucion.cliente?.nombreCompleto

                        ||

                        devolucion.clienteNombre

                        ||

                        "Consumidor Final"

                    );


                const producto =
                    normalizarTexto(

                        devolucion.producto?.nombre

                        ||

                        devolucion.producto?.nombreProducto

                        ||

                        devolucion.productoNombre

                        ||

                        "Producto"

                    );


                const motivo =
                    normalizarTexto(
                        devolucion.motivo
                        || ""
                    );


                const estado =
                    normalizarTexto(
                        devolucion.estado
                        || "ACTIVA"
                    );


                const fecha =
                    normalizarTexto(
                        devolucion.fechaDevolucion
                        || ""
                    );


                const cantidad =
                    normalizarTexto(
                        devolucion.cantidad
                        || ""
                    );


                const monto =
                    normalizarTexto(
                        devolucion.monto
                        || ""
                    );


                return (

                    factura.includes(busqueda)

                    ||

                    cliente.includes(busqueda)

                    ||

                    producto.includes(busqueda)

                    ||

                    motivo.includes(busqueda)

                    ||

                    estado.includes(busqueda)

                    ||

                    fecha.includes(busqueda)

                    ||

                    cantidad.includes(busqueda)

                    ||

                    monto.includes(busqueda)

                );

            }
        );


    actualizarKPIs(
        filtradas
    );


    renderTabla(
        filtradas
    );

}


// ======================================================
// ⚙️ CONFIGURAR EVENTOS
// ======================================================

function configurarEventos() {

    if (esAdmin()) {

        const ventaId =
            document.getElementById(
                "ventaId"
            );

        if (ventaId) {

            ventaId.addEventListener(
                "change",
                cargarDatosFactura
            );

        }


        const productoId =
            document.getElementById(
                "productoId"
            );

        if (productoId) {

            productoId.addEventListener(
                "change",
                cargarDatosProducto
            );

        }


        const cantidad =
            document.getElementById(
                "cantidad"
            );

        if (cantidad) {

            cantidad.addEventListener(
                "input",
                calcularMonto
            );

        }

    }


    // ==================================================
    // ESCAPE
    // ==================================================

    document.addEventListener(
        "keydown",
        function (event) {

            if (
                event.key === "Escape"
            ) {

                cerrarModalDevolucion();

            }

        }
    );


    // ==================================================
    // CLICK FUERA
    // ==================================================

    document.addEventListener(
        "click",
        function (event) {

            const modal =
                document.getElementById(
                    "modalDevolucion"
                );

            if (!modal) {

                return;

            }

            if (
                event.target === modal
            ) {

                cerrarModalDevolucion();

            }

        }
    );

}


// ======================================================
// 📄 CARGAR VENTAS
// ======================================================

async function cargarVentas() {

    try {

        const response =
            await fetchConTimeout(
                "/api/ventas",
                {
                    method: "GET"
                }
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        ventasGlobal =
            await response.json();


        const selectVenta =
            document.getElementById(
                "ventaId"
            );


        if (!selectVenta) {

            return;

        }


        selectVenta.innerHTML = `

<option value="">
    Seleccione una factura
</option>

    `;


        ventasGlobal.forEach(
            function (venta) {

                const option =
                    document.createElement(
                        "option"
                    );


                option.value =
                    venta.id;


                option.textContent =
                    venta.numeroFactura
                    ||
                    `Factura #${venta.id}`;


                selectVenta.appendChild(
                    option
                );

            }
        );


    } catch (error) {

        console.error(
            "❌ Error cargando ventas:",
            error
        );


        mostrarError(
            error.message
            ||
            "No fue posible cargar las facturas."
        );

    }

}


// ======================================================
// 📄 CARGAR DATOS FACTURA
// ======================================================

async function cargarDatosFactura() {

    if (cargandoFactura) {

        return;

    }


    const ventaId =
        document.getElementById(
            "ventaId"
        )?.value;


    if (!ventaId) {

        limpiarDatosFactura();

        return;

    }


    cargandoFactura =
        true;


    try {

        const venta =
            ventasGlobal.find(
                function (item) {

                    return String(item.id)
                        ===
                        String(ventaId);

                }
            );


        clienteSeleccionado =
            null;


        const clienteInput =
            document.getElementById(
                "clienteNombre"
            );


        // ==================================================
        // CLIENTE
        // ==================================================

        if (venta) {

            const clienteId =
                venta.idCliente
                ||
                venta.clienteId
                ||
                venta.cliente?.id;


            if (clienteId) {

                clienteSeleccionado =
                    Number(clienteId);


                try {

                    const responseCliente =
                        await fetchConTimeout(
                            `/api/clientes/${clienteId}`,
                            {
                                method: "GET"
                            }
                        );


                    if (
                        responseCliente.ok
                    ) {

                        const cliente =
                            await responseCliente.json();


                        if (clienteInput) {

                            clienteInput.value =
                                cliente.nombre
                                ||
                                cliente.nombreCompleto
                                ||
                                "Cliente";

                        }

                    } else {

                        if (clienteInput) {

                            clienteInput.value =
                                "Cliente";

                        }

                    }


                } catch (errorCliente) {

                    console.warn(
                        "⚠️ No se pudo cargar el cliente:",
                        errorCliente
                    );


                    if (clienteInput) {

                        clienteInput.value =
                            "Cliente";

                    }

                }

            } else {

                if (clienteInput) {

                    clienteInput.value =
                        "Consumidor Final";

                }

            }

        }


        // ==================================================
        // DETALLES
        // ==================================================

        const responseDetalle =
            await fetchConTimeout(
                `/api/detalle-venta/venta/${ventaId}`,
                {
                    method: "GET"
                }
            );


        if (!responseDetalle.ok) {

            throw new Error(
                await obtenerMensajeError(
                    responseDetalle
                )
            );

        }


        detallesVenta =
            await responseDetalle.json();


        console.log(
            "📦 Detalles venta:",
            detallesVenta
        );


        const selectProducto =
            document.getElementById(
                "productoId"
            );


        if (!selectProducto) {

            return;

        }


        selectProducto.innerHTML = `

<option value="">
    Seleccione un producto
</option>

    `;


        detallesVenta.forEach(
            function (detalle) {

                const option =
                    document.createElement(
                        "option"
                    );


                const productoId =
                    detalle.idProducto
                    ||
                    detalle.productoId
                    ||
                    detalle.producto?.id;


                option.value =
                    productoId;


                option.dataset.detalle =
                    detalle.id;


                option.textContent =
                    detalle.nombreProducto
                    ||
                    detalle.producto?.nombre
                    ||
                    `Producto #${productoId}`;


                selectProducto.appendChild(
                    option
                );

            }
        );


        selectProducto.disabled =
            detallesVenta.length === 0;


        detalleSeleccionado =
            null;


        const cantidad =
            document.getElementById(
                "cantidad"
            );


        const monto =
            document.getElementById(
                "monto"
            );


        const disponibilidad =
            document.getElementById(
                "cantidadDisponible"
            );


        if (cantidad) {

            cantidad.value = "";

            cantidad.disabled = true;

            cantidad.removeAttribute(
                "max"
            );

        }


        if (monto) {

            monto.value = "";

        }


        if (disponibilidad) {

            disponibilidad.textContent =
                detallesVenta.length > 0
                    ? "Seleccione un producto."
                    : "La factura no tiene productos.";

        }


    } catch (error) {

        console.error(
            "❌ Error cargando factura:",
            error
        );


        mostrarError(
            error.message
            ||
            "No fue posible cargar los datos de la factura."
        );


    } finally {

        cargandoFactura =
            false;

    }

}


// ======================================================
// 🧹 LIMPIAR DATOS FACTURA
// ======================================================

function limpiarDatosFactura() {

    clienteSeleccionado =
        null;


    detalleSeleccionado =
        null;


    detallesVenta =
        [];


    const cliente =
        document.getElementById(
            "clienteNombre"
        );


    const producto =
        document.getElementById(
            "productoId"
        );


    const cantidad =
        document.getElementById(
            "cantidad"
        );


    const monto =
        document.getElementById(
            "monto"
        );


    const disponibilidad =
        document.getElementById(
            "cantidadDisponible"
        );


    if (cliente) {

        cliente.value = "";

    }


    if (producto) {

        producto.innerHTML = `

<option value="">
    Seleccione un producto
</option>

    `;

        producto.value = "";

        producto.disabled =
            true;

    }


    if (cantidad) {

        cantidad.value = "";

        cantidad.disabled =
            true;

        cantidad.removeAttribute(
            "max"
        );

    }


    if (monto) {

        monto.value = "";

    }


    if (disponibilidad) {

        disponibilidad.textContent =
            "Seleccione un producto.";

    }

}


// ======================================================
// 📦 CARGAR PRODUCTO
// ======================================================

function cargarDatosProducto() {

    const productoSelect =
        document.getElementById(
            "productoId"
        );


    const productoId =
        productoSelect?.value;


    if (!productoId) {

        detalleSeleccionado =
            null;


        const cantidad =
            document.getElementById(
                "cantidad"
            );


        const monto =
            document.getElementById(
                "monto"
            );


        if (cantidad) {

            cantidad.value = "";

            cantidad.disabled =
                true;

            cantidad.removeAttribute(
                "max"
            );

        }


        if (monto) {

            monto.value = "";

        }


        return;

    }


    detalleSeleccionado =
        detallesVenta.find(
            function (detalle) {

                const idProducto =
                    detalle.idProducto
                    ||
                    detalle.productoId
                    ||
                    detalle.producto?.id;


                return String(idProducto)
                    ===
                    String(productoId);

            }
        );


    if (!detalleSeleccionado) {

        mostrarError(
            "No se encontró el detalle del producto."
        );

        return;

    }


    const cantidadVendida =
        Number(

            detalleSeleccionado.cantidad
            ||
            detalleSeleccionado.cantidadVendida
            ||
            0

        );


    const cantidad =
        document.getElementById(
            "cantidad"
        );


    const disponibilidad =
        document.getElementById(
            "cantidadDisponible"
        );


    if (cantidad) {

        cantidad.disabled =
            cantidadVendida <= 0;


        cantidad.min =
            "1";


        cantidad.max =
            cantidadVendida;


        cantidad.value =
            cantidadVendida > 0
                ? 1
                : "";

    }


    if (disponibilidad) {

        disponibilidad.textContent =
            `Cantidad vendida: ${cantidadVendida}`;

    }


    calcularMonto();

}


// ======================================================
// 💰 CALCULAR MONTO
// ======================================================

function calcularMonto() {

    const cantidadInput =
        document.getElementById(
            "cantidad"
        );


    const montoInput =
        document.getElementById(
            "monto"
        );


    if (
        !cantidadInput
        ||
        !montoInput
        ||
        !detalleSeleccionado
    ) {

        return;

    }


    let cantidad =
        Number(
            cantidadInput.value
        );


    const cantidadVendida =
        Number(

            detalleSeleccionado.cantidad
            ||
            detalleSeleccionado.cantidadVendida
            ||
            0

        );


    if (
        !Number.isInteger(cantidad)
        ||
        cantidad <= 0
    ) {

        montoInput.value = "";

        return;

    }


    if (
        cantidad > cantidadVendida
    ) {

        cantidad =
            cantidadVendida;


        cantidadInput.value =
            cantidadVendida;


        Swal.fire({

            icon:
                "warning",

            title:
                "Cantidad no válida",

            text:
                `Solo se pueden devolver ${cantidadVendida} unidades.`,

            confirmButtonText:
                "Entendido"

        });

    }


    const precio =
        Number(

            detalleSeleccionado.precio
            ||
            detalleSeleccionado.precioUnitario
            ||
            detalleSeleccionado.precioVenta
            ||
            0

        );


    const monto =
        cantidad * precio;


    montoInput.value =
        formatearMoneda(
            monto
        );

}


// ======================================================
// ↩️ CARGAR DEVOLUCIONES
// ======================================================

async function cargarDevoluciones() {

    try {

        const response =
            await fetchConTimeout(
                "/api/devoluciones",
                {
                    method: "GET"
                }
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        devolucionesGlobal =
            await response.json();


        actualizarKPIs(
            devolucionesGlobal
        );


        renderTabla(
            devolucionesGlobal
        );


    } catch (error) {

        console.error(
            "❌ Error cargando devoluciones:",
            error
        );


        mostrarError(
            error.message
            ||
            "No fue posible cargar las devoluciones."
        );

    }

}


// ======================================================
// 📊 KPIs
// ======================================================

function actualizarKPIs(lista) {

    const datos =
        Array.isArray(lista)
            ? lista
            : [];


    const total =
        datos.length;


    let montoTotal =
        0;


    let productosTotal =
        0;


    datos.forEach(
        function (devolucion) {

            const estado =
                normalizarTexto(
                    devolucion.estado
                    ||
                    "ACTIVA"
                );


            if (
                estado === "anulada"
            ) {

                return;

            }


            montoTotal +=
                Number(
                    devolucion.monto
                    || 0
                );


            productosTotal +=
                Number(
                    devolucion.cantidad
                    || 0
                );

        }
    );


    const totalElement =
        document.getElementById(
            "totalDevoluciones"
        );


    if (totalElement) {

        totalElement.textContent =
            total;

    }


    const montoElement =
        document.getElementById(
            "montoDevuelto"
        );


    if (montoElement) {

        montoElement.textContent =
            formatearMoneda(
                montoTotal
            );

    }


    const productosElement =
        document.getElementById(
            "productosDevueltos"
        );


    if (productosElement) {

        productosElement.textContent =
            productosTotal;

    }

}


// ======================================================
// 📋 TABLA
// ======================================================

function renderTabla(lista) {

    const tabla =
        document.getElementById(
            "tablaDevoluciones"
        );


    if (!tabla) {

        return;

    }


    tabla.innerHTML = "";


    const datos =
        Array.isArray(lista)
            ? lista
            : [];


    if (
        datos.length === 0
    ) {

        const columnas =
            esAdmin()
                ? 9
                : 8;


        tabla.innerHTML = `

<tr>

<td
colspan="${columnas}"
class="devoluciones-empty">

    <div class="empty-content">

    <i class="fa-solid fa-box-open"></i>

<strong>
    No se encontraron devoluciones
</strong>

<span>
                            No existen registros que coincidan
                            con la búsqueda.
                        </span>

</div>

</td>

</tr>

`;

        return;

    }


    datos.forEach(
        function (devolucion) {

            const factura =
                devolucion.venta?.numeroFactura
                ||
                devolucion.numeroFactura
                ||
                (
                    "Venta #" +
                    (
                        devolucion.venta?.id
                        ||
                        devolucion.ventaId
                        ||
                        "-"
                    )
                );


            const cliente =
                devolucion.cliente?.nombre
                ||
                devolucion.cliente?.nombreCompleto
                ||
                devolucion.clienteNombre
                ||
                "Consumidor Final";


            const producto =
                devolucion.producto?.nombre
                ||
                devolucion.producto?.nombreProducto
                ||
                devolucion.productoNombre
                ||
                "Producto";


            const cantidad =
                Number(
                    devolucion.cantidad
                    || 0
                );


            const monto =
                Number(
                    devolucion.monto
                    || 0
                );


            const motivo =
                devolucion.motivo
                ||
                "Sin motivo";


            const fecha =
                formatearFecha(
                    devolucion.fechaDevolucion
                    ||
                    devolucion.fecha
                );


            const estado =
                String(
                    devolucion.estado
                    ||
                    "ACTIVA"
                ).toUpperCase();


            const esActiva =
                estado === "ACTIVA";


            const fila =
                document.createElement(
                    "tr"
                );


            let contenido = `

<td>

<span class="factura-badge">

    <i class="fa-solid fa-file-invoice"></i>

${escapeHtml(factura)}

</span>

</td>


<td>

                    <span class="cliente-cell">

                        <i class="fa-solid fa-user"></i>

                        ${escapeHtml(cliente)}

                    </span>

</td>


<td>

                    <span
                        class="producto-cell"
                        title="${escapeHtml(producto)}">

                        ${escapeHtml(producto)}

                    </span>

</td>


<td>

                    <span class="cantidad-badge">

                        ${cantidad}

                    </span>

</td>


<td>

    <strong class="monto-cell">

        ${formatearMoneda(monto)}

    </strong>

</td>


<td>

                    <span
                        class="motivo-cell"
                        title="${escapeHtml(motivo)}">

                        ${escapeHtml(motivo)}

                    </span>

</td>


<td>

                    <span class="fecha-cell">

                        ${escapeHtml(fecha)}

                    </span>

</td>


<td>

                    <span
                        class="estado-badge
                        ${esActiva
                            ? "estado-activa"
                            : "estado-anulada"}">

                        <i class="fa-solid
                            ${esActiva
                                ? "fa-circle-check"
                                : "fa-circle-xmark"}">
                        </i>

                        ${escapeHtml(estado)}

                    </span>

</td>

    `;


            if (esAdmin()) {

                contenido += `

<td>

<div class="devolucion-actions">

    <button
type="button"
class="btn-action btn-edit"
title="Editar devolución"
onclick="editarDevolucion(${Number(devolucion.id)})">

    <i class="fa-solid fa-pen"></i>

</button>


<button
    type="button"
    class="btn-action btn-delete"
    title="Eliminar devolución"
    onclick="eliminarDevolucion(${Number(devolucion.id)})">

    <i class="fa-solid fa-trash"></i>

</button>

</div>

</td>

`;

            }


            fila.innerHTML =
                contenido;


            tabla.appendChild(
                fila
            );

        }
    );

}


// ======================================================
// ➕ ABRIR MODAL
// ======================================================

function abrirModalDevolucion() {

    if (!esAdmin()) {

        mostrarError(
            "No tienes permisos para realizar esta acción."
        );

        return;

    }


    if (guardandoDevolucion) {

        return;

    }


    const modal =
        document.getElementById(
            "modalDevolucion"
        );


    if (!modal) {

        return;

    }


    limpiarFormulario();


    devolucionEditando =
        null;


    cambiarModoModal(
        false
    );


    modal.classList.add(
        "active"
    );


    document.body.classList.add(
        "modal-open"
    );


    setTimeout(
        function () {

            const venta =
                document.getElementById(
                    "ventaId"
                );


            if (venta) {

                venta.focus();

            }

        },
        100
    );

}


// ======================================================
// ❌ CERRAR MODAL
// ======================================================

function cerrarModalDevolucion() {

    if (guardandoDevolucion) {

        return;

    }


    const modal =
        document.getElementById(
            "modalDevolucion"
        );


    if (!modal) {

        return;

    }


    modal.classList.remove(
        "active"
    );


    document.body.classList.remove(
        "modal-open"
    );


    devolucionEditando =
        null;

}


// ======================================================
// 🔄 CAMBIAR MODO
// ======================================================

function cambiarModoModal(edicion) {

    const titulo =
        document.getElementById(
            "tituloDevolucion"
        );


    const subtitulo =
        document.getElementById(
            "subtituloDevolucion"
        );


    const icono =
        document.getElementById(
            "iconoTituloDevolucion"
        );


    const boton =
        document.getElementById(
            "btnGuardarDevolucion"
        );


    if (edicion) {

        if (titulo) {

            titulo.textContent =
                "Editar devolución";

        }


        if (subtitulo) {

            subtitulo.textContent =
                "Actualiza la información de la devolución.";

        }


        if (icono) {

            icono.className =
                "fa-solid fa-pen";

        }


        if (boton) {

            boton.innerHTML = `

<i class="fa-solid fa-floppy-disk"></i>

<span>
                    Actualizar devolución
                </span>

    `;

        }

    } else {

        if (titulo) {

            titulo.textContent =
                "Nueva devolución";

        }


        if (subtitulo) {

            subtitulo.textContent =
                "Registra una nueva devolución de venta.";

        }


        if (icono) {

            icono.className =
                "fa-solid fa-arrow-rotate-left";

        }


        if (boton) {

            boton.innerHTML = `

<i class="fa-solid fa-floppy-disk"></i>

<span>
                    Guardar devolución
                </span>

    `;

        }

    }

}


// ======================================================
// 💾 GUARDAR / ACTUALIZAR
// ======================================================

async function guardarDevolucion(event) {

    // ==================================================
    // DETENER SUBMIT NORMAL
    // ==================================================

    if (event) {

        event.preventDefault();

        event.stopPropagation();

    }


    // ==================================================
    // 🔒 EVITAR DOBLE REGISTRO
    // ==================================================

    if (guardandoDevolucion) {

        console.warn(
            "⚠️ Guardado ignorado: ya existe una operación en proceso."
        );

        return;

    }


    if (!esAdmin()) {

        mostrarError(
            "No tienes permisos para realizar esta acción."
        );

        return;

    }


    // ==================================================
    // 🔒 BLOQUEAR INMEDIATAMENTE
    // ==================================================

    guardandoDevolucion =
        true;


    const boton =
        document.getElementById(
            "btnGuardarDevolucion"
        );


    if (boton) {

        boton.disabled =
            true;


        boton.innerHTML = `

<i class="fa-solid fa-spinner fa-spin"></i>

<span>
                ${devolucionEditando
    ? "Actualizando..."
    : "Guardando..."}
            </span>

    `;

    }


    try {

        // ==================================================
        // CAMPOS
        // ==================================================

        const ventaId =
            document.getElementById(
                "ventaId"
            )?.value;


        const productoSelect =
            document.getElementById(
                "productoId"
            );


        const productoId =
            productoSelect?.value;


        const cantidad =
            Number(
                document.getElementById(
                    "cantidad"
                )?.value
            );


        const monto =
            obtenerNumeroMoneda(

                document.getElementById(
                    "monto"
                )?.value

            );


        const motivo =
            document.getElementById(
                "motivo"
            )?.value.trim();


        const estado =
            document.getElementById(
                "estado"
            )?.value
            ||
            "ACTIVA";


        // ==================================================
        // VALIDACIONES
        // ==================================================

        if (!ventaId) {

            throw new Error(
                "Debe seleccionar una factura."
            );

        }


        if (!productoId) {

            throw new Error(
                "Debe seleccionar un producto."
            );

        }


        if (!detalleSeleccionado) {

            throw new Error(
                "No se encontró el detalle de la venta."
            );

        }


        const cantidadVendida =
            Number(

                detalleSeleccionado.cantidad
                ||
                detalleSeleccionado.cantidadVendida
                ||
                0

            );


        if (
            !Number.isInteger(cantidad)
            ||
            cantidad <= 0
        ) {

            throw new Error(
                "La cantidad debe ser un número entero mayor que cero."
            );

        }


        if (
            cantidad > cantidadVendida
        ) {

            throw new Error(
                `La cantidad no puede ser mayor a ${cantidadVendida}.`
            );

        }


        if (
            !Number.isFinite(monto)
            ||
            monto < 0
        ) {

            throw new Error(
                "El monto de devolución no es válido."
            );

        }


        // ==================================================
        // DETALLE VENTA
        // ==================================================

        const opcionProducto =
            productoSelect.options[
                productoSelect.selectedIndex
            ];


        const detalleVentaId =
            opcionProducto?.dataset.detalle;


        if (!detalleVentaId) {

            throw new Error(
                "No se encontró el detalle asociado al producto."
            );

        }


        // ==================================================
        // DETERMINAR OPERACIÓN
        // ==================================================

        const idActual =
            devolucionEditando;


        const esEdicion =
            idActual !== null
            &&
            idActual !== undefined;


        const url =
            esEdicion

                ? `/api/devoluciones/${idActual}`

                : "/api/devoluciones";


        const method =
            esEdicion
                ? "PUT"
                : "POST";


        // ==================================================
        // OBJETO
        // ==================================================

        const devolucion = {

            venta: {

                id:
                    Number(ventaId)

            },


            detalleVenta: {

                id:
                    Number(detalleVentaId)

            },


            producto: {

                id:
                    Number(productoId)

            },


            cliente:
                clienteSeleccionado

                    ? {

                        id:
                            Number(
                                clienteSeleccionado
                            )

                    }

                    : null,


            cantidad:
                cantidad,


            monto:
                monto,


            motivo:
                motivo || null,


            estado:
                estado

        };


        console.log(
            "📤 Enviando devolución:",
            {
                url,
                method,
                devolucion
            }
        );


        // ==================================================
        // PETICIÓN
        // ==================================================

        const response =
            await fetchConTimeout(
                url,
                {

                    method:
                        method,

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Accept":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            devolucion
                        )

                }
            );


        // ==================================================
        // ERROR HTTP
        // ==================================================

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        // ==================================================
        // LEER RESPUESTA SI EXISTE
        // ==================================================

        const contentType =
            response.headers.get(
                "content-type"
            )
            || "";


        if (
            contentType.includes(
                "application/json"
            )
        ) {

            await response.json();

        }


        console.log(
            esEdicion
                ? "✅ Devolución actualizada correctamente."
                : "✅ Devolución registrada correctamente."
        );


        // ==================================================
        // 🚨 MUY IMPORTANTE
        // ==================================================
        //
        // AQUÍ LIBERAMOS EL BOTÓN Y CERRAMOS EL MODAL
        // ANTES DE RECARGAR LA TABLA.
        //
        // Así nunca se queda:
        //
        // "Guardando..."
        //
        // porque /api/devoluciones tarde en responder.
        //
        // ==================================================

        guardandoDevolucion =
            false;


        if (boton) {

            boton.disabled =
                false;

        }


        devolucionEditando =
            null;


        cerrarModalForzado();


        // ==================================================
        // MENSAJE DE ÉXITO
        // ==================================================

        await Swal.fire({

            icon:
                "success",

            title:
                esEdicion
                    ? "Devolución actualizada"
                    : "Devolución registrada",

            text:
                esEdicion
                    ? "La devolución fue actualizada correctamente."
                    : "La devolución fue registrada correctamente.",

            confirmButtonText:
                "Aceptar"

        });


        // ==================================================
        // RECARGAR TABLA
        // ==================================================
        //
        // NO bloqueamos el modal esperando esto.
        //
        // ==================================================

        cargarDevoluciones()
            .catch(
                function (error) {

                    console.error(
                        "⚠️ La devolución se guardó, pero no se pudo actualizar la tabla:",
                        error
                    );

                }
            );


        limpiarBuscadorTopbar();


    } catch (error) {

        console.error(
            "❌ Error guardando devolución:",
            error
        );


        // ==================================================
        // LIBERAR BLOQUEO SI HUBO ERROR
        // ==================================================

        guardandoDevolucion =
            false;


        if (boton) {

            boton.disabled =
                false;

        }


        mostrarError(
            error.message
            ||
            "No fue posible guardar la devolución."
        );


        // ==================================================
        // RESTAURAR BOTÓN
        // ==================================================

        cambiarModoModal(
            Boolean(
                devolucionEditando
            )
        );

    }

}


// ======================================================
// ✏️ EDITAR DEVOLUCION
// ======================================================

async function editarDevolucion(id) {

    if (!esAdmin()) {

        mostrarError(
            "No tienes permisos para editar devoluciones."
        );

        return;

    }


    if (guardandoDevolucion) {

        return;

    }


    const devolucion =
        devolucionesGlobal.find(
            function (item) {

                return Number(item.id)
                    ===
                    Number(id);

            }
        );


    if (!devolucion) {

        mostrarError(
            "No se encontró la devolución."
        );

        return;

    }


    console.log(
        "✏️ Editando devolución:",
        devolucion
    );


    // ==================================================
    // ABRIR
    // ==================================================

    abrirModalDevolucion();


    devolucionEditando =
        Number(id);


    const idInput =
        document.getElementById(
            "devolucionId"
        );


    if (idInput) {

        idInput.value =
            devolucionEditando;

    }


    cambiarModoModal(
        true
    );


    // ==================================================
    // VENTA
    // ==================================================

    const ventaId =
        devolucion.venta?.id
        ||
        devolucion.ventaId;


    const ventaSelect =
        document.getElementById(
            "ventaId"
        );


    if (
        ventaSelect
        &&
        ventaId
    ) {

        ventaSelect.value =
            String(ventaId);


        await cargarDatosFactura();

    }


    // ==================================================
    // PRODUCTO
    // ==================================================

    const productoId =
        devolucion.producto?.id
        ||
        devolucion.productoId
        ||
        devolucion.detalleVenta?.producto?.id;


    const productoSelect =
        document.getElementById(
            "productoId"
        );


    if (
        productoSelect
        &&
        productoId
    ) {

        productoSelect.value =
            String(productoId);


        cargarDatosProducto();

    }


    // ==================================================
    // CANTIDAD
    // ==================================================

    const cantidadInput =
        document.getElementById(
            "cantidad"
        );


    if (cantidadInput) {

        cantidadInput.value =
            Number(
                devolucion.cantidad
                || 1
            );


        cantidadInput.disabled =
            false;

    }


    // ==================================================
    // MONTO
    // ==================================================

    const montoInput =
        document.getElementById(
            "monto"
        );


    if (montoInput) {

        montoInput.value =
            formatearMoneda(
                Number(
                    devolucion.monto
                    || 0
                )
            );

    }


    // ==================================================
    // MOTIVO
    // ==================================================

    const motivoInput =
        document.getElementById(
            "motivo"
        );


    if (motivoInput) {

        motivoInput.value =
            devolucion.motivo
            || "";

    }


    // ==================================================
    // ESTADO
    // ==================================================

    const estadoSelect =
        document.getElementById(
            "estado"
        );


    if (estadoSelect) {

        estadoSelect.value =
            devolucion.estado
            ||
            "ACTIVA";

    }


    calcularMonto();

}


// ======================================================
// 🗑️ ELIMINAR DEVOLUCION
// ======================================================

async function eliminarDevolucion(id) {

    if (!esAdmin()) {

        mostrarError(
            "No tienes permisos para eliminar devoluciones."
        );

        return;

    }


    if (eliminandoDevolucion) {

        return;

    }


    const resultado =
        await Swal.fire({

            icon:
                "warning",

            title:
                "¿Eliminar devolución?",

            text:
                "Esta acción eliminará el registro de devolución.",

            showCancelButton:
                true,

            confirmButtonText:
                "Sí, eliminar",

            cancelButtonText:
                "Cancelar",

            reverseButtons:
                true

        });


    if (
        !resultado.isConfirmed
    ) {

        return;

    }


    eliminandoDevolucion =
        true;


    try {

        const response =
            await fetchConTimeout(
                `/api/devoluciones/${id}`,
                {

                    method:
                        "DELETE"

                }
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        eliminandoDevolucion =
            false;


        await Swal.fire({

            icon:
                "success",

            title:
                "Eliminada",

            text:
                "La devolución fue eliminada correctamente.",

            confirmButtonText:
                "Aceptar"

        });


        cargarDevoluciones()
            .catch(
                function (error) {

                    console.error(
                        "⚠️ Error actualizando tabla:",
                        error
                    );

                }
            );


        limpiarBuscadorTopbar();


    } catch (error) {

        eliminandoDevolucion =
            false;


        console.error(
            "❌ Error eliminando devolución:",
            error
        );


        mostrarError(
            error.message
            ||
            "No fue posible eliminar la devolución."
        );

    }

}


// ======================================================
// 🧹 LIMPIAR FORMULARIO
// ======================================================

function limpiarFormulario() {

    const form =
        document.getElementById(
            "formDevolucion"
        );


    if (form) {

        form.reset();

    }


    const id =
        document.getElementById(
            "devolucionId"
        );


    if (id) {

        id.value = "";

    }


    const estado =
        document.getElementById(
            "estado"
        );


    if (estado) {

        estado.value =
            "ACTIVA";

    }


    clienteSeleccionado =
        null;


    detalleSeleccionado =
        null;


    detallesVenta =
        [];


    const producto =
        document.getElementById(
            "productoId"
        );


    if (producto) {

        producto.innerHTML = `

<option value="">
    Seleccione un producto
</option>

    `;


        producto.value =
            "";


        producto.disabled =
            true;

    }


    const cliente =
        document.getElementById(
            "clienteNombre"
        );


    if (cliente) {

        cliente.value =
            "";

    }


    const cantidad =
        document.getElementById(
            "cantidad"
        );


    if (cantidad) {

        cantidad.value =
            "";


        cantidad.disabled =
            true;


        cantidad.removeAttribute(
            "max"
        );

    }


    const monto =
        document.getElementById(
            "monto"
        );


    if (monto) {

        monto.value =
            "";

    }


    const disponibilidad =
        document.getElementById(
            "cantidadDisponible"
        );


    if (disponibilidad) {

        disponibilidad.textContent =
            "Seleccione un producto.";

    }


    cambiarModoModal(
        false
    );

}


// ======================================================
// 🔒 CERRAR MODAL FORZADO
// ======================================================

function cerrarModalForzado() {

    const modal =
        document.getElementById(
            "modalDevolucion"
        );


    if (!modal) {

        return;

    }


    modal.classList.remove(
        "active"
    );


    document.body.classList.remove(
        "modal-open"
    );


    limpiarFormulario();


    devolucionEditando =
        null;

}


// ======================================================
// 💰 FORMATEAR MONEDA
// ======================================================

function formatearMoneda(valor) {

    return new Intl.NumberFormat(
        "es-CO",
        {

            style:
                "currency",

            currency:
                "COP",

            minimumFractionDigits:
                0,

            maximumFractionDigits:
                0

        }
    ).format(
        Number(valor) || 0
    );

}


// ======================================================
// 💰 OBTENER NUMERO MONEDA
// ======================================================

function obtenerNumeroMoneda(valor) {

    if (
        valor === null
        ||
        valor === undefined
        ||
        valor === ""
    ) {

        return 0;

    }


    if (
        typeof valor === "number"
    ) {

        return valor;

    }


    const limpio =
        String(valor)

            .replace(
                /[^\d,-]/g,
                ""
            )

            .replace(
                /\./g,
                ""
            )

            .replace(
                ",",
                "."
            );


    const numero =
        Number(limpio);


    return Number.isFinite(numero)
        ? numero
        : 0;

}


// ======================================================
// 📅 FECHA
// ======================================================

function formatearFecha(fecha) {

    if (!fecha) {

        return "-";

    }


    try {

        const date =
            new Date(fecha);


        if (
            Number.isNaN(
                date.getTime()
            )
        ) {

            return String(fecha)
                .substring(
                    0,
                    10
                );

        }


        return date.toLocaleDateString(
            "es-CO",
            {

                year:
                    "numeric",

                month:
                    "2-digit",

                day:
                    "2-digit"

            }
        );

    } catch (error) {

        return String(fecha)
            .substring(
                0,
                10
            );

    }

}


// ======================================================
// 🛡️ ESCAPAR HTML
// ======================================================

function escapeHtml(texto) {

    if (
        texto === null
        ||
        texto === undefined
    ) {

        return "";

    }


    return String(texto)

        .replace(
            /&/g,
            "&amp;"
        )

        .replace(
            /</g,
            "&lt;"
        )

        .replace(
            />/g,
            "&gt;"
        )

        .replace(
            /"/g,
            "&quot;"
        )

        .replace(
            /'/g,
            "&#039;"
        );

}


// ======================================================
// ❌ OBTENER MENSAJE ERROR
// ======================================================

async function obtenerMensajeError(
    response
) {

    try {

        const texto =
            await response.text();


        if (!texto) {

            return `Error HTTP ${response.status}`;

        }


        try {

            const json =
                JSON.parse(texto);


            return (

                json.message

                ||

                json.error

                ||

                json.mensaje

                ||

                `Error HTTP ${response.status}`

            );

        } catch (errorJson) {

            return texto;

        }

    } catch (error) {

        return `Error HTTP ${response.status}`;

    }

}


// ======================================================
// ⚠️ MOSTRAR ERROR
// ======================================================

function mostrarError(mensaje) {

    Swal.fire({

        icon:
            "error",

        title:
            "Ocurrió un problema",

        text:
            mensaje,

        confirmButtonText:
            "Aceptar"

    });

}


// ======================================================
// 🔎 LIMPIAR BUSCADOR
// ======================================================

function limpiarBuscadorTopbar() {

    const buscarTopbar =
        document.getElementById(
            "buscarTopbar"
        );


    if (buscarTopbar) {

        buscarTopbar.value =
            "";

    }

}


// ======================================================
// ⏱️ FETCH CON TIMEOUT
// ======================================================

async function fetchConTimeout(
    url,
    opciones = {},
    tiempo = TIMEOUT_PETICION
) {

    const controller =
        new AbortController();


    const timeout =
        setTimeout(
            function () {

                controller.abort();

            },
            tiempo
        );


    try {

        return await fetch(
            url,
            {
                ...opciones,
                signal:
                    controller.signal
            }
        );

    } catch (error) {

        if (
            error.name === "AbortError"
        ) {

            throw new Error(
                "El servidor está tardando demasiado en responder."
            );

        }


        throw error;

    } finally {

        clearTimeout(
            timeout
        );

    }

}
