// ======================================================
// 👥 MARAV - MÓDULO CLIENTES
// ======================================================
// Este archivo controla:
//
// ✔ Carga de clientes
// ✔ Búsqueda desde el Topbar
// ✔ Renderizado de la tabla
// ✔ KPIs
// ✔ Crear cliente
// ✔ Editar cliente
// ✔ Actualizar cliente
// ✔ Eliminar cliente
// ✔ Control de permisos ADMIN / EMPLEADO
// ✔ Modal
// ✔ Validaciones
// ✔ SweetAlert2
// ✔ Prevención de operaciones duplicadas
//
// IMPORTANTE:
// La seguridad real debe estar también en Spring Security.
// Este JavaScript solamente controla la interfaz.
// ======================================================



// ======================================================
// 🌐 VARIABLES GLOBALES
// ======================================================

let clientesGlobal = [];


// ------------------------------------------------------
// 🔒 BLOQUEO DE OPERACIONES
// ------------------------------------------------------
// Evita que un doble clic pueda enviar:
//
// POST POST
// PUT PUT
// DELETE DELETE
//
// Esto es especialmente importante para evitar clientes
// duplicados cuando el usuario hace doble clic.
// ------------------------------------------------------

let operacionClienteEnCurso = false;



// ======================================================
// 🚀 INICIAR MÓDULO
// ======================================================

document.addEventListener("DOMContentLoaded", () => {

    configurarBuscadorTopbar();

    configurarEventos();

    cargarClientes();

});



// ======================================================
// 🔎 CONFIGURAR BUSCADOR DEL TOPBAR
// ======================================================

function configurarBuscadorTopbar() {

    const buscarTopbar =
        document.getElementById("buscarTopbar");


    if (!buscarTopbar) {

        console.warn(
            "No se encontró #buscarTopbar."
        );

        return;

    }


    // --------------------------------------------------
    // Placeholder
    // --------------------------------------------------

    buscarTopbar.placeholder =
        "Buscar clientes...";


    // --------------------------------------------------
    // Evitar registrar el evento dos veces
    // --------------------------------------------------

    if (
        buscarTopbar.dataset.clientesConfigurado === "true"
    ) {

        return;

    }


    buscarTopbar.dataset.clientesConfigurado =
        "true";


    // --------------------------------------------------
    // Buscar mientras escribe
    // --------------------------------------------------

    buscarTopbar.addEventListener(
        "input",
        () => {

            renderizarClientes();

        }
    );

}



// ======================================================
// ⚙️ CONFIGURAR EVENTOS
// ======================================================

function configurarEventos() {

    const modal =
        document.getElementById("modalCliente");


    // --------------------------------------------------
    // Cerrar haciendo clic en el fondo
    // --------------------------------------------------

    if (modal) {

        modal.addEventListener(
            "click",
            function (event) {

                if (
                    event.target === modal &&
                    !operacionClienteEnCurso
                ) {

                    cancelarEdicion();

                }

            }
        );

    }


    // --------------------------------------------------
    // Cerrar con ESC
    // --------------------------------------------------

    document.addEventListener(
        "keydown",
        function (event) {

            if (
                event.key === "Escape" &&
                !operacionClienteEnCurso
            ) {

                cancelarEdicion();

            }

        }
    );

}



// ======================================================
// 🔐 VERIFICAR SI ES ADMIN
// ======================================================

function esAdmin() {

    return (
        typeof ROL_USUARIO !== "undefined" &&
        String(ROL_USUARIO)
            .trim()
            .toUpperCase() === "ADMIN"
    );

}



// ======================================================
// 🔒 BLOQUEAR / DESBLOQUEAR OPERACIÓN
// ======================================================

function bloquearOperacionCliente() {

    if (operacionClienteEnCurso) {

        return false;

    }


    operacionClienteEnCurso = true;


    const btnGuardar =
        document.getElementById("btnGuardar");


    const btnActualizar =
        document.getElementById("btnActualizar");


    if (btnGuardar) {

        btnGuardar.disabled = true;

        btnGuardar.style.pointerEvents =
            "none";

        btnGuardar.style.opacity =
            "0.7";

    }


    if (btnActualizar) {

        btnActualizar.disabled = true;

        btnActualizar.style.pointerEvents =
            "none";

        btnActualizar.style.opacity =
            "0.7";

    }


    return true;

}



function desbloquearOperacionCliente() {

    operacionClienteEnCurso = false;


    const btnGuardar =
        document.getElementById("btnGuardar");


    const btnActualizar =
        document.getElementById("btnActualizar");


    if (btnGuardar) {

        btnGuardar.disabled = false;

        btnGuardar.style.pointerEvents =
            "";

        btnGuardar.style.opacity =
            "";

    }


    if (btnActualizar) {

        btnActualizar.disabled = false;

        btnActualizar.style.pointerEvents =
            "";

        btnActualizar.style.opacity =
            "";

    }

}



// ======================================================
// 📥 CARGAR CLIENTES
// ======================================================

async function cargarClientes() {

    try {

        const response =
            await fetch("/api/clientes", {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                }
            });


        if (!response.ok) {

            const mensaje =
                await obtenerMensajeError(response);

            throw new Error(mensaje);

        }


        const data =
            await response.json();


        clientesGlobal =
            Array.isArray(data)
                ? data
                : [];


        renderizarClientes();

        actualizarKPIs();


    } catch (error) {

        console.error(
            "Error cargando clientes:",
            error
        );


        mostrarError(
            error.message ||
            "No fue posible cargar los clientes."
        );

    }

}



// ======================================================
// 🔎 NORMALIZAR TEXTO
// ======================================================

function normalizarTexto(valor) {

    return String(valor ?? "")
        .normalize("NFD")
        .replace(
            /[\u0300-\u036f]/g,
            ""
        )
        .toLowerCase()
        .trim();

}



// ======================================================
// 🔎 OBTENER CLIENTES FILTRADOS
// ======================================================

function obtenerClientesFiltrados() {

    const buscarTopbar =
        document.getElementById("buscarTopbar");


    if (!buscarTopbar) {

        return clientesGlobal;

    }


    const termino =
        normalizarTexto(
            buscarTopbar.value
        );


    if (!termino) {

        return clientesGlobal;

    }


    return clientesGlobal.filter(
        cliente => {

            const nombre =
                normalizarTexto(
                    cliente.nombre
                );


            const tipoIdentificacion =
                normalizarTexto(
                    cliente.tipoIdentificacion
                );


            const numeroIdentificacion =
                normalizarTexto(
                    cliente.numeroIdentificacion
                );


            const telefono =
                normalizarTexto(
                    cliente.telefono
                );


            const direccion =
                normalizarTexto(
                    cliente.direccion
                );


            const estado =
                obtenerEstadoCliente(cliente)
                    ? "activo"
                    : "inactivo";


            const deuda =
                normalizarTexto(
                    cliente.deuda
                );


            return (

                nombre.includes(termino) ||

                tipoIdentificacion.includes(termino) ||

                numeroIdentificacion.includes(termino) ||

                telefono.includes(termino) ||

                direccion.includes(termino) ||

                estado.includes(termino) ||

                deuda.includes(termino)

            );

        }
    );

}



// ======================================================
// 👥 RENDERIZAR CLIENTES
// ======================================================

function renderizarClientes() {

    const tabla =
        document.getElementById("tablaClientes");


    if (!tabla) {

        console.warn(
            "No se encontró #tablaClientes."
        );

        return;

    }


    tabla.innerHTML = "";


    const clientesFiltrados =
        obtenerClientesFiltrados();


    const buscarTopbar =
        document.getElementById("buscarTopbar");


    const tieneBusqueda =
        buscarTopbar &&
        buscarTopbar.value.trim() !== "";


    // ==================================================
    // 🚫 SIN RESULTADOS
    // ==================================================

    if (
        clientesFiltrados.length === 0
    ) {

        const fila =
            document.createElement("tr");


        const mensaje =
            tieneBusqueda
                ? "No se encontraron clientes"
                : "No hay clientes registrados";


        const icono =
            tieneBusqueda
                ? "fa-magnifying-glass"
                : "fa-users-slash";


        fila.innerHTML = `

<td
    colspan="${esAdmin() ? 7 : 6}"
    class="clientes-empty">

    <i class="fa-solid ${icono}"></i>

    <span>
        ${mensaje}
    </span>

</td>

`;


        tabla.appendChild(fila);

        return;

    }



    // ==================================================
    // 📋 MOSTRAR CLIENTES
    // ==================================================

    clientesFiltrados.forEach(
        cliente => {

            const fila =
                document.createElement("tr");


            const nombre =
                obtenerValor(
                    cliente.nombre,
                    "Sin nombre"
                );


            const tipo =
                obtenerValor(
                    cliente.tipoIdentificacion,
                    ""
                );


            const documento =
                obtenerValor(
                    cliente.numeroIdentificacion,
                    "Sin documento"
                );


            const telefono =
                obtenerValor(
                    cliente.telefono,
                    "No registrado"
                );


            const direccion =
                obtenerValor(
                    cliente.direccion,
                    "No registrada"
                );


            const deuda =
                obtenerNumero(
                    cliente.deuda
                );


            const activo =
                obtenerEstadoCliente(
                    cliente
                );


            // ------------------------------------------------
            // Estado
            // ------------------------------------------------

            const estadoClase =
                activo
                    ? "estado-activo"
                    : "estado-inactivo";


            const estadoTexto =
                activo
                    ? "Activo"
                    : "Inactivo";


            // ------------------------------------------------
            // Deuda
            // ------------------------------------------------

            const deudaClase =
                deuda > 0
                    ? "con-deuda"
                    : "sin-deuda";


            // ------------------------------------------------
            // Documento
            // ------------------------------------------------

            const documentoMostrar =
                tipo
                    ? `${escapeHTML(tipo)} ${escapeHTML(documento)}`
                    : escapeHTML(documento);



            // ==================================================
            // HTML DE LA FILA
            // ==================================================

            fila.innerHTML = `

<td>

    <div class="cliente-nombre">

        <div class="cliente-avatar">

            <i class="fa-solid fa-user"></i>

        </div>

        <span>
            ${escapeHTML(nombre)}
        </span>

    </div>

</td>


<td>

    <span class="cliente-documento">

        ${documentoMostrar}

    </span>

</td>


<td>

    <span class="cliente-telefono">

        ${escapeHTML(telefono)}

    </span>

</td>


<td>

    <span class="cliente-direccion">

        ${escapeHTML(direccion)}

    </span>

</td>


<td>

    <span class="cliente-deuda ${deudaClase}">

        ${formatearMoneda(deuda)}

    </span>

</td>


<td>

    <span class="estado-badge ${estadoClase}">

        <i class="fa-solid ${
                activo
                    ? "fa-circle-check"
                    : "fa-circle-xmark"
            }"></i>

        ${estadoTexto}

    </span>

</td>


${
                esAdmin()
                    ? `

<td>

    <div class="cliente-actions">

        <button
            type="button"
            class="cliente-action-btn cliente-btn-edit"
            title="Editar cliente"
            ${
                        operacionClienteEnCurso
                            ? "disabled"
                            : ""
                    }
            onclick="editarCliente(${Number(cliente.id)})">

            <i class="fa-solid fa-pen"></i>

        </button>


        <button
            type="button"
            class="cliente-action-btn cliente-btn-delete"
            title="Eliminar cliente"
            ${
                        operacionClienteEnCurso
                            ? "disabled"
                            : ""
                    }
            onclick="confirmarEliminarCliente(${Number(cliente.id)})">

            <i class="fa-solid fa-trash"></i>

        </button>

    </div>

</td>

`
                    : ""
            }

`;


            tabla.appendChild(fila);

        }
    );

}



// ======================================================
// 📊 ACTUALIZAR KPIs
// ======================================================

function actualizarKPIs() {

    const totalClientes =
        document.getElementById(
            "totalClientes"
        );


    const clientesActivos =
        document.getElementById(
            "clientesActivos"
        );


    const totalDeuda =
        document.getElementById(
            "totalDeuda"
        );


    const total =
        clientesGlobal.length;


    const activos =
        clientesGlobal.filter(
            cliente =>
                obtenerEstadoCliente(cliente)
        ).length;


    const deuda =
        clientesGlobal.reduce(
            (
                acumulado,
                cliente
            ) => {

                return (
                    acumulado +
                    obtenerNumero(
                        cliente.deuda
                    )
                );

            },
            0
        );


    if (totalClientes) {

        totalClientes.textContent =
            total;

    }


    if (clientesActivos) {

        clientesActivos.textContent =
            activos;

    }


    if (totalDeuda) {

        totalDeuda.textContent =
            formatearMoneda(deuda);

    }

}



// ======================================================
// ➕ ABRIR MODAL NUEVO CLIENTE
// ======================================================

function abrirModalCliente() {

    if (!esAdmin()) {

        mostrarAdvertencia(
            "No tienes permisos para crear clientes."
        );

        return;

    }


    if (operacionClienteEnCurso) {

        return;

    }


    limpiarFormulario();


    const titulo =
        document.getElementById(
            "tituloFormulario"
        );


    const subtitulo =
        document.getElementById(
            "subtituloFormulario"
        );


    if (titulo) {

        titulo.innerHTML = `

<i class="fa-solid fa-circle-plus"></i>

Nuevo Cliente

`;

    }


    if (subtitulo) {

        subtitulo.textContent =
            "Registra la información del cliente";

    }


    mostrarBotonGuardar();


    const modal =
        document.getElementById(
            "modalCliente"
        );


    if (!modal) {

        return;

    }


    modal.classList.add("active");


    document.body.style.overflow =
        "hidden";


    setTimeout(
        () => {

            document.getElementById(
                "nombre"
            )?.focus();

        },
        100
    );

}



// ======================================================
// ❌ CERRAR MODAL
// ======================================================

function cerrarModalCliente() {

    const modal =
        document.getElementById(
            "modalCliente"
        );


    if (!modal) {

        return;

    }


    modal.classList.remove(
        "active"
    );


    document.body.style.overflow =
        "";

}



// ======================================================
// 🟢 MOSTRAR BOTÓN GUARDAR
// ======================================================

function mostrarBotonGuardar() {

    const btnGuardar =
        document.getElementById(
            "btnGuardar"
        );


    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    if (btnGuardar) {

        btnGuardar.style.display =
            "inline-flex";

    }


    if (btnActualizar) {

        btnActualizar.style.display =
            "none";

    }

}



// ======================================================
// 🔵 MOSTRAR BOTÓN ACTUALIZAR
// ======================================================

function mostrarBotonActualizar() {

    const btnGuardar =
        document.getElementById(
            "btnGuardar"
        );


    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    if (btnGuardar) {

        btnGuardar.style.display =
            "none";

    }


    if (btnActualizar) {

        btnActualizar.style.display =
            "inline-flex";

    }

}



// ======================================================
// 💾 GUARDAR CLIENTE
// ======================================================

async function guardarCliente() {

    if (!esAdmin()) {

        mostrarAdvertencia(
            "No tienes permisos para guardar clientes."
        );

        return;

    }


    // --------------------------------------------------
    // 🔒 Evitar doble clic
    // --------------------------------------------------

    if (!bloquearOperacionCliente()) {

        return;

    }


    try {

        const cliente =
            obtenerDatosFormulario();


        if (
            !validarCliente(cliente)
        ) {

            return;

        }


        const response =
            await fetch(
                "/api/clientes",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Accept":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            cliente
                        )

                }
            );


        if (!response.ok) {

            const mensaje =
                await obtenerMensajeError(
                    response
                );


            throw new Error(
                mensaje
            );

        }


        // ------------------------------------------------
        // Intentar leer respuesta
        // ------------------------------------------------

        try {

            await response.json();

        } catch {

            // Puede que el backend devuelva 200/201
            // sin cuerpo JSON.

        }


        // ------------------------------------------------
        // Cerrar y limpiar primero
        // ------------------------------------------------

        limpiarFormulario();

        cerrarModalCliente();


        // ------------------------------------------------
        // Liberar botones antes de refrescar
        // ------------------------------------------------

        desbloquearOperacionCliente();


        // ------------------------------------------------
        // Mostrar confirmación
        // ------------------------------------------------

        await Swal.fire({

            icon: "success",

            title: "Cliente registrado",

            text:
                "El cliente se registró correctamente.",

            confirmButtonText:
                "Aceptar",

            confirmButtonColor:
                "#2563eb"

        });


        // ------------------------------------------------
        // Actualizar lista
        // ------------------------------------------------

        await cargarClientes();


    } catch (error) {

        console.error(
            "Error guardando cliente:",
            error
        );


        mostrarError(
            error.message ||
            "No fue posible registrar el cliente."
        );


    } finally {

        desbloquearOperacionCliente();

    }

}



// ======================================================
// ✏️ EDITAR CLIENTE
// ======================================================

function editarCliente(id) {

    if (!esAdmin()) {

        mostrarAdvertencia(
            "No tienes permisos para editar clientes."
        );

        return;

    }


    if (operacionClienteEnCurso) {

        return;

    }


    const cliente =
        clientesGlobal.find(
            item =>
                Number(item.id) ===
                Number(id)
        );


    if (!cliente) {

        mostrarError(
            "No se encontró el cliente."
        );

        return;

    }


    // --------------------------------------------------
    // Cargar datos
    // --------------------------------------------------

    const campoId =
        document.getElementById("id");


    const nombre =
        document.getElementById("nombre");


    const tipoIdentificacion =
        document.getElementById(
            "tipoIdentificacion"
        );


    const numeroIdentificacion =
        document.getElementById(
            "numeroIdentificacion"
        );


    const telefono =
        document.getElementById("telefono");


    const direccion =
        document.getElementById("direccion");


    if (campoId) {

        campoId.value =
            cliente.id ?? "";

    }


    if (nombre) {

        nombre.value =
            cliente.nombre ?? "";

    }


    if (tipoIdentificacion) {

        tipoIdentificacion.value =
            cliente.tipoIdentificacion ?? "";

    }


    if (numeroIdentificacion) {

        numeroIdentificacion.value =
            cliente.numeroIdentificacion ?? "";

    }


    if (telefono) {

        telefono.value =
            cliente.telefono ?? "";

    }


    if (direccion) {

        direccion.value =
            cliente.direccion ?? "";

    }


    // --------------------------------------------------
    // Título
    // --------------------------------------------------

    const titulo =
        document.getElementById(
            "tituloFormulario"
        );


    const subtitulo =
        document.getElementById(
            "subtituloFormulario"
        );


    if (titulo) {

        titulo.innerHTML = `

<i class="fa-solid fa-pen-to-square"></i>

Editar Cliente

`;

    }


    if (subtitulo) {

        subtitulo.textContent =
            "Actualiza la información del cliente";

    }


    mostrarBotonActualizar();


    // --------------------------------------------------
    // Abrir modal
    // --------------------------------------------------

    const modal =
        document.getElementById(
            "modalCliente"
        );


    if (!modal) {

        return;

    }


    modal.classList.add("active");


    document.body.style.overflow =
        "hidden";


    setTimeout(
        () => {

            nombre?.focus();

        },
        100
    );

}



// ======================================================
// 🔄 ACTUALIZAR CLIENTE
// ======================================================

async function actualizarCliente() {

    if (!esAdmin()) {

        mostrarAdvertencia(
            "No tienes permisos para actualizar clientes."
        );

        return;

    }


    if (!bloquearOperacionCliente()) {

        return;

    }


    try {

        const campoId =
            document.getElementById("id");


        const id =
            campoId?.value;


        if (!id) {

            throw new Error(
                "No se encontró el cliente a actualizar."
            );

        }


        const cliente =
            obtenerDatosFormulario();


        if (
            !validarCliente(cliente)
        ) {

            return;

        }


        const response =
            await fetch(
                `/api/clientes/${encodeURIComponent(id)}`,
                {

                    method: "PUT",

                    headers: {

                        "Content-Type":
                            "application/json",

                        "Accept":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            cliente
                        )

                }
            );


        if (!response.ok) {

            const mensaje =
                await obtenerMensajeError(
                    response
                );


            throw new Error(
                mensaje
            );

        }


        // ------------------------------------------------
        // Intentar leer respuesta
        // ------------------------------------------------

        try {

            await response.json();

        } catch {

            // Backend puede devolver respuesta vacía.

        }


        // ------------------------------------------------
        // Cerrar modal
        // ------------------------------------------------

        limpiarFormulario();

        cerrarModalCliente();


        // ------------------------------------------------
        // Liberar operación
        // ------------------------------------------------

        desbloquearOperacionCliente();


        // ------------------------------------------------
        // Confirmación
        // ------------------------------------------------

        await Swal.fire({

            icon: "success",

            title: "Cliente actualizado",

            text:
                "La información se actualizó correctamente.",

            confirmButtonText:
                "Aceptar",

            confirmButtonColor:
                "#2563eb"

        });


        // ------------------------------------------------
        // Actualizar lista
        // ------------------------------------------------

        await cargarClientes();


    } catch (error) {

        console.error(
            "Error actualizando cliente:",
            error
        );


        mostrarError(
            error.message ||
            "No fue posible actualizar el cliente."
        );


    } finally {

        desbloquearOperacionCliente();

    }

}



// ======================================================
// 🗑️ CONFIRMAR ELIMINACIÓN
// ======================================================

async function confirmarEliminarCliente(id) {

    if (!esAdmin()) {

        mostrarAdvertencia(
            "No tienes permisos para eliminar clientes."
        );

        return;

    }


    if (operacionClienteEnCurso) {

        return;

    }


    const cliente =
        clientesGlobal.find(
            item =>
                Number(item.id) ===
                Number(id)
        );


    if (!cliente) {

        mostrarError(
            "No se encontró el cliente."
        );

        return;

    }


    const resultado =
        await Swal.fire({

            icon: "warning",

            title: "¿Eliminar cliente?",

            html: `

<p>
    ¿Deseas eliminar a
    <strong>
        ${escapeHTML(
                cliente.nombre ||
                "este cliente"
            )}
    </strong>?
</p>

<small>
    Esta acción no se puede deshacer.
</small>

`,

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
        !resultado.isConfirmed
    ) {

        return;

    }


    await eliminarCliente(id);

}



// ======================================================
// 🗑️ ELIMINAR CLIENTE
// ======================================================

async function eliminarCliente(id) {

    if (!esAdmin()) {

        mostrarAdvertencia(
            "No tienes permisos para eliminar clientes."
        );

        return;

    }


    if (!bloquearOperacionCliente()) {

        return;

    }


    try {

        const response =
            await fetch(
                `/api/clientes/${encodeURIComponent(id)}`,
                {

                    method: "DELETE",

                    headers: {

                        "Accept":
                            "application/json"

                    }

                }
            );


        if (!response.ok) {

            const mensaje =
                await obtenerMensajeError(
                    response
                );


            throw new Error(
                mensaje
            );

        }


        // ------------------------------------------------
        // Liberar bloqueo inmediatamente
        // ------------------------------------------------

        desbloquearOperacionCliente();


        // ------------------------------------------------
        // Confirmación
        // ------------------------------------------------

        await Swal.fire({

            icon: "success",

            title: "Cliente eliminado",

            text:
                "El cliente fue eliminado correctamente.",

            confirmButtonText:
                "Aceptar",

            confirmButtonColor:
                "#2563eb"

        });


        // ------------------------------------------------
        // Actualizar lista
        // ------------------------------------------------

        await cargarClientes();


    } catch (error) {

        console.error(
            "Error eliminando cliente:",
            error
        );


        mostrarError(
            error.message ||
            "No fue posible eliminar el cliente."
        );


    } finally {

        desbloquearOperacionCliente();

    }

}



// ======================================================
// 📝 OBTENER DATOS DEL FORMULARIO
// ======================================================

function obtenerDatosFormulario() {

    return {

        nombre:
            document.getElementById(
                "nombre"
            )?.value.trim() || "",


        tipoIdentificacion:
            document.getElementById(
                "tipoIdentificacion"
            )?.value || "",


        numeroIdentificacion:
            document.getElementById(
                "numeroIdentificacion"
            )?.value.trim() || "",


        telefono:
            document.getElementById(
                "telefono"
            )?.value.trim() || "",


        direccion:
            document.getElementById(
                "direccion"
            )?.value.trim() || ""

    };

}



// ======================================================
// ✅ VALIDAR CLIENTE
// ======================================================

function validarCliente(cliente) {

    // --------------------------------------------------
    // Nombre
    // --------------------------------------------------

    if (!cliente.nombre) {

        mostrarAdvertencia(
            "Ingrese el nombre del cliente."
        );


        document.getElementById(
            "nombre"
        )?.focus();


        return false;

    }


    // --------------------------------------------------
    // Tipo identificación
    // --------------------------------------------------

    if (
        !cliente.tipoIdentificacion
    ) {

        mostrarAdvertencia(
            "Seleccione el tipo de identificación."
        );


        document.getElementById(
            "tipoIdentificacion"
        )?.focus();


        return false;

    }


    // --------------------------------------------------
    // Número identificación
    // --------------------------------------------------

    if (
        !cliente.numeroIdentificacion
    ) {

        mostrarAdvertencia(
            "Ingrese el número de identificación."
        );


        document.getElementById(
            "numeroIdentificacion"
        )?.focus();


        return false;

    }


    return true;

}



// ======================================================
// 🧹 LIMPIAR FORMULARIO
// ======================================================

function limpiarFormulario() {

    const formulario =
        document.getElementById(
            "formCliente"
        );


    if (formulario) {

        formulario.reset();

    }


    const id =
        document.getElementById("id");


    if (id) {

        id.value = "";

    }


    mostrarBotonGuardar();


    const titulo =
        document.getElementById(
            "tituloFormulario"
        );


    const subtitulo =
        document.getElementById(
            "subtituloFormulario"
        );


    if (titulo) {

        titulo.innerHTML = `

<i class="fa-solid fa-circle-plus"></i>

Nuevo Cliente

`;

    }


    if (subtitulo) {

        subtitulo.textContent =
            "Registra la información del cliente";

    }

}



// ======================================================
// ❌ CANCELAR EDICIÓN
// ======================================================

function cancelarEdicion() {

    if (operacionClienteEnCurso) {

        return;

    }


    limpiarFormulario();

    cerrarModalCliente();

}



// ======================================================
// 🟢 OBTENER ESTADO DEL CLIENTE
// ======================================================

function obtenerEstadoCliente(cliente) {

    // --------------------------------------------------
    // activo como boolean
    // --------------------------------------------------

    if (
        typeof cliente.activo === "boolean"
    ) {

        return cliente.activo;

    }


    // --------------------------------------------------
    // estado como boolean
    // --------------------------------------------------

    if (
        typeof cliente.estado === "boolean"
    ) {

        return cliente.estado;

    }


    // --------------------------------------------------
    // estado como número
    // --------------------------------------------------

    if (
        typeof cliente.estado === "number"
    ) {

        return cliente.estado !== 0;

    }


    // --------------------------------------------------
    // estado como texto
    // --------------------------------------------------

    if (
        typeof cliente.estado === "string"
    ) {

        const estado =
            cliente.estado
                .trim()
                .toUpperCase();


        return ![
            "INACTIVO",
            "INACTIVA",
            "0",
            "FALSE",
            "ELIMINADO"
        ].includes(
            estado
        );

    }


    // --------------------------------------------------
    // Por defecto
    // --------------------------------------------------

    return true;

}



// ======================================================
// 📌 OBTENER VALOR
// ======================================================

function obtenerValor(
    valor,
    defecto = ""
) {

    if (
        valor === null ||
        valor === undefined ||
        String(valor).trim() === ""
    ) {

        return defecto;

    }


    return String(valor);

}



// ======================================================
// 🔢 OBTENER NÚMERO
// ======================================================

function obtenerNumero(valor) {

    if (
        valor === null ||
        valor === undefined ||
        valor === ""
    ) {

        return 0;

    }


    const numero =
        Number(valor);


    return Number.isFinite(numero)
        ? numero
        : 0;

}



// ======================================================
// 💰 FORMATEAR MONEDA
// ======================================================

function formatearMoneda(valor) {

    const numero =
        obtenerNumero(valor);


    return new Intl.NumberFormat(
        "es-CO",
        {

            style: "currency",

            currency: "COP",

            minimumFractionDigits: 0

        }
    ).format(numero);

}



// ======================================================
// 🛡️ ESCAPAR HTML
// ======================================================

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



// ======================================================
// ⚠️ OBTENER MENSAJE DEL SERVIDOR
// ======================================================

async function obtenerMensajeError(response) {

    try {

        const texto =
            await response.text();


        if (!texto) {

            return `Error HTTP ${response.status}`;

        }


        // ------------------------------------------------
        // Intentar interpretar JSON
        // ------------------------------------------------

        try {

            const json =
                JSON.parse(texto);


            return (
                json.message ||
                json.error ||
                json.mensaje ||
                texto
            );

        } catch {

            return texto;

        }

    } catch {

        return `Error HTTP ${response.status}`;

    }

}



// ======================================================
// ❌ SWEET ALERT — ERROR
// ======================================================

function mostrarError(mensaje) {

    Swal.fire({

        icon: "error",

        title: "Error",

        text: mensaje,

        confirmButtonText:
            "Aceptar",

        confirmButtonColor:
            "#2563eb"

    });

}



// ======================================================
// ⚠️ SWEET ALERT — ADVERTENCIA
// ======================================================

function mostrarAdvertencia(mensaje) {

    Swal.fire({

        icon: "warning",

        title: "Atención",

        text: mensaje,

        confirmButtonText:
            "Aceptar",

        confirmButtonColor:
            "#2563eb"

    });

}