// =====================================================
// 🚚 MARAV | PROVEEDORES
// =====================================================
// Archivo: proveedores.js
//
// Funciones principales:
// - Cargar proveedores
// - Buscar proveedores
// - Crear proveedor
// - Editar proveedor
// - Actualizar proveedor
// - Eliminar proveedor
// - Control de permisos ADMIN
// - Evitar doble registro / doble actualización
// - Manejo seguro del modal
// =====================================================


// =====================================================
// VARIABLES GLOBALES
// =====================================================

let proveedores = [];

/*
 * Esta variable evita que se ejecuten simultáneamente
 * dos operaciones de guardar, actualizar o eliminar.
 *
 * Ejemplo:
 * El usuario hace doble clic en "Guardar".
 *
 * Primer clic:
 *    false -> true
 *
 * Segundo clic:
 *    detecta true -> no ejecuta nada
 */
let operacionProveedorEnCurso = false;


// =====================================================
// 🚀 INICIAR MÓDULO
// =====================================================

document.addEventListener("DOMContentLoaded", async () => {

    console.log("🚚 Módulo Proveedores iniciado");

    // -------------------------------------------------
    // CONFIGURAR BUSCADOR TOPBAR
    // -------------------------------------------------

    configurarBuscadorTopbar();


    // -------------------------------------------------
    // CONFIGURAR FORMULARIO
    // -------------------------------------------------

    configurarFormularioProveedor();


    // -------------------------------------------------
    // CONFIGURAR BOTONES
    // -------------------------------------------------

    configurarBotonesProveedor();


    // -------------------------------------------------
    // CONFIGURAR ESC
    // -------------------------------------------------

    document.addEventListener(
        "keydown",
        manejarTeclaEscape
    );


    // -------------------------------------------------
    // CLICK FUERA DEL MODAL
    // -------------------------------------------------

    const modal =
        document.getElementById("modalProveedor");


    if (modal) {

        modal.addEventListener(
            "click",
            function (event) {

                if (event.target === modal) {

                    cerrarModalProveedor();

                }

            }
        );

    }


    // -------------------------------------------------
    // CARGAR PROVEEDORES
    // -------------------------------------------------

    await cargarProveedores();

});


// =====================================================
// 🔐 CONTROL DE ROL
// =====================================================

function esAdmin() {

    /*
     * ROL_USUARIO viene desde Thymeleaf.
     *
     * Ejemplo:
     * ADMIN
     * EMPLEADO
     */

    return (
        typeof ROL_USUARIO !== "undefined" &&
        String(ROL_USUARIO).toUpperCase() === "ADMIN"
    );

}


// =====================================================
// 📝 CONFIGURAR FORMULARIO
// =====================================================

function configurarFormularioProveedor() {

    const formulario =
        document.getElementById("formProveedor");


    if (!formulario) {

        console.warn(
            "⚠ No se encontró #formProveedor"
        );

        return;

    }


    /*
     * Evitamos registrar el mismo evento más de una vez.
     */

    if (
        formulario.dataset.proveedoresConfigurado === "true"
    ) {

        return;

    }


    formulario.dataset.proveedoresConfigurado =
        "true";


    // -------------------------------------------------
    // SUBMIT DEL FORMULARIO
    // -------------------------------------------------

    formulario.addEventListener(
        "submit",
        async function (event) {

            /*
             * IMPORTANTE:
             * Evita que el navegador recargue la página.
             */

            event.preventDefault();

            event.stopPropagation();


            /*
             * Si ya hay una operación en curso,
             * no hacemos nada.
             */

            if (operacionProveedorEnCurso) {

                console.warn(
                    "⚠ Ya existe una operación de proveedor en curso."
                );

                return;

            }


            /*
             * Revisamos si estamos creando
             * o actualizando.
             */

            const id =
                document.getElementById("id")?.value.trim();


            if (id) {

                await actualizarProveedor();

            } else {

                await guardarProveedor();

            }

        }
    );

}


// =====================================================
// 🔘 CONFIGURAR BOTONES
// =====================================================

function configurarBotonesProveedor() {

    // -------------------------------------------------
    // BOTÓN GUARDAR
    // -------------------------------------------------

    const btnGuardar =
        document.getElementById("btnGuardar");


    if (btnGuardar) {

        /*
         * Si el botón es type="submit", no necesitamos
         * agregar otro evento.
         *
         * El formulario se encargará.
         */

        btnGuardar.type = "submit";

    }


    // -------------------------------------------------
    // BOTÓN ACTUALIZAR
    // -------------------------------------------------

    const btnActualizar =
        document.getElementById("btnActualizar");


    if (btnActualizar) {

        /*
         * Lo configuramos como submit para que pase
         * por el mismo controlador del formulario.
         */

        btnActualizar.type = "submit";

    }


    // -------------------------------------------------
    // BOTÓN CANCELAR
    // -------------------------------------------------

    const btnCancelar =
        document.getElementById("btnCancelar");


    if (btnCancelar) {

        btnCancelar.addEventListener(
            "click",
            function (event) {

                event.preventDefault();

                event.stopPropagation();

                cancelarEdicion();

            }
        );

    }

}


// =====================================================
// 🔍 BUSCADOR TOPBAR
// =====================================================

function configurarBuscadorTopbar() {

    const buscarTopbar =
        document.getElementById("buscarTopbar");


    if (!buscarTopbar) {

        console.warn(
            "⚠ No se encontró #buscarTopbar"
        );

        return;

    }


    /*
     * Evita registrar varias veces el evento.
     */

    if (
        buscarTopbar.dataset.proveedoresConfigurado === "true"
    ) {

        return;

    }


    buscarTopbar.dataset.proveedoresConfigurado =
        "true";


    buscarTopbar.placeholder =
        "Buscar proveedores...";


    buscarTopbar.addEventListener(
        "input",
        filtrarProveedores
    );

}


// =====================================================
// 📦 CARGAR PROVEEDORES
// =====================================================

async function cargarProveedores() {

    try {

        const response =
            await fetch(
                "/proveedores/api",
                {
                    method: "GET",
                    headers: {
                        "Accept": "application/json"
                    }
                }
            );


        if (!response.ok) {

            throw new Error(
                `Error HTTP ${response.status}`
            );

        }


        proveedores =
            await response.json();


        /*
         * Nos aseguramos de trabajar con un arreglo.
         */

        if (!Array.isArray(proveedores)) {

            proveedores = [];

        }


        console.log(
            "🚚 Proveedores cargados:",
            proveedores
        );


        renderProveedores(proveedores);

        actualizarKPIs();


    } catch (error) {

        console.error(
            "❌ Error cargando proveedores:",
            error
        );


        const tabla =
            document.getElementById(
                "tablaProveedores"
            );


        if (tabla) {

            tabla.innerHTML = `

<tr>

<td
    colspan="${esAdmin() ? 7 : 6}"
    class="proveedores-empty"
>

    <i class="fa-solid fa-triangle-exclamation"></i>

    <span>
        No fue posible cargar los proveedores.
    </span>

</td>

</tr>

`;

        }


        /*
         * Solo mostramos SweetAlert si SweetAlert2
         * está disponible.
         */

        if (typeof Swal !== "undefined") {

            Swal.fire({

                icon: "error",

                title: "Error",

                text:
                    "No fue posible cargar los proveedores."

            });

        }

    }

}


// =====================================================
// 🎨 RENDERIZAR TABLA
// =====================================================

function renderProveedores(lista) {

    const tabla =
        document.getElementById(
            "tablaProveedores"
        );


    const vacio =
        document.getElementById(
            "proveedoresVacio"
        );


    if (!tabla) {

        console.error(
            "❌ No existe #tablaProveedores"
        );

        return;

    }


    tabla.innerHTML = "";


    // -------------------------------------------------
    // SIN RESULTADOS
    // -------------------------------------------------

    if (
        !Array.isArray(lista) ||
        lista.length === 0
    ) {

        if (vacio) {

            vacio.style.display = "block";

        }

        return;

    }


    // -------------------------------------------------
    // OCULTAR ESTADO VACÍO
    // -------------------------------------------------

    if (vacio) {

        vacio.style.display = "none";

    }


    // -------------------------------------------------
    // CREAR FILAS
    // -------------------------------------------------

    lista.forEach(proveedor => {

        const estado =
            String(
                proveedor.estado || "ACTIVO"
            ).toUpperCase();


        const claseEstado =
            estado === "ACTIVO"
                ? "estado-activo"
                : "estado-inactivo";


        const iconoEstado =
            estado === "ACTIVO"
                ? "fa-circle-check"
                : "fa-circle-xmark";


        // -------------------------------------------------
        // NOMBRE
        // -------------------------------------------------

        const nombre =
            escapeHTML(
                proveedor.nombre || "Sin nombre"
            );


        // -------------------------------------------------
        // ACCIONES
        // -------------------------------------------------

        let acciones = "";


        if (esAdmin()) {

            acciones = `

<td>

    <div class="proveedor-acciones">

        <button
            type="button"
            class="proveedor-btn-table proveedor-btn-edit"
            title="Editar proveedor"
            onclick="editarProveedor(${Number(proveedor.id)})"
        >

            <i class="fa-solid fa-pen"></i>

        </button>


        <button
            type="button"
            class="proveedor-btn-table proveedor-btn-delete"
            title="Eliminar proveedor"
            onclick="eliminarProveedor(${Number(proveedor.id)})"
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

        tabla.innerHTML += `

<tr>

    <td>

        <div class="proveedor-nombre">

            <div class="proveedor-avatar">

                <i class="fa-solid fa-truck"></i>

            </div>

            <span>
                ${nombre}
            </span>

        </div>

    </td>


    <td class="proveedor-nit">

        ${escapeHTML(
            proveedor.nit || "—"
        )}

    </td>


    <td>

        ${escapeHTML(
            proveedor.telefono || "—"
        )}

    </td>


    <td class="proveedor-email">

        ${escapeHTML(
            proveedor.email || "—"
        )}

    </td>


    <td>

        ${escapeHTML(
            proveedor.contacto || "—"
        )}

    </td>


    <td>

        <span class="estado-badge ${claseEstado}">

            <i class="fa-solid ${iconoEstado}"></i>

            ${escapeHTML(estado)}

        </span>

    </td>


    ${acciones}

</tr>

`;

    });

}


// =====================================================
// 📊 ACTUALIZAR KPIs
// =====================================================

function actualizarKPIs() {

    // -------------------------------------------------
    // TOTAL
    // -------------------------------------------------

    const total =
        document.getElementById(
            "totalProveedores"
        );


    if (total) {

        total.textContent =
            proveedores.length;

    }


    // -------------------------------------------------
    // ACTIVOS
    // -------------------------------------------------

    const activos =
        proveedores.filter(
            proveedor =>
                String(
                    proveedor.estado || ""
                ).toUpperCase() === "ACTIVO"
        ).length;


    const activosElemento =
        document.getElementById(
            "proveedoresActivos"
        );


    if (activosElemento) {

        activosElemento.textContent =
            activos;

    }


    // -------------------------------------------------
    // EMPRESAS
    // -------------------------------------------------

    const empresas =
        document.getElementById(
            "totalEmpresas"
        );


    if (empresas) {

        empresas.textContent =
            proveedores.length;

    }

}


// =====================================================
// 🔍 FILTRAR PROVEEDORES
// =====================================================

function filtrarProveedores() {

    const buscarTopbar =
        document.getElementById(
            "buscarTopbar"
        );


    if (!buscarTopbar) {

        return;

    }


    const texto =
        normalizarTexto(
            buscarTopbar.value
        );


    // -------------------------------------------------
    // SIN BÚSQUEDA
    // -------------------------------------------------

    if (texto === "") {

        renderProveedores(proveedores);

        return;

    }


    // -------------------------------------------------
    // FILTRAR
    // -------------------------------------------------

    const filtrados =
        proveedores.filter(proveedor => {

            return (

                normalizarTexto(
                    proveedor.nombre
                ).includes(texto)

                ||

                normalizarTexto(
                    proveedor.nit
                ).includes(texto)

                ||

                normalizarTexto(
                    proveedor.telefono
                ).includes(texto)

                ||

                normalizarTexto(
                    proveedor.email
                ).includes(texto)

                ||

                normalizarTexto(
                    proveedor.direccion
                ).includes(texto)

                ||

                normalizarTexto(
                    proveedor.contacto
                ).includes(texto)

                ||

                normalizarTexto(
                    proveedor.estado
                ).includes(texto)

            );

        });


    renderProveedores(filtrados);

}


// =====================================================
// ✏ EDITAR PROVEEDOR
// =====================================================

function editarProveedor(id) {

    if (!esAdmin()) {

        return;

    }


    /*
     * Si ya existe una operación en curso,
     * no permitimos abrir otra edición.
     */

    if (operacionProveedorEnCurso) {

        return;

    }


    const proveedor =
        proveedores.find(
            item =>
                Number(item.id) === Number(id)
        );


    if (!proveedor) {

        Swal.fire({

            icon: "error",

            title: "Proveedor no encontrado",

            text:
                "No fue posible encontrar el proveedor seleccionado."

        });

        return;

    }


    // -------------------------------------------------
    // CARGAR ID
    // -------------------------------------------------

    const idInput =
        document.getElementById("id");


    if (idInput) {

        idInput.value =
            proveedor.id ?? "";

    }


    // -------------------------------------------------
    // CARGAR CAMPOS
    // -------------------------------------------------

    establecerValor(
        "nombre",
        proveedor.nombre
    );


    establecerValor(
        "nit",
        proveedor.nit
    );


    establecerValor(
        "telefono",
        proveedor.telefono
    );


    establecerValor(
        "email",
        proveedor.email
    );


    establecerValor(
        "direccion",
        proveedor.direccion
    );


    establecerValor(
        "contacto",
        proveedor.contacto
    );


    establecerValor(
        "estado",
        proveedor.estado || "ACTIVO"
    );


    // -------------------------------------------------
    // CAMBIAR MODAL A MODO EDICIÓN
    // -------------------------------------------------

    cambiarModoModalProveedor("editar");


    // -------------------------------------------------
    // ABRIR
    // -------------------------------------------------

    abrirModalProveedorEdicion();

}


// =====================================================
// ➕ NUEVO PROVEEDOR
// =====================================================

function abrirModalProveedor() {

    if (!esAdmin()) {

        return;

    }


    if (operacionProveedorEnCurso) {

        return;

    }


    const modal =
        document.getElementById(
            "modalProveedor"
        );


    if (!modal) {

        console.error(
            "❌ No existe #modalProveedor"
        );

        return;

    }


    // -------------------------------------------------
    // LIMPIAR
    // -------------------------------------------------

    limpiarFormularioProveedor();


    // -------------------------------------------------
    // MODO NUEVO
    // -------------------------------------------------

    cambiarModoModalProveedor("nuevo");


    // -------------------------------------------------
    // ABRIR MODAL
    // -------------------------------------------------

    modal.classList.add("active");

    document.body.style.overflow =
        "hidden";


    // -------------------------------------------------
    // ENFOCAR NOMBRE
    // -------------------------------------------------

    setTimeout(() => {

        const nombre =
            document.getElementById(
                "nombre"
            );


        if (nombre) {

            nombre.focus();

        }

    }, 150);

}


// =====================================================
// ✏ ABRIR MODAL EDICIÓN
// =====================================================

function abrirModalProveedorEdicion() {

    const modal =
        document.getElementById(
            "modalProveedor"
        );


    if (!modal) {

        return;

    }


    modal.classList.add("active");

    document.body.style.overflow =
        "hidden";


    setTimeout(() => {

        const nombre =
            document.getElementById(
                "nombre"
            );


        if (nombre) {

            nombre.focus();

        }

    }, 150);

}


// =====================================================
// 🔄 CAMBIAR MODO DEL MODAL
// =====================================================

function cambiarModoModalProveedor(modo) {

    const btnGuardar =
        document.getElementById(
            "btnGuardar"
        );


    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    const titulo =
        document.getElementById(
            "tituloFormulario"
        );


    const subtitulo =
        document.getElementById(
            "subtituloFormulario"
        );


    // -------------------------------------------------
    // MODO NUEVO
    // -------------------------------------------------

    if (modo === "nuevo") {

        if (btnGuardar) {

            btnGuardar.style.display =
                "inline-flex";

        }


        if (btnActualizar) {

            btnActualizar.style.display =
                "none";

        }


        if (titulo) {

            titulo.innerHTML = `

<i class="fa-solid fa-circle-plus"></i>

Nuevo Proveedor

`;

        }


        if (subtitulo) {

            subtitulo.textContent =
                "Registra la información del proveedor";

        }

        return;

    }


    // -------------------------------------------------
    // MODO EDITAR
    // -------------------------------------------------

    if (modo === "editar") {

        if (btnGuardar) {

            btnGuardar.style.display =
                "none";

        }


        if (btnActualizar) {

            btnActualizar.style.display =
                "inline-flex";

        }


        if (titulo) {

            titulo.innerHTML = `

<i class="fa-solid fa-pen-to-square"></i>

Editar Proveedor

`;

        }


        if (subtitulo) {

            subtitulo.textContent =
                "Modifica la información del proveedor";

        }

    }

}


// =====================================================
// ❌ CERRAR MODAL
// =====================================================

function cerrarModalProveedor() {

    const modal =
        document.getElementById(
            "modalProveedor"
        );


    if (!modal) {

        return;

    }


    modal.classList.remove("active");

    document.body.style.overflow =
        "";

}


// =====================================================
// 💾 OBTENER DATOS
// =====================================================

function obtenerDatosProveedor() {

    return {

        nombre:
            obtenerValor("nombre"),

        nit:
            obtenerValor("nit"),

        telefono:
            obtenerValor("telefono"),

        email:
            obtenerValor("email"),

        direccion:
            obtenerValor("direccion"),

        contacto:
            obtenerValor("contacto"),

        estado:
            obtenerValor("estado") || "ACTIVO"

    };

}


// =====================================================
// ✅ VALIDAR FORMULARIO
// =====================================================

function validarFormularioProveedor() {

    const nombre =
        document.getElementById(
            "nombre"
        );


    if (!nombre) {

        return false;

    }


    // -------------------------------------------------
    // NOMBRE
    // -------------------------------------------------

    if (
        nombre.value.trim() === ""
    ) {

        Swal.fire({

            icon: "warning",

            title: "Validación",

            text:
                "Ingrese el nombre del proveedor."

        });


        nombre.focus();

        return false;

    }


    // -------------------------------------------------
    // EMAIL
    // -------------------------------------------------

    const email =
        document.getElementById(
            "email"
        );


    if (
        email &&
        email.value.trim() !== "" &&
        !email.checkValidity()
    ) {

        Swal.fire({

            icon: "warning",

            title: "Correo inválido",

            text:
                "Ingrese un correo electrónico válido."

        });


        email.focus();

        return false;

    }


    return true;

}


// =====================================================
// 💾 GUARDAR PROVEEDOR
// =====================================================

async function guardarProveedor() {

    // -------------------------------------------------
    // SEGURIDAD
    // -------------------------------------------------

    if (!esAdmin()) {

        return;

    }


    // -------------------------------------------------
    // EVITAR DOBLE REGISTRO
    // -------------------------------------------------

    if (operacionProveedorEnCurso) {

        console.warn(
            "⚠ Guardado ya en proceso."
        );

        return;

    }


    // -------------------------------------------------
    // VALIDAR
    // -------------------------------------------------

    if (!validarFormularioProveedor()) {

        return;

    }


    /*
     * Activamos el bloqueo ANTES del fetch.
     */

    operacionProveedorEnCurso = true;


    const btnGuardar =
        document.getElementById(
            "btnGuardar"
        );


    try {

        // -------------------------------------------------
        // BLOQUEAR BOTONES
        // -------------------------------------------------

        cambiarEstadoBotonesProveedor(true);


        if (btnGuardar) {

            btnGuardar.innerHTML = `

<i class="fa-solid fa-spinner fa-spin"></i>

Guardando...

`;

        }


        // -------------------------------------------------
        // OBTENER DATOS
        // -------------------------------------------------

        const proveedor =
            obtenerDatosProveedor();


        // -------------------------------------------------
        // POST
        // -------------------------------------------------

        const response =
            await fetch(
                "/proveedores/api",
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
                            proveedor
                        )

                }
            );


        // -------------------------------------------------
        // VALIDAR RESPUESTA
        // -------------------------------------------------

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response,
                    "No fue posible guardar el proveedor."
                )
            );

        }


        /*
         * MUY IMPORTANTE:
         *
         * El servidor ya confirmó el registro.
         *
         * Primero cerramos y liberamos la interfaz.
         * Después actualizamos la tabla.
         *
         * Así evitamos que el modal se quede atrapado
         * esperando cargar toda la tabla.
         */

        limpiarFormularioProveedor();

        cerrarModalProveedor();

        cambiarModoModalProveedor("nuevo");

        operacionProveedorEnCurso = false;

        cambiarEstadoBotonesProveedor(false);


        // -------------------------------------------------
        // MENSAJE
        // -------------------------------------------------

        Swal.fire({

            icon: "success",

            title: "Proveedor guardado",

            text:
                "El proveedor fue registrado correctamente.",

            confirmButtonText:
                "Aceptar"

        });


        // -------------------------------------------------
        // ACTUALIZAR TABLA
        // -------------------------------------------------

        cargarProveedores();


    } catch (error) {

        console.error(
            "❌ Error guardando proveedor:",
            error
        );


        /*
         * Liberamos el bloqueo si hubo error.
         */

        operacionProveedorEnCurso = false;

        cambiarEstadoBotonesProveedor(false);


        Swal.fire({

            icon: "error",

            title: "Error",

            text:
                error.message ||
                "No fue posible guardar el proveedor."

        });

    }

}


// =====================================================
// ✏ ACTUALIZAR PROVEEDOR
// =====================================================

async function actualizarProveedor() {

    // -------------------------------------------------
    // SEGURIDAD
    // -------------------------------------------------

    if (!esAdmin()) {

        return;

    }


    // -------------------------------------------------
    // EVITAR DOBLE ACTUALIZACIÓN
    // -------------------------------------------------

    if (operacionProveedorEnCurso) {

        console.warn(
            "⚠ Actualización ya en proceso."
        );

        return;

    }


    // -------------------------------------------------
    // VALIDAR
    // -------------------------------------------------

    if (!validarFormularioProveedor()) {

        return;

    }


    // -------------------------------------------------
    // OBTENER ID
    // -------------------------------------------------

    const id =
        obtenerValor("id");


    if (!id) {

        Swal.fire({

            icon: "warning",

            title: "Proveedor no seleccionado",

            text:
                "Seleccione un proveedor para actualizar."

        });

        return;

    }


    /*
     * Activamos bloqueo ANTES del PUT.
     */

    operacionProveedorEnCurso = true;


    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    try {

        // -------------------------------------------------
        // BLOQUEAR BOTONES
        // -------------------------------------------------

        cambiarEstadoBotonesProveedor(true);


        if (btnActualizar) {

            btnActualizar.innerHTML = `

<i class="fa-solid fa-spinner fa-spin"></i>

Actualizando...

`;

        }


        // -------------------------------------------------
        // OBTENER DATOS
        // -------------------------------------------------

        const proveedor =
            obtenerDatosProveedor();


        // -------------------------------------------------
        // PUT
        // -------------------------------------------------

        const response =
            await fetch(
                `/proveedores/api/${encodeURIComponent(id)}`,
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
                            proveedor
                        )

                }
            );


        // -------------------------------------------------
        // VALIDAR RESPUESTA
        // -------------------------------------------------

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response,
                    "No fue posible actualizar el proveedor."
                )
            );

        }


        /*
         * El PUT terminó correctamente.
         *
         * Cerramos primero el modal.
         */

        limpiarFormularioProveedor();

        cerrarModalProveedor();

        cambiarModoModalProveedor("nuevo");

        operacionProveedorEnCurso = false;

        cambiarEstadoBotonesProveedor(false);


        // -------------------------------------------------
        // MENSAJE
        // -------------------------------------------------

        Swal.fire({

            icon: "success",

            title: "Proveedor actualizado",

            text:
                "Los cambios fueron guardados correctamente.",

            confirmButtonText:
                "Aceptar"

        });


        // -------------------------------------------------
        // RECARGAR TABLA
        // -------------------------------------------------

        cargarProveedores();


    } catch (error) {

        console.error(
            "❌ Error actualizando proveedor:",
            error
        );


        operacionProveedorEnCurso = false;

        cambiarEstadoBotonesProveedor(false);


        Swal.fire({

            icon: "error",

            title: "Error",

            text:
                error.message ||
                "No fue posible actualizar el proveedor."

        });

    }

}


// =====================================================
// 🗑 ELIMINAR PROVEEDOR
// =====================================================

async function eliminarProveedor(id) {

    // -------------------------------------------------
    // SEGURIDAD
    // -------------------------------------------------

    if (!esAdmin()) {

        return;

    }


    // -------------------------------------------------
    // EVITAR DOBLE OPERACIÓN
    // -------------------------------------------------

    if (operacionProveedorEnCurso) {

        return;

    }


    const proveedor =
        proveedores.find(
            item =>
                Number(item.id) === Number(id)
        );


    if (!proveedor) {

        Swal.fire({

            icon: "warning",

            title: "Proveedor no encontrado",

            text:
                "No fue posible encontrar el proveedor."

        });

        return;

    }


    // -------------------------------------------------
    // CONFIRMAR
    // -------------------------------------------------

    const confirmar =
        await Swal.fire({

            title:
                "¿Eliminar proveedor?",

            text:
                `Se eliminará "${proveedor.nombre || "este proveedor"}". Esta acción no se puede deshacer.`,

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
                "#64748b"

        });


    if (!confirmar.isConfirmed) {

        return;

    }


    /*
     * Activar bloqueo después de confirmar.
     */

    operacionProveedorEnCurso = true;


    try {

        const response =
            await fetch(
                `/proveedores/api/${encodeURIComponent(id)}`,
                {

                    method: "DELETE",

                    headers: {

                        "Accept":
                            "application/json"

                    }

                }
            );


        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response,
                    "No fue posible eliminar el proveedor."
                )
            );

        }


        /*
         * Liberar inmediatamente después de confirmar
         * que el DELETE terminó correctamente.
         */

        operacionProveedorEnCurso = false;


        limpiarFormularioProveedor();


        Swal.fire({

            icon:
                "success",

            title:
                "Proveedor eliminado",

            text:
                "El proveedor fue eliminado correctamente.",

            confirmButtonText:
                "Aceptar"

        });


        // -------------------------------------------------
        // ACTUALIZAR TABLA
        // -------------------------------------------------

        cargarProveedores();


    } catch (error) {

        console.error(
            "❌ Error eliminando proveedor:",
            error
        );


        operacionProveedorEnCurso = false;


        Swal.fire({

            icon:
                "error",

            title:
                "Error",

            text:
                error.message ||
                "No fue posible eliminar el proveedor."

        });

    }

}


// =====================================================
// 🔘 ESTADO DE BOTONES
// =====================================================

function cambiarEstadoBotonesProveedor(bloquear) {

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


// =====================================================
// 🧹 LIMPIAR FORMULARIO
// =====================================================

function limpiarFormularioProveedor() {

    const id =
        document.getElementById("id");

    const nombre =
        document.getElementById("nombre");

    const nit =
        document.getElementById("nit");

    const telefono =
        document.getElementById("telefono");

    const email =
        document.getElementById("email");

    const direccion =
        document.getElementById("direccion");

    const contacto =
        document.getElementById("contacto");

    const estado =
        document.getElementById("estado");


    if (id) {

        id.value = "";

    }


    if (nombre) {

        nombre.value = "";

    }


    if (nit) {

        nit.value = "";

    }


    if (telefono) {

        telefono.value = "";

    }


    if (email) {

        email.value = "";

    }


    if (direccion) {

        direccion.value = "";

    }


    if (contacto) {

        contacto.value = "";

    }


    if (estado) {

        estado.value = "ACTIVO";

    }

}


// =====================================================
// ❌ CANCELAR EDICIÓN
// =====================================================

function cancelarEdicion() {

    /*
     * No permitimos cancelar mientras se está enviando
     * una petición.
     */

    if (operacionProveedorEnCurso) {

        return;

    }


    limpiarFormularioProveedor();


    cambiarModoModalProveedor("nuevo");


    cerrarModalProveedor();

}


// =====================================================
// ⌨ ESC
// =====================================================

function manejarTeclaEscape(event) {

    if (event.key !== "Escape") {

        return;

    }


    const modal =
        document.getElementById(
            "modalProveedor"
        );


    if (
        modal &&
        modal.classList.contains("active")
    ) {

        cancelarEdicion();

    }

}


// =====================================================
// 🛡 MENSAJE ERROR BACKEND
// =====================================================

async function obtenerMensajeError(
    response,
    mensajePredeterminado
) {

    try {

        const texto =
            await response.text();


        if (!texto) {

            return mensajePredeterminado;

        }


        /*
         * Intentamos convertir la respuesta
         * a JSON.
         */

        try {

            const data =
                JSON.parse(texto);


            if (data.message) {

                return data.message;

            }


            if (data.error) {

                return data.error;

            }


            if (data.mensaje) {

                return data.mensaje;

            }


            /*
             * Algunos backends pueden enviar:
             *
             * { "detail": "..." }
             */

            if (data.detail) {

                return data.detail;

            }


        } catch (errorJSON) {

            /*
             * Si no es JSON, usamos el texto
             * enviado directamente por el backend.
             */

            return texto;

        }


        return mensajePredeterminado;


    } catch (error) {

        return mensajePredeterminado;

    }

}


// =====================================================
// 🔤 NORMALIZAR TEXTO
// =====================================================

function normalizarTexto(valor) {

    if (
        valor === null ||
        valor === undefined
    ) {

        return "";

    }


    return String(valor)

        .toLowerCase()

        .normalize("NFD")

        .replace(
            /[\u0300-\u036f]/g,
            ""
        )

        .trim();

}


// =====================================================
// 🛡 ESCAPAR HTML
// =====================================================

function escapeHTML(valor) {

    if (
        valor === null ||
        valor === undefined
    ) {

        return "";

    }


    return String(valor)

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


// =====================================================
// 🧰 OBTENER VALOR
// =====================================================

function obtenerValor(id) {

    const elemento =
        document.getElementById(id);


    if (!elemento) {

        return "";

    }


    return String(
        elemento.value ?? ""
    ).trim();

}


// =====================================================
// 🧰 ESTABLECER VALOR
// =====================================================

function establecerValor(id, valor) {

    const elemento =
        document.getElementById(id);


    if (!elemento) {

        return;

    }


    elemento.value =
        valor ?? "";

}