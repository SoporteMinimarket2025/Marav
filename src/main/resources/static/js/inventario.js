// ======================================================
// 📦 MARAV | INVENTARIO
// ======================================================
// Archivo: inventario.js
//
// Funciones:
// - Cargar productos
// - Cargar inventario
// - Buscar inventario
// - Crear registro de inventario
// - Editar inventario
// - Actualizar inventario
// - Eliminar inventario
// - Mostrar estados de stock
// - Mostrar productos correctamente
// - Controlar permisos ADMIN / EMPLEADO
// - Evitar doble registro
// - Evitar doble actualización
// - Evitar doble eliminación
// - Evitar bloqueo del modal
// ======================================================


// ======================================================
// VARIABLES GLOBALES
// ======================================================

let inventario = [];

let productos = [];


/*
 * Bloqueo global de operaciones.
 *
 * Sirve para impedir que el usuario pueda hacer:
 *
 * doble POST
 * doble PUT
 * doble DELETE
 *
 * haciendo doble clic rápidamente.
 */

let operacionInventarioEnCurso = false;


// ======================================================
// 🚀 INICIALIZACIÓN
// ======================================================

document.addEventListener(
    "DOMContentLoaded",
    async () => {

        console.log(
            "📦 Módulo Inventario iniciado"
        );


        // -------------------------------------------------
        // BUSCADOR TOPBAR
        // -------------------------------------------------

        configurarBuscadorTopbar();


        // -------------------------------------------------
        // FORMULARIO
        // -------------------------------------------------

        configurarFormularioInventario();


        // -------------------------------------------------
        // BOTONES
        // -------------------------------------------------

        configurarBotonesInventario();


        // -------------------------------------------------
        // TECLADO Y MODAL
        // -------------------------------------------------

        configurarEventosTeclado();


        // -------------------------------------------------
        // CARGAR PRODUCTOS
        // -------------------------------------------------

        await cargarProductos();


        // -------------------------------------------------
        // CARGAR INVENTARIO
        // -------------------------------------------------

        await cargarInventario();

    }
);


// ======================================================
// 🔐 CONTROL DE ROL
// ======================================================

function esAdmin() {

    /*
     * ROL_USUARIO viene desde Thymeleaf.
     *
     * Solo ADMIN puede crear, editar o eliminar.
     */

    return (
        typeof ROL_USUARIO !== "undefined" &&
        String(ROL_USUARIO).toUpperCase() === "ADMIN"
    );

}


// ======================================================
// 📝 CONFIGURAR FORMULARIO
// ======================================================

function configurarFormularioInventario() {

    const formulario =
        document.getElementById(
            "formInventario"
        );


    if (!formulario) {

        console.warn(
            "⚠ No se encontró #formInventario"
        );

        return;

    }


    /*
     * Evitar registrar el evento dos veces.
     */

    if (
        formulario.dataset.inventarioConfigurado === "true"
    ) {

        return;

    }


    formulario.dataset.inventarioConfigurado =
        "true";


    // -------------------------------------------------
    // SUBMIT
    // -------------------------------------------------

    formulario.addEventListener(
        "submit",
        async function (event) {

            /*
             * Evita recargar la página.
             */

            event.preventDefault();

            event.stopPropagation();


            /*
             * Evita doble operación.
             */

            if (operacionInventarioEnCurso) {

                console.warn(
                    "⚠ Ya existe una operación de inventario en curso."
                );

                return;

            }


            /*
             * Revisamos si existe ID.
             *
             * Sin ID:
             *    GUARDAR
             *
             * Con ID:
             *    ACTUALIZAR
             */

            const id =
                obtenerValor("inventarioId");


            if (id) {

                await actualizarInventario();

            } else {

                await guardarInventario();

            }

        }
    );

}


// ======================================================
// 🔘 CONFIGURAR BOTONES
// ======================================================

function configurarBotonesInventario() {

    // -------------------------------------------------
    // BOTÓN GUARDAR
    // -------------------------------------------------

    const btnGuardar =
        document.getElementById(
            "btnGuardar"
        );


    if (btnGuardar) {

        /*
         * El botón trabaja con el submit
         * del formulario.
         */

        btnGuardar.type = "submit";

    }


    // -------------------------------------------------
    // BOTÓN ACTUALIZAR
    // -------------------------------------------------

    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    if (btnActualizar) {

        /*
         * También utiliza el mismo submit.
         */

        btnActualizar.type = "submit";

    }


    // -------------------------------------------------
    // BOTÓN CANCELAR
    // -------------------------------------------------

    const btnCancelar =
        document.getElementById(
            "btnCancelar"
        );


    if (btnCancelar) {

        btnCancelar.addEventListener(
            "click",
            function (event) {

                event.preventDefault();

                event.stopPropagation();

                cancelarInventario();

            }
        );

    }

}


// ======================================================
// 📦 CARGAR PRODUCTOS
// ======================================================

async function cargarProductos() {

    try {

        const respuesta =
            await fetch(
                "/api/productos",
                {
                    method: "GET",

                    headers: {
                        "Accept":
                            "application/json"
                    }
                }
            );


        if (!respuesta.ok) {

            throw new Error(
                `Error HTTP ${respuesta.status}`
            );

        }


        productos =
            await respuesta.json();


        /*
         * Nos aseguramos de tener un arreglo.
         */

        if (!Array.isArray(productos)) {

            productos = [];

        }


        console.log(
            "📦 Productos cargados:",
            productos
        );


        llenarSelectProductos();


    } catch (error) {

        console.error(
            "❌ Error cargando productos:",
            error
        );


        mostrarAlerta(
            "error",
            "Error",
            "No fue posible cargar los productos."
        );

    }

}


// ======================================================
// 🔽 LLENAR SELECT PRODUCTOS
// ======================================================

function llenarSelectProductos() {

    const select =
        document.getElementById(
            "productoId"
        );


    if (!select) {

        console.warn(
            "⚠ No existe #productoId"
        );

        return;

    }


    /*
     * Limpiar opciones anteriores.
     */

    select.innerHTML = `

        <option value="">
            Seleccione un producto
        </option>

    `;


    /*
     * Agregar productos.
     */

    productos.forEach(producto => {

        const option =
            document.createElement(
                "option"
            );


        option.value =
            producto.id;


        option.textContent =
            producto.nombre ||
            producto.descripcion ||
            `Producto ${producto.id}`;


        select.appendChild(option);

    });

}


// ======================================================
// 📦 CARGAR INVENTARIO
// ======================================================

async function cargarInventario() {

    try {

        const respuesta =
            await fetch(
                "/api/inventario",
                {
                    method: "GET",

                    headers: {
                        "Accept":
                            "application/json"
                    }
                }
            );


        if (!respuesta.ok) {

            throw new Error(
                `Error HTTP ${respuesta.status}`
            );

        }


        inventario =
            await respuesta.json();


        if (!Array.isArray(inventario)) {

            inventario = [];

        }


        console.log(
            "📦 Inventario cargado:",
            inventario
        );


        renderInventario(
            obtenerInventarioFiltrado()
        );


        actualizarDashboard();


    } catch (error) {

        console.error(
            "❌ Error cargando inventario:",
            error
        );


        mostrarAlerta(
            "error",
            "Error",
            "No fue posible cargar el inventario."
        );

    }

}


// ======================================================
// 🔎 OBTENER NOMBRE PRODUCTO
// ======================================================

function obtenerNombreProducto(item) {

    const productoId =
        item.productoId ??
        item.producto_id ??
        item.producto?.id;


    /*
     * Buscar primero en el arreglo de productos.
     */

    const producto =
        productos.find(
            producto =>
                Number(producto.id) ===
                Number(productoId)
        );


    if (producto) {

        return (
            producto.nombre ||
            producto.descripcion ||
            "Producto sin nombre"
        );

    }


    /*
     * Si el backend trae el objeto producto.
     */

    if (
        item.producto &&
        item.producto.nombre
    ) {

        return item.producto.nombre;

    }


    return "Producto no disponible";

}


// ======================================================
// 🔤 NORMALIZAR TEXTO
// ======================================================

function normalizarTexto(valor) {

    return String(
        valor ?? ""
    )

        .toLowerCase()

        .normalize("NFD")

        .replace(
            /[\u0300-\u036f]/g,
            ""
        )

        .trim();

}


// ======================================================
// 🎨 RENDERIZAR INVENTARIO
// ======================================================

function renderInventario(lista) {

    const tbody =
        document.getElementById(
            "tablaInventario"
        );


    const vacio =
        document.getElementById(
            "inventarioVacio"
        );


    if (!tbody) {

        console.error(
            "❌ No existe #tablaInventario"
        );

        return;

    }


    tbody.innerHTML = "";


    // -------------------------------------------------
    // SIN RESULTADOS
    // -------------------------------------------------

    if (
        !Array.isArray(lista) ||
        lista.length === 0
    ) {

        if (vacio) {

            vacio.style.display =
                "block";


            const span =
                vacio.querySelector(
                    "span"
                );


            if (span) {

                if (inventario.length === 0) {

                    span.textContent =
                        "No hay registros de inventario disponibles.";

                } else {

                    span.textContent =
                        "No se encontraron registros con la búsqueda.";

                }

            }

        }

        return;

    }


    // -------------------------------------------------
    // OCULTAR VACÍO
    // -------------------------------------------------

    if (vacio) {

        vacio.style.display =
            "none";

    }


    // -------------------------------------------------
    // CREAR FILAS
    // -------------------------------------------------

    lista.forEach(item => {

        const fila =
            document.createElement(
                "tr"
            );


        const nombreProducto =
            obtenerNombreProducto(item);


        const stock =
            Number(
                item.stockActual ?? 0
            );


        const minimo =
            Number(
                item.stockMinimo ?? 0
            );


        const lote =
            item.lote || "—";


        const ubicacion =
            item.ubicacion || "—";


        const fecha =
            formatearFecha(
                item.fechaVencimiento
            );


        const estado =
            obtenerEstado(item);


        // -------------------------------------------------
        // ACCIONES ADMIN
        // -------------------------------------------------

        let acciones = "";


        if (esAdmin()) {

            acciones = `

<td>

    <div class="inventario-actions-table">

        <button
            type="button"
            class="inventario-action-btn inventario-btn-edit"
            title="Editar"
            onclick="editarInventario(${Number(item.id)})"
        >

            <i class="fa-solid fa-pen"></i>

        </button>


        <button
            type="button"
            class="inventario-action-btn inventario-btn-delete"
            title="Eliminar"
            onclick="eliminarInventario(${Number(item.id)})"
        >

            <i class="fa-solid fa-trash"></i>

        </button>

    </div>

</td>

`;

        }


        // -------------------------------------------------
        // FILA
        // -------------------------------------------------

        fila.innerHTML = `

<td>

    <div class="inventario-producto">

        <div class="inventario-avatar">

            <i class="fa-solid fa-box"></i>

        </div>

        <span>
            ${escapeHtml(nombreProducto)}
        </span>

    </div>

</td>


<td>

    <span class="inventario-stock">

        ${stock}

    </span>

</td>


<td>

    <span class="inventario-minimo">

        ${minimo}

    </span>

</td>


<td>

    <span class="inventario-lote">

        ${escapeHtml(lote)}

    </span>

</td>


<td>

    <span class="inventario-ubicacion">

        ${escapeHtml(ubicacion)}

    </span>

</td>


<td>

    <span class="inventario-vencimiento">

        ${fecha}

    </span>

</td>


<td>

    ${renderEstado(estado)}

</td>


${acciones}

`;


        tbody.appendChild(
            fila
        );

    });

}


// ======================================================
// 📊 OBTENER ESTADO
// ======================================================

function obtenerEstado(item) {

    const stock =
        Number(
            item.stockActual ?? 0
        );


    const minimo =
        Number(
            item.stockMinimo ?? 0
        );


    // -------------------------------------------------
    // PRODUCTO VENCIDO
    // -------------------------------------------------

    if (
        item.fechaVencimiento
    ) {

        const fechaVencimiento =
            convertirFechaLocal(
                item.fechaVencimiento
            );


        const hoy =
            new Date();


        hoy.setHours(
            0,
            0,
            0,
            0
        );


        if (
            fechaVencimiento <
            hoy
        ) {

            return "VENCIDO";

        }

    }


    // -------------------------------------------------
    // STOCK BAJO
    // -------------------------------------------------

    if (
        stock <= minimo
    ) {

        return "STOCK BAJO";

    }


    // -------------------------------------------------
    // DISPONIBLE
    // -------------------------------------------------

    return "DISPONIBLE";

}


// ======================================================
// 🎨 RENDER ESTADO
// ======================================================

function renderEstado(estado) {

    if (
        estado === "VENCIDO"
    ) {

        return `

<span class="inventario-estado estado-vencido">

    <i class="fa-solid fa-circle-xmark"></i>

    Vencido

</span>

`;

    }


    if (
        estado === "STOCK BAJO"
    ) {

        return `

<span class="inventario-estado estado-stock-bajo">

    <i class="fa-solid fa-triangle-exclamation"></i>

    Stock bajo

</span>

`;

    }


    return `

<span class="inventario-estado estado-disponible">

    <i class="fa-solid fa-circle-check"></i>

    Disponible

</span>

`;

}


// ======================================================
// 📅 FORMATEAR FECHA
// ======================================================

function formatearFecha(fecha) {

    if (!fecha) {

        return "—";

    }


    const fechaLocal =
        convertirFechaLocal(
            fecha
        );


    if (
        isNaN(
            fechaLocal.getTime()
        )
    ) {

        return "—";

    }


    return fechaLocal.toLocaleDateString(
        "es-CO",
        {
            day: "2-digit",
            month: "2-digit",
            year: "numeric"
        }
    );

}


// ======================================================
// 📅 CONVERTIR FECHA LOCAL
// ======================================================

function convertirFechaLocal(fecha) {

    if (
        fecha instanceof Date
    ) {

        return new Date(
            fecha
        );

    }


    const texto =
        String(fecha);


    /*
     * Para YYYY-MM-DD evitamos
     * el desplazamiento de zona horaria.
     */

    if (
        /^\d{4}-\d{2}-\d{2}$/.test(texto)
    ) {

        const partes =
            texto.split("-");


        return new Date(

            Number(partes[0]),

            Number(partes[1]) - 1,

            Number(partes[2])

        );

    }


    return new Date(
        texto
    );

}


// ======================================================
// ➕ ABRIR MODAL NUEVO
// ======================================================

function abrirModalInventario() {

    if (!esAdmin()) {

        mostrarSinPermiso();

        return;

    }


    if (
        operacionInventarioEnCurso
    ) {

        return;

    }


    // -------------------------------------------------
    // LIMPIAR
    // -------------------------------------------------

    limpiarFormulario();


    // -------------------------------------------------
    // MODO NUEVO
    // -------------------------------------------------

    cambiarModoModalInventario(
        "nuevo"
    );


    // -------------------------------------------------
    // MODAL
    // -------------------------------------------------

    const modal =
        document.getElementById(
            "modalInventario"
        );


    if (!modal) {

        console.error(
            "❌ No existe #modalInventario"
        );

        return;

    }


    modal.classList.add(
        "active"
    );


    document.body.style.overflow =
        "hidden";


    // -------------------------------------------------
    // ENFOCAR PRODUCTO
    // -------------------------------------------------

    setTimeout(() => {

        const producto =
            document.getElementById(
                "productoId"
            );


        if (producto) {

            producto.focus();

        }

    }, 150);

}


// ======================================================
// ✏ EDITAR INVENTARIO
// ======================================================

function editarInventario(id) {

    if (!esAdmin()) {

        mostrarSinPermiso();

        return;

    }


    if (
        operacionInventarioEnCurso
    ) {

        return;

    }


    const item =
        inventario.find(
            registro =>
                Number(registro.id) ===
                Number(id)
        );


    if (!item) {

        mostrarAlerta(
            "error",
            "Error",
            "No se encontró el registro de inventario."
        );

        return;

    }


    // -------------------------------------------------
    // ID
    // -------------------------------------------------

    establecerValor(
        "inventarioId",
        item.id
    );


    // -------------------------------------------------
    // PRODUCTO
    // -------------------------------------------------

    establecerValor(
        "productoId",

        item.productoId ??
        item.producto_id ??
        item.producto?.id ??
        ""
    );


    // -------------------------------------------------
    // STOCK ACTUAL
    // -------------------------------------------------

    establecerValor(
        "stockActual",
        item.stockActual ?? 0
    );


    // -------------------------------------------------
    // STOCK MÍNIMO
    // -------------------------------------------------

    establecerValor(
        "stockMinimo",
        item.stockMinimo ?? 0
    );


    // -------------------------------------------------
    // LOTE
    // -------------------------------------------------

    establecerValor(
        "lote",
        item.lote ?? ""
    );


    // -------------------------------------------------
    // UBICACIÓN
    // -------------------------------------------------

    establecerValor(
        "ubicacion",
        item.ubicacion ?? ""
    );


    // -------------------------------------------------
    // FECHA VENCIMIENTO
    // -------------------------------------------------

    establecerValor(
        "fechaVencimiento",

        obtenerFechaInput(
            item.fechaVencimiento
        )
    );


    // -------------------------------------------------
    // MODO EDITAR
    // -------------------------------------------------

    cambiarModoModalInventario(
        "editar"
    );


    // -------------------------------------------------
    // ABRIR MODAL
    // -------------------------------------------------

    const modal =
        document.getElementById(
            "modalInventario"
        );


    if (modal) {

        modal.classList.add(
            "active"
        );

        document.body.style.overflow =
            "hidden";

    }

}


// ======================================================
// 🔄 CAMBIAR MODO MODAL
// ======================================================

function cambiarModoModalInventario(
    modo
) {

    const titulo =
        document.getElementById(
            "tituloFormularioInventario"
        );


    const subtitulo =
        document.getElementById(
            "subtituloFormularioInventario"
        );


    const btnGuardar =
        document.getElementById(
            "btnGuardar"
        );


    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    // -------------------------------------------------
    // NUEVO
    // -------------------------------------------------

    if (
        modo === "nuevo"
    ) {

        if (titulo) {

            titulo.innerHTML = `

<i class="fa-solid fa-circle-plus"></i>

Nuevo Inventario

`;

        }


        if (subtitulo) {

            subtitulo.textContent =
                "Registra la información del inventario";

        }


        if (btnGuardar) {

            btnGuardar.style.display =
                "inline-flex";

        }


        if (btnActualizar) {

            btnActualizar.style.display =
                "none";

        }


        return;

    }


    // -------------------------------------------------
    // EDITAR
    // -------------------------------------------------

    if (
        modo === "editar"
    ) {

        if (titulo) {

            titulo.innerHTML = `

<i class="fa-solid fa-pen-to-square"></i>

Editar Inventario

`;

        }


        if (subtitulo) {

            subtitulo.textContent =
                "Actualiza la información del inventario";

        }


        if (btnGuardar) {

            btnGuardar.style.display =
                "none";

        }


        if (btnActualizar) {

            btnActualizar.style.display =
                "inline-flex";

        }

    }

}


// ======================================================
// ❌ CERRAR MODAL
// ======================================================

function cerrarModalInventario() {

    const modal =
        document.getElementById(
            "modalInventario"
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
// 📅 FECHA PARA INPUT DATE
// ======================================================

function obtenerFechaInput(fecha) {

    if (!fecha) {

        return "";

    }


    const texto =
        String(fecha);


    /*
     * Si ya viene como YYYY-MM-DD,
     * podemos utilizarla directamente.
     */

    if (
        /^\d{4}-\d{2}-\d{2}$/.test(texto)
    ) {

        return texto;

    }


    const fechaLocal =
        new Date(
            fecha
        );


    if (
        isNaN(
            fechaLocal.getTime()
        )
    ) {

        return "";

    }


    const año =
        fechaLocal.getFullYear();


    const mes =
        String(
            fechaLocal.getMonth() + 1
        ).padStart(
            2,
            "0"
        );


    const dia =
        String(
            fechaLocal.getDate()
        ).padStart(
            2,
            "0"
        );


    return `${año}-${mes}-${dia}`;

}


// ======================================================
// 💾 GUARDAR INVENTARIO
// ======================================================

async function guardarInventario() {

    // -------------------------------------------------
    // PERMISOS
    // -------------------------------------------------

    if (!esAdmin()) {

        mostrarSinPermiso();

        return;

    }


    // -------------------------------------------------
    // EVITAR DOBLE REGISTRO
    // -------------------------------------------------

    if (
        operacionInventarioEnCurso
    ) {

        console.warn(
            "⚠ Guardado de inventario ya en proceso."
        );

        return;

    }


    // -------------------------------------------------
    // DATOS
    // -------------------------------------------------

    const datos =
        obtenerDatosFormulario();


    // -------------------------------------------------
    // VALIDACIÓN
    // -------------------------------------------------

    if (
        !validarDatos(datos)
    ) {

        return;

    }


    /*
     * Activamos el bloqueo ANTES
     * de enviar el POST.
     */

    operacionInventarioEnCurso =
        true;


    const btnGuardar =
        document.getElementById(
            "btnGuardar"
        );


    try {

        // -------------------------------------------------
        // BLOQUEAR BOTONES
        // -------------------------------------------------

        cambiarEstadoBotones(
            true
        );


        if (btnGuardar) {

            btnGuardar.innerHTML = `

<i class="fa-solid fa-spinner fa-spin"></i>

Guardando...

`;

        }


        // -------------------------------------------------
        // POST
        // -------------------------------------------------

        const respuesta =
            await fetch(
                "/api/inventario",
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
                            datos
                        )

                }
            );


        // -------------------------------------------------
        // RESPUESTA
        // -------------------------------------------------

        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta,
                    "No fue posible guardar el inventario."
                )
            );

        }


        /*
         * IMPORTANTE:
         *
         * El servidor ya confirmó el POST.
         *
         * Cerramos primero el modal.
         * Liberamos el bloqueo.
         * Luego actualizamos la tabla.
         *
         * Así el modal nunca queda esperando
         * la recarga completa.
         */

        limpiarFormulario();

        cerrarModalInventario();

        cambiarModoModalInventario(
            "nuevo"
        );


        operacionInventarioEnCurso =
            false;


        cambiarEstadoBotones(
            false
        );


        // -------------------------------------------------
        // MENSAJE ÉXITO
        // -------------------------------------------------

        mostrarAlerta(
            "success",
            "Inventario guardado",
            "El registro se creó correctamente."
        );


        // -------------------------------------------------
        // ACTUALIZAR TABLA
        // -------------------------------------------------

        cargarInventario();


    } catch (error) {

        console.error(
            "❌ Error guardando inventario:",
            error
        );


        operacionInventarioEnCurso =
            false;


        cambiarEstadoBotones(
            false
        );


        mostrarAlerta(
            "error",
            "Error",
            error.message ||
            "No fue posible guardar el inventario."
        );

    }

}


// ======================================================
// ✏ ACTUALIZAR INVENTARIO
// ======================================================

async function actualizarInventario() {

    // -------------------------------------------------
    // PERMISOS
    // -------------------------------------------------

    if (!esAdmin()) {

        mostrarSinPermiso();

        return;

    }


    // -------------------------------------------------
    // EVITAR DOBLE ACTUALIZACIÓN
    // -------------------------------------------------

    if (
        operacionInventarioEnCurso
    ) {

        console.warn(
            "⚠ Actualización de inventario ya en proceso."
        );

        return;

    }


    // -------------------------------------------------
    // ID
    // -------------------------------------------------

    const id =
        obtenerValor(
            "inventarioId"
        );


    if (!id) {

        mostrarAlerta(
            "warning",
            "Registro inválido",
            "No se encontró el identificador del inventario."
        );

        return;

    }


    // -------------------------------------------------
    // DATOS
    // -------------------------------------------------

    const datos =
        obtenerDatosFormulario();


    // -------------------------------------------------
    // VALIDACIÓN
    // -------------------------------------------------

    if (
        !validarDatos(datos)
    ) {

        return;

    }


    /*
     * Activar bloqueo ANTES del PUT.
     */

    operacionInventarioEnCurso =
        true;


    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    try {

        // -------------------------------------------------
        // BLOQUEAR BOTONES
        // -------------------------------------------------

        cambiarEstadoBotones(
            true
        );


        if (btnActualizar) {

            btnActualizar.innerHTML = `

<i class="fa-solid fa-spinner fa-spin"></i>

Actualizando...

`;

        }


        // -------------------------------------------------
        // PUT
        // -------------------------------------------------

        const respuesta =
            await fetch(
                `/api/inventario/${encodeURIComponent(id)}`,
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
                            datos
                        )

                }
            );


        // -------------------------------------------------
        // RESPUESTA
        // -------------------------------------------------

        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta,
                    "No fue posible actualizar el inventario."
                )
            );

        }


        /*
         * El PUT terminó correctamente.
         *
         * Cerramos el modal inmediatamente.
         */

        limpiarFormulario();

        cerrarModalInventario();

        cambiarModoModalInventario(
            "nuevo"
        );


        operacionInventarioEnCurso =
            false;


        cambiarEstadoBotones(
            false
        );


        // -------------------------------------------------
        // MENSAJE ÉXITO
        // -------------------------------------------------

        mostrarAlerta(
            "success",
            "Inventario actualizado",
            "Los cambios se guardaron correctamente."
        );


        // -------------------------------------------------
        // ACTUALIZAR TABLA
        // -------------------------------------------------

        cargarInventario();


    } catch (error) {

        console.error(
            "❌ Error actualizando inventario:",
            error
        );


        operacionInventarioEnCurso =
            false;


        cambiarEstadoBotones(
            false
        );


        mostrarAlerta(
            "error",
            "Error",
            error.message ||
            "No fue posible actualizar el inventario."
        );

    }

}


// ======================================================
// 🗑 ELIMINAR INVENTARIO
// ======================================================

async function eliminarInventario(id) {

    // -------------------------------------------------
    // PERMISOS
    // -------------------------------------------------

    if (!esAdmin()) {

        mostrarSinPermiso();

        return;

    }


    // -------------------------------------------------
    // EVITAR DOBLE OPERACIÓN
    // -------------------------------------------------

    if (
        operacionInventarioEnCurso
    ) {

        return;

    }


    // -------------------------------------------------
    // BUSCAR REGISTRO
    // -------------------------------------------------

    const item =
        inventario.find(
            registro =>
                Number(registro.id) ===
                Number(id)
        );


    if (!item) {

        mostrarAlerta(
            "warning",
            "Registro no encontrado",
            "No fue posible encontrar el registro seleccionado."
        );

        return;

    }


    // -------------------------------------------------
    // CONFIRMACIÓN
    // -------------------------------------------------

    const nombreProducto =
        obtenerNombreProducto(item);


    const confirmacion =
        await Swal.fire({

            title:
                "¿Eliminar registro?",

            text:
                `Se eliminará el inventario de "${nombreProducto}". Esta acción no se puede deshacer.`,

            icon:
                "warning",

            showCancelButton:
                true,

            confirmButtonText:
                "Sí, eliminar",

            cancelButtonText:
                "Cancelar",

            confirmButtonColor:
                "#dc2626",

            cancelButtonColor:
                "#64748b",

            reverseButtons:
                true

        });


    if (
        !confirmacion.isConfirmed
    ) {

        return;

    }


    /*
     * Activamos bloqueo DESPUÉS de la
     * confirmación.
     */

    operacionInventarioEnCurso =
        true;


    try {

        // -------------------------------------------------
        // DELETE
        // -------------------------------------------------

        const respuesta =
            await fetch(
                `/api/inventario/${encodeURIComponent(id)}`,
                {

                    method: "DELETE",

                    headers: {

                        "Accept":
                            "application/json"

                    }

                }
            );


        // -------------------------------------------------
        // VALIDAR RESPUESTA
        // -------------------------------------------------

        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta,
                    "No fue posible eliminar el inventario."
                )
            );

        }


        /*
         * DELETE correcto.
         */

        operacionInventarioEnCurso =
            false;


        // -------------------------------------------------
        // MENSAJE
        // -------------------------------------------------

        mostrarAlerta(
            "success",
            "Registro eliminado",
            "El inventario fue eliminado correctamente."
        );


        // -------------------------------------------------
        // ACTUALIZAR TABLA
        // -------------------------------------------------

        cargarInventario();


    } catch (error) {

        console.error(
            "❌ Error eliminando inventario:",
            error
        );


        operacionInventarioEnCurso =
            false;


        mostrarAlerta(
            "error",
            "Error",
            error.message ||
            "No fue posible eliminar el registro."
        );

    }

}


// ======================================================
// 💾 OBTENER DATOS FORMULARIO
// ======================================================

function obtenerDatosFormulario() {

    const productoInput =
        document.getElementById(
            "productoId"
        );


    const stockActualInput =
        document.getElementById(
            "stockActual"
        );


    const stockMinimoInput =
        document.getElementById(
            "stockMinimo"
        );


    const loteInput =
        document.getElementById(
            "lote"
        );


    const ubicacionInput =
        document.getElementById(
            "ubicacion"
        );


    const fechaInput =
        document.getElementById(
            "fechaVencimiento"
        );


    const productoId =
        Number(
            productoInput?.value || 0
        );


    const stockActual =
        Number(
            stockActualInput?.value || 0
        );


    const stockMinimo =
        Number(
            stockMinimoInput?.value || 0
        );


    const lote =
        String(
            loteInput?.value || ""
        ).trim();


    const ubicacion =
        String(
            ubicacionInput?.value || ""
        ).trim();


    const fechaVencimiento =
        fechaInput?.value || "";


    return {

        productoId:
        productoId,

        stockActual:
        stockActual,

        stockMinimo:
        stockMinimo,

        lote:
            lote || null,

        ubicacion:
            ubicacion || null,

        fechaVencimiento:
            fechaVencimiento || null

    };

}


// ======================================================
// ✅ VALIDAR DATOS
// ======================================================

function validarDatos(datos) {

    // -------------------------------------------------
    // PRODUCTO
    // -------------------------------------------------

    if (
        !Number.isFinite(
            datos.productoId
        ) ||
        datos.productoId <= 0
    ) {

        mostrarAlerta(
            "warning",
            "Producto requerido",
            "Seleccione un producto."
        );

        return false;

    }


    // -------------------------------------------------
    // STOCK ACTUAL
    // -------------------------------------------------

    if (
        !Number.isFinite(
            datos.stockActual
        ) ||
        datos.stockActual < 0
    ) {

        mostrarAlerta(
            "warning",
            "Stock inválido",
            "El stock actual debe ser un número mayor o igual a cero."
        );

        return false;

    }


    // -------------------------------------------------
    // STOCK MÍNIMO
    // -------------------------------------------------

    if (
        !Number.isFinite(
            datos.stockMinimo
        ) ||
        datos.stockMinimo < 0
    ) {

        mostrarAlerta(
            "warning",
            "Stock mínimo inválido",
            "El stock mínimo debe ser un número mayor o igual a cero."
        );

        return false;

    }


    // -------------------------------------------------
    // VALIDAR FECHA
    // -------------------------------------------------

    if (
        datos.fechaVencimiento
    ) {

        const fecha =
            convertirFechaLocal(
                datos.fechaVencimiento
            );


        if (
            isNaN(
                fecha.getTime()
            )
        ) {

            mostrarAlerta(
                "warning",
                "Fecha inválida",
                "La fecha de vencimiento no es válida."
            );

            return false;

        }

    }


    return true;

}


// ======================================================
// 🔘 CAMBIAR ESTADO DE BOTONES
// ======================================================

function cambiarEstadoBotones(
    bloquear
) {

    const btnGuardar =
        document.getElementById(
            "btnGuardar"
        );


    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    const btnCancelar =
        document.getElementById(
            "btnCancelar"
        );


    if (btnGuardar) {

        btnGuardar.disabled =
            bloquear;

    }


    if (btnActualizar) {

        btnActualizar.disabled =
            bloquear;

    }


    if (btnCancelar) {

        btnCancelar.disabled =
            bloquear;

    }

}


// ======================================================
// 🧹 LIMPIAR FORMULARIO
// ======================================================

function limpiarFormulario() {

    const formulario =
        document.getElementById(
            "formInventario"
        );


    if (formulario) {

        formulario.reset();

    }


    const id =
        document.getElementById(
            "inventarioId"
        );


    if (id) {

        id.value = "";

    }


    /*
     * Dejamos valores iniciales recomendados.
     */

    const stockActual =
        document.getElementById(
            "stockActual"
        );


    const stockMinimo =
        document.getElementById(
            "stockMinimo"
        );


    if (stockActual) {

        stockActual.value =
            "0";

    }


    if (stockMinimo) {

        stockMinimo.value =
            "5";

    }

}


// ======================================================
// ❌ CANCELAR
// ======================================================

function cancelarInventario() {

    /*
     * No permitir cancelar mientras se procesa
     * una operación.
     */

    if (
        operacionInventarioEnCurso
    ) {

        return;

    }


    limpiarFormulario();


    cambiarModoModalInventario(
        "nuevo"
    );


    cerrarModalInventario();

}


// ======================================================
// 🔍 BUSCADOR TOPBAR
// ======================================================

function configurarBuscadorTopbar() {

    const buscador =
        document.getElementById(
            "buscarTopbar"
        );


    if (!buscador) {

        console.warn(
            "⚠ No se encontró #buscarTopbar"
        );

        return;

    }


    /*
     * Evitar evento duplicado.
     */

    if (
        buscador.dataset.inventarioConfigurado === "true"
    ) {

        return;

    }


    buscador.dataset.inventarioConfigurado =
        "true";


    buscador.placeholder =
        "Buscar inventario...";


    buscador.addEventListener(
        "input",
        function () {

            renderInventario(
                obtenerInventarioFiltrado()
            );

        }
    );

}


// ======================================================
// 🔎 OBTENER INVENTARIO FILTRADO
// ======================================================

function obtenerInventarioFiltrado() {

    const buscador =
        document.getElementById(
            "buscarTopbar"
        );


    if (!buscador) {

        return inventario;

    }


    const texto =
        normalizarTexto(
            buscador.value
        );


    // -------------------------------------------------
    // SIN BÚSQUEDA
    // -------------------------------------------------

    if (!texto) {

        return inventario;

    }


    // -------------------------------------------------
    // FILTRAR
    // -------------------------------------------------

    return inventario.filter(
        item => {

            const nombreProducto =
                obtenerNombreProducto(
                    item
                );


            const estado =
                obtenerEstado(
                    item
                );


            const fechaOriginal =
                item.fechaVencimiento ||
                "";


            const fechaFormateada =
                formatearFecha(
                    item.fechaVencimiento
                );


            const valores = [

                nombreProducto,

                item.stockActual,

                item.stockMinimo,

                item.lote,

                item.ubicacion,

                fechaOriginal,

                fechaFormateada,

                estado

            ];


            return valores.some(
                valor =>
                    normalizarTexto(
                        valor
                    ).includes(
                        texto
                    )
            );

        }
    );

}


// ======================================================
// 🔎 COMPATIBILIDAD
// ======================================================

function buscarInventario() {

    renderInventario(
        obtenerInventarioFiltrado()
    );

}


// ======================================================
// ⌨ EVENTOS DE TECLADO
// ======================================================

function configurarEventosTeclado() {

    // -------------------------------------------------
    // ESC
    // -------------------------------------------------

    document.addEventListener(
        "keydown",
        event => {

            if (
                event.key === "Escape"
            ) {

                cancelarInventario();

            }

        }
    );


    // -------------------------------------------------
    // CLICK FUERA DEL MODAL
    // -------------------------------------------------

    const modal =
        document.getElementById(
            "modalInventario"
        );


    if (modal) {

        modal.addEventListener(
            "click",
            event => {

                if (
                    event.target === modal
                ) {

                    cancelarInventario();

                }

            }
        );

    }

}


// ======================================================
// 📊 ACTUALIZAR DASHBOARD
// ======================================================

function actualizarDashboard() {

    const total =
        inventario.length;


    let stockBajo =
        0;


    let vencidos =
        0;


    // -------------------------------------------------
    // CONTAR ESTADOS
    // -------------------------------------------------

    inventario.forEach(
        item => {

            const estado =
                obtenerEstado(
                    item
                );


            if (
                estado === "STOCK BAJO"
            ) {

                stockBajo++;

            }


            if (
                estado === "VENCIDO"
            ) {

                vencidos++;

            }

        }
    );


    // -------------------------------------------------
    // ELEMENTOS
    // -------------------------------------------------

    const totalElement =
        document.getElementById(
            "totalInventario"
        );


    const stockBajoElement =
        document.getElementById(
            "stockBajo"
        );


    const vencidosElement =
        document.getElementById(
            "vencidos"
        );


    // -------------------------------------------------
    // MOSTRAR
    // -------------------------------------------------

    if (totalElement) {

        totalElement.textContent =
            total;

    }


    if (stockBajoElement) {

        stockBajoElement.textContent =
            stockBajo;

    }


    if (vencidosElement) {

        vencidosElement.textContent =
            vencidos;

    }

}


// ======================================================
// 🔔 ALERTA GENERAL
// ======================================================

function mostrarAlerta(
    icono,
    titulo,
    texto
) {

    if (
        typeof Swal === "undefined"
    ) {

        alert(
            `${titulo}: ${texto}`
        );

        return Promise.resolve();

    }


    return Swal.fire({

        icon:
        icono,

        title:
        titulo,

        text:
        texto,

        confirmButtonText:
            "Aceptar"

    });

}


// ======================================================
// 🔐 SIN PERMISO
// ======================================================

function mostrarSinPermiso() {

    mostrarAlerta(
        "warning",
        "Acceso restringido",
        "No tienes permisos para modificar el inventario."
    );

}


// ======================================================
// 🛡 MENSAJE ERROR BACKEND
// ======================================================

async function obtenerMensajeError(
    respuesta,
    mensajePredeterminado =
    "Ocurrió un error en la operación."
) {

    try {

        const texto =
            await respuesta.text();


        if (!texto) {

            return mensajePredeterminado;

        }


        /*
         * Intentar JSON.
         */

        try {

            const json =
                JSON.parse(
                    texto
                );


            return (
                json.message ||
                json.error ||
                json.mensaje ||
                json.detail ||
                mensajePredeterminado
            );

        } catch {

            /*
             * Si el backend respondió texto plano,
             * lo mostramos directamente.
             */

            return texto;

        }

    } catch {

        return mensajePredeterminado;

    }

}


// ======================================================
// 🛡 ESCAPAR HTML
// ======================================================

function escapeHtml(valor) {

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
// 🧰 OBTENER VALOR
// ======================================================

function obtenerValor(id) {

    const elemento =
        document.getElementById(
            id
        );


    if (!elemento) {

        return "";

    }


    return String(
        elemento.value ?? ""
    ).trim();

}


// ======================================================
// 🧰 ESTABLECER VALOR
// ======================================================

function establecerValor(
    id,
    valor
) {

    const elemento =
        document.getElementById(
            id
        );


    if (!elemento) {

        return;

    }


    elemento.value =
        valor ?? "";

}