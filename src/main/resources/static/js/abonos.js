// ============================================================
// ABONOS | MARAV
// ============================================================
//
// Módulo encargado de:
// - Listar abonos
// - Registrar abonos
// - Editar abonos
// - Eliminar abonos
// - Mostrar clientes con deuda
// - Mostrar facturas pendientes por cliente
// - Buscar abonos desde el TOPBAR
// - Actualizar KPIs
// - Controlar permisos ADMIN / EMPLEADO
//
// IMPORTANTE:
// El negocio NO se envía desde JavaScript.
// El backend obtiene negocioId desde CustomUserDetails.
//
// ============================================================


// ============================================================
// VARIABLES GLOBALES
// ============================================================

let abonos = [];

let clientesConDeuda = [];

let facturasCliente = [];


// ============================================================
// CONTROL DE OPERACIONES
// ============================================================
//
// Evita que un doble clic o un doble submit cree dos abonos.
//

let guardandoAbono = false;

let actualizandoAbono = false;

let eliminandoAbono = false;


// ============================================================
// CONTROL DE INICIALIZACIÓN
// ============================================================

let abonosInicializado = false;


// ============================================================
// INICIO DEL MÓDULO
// ============================================================

document.addEventListener("DOMContentLoaded", async function () {

    // --------------------------------------------------------
    // Evitar inicialización duplicada
    // --------------------------------------------------------

    if (abonosInicializado) {

        return;

    }

    abonosInicializado = true;


    console.log("💰 Abonos | MARAV iniciado");


    // --------------------------------------------------------
    // Buscador global del TOPBAR
    // --------------------------------------------------------

    configurarBuscadorTopbar();


    // --------------------------------------------------------
    // Tecla ESC
    // --------------------------------------------------------

    document.addEventListener(
        "keydown",
        function (event) {

            if (event.key === "Escape") {

                cerrarModalAbono();

            }

        }
    );


    // --------------------------------------------------------
    // Cerrar modal haciendo clic fuera
    // --------------------------------------------------------

    const modal =
        document.getElementById("modalAbono");


    if (modal) {

        modal.addEventListener(
            "click",
            function (event) {

                if (event.target === modal) {

                    cerrarModalAbono();

                }

            }
        );

    }


    // --------------------------------------------------------
    // Eventos del formulario
    // --------------------------------------------------------

    configurarFormulario();


    // --------------------------------------------------------
    // Cargar historial
    // --------------------------------------------------------

    await cargarAbonos();


    // --------------------------------------------------------
    // Si ADMIN, cargar clientes con deuda
    // --------------------------------------------------------

    if (esAdmin()) {

        await cargarClientesConDeuda();

    }

});


// ============================================================
// CONTROL DE ROL
// ============================================================

function esAdmin() {

    return String(
        typeof ROL_USUARIO !== "undefined"
            ? ROL_USUARIO
            : ""
    )
        .trim()
        .toUpperCase() === "ADMIN";

}


// ============================================================
// CONFIGURAR BUSCADOR TOPBAR
// ============================================================

function configurarBuscadorTopbar() {

    const buscador =
        document.getElementById("buscarTopbar");


    if (!buscador) {

        console.warn(
            "⚠️ No se encontró #buscarTopbar"
        );

        return;

    }


    // --------------------------------------------------------
    // Evitar eventos duplicados
    // --------------------------------------------------------

    if (
        buscador.dataset.abonosConfigurado === "true"
    ) {

        return;

    }


    buscador.dataset.abonosConfigurado =
        "true";


    // --------------------------------------------------------
    // Placeholder
    // --------------------------------------------------------

    buscador.placeholder =
        "Buscar abonos...";


    // --------------------------------------------------------
    // Buscar
    // --------------------------------------------------------

    buscador.addEventListener(
        "input",
        function () {

            filtrarAbonos(
                this.value
            );

        }
    );

}


// ============================================================
// CONFIGURAR FORMULARIO
// ============================================================

function configurarFormulario() {

    const selectCliente =
        document.getElementById("idCliente");


    const selectVenta =
        document.getElementById("idVenta");


    const formulario =
        document.getElementById("formAbono");


    const btnActualizar =
        document.getElementById(
            "btnActualizarAbono"
        );


    // --------------------------------------------------------
    // Cambio de cliente
    // --------------------------------------------------------

    if (selectCliente) {

        selectCliente.addEventListener(
            "change",
            function () {

                cargarFacturasCliente(
                    this.value
                );

            }
        );

    }


    // --------------------------------------------------------
    // Cambio de factura
    // --------------------------------------------------------

    if (selectVenta) {

        selectVenta.addEventListener(
            "change",
            function () {

                actualizarFacturaSeleccionada();

            }
        );

    }


    // --------------------------------------------------------
    // Submit del formulario
    // --------------------------------------------------------

    if (formulario) {

        formulario.addEventListener(
            "submit",
            function (event) {

                event.preventDefault();


                // --------------------------------------------
                // Evitar doble envío
                // --------------------------------------------

                if (
                    guardandoAbono ||
                    actualizandoAbono
                ) {

                    return;

                }


                const id =
                    document.getElementById(
                        "abonoId"
                    )?.value;


                if (id) {

                    actualizarAbono();

                } else {

                    guardarAbono();

                }

            }
        );

    }


    // --------------------------------------------------------
    // Botón actualizar
    //
    // IMPORTANTE:
    // Si este botón es type="submit", el click llegará
    // también al submit del formulario.
    //
    // Por eso NO llamamos actualizarAbono() aquí.
    // El submit se encarga de hacerlo.
    // --------------------------------------------------------

    if (btnActualizar) {

        btnActualizar.type = "submit";

    }

}


// ============================================================
// CARGAR ABONOS
// ============================================================

async function cargarAbonos() {

    try {

        const response =
            await fetch(
                "/api/abonos"
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        abonos =
            await response.json();


        if (!Array.isArray(abonos)) {

            abonos = [];

        }


        console.log(
            "💰 Abonos cargados:",
            abonos
        );


        renderAbonos(abonos);

        actualizarKPIs();


    } catch (error) {

        console.error(
            "❌ Error cargando abonos:",
            error
        );


        const tabla =
            document.getElementById(
                "tablaAbonos"
            );


        if (tabla) {

            tabla.innerHTML = "";

        }


        const vacio =
            document.getElementById(
                "abonosVacio"
            );


        if (vacio) {

            vacio.style.display =
                "block";


            vacio.innerHTML = `

                <i class="fa-solid fa-triangle-exclamation"></i>

                <span>
                    No fue posible cargar los abonos.
                </span>

            `;

        }


        Swal.fire({

            icon: "error",

            title: "Error",

            text:
                error.message ||
                "No fue posible cargar los abonos."

        });

    }

}


// ============================================================
// ACTUALIZAR DATOS DESPUÉS DE GUARDAR
// ============================================================
//
// Esta función NO debe bloquear el cierre del modal.
//
// Se ejecuta en segundo plano.
//

async function refrescarDatosAbonos() {

    try {

        await cargarAbonos();

    } catch (error) {

        console.error(
            "❌ Error actualizando historial de abonos:",
            error
        );

    }


    if (esAdmin()) {

        try {

            await cargarClientesConDeuda();

        } catch (error) {

            console.error(
                "❌ Error actualizando clientes con deuda:",
                error
            );

        }

    }

}


// ============================================================
// RENDERIZAR ABONOS
// ============================================================

function renderAbonos(lista) {

    const tabla =
        document.getElementById(
            "tablaAbonos"
        );


    const vacio =
        document.getElementById(
            "abonosVacio"
        );


    if (!tabla) {

        return;

    }


    tabla.innerHTML = "";


    if (vacio) {

        vacio.style.display =
            "none";

    }


    if (
        !lista ||
        lista.length === 0
    ) {

        if (vacio) {

            vacio.style.display =
                "block";

        }

        return;

    }


    lista.forEach(
        function (abono) {

            const id =
                abono.id;


            const cliente =
                obtenerNombreCliente(
                    abono
                );


            const factura =
                obtenerNumeroFactura(
                    abono
                );


            const monto =
                Number(
                    abono.monto || 0
                );


            const fecha =
                formatearFecha(
                    abono.fecha
                );


            const descripcion =
                abono.descripcion ||
                "Sin descripción";


            const fila =
                document.createElement(
                    "tr"
                );


            let html = `

                <td>

                    <div class="abono-cliente">

                        <div class="abono-avatar">

                            <i class="fa-solid fa-user"></i>

                        </div>

                        <span>
                            ${escapeHTML(cliente)}
                        </span>

                    </div>

                </td>


                <td>

                    <span class="abono-factura">

                        ${escapeHTML(factura)}

                    </span>

                </td>


                <td>

                    <span class="abono-monto">

                        ${formatearPrecio(monto)}

                    </span>

                </td>


                <td>

                    <span class="abono-fecha">

                        ${escapeHTML(fecha)}

                    </span>

                </td>


                <td>

                    <div
                        class="abono-descripcion"
                        title="${escapeHTML(descripcion)}"
                    >

                        ${escapeHTML(descripcion)}

                    </div>

                </td>

            `;


            // ------------------------------------------------
            // ACCIONES ADMIN
            // ------------------------------------------------

            if (esAdmin()) {

                html += `

                    <td>

                        <div class="abono-actions">

                            <button
                                type="button"
                                class="abono-action-btn abono-btn-edit"
                                title="Editar abono"
                                onclick="editarAbono(${Number(id)})"
                            >

                                <i class="fa-solid fa-pen"></i>

                            </button>


                            <button
                                type="button"
                                class="abono-action-btn abono-btn-delete"
                                title="Eliminar abono"
                                onclick="eliminarAbono(${Number(id)})"
                            >

                                <i class="fa-solid fa-trash"></i>

                            </button>

                        </div>

                    </td>

                `;

            }


            fila.innerHTML =
                html;


            tabla.appendChild(
                fila
            );

        }
    );

}


// ============================================================
// OBTENER NOMBRE CLIENTE
// ============================================================

function obtenerNombreCliente(abono) {

    if (
        abono?.cliente &&
        abono.cliente.nombre
    ) {

        return abono.cliente.nombre;

    }


    if (abono?.nombreCliente) {

        return abono.nombreCliente;

    }


    if (
        abono?.idCliente &&
        clientesConDeuda.length
    ) {

        const cliente =
            clientesConDeuda.find(
                function (item) {

                    return Number(item.id) ===
                        Number(abono.idCliente);

                }
            );


        if (cliente) {

            return cliente.nombre;

        }

    }


    return "Consumidor Final";

}


// ============================================================
// OBTENER NUMERO FACTURA
// ============================================================

function obtenerNumeroFactura(abono) {

    if (
        abono?.venta &&
        abono.venta.numeroFactura
    ) {

        return abono.venta.numeroFactura;

    }


    if (abono?.numeroFactura) {

        return abono.numeroFactura;

    }


    if (abono?.idVenta) {

        return "Venta #" +
            abono.idVenta;

    }


    return "Sin factura";

}


// ============================================================
// ACTUALIZAR KPIs
// ============================================================

function actualizarKPIs() {

    // --------------------------------------------------------
    // TOTAL ABONOS
    // --------------------------------------------------------

    const total =
        document.getElementById(
            "totalAbonos"
        );


    if (total) {

        total.textContent =
            abonos.length;

    }


    // --------------------------------------------------------
    // TOTAL RECAUDADO
    // --------------------------------------------------------

    let suma =
        0;


    abonos.forEach(
        function (abono) {

            suma += Number(
                abono.monto || 0
            );

        }
    );


    const totalRecaudado =
        document.getElementById(
            "totalRecaudado"
        );


    if (totalRecaudado) {

        totalRecaudado.textContent =
            formatearPrecio(
                suma
            );

    }


    // --------------------------------------------------------
    // CLIENTES CON DEUDA
    // --------------------------------------------------------

    const clientes =
        document.getElementById(
            "clientesConDeuda"
        );


    if (clientes) {

        if (
            clientesConDeuda.length > 0
        ) {

            clientes.textContent =
                clientesConDeuda.length;

        } else {

            const ids =
                new Set();


            abonos.forEach(
                function (abono) {

                    if (abono.idCliente) {

                        ids.add(
                            abono.idCliente
                        );

                    }

                }
            );


            clientes.textContent =
                ids.size;

        }

    }

}


// ============================================================
// CARGAR CLIENTES CON DEUDA
// ============================================================

async function cargarClientesConDeuda() {

    const select =
        document.getElementById(
            "idCliente"
        );


    if (!select) {

        return;

    }


    try {

        const response =
            await fetch(
                "/api/abonos/clientes-deuda"
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        clientesConDeuda =
            await response.json();


        if (
            !Array.isArray(
                clientesConDeuda
            )
        ) {

            clientesConDeuda = [];

        }


        console.log(
            "👤 Clientes con deuda:",
            clientesConDeuda
        );


        llenarSelectClientes();

        actualizarKPIs();


    } catch (error) {

        console.error(
            "❌ Error cargando clientes:",
            error
        );


        Swal.fire({

            icon: "error",

            title: "Error",

            text:
                error.message ||
                "No fue posible cargar los clientes con deuda."

        });

    }

}


// ============================================================
// LLENAR SELECT CLIENTES
// ============================================================

function llenarSelectClientes() {

    const select =
        document.getElementById(
            "idCliente"
        );


    if (!select) {

        return;

    }


    select.innerHTML = "";


    const inicial =
        document.createElement(
            "option"
        );


    inicial.value =
        "";


    inicial.textContent =
        "Seleccione un cliente con deuda";


    select.appendChild(
        inicial
    );


    clientesConDeuda.forEach(
        function (cliente) {

            const option =
                document.createElement(
                    "option"
                );


            option.value =
                cliente.id;


            option.textContent =
                cliente.nombre +
                " — Deuda: " +
                formatearPrecio(
                    cliente.deuda
                );


            option.dataset.deuda =
                cliente.deuda || 0;


            select.appendChild(
                option
            );

        }
    );

}


// ============================================================
// CARGAR FACTURAS DEL CLIENTE
// ============================================================

async function cargarFacturasCliente(
    idCliente
) {

    const selectVenta =
        document.getElementById(
            "idVenta"
        );


    const monto =
        document.getElementById(
            "monto"
        );


    if (!selectVenta) {

        return;

    }


    // --------------------------------------------------------
    // Limpiar estado anterior
    // --------------------------------------------------------

    selectVenta.innerHTML = "";


    if (monto) {

        monto.value = "";

        monto.disabled =
            true;

    }


    limpiarResumenFactura();


    if (!idCliente) {

        selectVenta.disabled =
            true;


        const option =
            document.createElement(
                "option"
            );


        option.value =
            "";


        option.textContent =
            "Seleccione primero un cliente";


        selectVenta.appendChild(
            option
        );


        return;

    }


    // --------------------------------------------------------
    // Mostrar deuda del cliente
    // --------------------------------------------------------

    const selectCliente =
        document.getElementById(
            "idCliente"
        );


    if (selectCliente) {

        const optionSeleccionada =
            selectCliente.options[
                selectCliente.selectedIndex
                ];


        const deuda =
            Number(
                optionSeleccionada?.dataset.deuda ||
                0
            );


        const texto =
            document.getElementById(
                "clienteDeudaTexto"
            );


        if (texto) {

            texto.textContent =
                "Deuda actual: " +
                formatearPrecio(
                    deuda
                );

        }

    }


    try {

        const response =
            await fetch(
                `/api/abonos/facturas-cliente/${idCliente}`
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        facturasCliente =
            await response.json();


        if (
            !Array.isArray(
                facturasCliente
            )
        ) {

            facturasCliente = [];

        }


        console.log(
            "🧾 Facturas del cliente:",
            facturasCliente
        );


        selectVenta.disabled =
            false;


        llenarSelectFacturas();


    } catch (error) {

        console.error(
            "❌ Error cargando facturas:",
            error
        );


        selectVenta.disabled =
            true;


        const option =
            document.createElement(
                "option"
            );


        option.value =
            "";


        option.textContent =
            "No fue posible cargar las facturas";


        selectVenta.appendChild(
            option
        );


        Swal.fire({

            icon: "error",

            title: "Error",

            text:
                error.message ||
                "No fue posible cargar las facturas del cliente."

        });

    }

}


// ============================================================
// LLENAR SELECT FACTURAS
// ============================================================

function llenarSelectFacturas() {

    const select =
        document.getElementById(
            "idVenta"
        );


    if (!select) {

        return;

    }


    select.innerHTML = "";


    const inicial =
        document.createElement(
            "option"
        );


    inicial.value =
        "";


    inicial.textContent =
        "Seleccione una factura";


    select.appendChild(
        inicial
    );


    if (
        !facturasCliente ||
        facturasCliente.length === 0
    ) {

        inicial.textContent =
            "Este cliente no tiene facturas pendientes";


        select.disabled =
            true;


        return;

    }


    facturasCliente.forEach(
        function (venta) {

            const option =
                document.createElement(
                    "option"
                );


            option.value =
                venta.id;


            const numeroFactura =
                venta.numeroFactura ||
                "Factura #" +
                venta.id;


            const total =
                Number(
                    venta.total || 0
                );


            option.textContent =
                numeroFactura +
                " — " +
                formatearPrecio(
                    total
                );


            option.dataset.total =
                total;


            option.dataset.numeroFactura =
                numeroFactura;


            select.appendChild(
                option
            );

        }
    );

}


// ============================================================
// FACTURA SELECCIONADA
// ============================================================

function actualizarFacturaSeleccionada() {

    const selectVenta =
        document.getElementById(
            "idVenta"
        );


    const monto =
        document.getElementById(
            "monto"
        );


    const textoSaldo =
        document.getElementById(
            "facturaSaldoTexto"
        );


    const resumenFactura =
        document.getElementById(
            "resumenFactura"
        );


    const resumenSaldo =
        document.getElementById(
            "resumenSaldo"
        );


    if (!selectVenta) {

        return;

    }


    const option =
        selectVenta.options[
            selectVenta.selectedIndex
            ];


    if (
        !option ||
        !option.value
    ) {

        if (monto) {

            monto.value = "";

            monto.disabled =
                true;

        }


        if (textoSaldo) {

            textoSaldo.textContent =
                "Seleccione una factura.";

        }


        if (resumenFactura) {

            resumenFactura.textContent =
                "—";

        }


        if (resumenSaldo) {

            resumenSaldo.textContent =
                "$0";

        }


        return;

    }


    const total =
        Number(
            option.dataset.total || 0
        );


    const factura =
        option.dataset.numeroFactura ||
        option.textContent;


    if (monto) {

        monto.disabled =
            false;

        monto.max =
            total;

    }


    if (textoSaldo) {

        textoSaldo.textContent =
            "Saldo de la factura: " +
            formatearPrecio(
                total
            );

    }


    if (resumenFactura) {

        resumenFactura.textContent =
            factura;

    }


    if (resumenSaldo) {

        resumenSaldo.textContent =
            formatearPrecio(
                total
            );

    }


    actualizarResumenCliente();

}


// ============================================================
// ACTUALIZAR RESUMEN CLIENTE
// ============================================================

function actualizarResumenCliente() {

    const selectCliente =
        document.getElementById(
            "idCliente"
        );


    const resumenCliente =
        document.getElementById(
            "resumenCliente"
        );


    if (
        !selectCliente ||
        !resumenCliente
    ) {

        return;

    }


    const option =
        selectCliente.options[
            selectCliente.selectedIndex
            ];


    resumenCliente.textContent =
        option?.value
            ? option.textContent.split(
                " — Deuda:"
            )[0]
            : "—";

}


// ============================================================
// LIMPIAR RESUMEN FACTURA
// ============================================================

function limpiarResumenFactura() {

    const textoSaldo =
        document.getElementById(
            "facturaSaldoTexto"
        );


    const resumenFactura =
        document.getElementById(
            "resumenFactura"
        );


    const resumenSaldo =
        document.getElementById(
            "resumenSaldo"
        );


    if (textoSaldo) {

        textoSaldo.textContent =
            "Las facturas se cargarán al seleccionar el cliente.";

    }


    if (resumenFactura) {

        resumenFactura.textContent =
            "—";

    }


    if (resumenSaldo) {

        resumenSaldo.textContent =
            "$0";

    }


    actualizarResumenCliente();

}


// ============================================================
// BUSCAR ABONOS
// ============================================================

function filtrarAbonos(texto) {

    const termino =
        normalizarTexto(
            texto
        );


    if (!termino) {

        renderAbonos(
            abonos
        );

        return;

    }


    const resultados =
        abonos.filter(
            function (abono) {

                const cliente =
                    normalizarTexto(
                        obtenerNombreCliente(
                            abono
                        )
                    );


                const factura =
                    normalizarTexto(
                        obtenerNumeroFactura(
                            abono
                        )
                    );


                const descripcion =
                    normalizarTexto(
                        abono.descripcion
                    );


                const monto =
                    normalizarTexto(
                        abono.monto
                    );


                return (

                    cliente.includes(
                        termino
                    ) ||

                    factura.includes(
                        termino
                    ) ||

                    descripcion.includes(
                        termino
                    ) ||

                    monto.includes(
                        termino
                    )

                );

            }
        );


    renderAbonos(
        resultados
    );


    if (
        resultados.length === 0
    ) {

        const vacio =
            document.getElementById(
                "abonosVacio"
            );


        if (vacio) {

            vacio.style.display =
                "block";


            vacio.innerHTML = `

                <i class="fa-solid fa-magnifying-glass"></i>

                <span>
                    No se encontraron abonos para
                    "<strong>${escapeHTML(texto)}</strong>".
                </span>

            `;

        }

    }

}


// ============================================================
// ABRIR MODAL NUEVO
// ============================================================

function abrirModalAbono() {

    if (!esAdmin()) {

        return;

    }


    // --------------------------------------------------------
    // No permitir abrir mientras se está guardando
    // --------------------------------------------------------

    if (
        guardandoAbono ||
        actualizandoAbono
    ) {

        return;

    }


    const modal =
        document.getElementById(
            "modalAbono"
        );


    if (!modal) {

        return;

    }


    limpiarFormularioAbono();


    cambiarModoModalAbono(
        "nuevo"
    );


    modal.classList.add(
        "active"
    );


    modal.setAttribute(
        "aria-hidden",
        "false"
    );


    document.body.style.overflow =
        "hidden";


    setTimeout(
        function () {

            document.getElementById(
                "idCliente"
            )?.focus();

        },
        100
    );

}


// ============================================================
// EDITAR ABONO
// ============================================================

async function editarAbono(id) {

    if (!esAdmin()) {

        return;

    }


    if (
        guardandoAbono ||
        actualizandoAbono ||
        eliminandoAbono
    ) {

        return;

    }


    try {

        const response =
            await fetch(
                `/api/abonos/${id}`
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        const abono =
            await response.json();


        // ----------------------------------------------------
        // Abrir modal
        // ----------------------------------------------------

        const modal =
            document.getElementById(
                "modalAbono"
            );


        if (!modal) {

            return;

        }


        limpiarFormularioAbono();


        // ----------------------------------------------------
        // ID
        // ----------------------------------------------------

        const campoId =
            document.getElementById(
                "abonoId"
            );


        if (campoId) {

            campoId.value =
                abono.id || "";

        }


        // ----------------------------------------------------
        // Seleccionar cliente
        // ----------------------------------------------------

        const clienteSelect =
            document.getElementById(
                "idCliente"
            );


        if (clienteSelect) {

            clienteSelect.value =
                abono.idCliente || "";

        }


        // ----------------------------------------------------
        // Cargar facturas del cliente
        // ----------------------------------------------------

        await cargarFacturasCliente(
            abono.idCliente
        );


        // ----------------------------------------------------
        // Seleccionar factura
        // ----------------------------------------------------

        const ventaSelect =
            document.getElementById(
                "idVenta"
            );


        if (ventaSelect) {

            ventaSelect.value =
                abono.idVenta || "";


            actualizarFacturaSeleccionada();

        }


        // ----------------------------------------------------
        // Monto
        // ----------------------------------------------------

        const campoMonto =
            document.getElementById(
                "monto"
            );


        if (campoMonto) {

            campoMonto.value =
                abono.monto ?? "";

        }


        // ----------------------------------------------------
        // Descripción
        // ----------------------------------------------------

        const campoDescripcion =
            document.getElementById(
                "descripcion"
            );


        if (campoDescripcion) {

            campoDescripcion.value =
                abono.descripcion || "";

        }


        // ----------------------------------------------------
        // Modo edición
        // ----------------------------------------------------

        cambiarModoModalAbono(
            "editar"
        );


        // ----------------------------------------------------
        // Mostrar modal
        // ----------------------------------------------------

        modal.classList.add(
            "active"
        );


        modal.setAttribute(
            "aria-hidden",
            "false"
        );


        document.body.style.overflow =
            "hidden";


    } catch (error) {

        console.error(
            "❌ Error obteniendo abono:",
            error
        );


        Swal.fire({

            icon: "error",

            title: "Error",

            text:
                error.message ||
                "No fue posible cargar el abono."

        });

    }

}


// ============================================================
// CAMBIAR MODO MODAL
// ============================================================

function cambiarModoModalAbono(modo) {

    const titulo =
        document.getElementById(
            "tituloFormularioAbono"
        );


    const subtitulo =
        document.getElementById(
            "subtituloFormularioAbono"
        );


    const btnGuardar =
        document.getElementById(
            "btnGuardarAbono"
        );


    const btnActualizar =
        document.getElementById(
            "btnActualizarAbono"
        );


    if (modo === "nuevo") {

        if (titulo) {

            titulo.innerHTML = `

                <i class="fa-solid fa-hand-holding-dollar"></i>

                Nuevo Abono

            `;

        }


        if (subtitulo) {

            subtitulo.textContent =
                "Registra un nuevo pago de un cliente con deuda";

        }


        if (btnGuardar) {

            btnGuardar.style.display =
                "inline-flex";

            btnGuardar.disabled =
                false;

            btnGuardar.innerHTML = `

                <i class="fa-solid fa-floppy-disk"></i>

                Guardar Abono

            `;

        }


        if (btnActualizar) {

            btnActualizar.style.display =
                "none";

            btnActualizar.disabled =
                false;

        }


        return;

    }


    // --------------------------------------------------------
    // EDITAR
    // --------------------------------------------------------

    if (titulo) {

        titulo.innerHTML = `

            <i class="fa-solid fa-hand-holding-dollar"></i>

            Editar Abono

        `;

    }


    if (subtitulo) {

        subtitulo.textContent =
            "Actualiza la información del abono";

    }


    if (btnGuardar) {

        btnGuardar.style.display =
            "none";

        btnGuardar.disabled =
            false;

    }


    if (btnActualizar) {

        btnActualizar.style.display =
            "inline-flex";

        btnActualizar.disabled =
            false;

        btnActualizar.innerHTML = `

            <i class="fa-solid fa-rotate"></i>

            Actualizar Abono

        `;

    }

}


// ============================================================
// OBTENER DATOS FORMULARIO
// ============================================================

function obtenerDatosFormularioAbono() {

    const clienteTexto =
        document.getElementById(
            "idCliente"
        )?.value || "";


    const ventaTexto =
        document.getElementById(
            "idVenta"
        )?.value || "";


    const montoTexto =
        document.getElementById(
            "monto"
        )?.value || "";


    const descripcion =
        document.getElementById(
            "descripcion"
        )?.value
            .trim() || "";


    return {

        idCliente:
            clienteTexto
                ? parseInt(
                    clienteTexto,
                    10
                )
                : null,

        idVenta:
            ventaTexto
                ? parseInt(
                    ventaTexto,
                    10
                )
                : null,

        monto:
            Number(montoTexto),

        descripcion:
        descripcion

    };

}


// ============================================================
// VALIDAR FORMULARIO
// ============================================================

function validarFormularioAbono(
    datos
) {

    if (!datos.idCliente) {

        Swal.fire({

            icon: "warning",

            title: "Cliente requerido",

            text:
                "Seleccione un cliente con deuda."

        });

        return false;

    }


    if (!datos.idVenta) {

        Swal.fire({

            icon: "warning",

            title: "Factura requerida",

            text:
                "Seleccione una factura."

        });

        return false;

    }


    if (
        Number.isNaN(
            datos.monto
        ) ||
        datos.monto <= 0
    ) {

        Swal.fire({

            icon: "warning",

            title: "Monto inválido",

            text:
                "Ingrese un monto de abono válido."

        });

        return false;

    }


    // --------------------------------------------------------
    // Validar contra saldo mostrado
    // --------------------------------------------------------

    const selectVenta =
        document.getElementById(
            "idVenta"
        );


    if (selectVenta) {

        const option =
            selectVenta.options[
                selectVenta.selectedIndex
                ];


        const saldo =
            Number(
                option?.dataset.total || 0
            );


        if (
            saldo > 0 &&
            datos.monto > saldo
        ) {

            Swal.fire({

                icon: "warning",

                title: "Monto superior al saldo",

                text:
                    "El abono no puede superar el saldo de la factura."

            });

            return false;

        }

    }


    return true;

}


// ============================================================
// PREPARAR BOTÓN GUARDAR
// ============================================================

function bloquearBotonGuardar() {

    const boton =
        document.getElementById(
            "btnGuardarAbono"
        );


    if (!boton) {

        return;

    }


    boton.disabled =
        true;


    boton.innerHTML = `

        <i class="fa-solid fa-spinner fa-spin"></i>

        Guardando...

    `;

}


// ============================================================
// RESTAURAR BOTÓN GUARDAR
// ============================================================

function restaurarBotonesAbono() {

    const btnGuardar =
        document.getElementById(
            "btnGuardarAbono"
        );


    const btnActualizar =
        document.getElementById(
            "btnActualizarAbono"
        );


    if (btnGuardar) {

        btnGuardar.disabled =
            false;

        btnGuardar.innerHTML = `

            <i class="fa-solid fa-floppy-disk"></i>

            Guardar Abono

        `;

    }


    if (btnActualizar) {

        btnActualizar.disabled =
            false;

        btnActualizar.innerHTML = `

            <i class="fa-solid fa-rotate"></i>

            Actualizar Abono

        `;

    }

}


// ============================================================
// PREPARAR BOTÓN ACTUALIZAR
// ============================================================

function bloquearBotonActualizar() {

    const boton =
        document.getElementById(
            "btnActualizarAbono"
        );


    if (!boton) {

        return;

    }


    boton.disabled =
        true;


    boton.innerHTML = `

        <i class="fa-solid fa-spinner fa-spin"></i>

        Actualizando...

    `;

}


// ============================================================
// GUARDAR ABONO
// ============================================================

async function guardarAbono() {

    if (!esAdmin()) {

        return;

    }


    // --------------------------------------------------------
    // BLOQUEO CONTRA DOBLE ENVÍO
    // --------------------------------------------------------

    if (
        guardandoAbono ||
        actualizandoAbono
    ) {

        return;

    }


    const datos =
        obtenerDatosFormularioAbono();


    if (
        !validarFormularioAbono(
            datos
        )
    ) {

        return;

    }


    // --------------------------------------------------------
    // Activar bloqueo
    // --------------------------------------------------------

    guardandoAbono =
        true;


    bloquearBotonGuardar();


    try {

        console.log(
            "💾 Registrando abono:",
            datos
        );


        const response =
            await fetch(
                "/api/abonos",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            datos
                        )

                }
            );


        // ----------------------------------------------------
        // IMPORTANTE:
        // Solo consideramos guardado si el backend responde OK.
        // 201 CREATED también entra aquí porque response.ok=true.
        // ----------------------------------------------------

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        console.log(
            "✅ Abono registrado correctamente."
        );


        // ----------------------------------------------------
        // LIBERAR EL BLOQUEO INMEDIATAMENTE
        // ----------------------------------------------------

        guardandoAbono =
            false;


        restaurarBotonesAbono();


        // ----------------------------------------------------
        // Cerrar y limpiar modal inmediatamente
        //
        // NO esperamos a cargarAbonos().
        // ----------------------------------------------------

        limpiarFormularioAbono();

        cerrarModalAbono();


        // ----------------------------------------------------
        // Actualizar información en segundo plano
        // ----------------------------------------------------

        refrescarDatosAbonos()
            .catch(
                function (error) {

                    console.error(
                        "❌ Error refrescando datos:",
                        error
                    );

                }
            );


        // ----------------------------------------------------
        // Mostrar confirmación
        // ----------------------------------------------------

        await Swal.fire({

            icon: "success",

            title: "Abono registrado",

            text:
                "El abono fue registrado correctamente.",

            confirmButtonText:
                "Aceptar"

        });


    } catch (error) {

        console.error(
            "❌ Error guardando abono:",
            error
        );


        // ----------------------------------------------------
        // Liberar bloqueo
        // ----------------------------------------------------

        guardandoAbono =
            false;


        restaurarBotonesAbono();


        Swal.fire({

            icon: "error",

            title: "No se pudo guardar",

            text:
                error.message ||
                "Ocurrió un error al guardar el abono."

        });

    }

}


// ============================================================
// ACTUALIZAR ABONO
// ============================================================

async function actualizarAbono() {

    if (!esAdmin()) {

        return;

    }


    // --------------------------------------------------------
    // BLOQUEO CONTRA DOBLE ENVÍO
    // --------------------------------------------------------

    if (
        guardandoAbono ||
        actualizandoAbono
    ) {

        return;

    }


    const id =
        document.getElementById(
            "abonoId"
        )?.value;


    if (!id) {

        Swal.fire({

            icon: "error",

            title: "Error",

            text:
                "No se encontró el abono a actualizar."

        });

        return;

    }


    const datos =
        obtenerDatosFormularioAbono();


    if (
        !validarFormularioAbono(
            datos
        )
    ) {

        return;

    }


    // --------------------------------------------------------
    // Activar bloqueo
    // --------------------------------------------------------

    actualizandoAbono =
        true;


    bloquearBotonActualizar();


    try {

        console.log(
            "✏️ Actualizando abono:",
            id,
            datos
        );


        const response =
            await fetch(
                `/api/abonos/${id}`,
                {

                    method: "PUT",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            datos
                        )

                }
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        console.log(
            "✅ Abono actualizado correctamente."
        );


        // ----------------------------------------------------
        // LIBERAR BLOQUEO INMEDIATAMENTE
        // ----------------------------------------------------

        actualizandoAbono =
            false;


        restaurarBotonesAbono();


        // ----------------------------------------------------
        // Cerrar modal inmediatamente
        // ----------------------------------------------------

        limpiarFormularioAbono();

        cerrarModalAbono();


        // ----------------------------------------------------
        // Actualizar información en segundo plano
        // ----------------------------------------------------

        refrescarDatosAbonos()
            .catch(
                function (error) {

                    console.error(
                        "❌ Error refrescando datos:",
                        error
                    );

                }
            );


        // ----------------------------------------------------
        // Confirmación
        // ----------------------------------------------------

        await Swal.fire({

            icon: "success",

            title: "Abono actualizado",

            text:
                "El abono fue actualizado correctamente.",

            confirmButtonText:
                "Aceptar"

        });


    } catch (error) {

        console.error(
            "❌ Error actualizando abono:",
            error
        );


        actualizandoAbono =
            false;


        restaurarBotonesAbono();


        Swal.fire({

            icon: "error",

            title: "No se pudo actualizar",

            text:
                error.message ||
                "Ocurrió un error al actualizar el abono."

        });

    }

}


// ============================================================
// ELIMINAR ABONO
// ============================================================

async function eliminarAbono(id) {

    if (!esAdmin()) {

        return;

    }


    // --------------------------------------------------------
    // Evitar operaciones simultáneas
    // --------------------------------------------------------

    if (
        guardandoAbono ||
        actualizandoAbono ||
        eliminandoAbono
    ) {

        return;

    }


    const abono =
        abonos.find(
            function (item) {

                return Number(item.id) ===
                    Number(id);

            }
        );


    const monto =
        formatearPrecio(
            abono?.monto || 0
        );


    const confirmacion =
        await Swal.fire({

            icon: "warning",

            title: "¿Eliminar abono?",

            html:
                `¿Deseas eliminar el abono de <strong>${escapeHTML(monto)}</strong>?`,

            showCancelButton: true,

            confirmButtonText:
                "Sí, eliminar",

            cancelButtonText:
                "Cancelar",

            confirmButtonColor:
                "#dc2626",

            cancelButtonColor:
                "#6b7280"

        });


    if (
        !confirmacion.isConfirmed
    ) {

        return;

    }


    eliminandoAbono =
        true;


    try {

        const response =
            await fetch(
                `/api/abonos/${id}`,
                {

                    method: "DELETE"

                }
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        console.log(
            "✅ Abono eliminado correctamente."
        );


        eliminandoAbono =
            false;


        // ----------------------------------------------------
        // Actualizar en segundo plano
        // ----------------------------------------------------

        refrescarDatosAbonos()
            .catch(
                function (error) {

                    console.error(
                        "❌ Error refrescando después de eliminar:",
                        error
                    );

                }
            );


        await Swal.fire({

            icon: "success",

            title: "Abono eliminado",

            text:
                "El abono fue eliminado correctamente.",

            confirmButtonText:
                "Aceptar"

        });


    } catch (error) {

        console.error(
            "❌ Error eliminando abono:",
            error
        );


        eliminandoAbono =
            false;


        Swal.fire({

            icon: "error",

            title: "No se pudo eliminar",

            text:
                error.message ||
                "Ocurrió un error al eliminar el abono."

        });

    }

}


// ============================================================
// LIMPIAR FORMULARIO
// ============================================================

function limpiarFormularioAbono() {

    const formulario =
        document.getElementById(
            "formAbono"
        );


    if (formulario) {

        formulario.reset();

    }


    // --------------------------------------------------------
    // ID
    // --------------------------------------------------------

    const id =
        document.getElementById(
            "abonoId"
        );


    if (id) {

        id.value = "";

    }


    // --------------------------------------------------------
    // Facturas
    // --------------------------------------------------------

    const venta =
        document.getElementById(
            "idVenta"
        );


    if (venta) {

        venta.innerHTML = `

            <option value="">
                Seleccione primero un cliente
            </option>

        `;

        venta.disabled =
            true;

    }


    // --------------------------------------------------------
    // Monto
    // --------------------------------------------------------

    const monto =
        document.getElementById(
            "monto"
        );


    if (monto) {

        monto.value = "";

        monto.disabled =
            true;

    }


    // --------------------------------------------------------
    // Texto deuda
    // --------------------------------------------------------

    const textoCliente =
        document.getElementById(
            "clienteDeudaTexto"
        );


    if (textoCliente) {

        textoCliente.textContent =
            "Seleccione un cliente para continuar.";

    }


    // --------------------------------------------------------
    // Facturas globales
    // --------------------------------------------------------

    facturasCliente = [];


    // --------------------------------------------------------
    // Resumen
    // --------------------------------------------------------

    limpiarResumenFactura();


    // --------------------------------------------------------
    // Modo nuevo
    // --------------------------------------------------------

    cambiarModoModalAbono(
        "nuevo"
    );

}


// ============================================================
// CERRAR MODAL
// ============================================================

function cerrarModalAbono() {

    const modal =
        document.getElementById(
            "modalAbono"
        );


    if (!modal) {

        document.body.style.overflow =
            "";

        return;

    }


    // --------------------------------------------------------
    // No cerrar durante una operación crítica
    //
    // Esto evita que un clic accidental en X o ESC
    // interrumpa visualmente el proceso.
    // --------------------------------------------------------

    if (
        guardandoAbono ||
        actualizandoAbono
    ) {

        return;

    }


    modal.classList.remove(
        "active"
    );


    modal.setAttribute(
        "aria-hidden",
        "true"
    );


    document.body.style.overflow =
        "";

}


// ============================================================
// FORMATEAR PRECIO
// ============================================================

function formatearPrecio(valor) {

    return Number(
        valor || 0
    )
        .toLocaleString(
            "es-CO",
            {

                style: "currency",

                currency: "COP",

                minimumFractionDigits: 0,

                maximumFractionDigits: 0

            }
        );

}


// ============================================================
// FORMATEAR FECHA
// ============================================================

function formatearFecha(valor) {

    if (!valor) {

        return "Sin fecha";

    }


    try {

        const fecha =
            new Date(valor);


        if (
            Number.isNaN(
                fecha.getTime()
            )
        ) {

            return String(valor);

        }


        return fecha.toLocaleString(
            "es-CO",
            {

                dateStyle: "short",

                timeStyle: "short"

            }
        );

    } catch (error) {

        return String(valor);

    }

}


// ============================================================
// NORMALIZAR TEXTO
// ============================================================

function normalizarTexto(valor) {

    return String(
        valor ?? ""
    )
        .normalize("NFD")
        .replace(
            /[\u0300-\u036f]/g,
            ""
        )
        .toLowerCase()
        .trim();

}


// ============================================================
// ESCAPE HTML
// ============================================================

function escapeHTML(valor) {

    return String(
        valor ?? ""
    )
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


// ============================================================
// OBTENER MENSAJE ERROR
// ============================================================

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

            const data =
                JSON.parse(
                    texto
                );


            return (

                data.message ||

                data.error ||

                data.mensaje ||

                texto

            );

        } catch (e) {

            return texto;

        }

    } catch (error) {

        return `Error HTTP ${response.status}`;

    }

}


// ============================================================
// FIN DEL MÓDULO
// ============================================================

console.log(
    "💰 abonos.js | MARAV cargado correctamente"
);