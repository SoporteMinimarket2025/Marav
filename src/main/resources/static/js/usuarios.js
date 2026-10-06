// ======================================================
// 👥 MARAV | MÓDULO USUARIOS
// ======================================================
//
// Funciones:
//
// ✔ Listar usuarios del negocio actual
// ✔ Buscar mediante el Topbar
// ✔ Mostrar estadísticas
// ✔ Crear usuario
// ✔ Editar usuario
// ✔ Actualizar usuario
// ✔ Activar / desactivar usuario
// ✔ Eliminar usuario
// ✔ Control ADMIN / EMPLEADO
// ✔ Modal de usuario
// ✔ Validaciones
// ✔ Evitar doble envío
//
// IMPORTANTE:
//
// La seguridad real debe estar protegida también
// en Spring Security y en el backend.
//
// El frontend NO debe ser la única protección.
//
// ======================================================



// ======================================================
// 🌐 VARIABLES GLOBALES
// ======================================================

let usuariosGlobal = [];


// ------------------------------------------------------
// 🔒 BLOQUEO DE OPERACIÓN
// ------------------------------------------------------
// Evita:
//
// POST POST
// PUT PUT
// DELETE DELETE
//
// cuando el usuario hace doble clic.
// ------------------------------------------------------

let operacionUsuarioEnCurso = false;



// ======================================================
// 🚀 INICIAR MÓDULO
// ======================================================

document.addEventListener(
    "DOMContentLoaded",
    () => {

        console.log(
            "👥 Módulo Usuarios MARAV iniciado"
        );


        configurarBuscadorTopbar();

        configurarEventos();

        cargarUsuarios();

    }
);



// ======================================================
// 🔐 COMPROBAR ADMIN
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
// 🔎 BUSCADOR GLOBAL DEL TOPBAR
// ======================================================

function configurarBuscadorTopbar() {

    const buscador =
        document.getElementById(
            "buscarTopbar"
        );


    if (!buscador) {

        console.warn(
            "⚠️ No se encontró #buscarTopbar"
        );

        return;

    }


    // --------------------------------------------------
    // Placeholder
    // --------------------------------------------------

    buscador.placeholder =
        "Buscar usuarios...";


    // --------------------------------------------------
    // Evitar registrar el evento dos veces
    // --------------------------------------------------

    if (
        buscador.dataset.usuariosConfigurado ===
        "true"
    ) {

        return;

    }


    buscador.dataset.usuariosConfigurado =
        "true";


    // --------------------------------------------------
    // Buscar mientras escribe
    // --------------------------------------------------

    buscador.addEventListener(
        "input",
        () => {

            renderizarUsuarios();

        }
    );

}



// ======================================================
// 🎛️ CONFIGURAR EVENTOS
// ======================================================

function configurarEventos() {

    // --------------------------------------------------
    // NUEVO USUARIO
    // --------------------------------------------------

    const btnNuevo =
        document.getElementById(
            "btnNuevoUsuario"
        );


    if (btnNuevo) {

        // Evitar duplicar listener
        if (
            btnNuevo.dataset.usuariosConfigurado !==
            "true"
        ) {

            btnNuevo.dataset.usuariosConfigurado =
                "true";


            btnNuevo.addEventListener(
                "click",
                abrirModalUsuario
            );

        }

    }



    // --------------------------------------------------
    // FORMULARIO
    // --------------------------------------------------

    const form =
        document.getElementById(
            "formUsuario"
        );


    if (form) {

        if (
            form.dataset.usuariosConfigurado !==
            "true"
        ) {

            form.dataset.usuariosConfigurado =
                "true";


            form.addEventListener(
                "submit",
                manejarSubmitFormulario
            );

        }

    }



    // --------------------------------------------------
    // CANCELAR
    // --------------------------------------------------

    const btnCancelar =
        document.getElementById(
            "btnCancelar"
        );


    if (btnCancelar) {

        btnCancelar.addEventListener(
            "click",
            cancelarFormularioUsuario
        );

    }



    // --------------------------------------------------
    // CERRAR MODAL
    // --------------------------------------------------

    const btnCerrar =
        document.getElementById(
            "btnCerrarModal"
        );


    if (btnCerrar) {

        btnCerrar.addEventListener(
            "click",
            cancelarFormularioUsuario
        );

    }



    // --------------------------------------------------
    // CLICK FUERA DEL MODAL
    // --------------------------------------------------

    const modal =
        document.getElementById(
            "modalUsuario"
        );


    if (modal) {

        modal.addEventListener(
            "click",
            event => {

                if (
                    event.target === modal &&
                    !operacionUsuarioEnCurso
                ) {

                    cancelarFormularioUsuario();

                }

            }
        );

    }



    // --------------------------------------------------
    // ESC
    // --------------------------------------------------

    document.addEventListener(
        "keydown",
        event => {

            if (
                event.key === "Escape" &&
                !operacionUsuarioEnCurso
            ) {

                cancelarFormularioUsuario();

            }

        }
    );

}



// ======================================================
// 📥 CARGAR USUARIOS
// ======================================================

async function cargarUsuarios() {

    try {

        mostrarCargandoTabla();


        /*
         * IMPORTANTE:
         *
         * Este endpoint debe devolver únicamente
         * los usuarios pertenecientes al negocio
         * del usuario autenticado.
         *
         * NO estamos enviando negocioId desde
         * JavaScript.
         *
         * El backend debe obtener el negocioId
         * desde el usuario autenticado.
         */

        const respuesta =
            await fetch(
                "/api/usuarios/mios",
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
                await obtenerMensajeError(
                    respuesta
                )
            );

        }


        const data =
            await respuesta.json();


        usuariosGlobal =
            Array.isArray(data)
                ? data
                : [];


        console.log(
            "👥 Usuarios cargados:",
            usuariosGlobal
        );


        actualizarEstadisticas();

        renderizarUsuarios();


    } catch (error) {

        console.error(
            "❌ Error cargando usuarios:",
            error
        );


        usuariosGlobal = [];

        actualizarEstadisticas();


        mostrarErrorTabla(
            error.message ||
            "No fue posible cargar los usuarios."
        );

    }

}



// ======================================================
// 📊 ESTADÍSTICAS
// ======================================================

function actualizarEstadisticas() {

    const total =
        usuariosGlobal.length;


    const admins =
        usuariosGlobal.filter(
            usuario =>
                normalizar(
                    usuario.rol
                ) === "admin"
        ).length;


    const empleados =
        usuariosGlobal.filter(
            usuario =>
                normalizar(
                    usuario.rol
                ) === "empleado"
        ).length;


    const activos =
        usuariosGlobal.filter(
            usuario =>
                obtenerEstadoBoolean(
                    usuario.estado
                )
        ).length;


    actualizarTexto(
        "totalUsuarios",
        total
    );


    actualizarTexto(
        "totalAdmins",
        admins
    );


    actualizarTexto(
        "totalEmpleados",
        empleados
    );


    actualizarTexto(
        "totalActivos",
        activos
    );

}



// ======================================================
// 📝 ACTUALIZAR TEXTO
// ======================================================

function actualizarTexto(
    id,
    valor
) {

    const elemento =
        document.getElementById(id);


    if (elemento) {

        elemento.textContent =
            valor;

    }

}



// ======================================================
// 🔎 RENDERIZAR USUARIOS
// ======================================================

function renderizarUsuarios() {

    const tabla =
        document.getElementById(
            "tablaUsuarios"
        );


    if (!tabla) {

        console.warn(
            "⚠️ No se encontró #tablaUsuarios"
        );

        return;

    }


    // --------------------------------------------------
    // BUSCADOR
    // --------------------------------------------------

    const buscador =
        document.getElementById(
            "buscarTopbar"
        );


    const texto =
        buscador
            ? normalizar(
                buscador.value
            )
            : "";


    // --------------------------------------------------
    // FILTRAR
    // --------------------------------------------------

    const filtrados =
        usuariosGlobal.filter(
            usuario => {

                const nombre =
                    normalizar(
                        usuario.nombre
                    );


                const tipo =
                    normalizar(
                        usuario.tipoIdentificacion
                    );


                const documento =
                    normalizar(
                        usuario.numeroIdentificacion
                    );


                const email =
                    normalizar(
                        usuario.email
                    );


                const rol =
                    normalizar(
                        usuario.rol
                    );


                const estado =
                    obtenerTextoEstado(
                        usuario.estado
                    );


                return (

                    nombre.includes(texto) ||

                    tipo.includes(texto) ||

                    documento.includes(texto) ||

                    email.includes(texto) ||

                    rol.includes(texto) ||

                    estado.includes(texto)

                );

            }
        );



    // ==================================================
    // 🚫 SIN RESULTADOS
    // ==================================================

    if (
        filtrados.length === 0
    ) {

        const tieneBusqueda =
            texto.length > 0;


        tabla.innerHTML = `

            <tr>

                <td
                    colspan="${esAdmin() ? 7 : 6}"
                    class="tabla-vacia">

                    <i class="fa-solid ${
            tieneBusqueda
                ? "fa-magnifying-glass"
                : "fa-users-slash"
        }"></i>

                    <strong>

                        ${
            tieneBusqueda
                ? "No se encontraron usuarios"
                : "No hay usuarios registrados"
        }

                    </strong>

                    <span>

                        ${
            tieneBusqueda
                ? "Intenta realizar otra búsqueda."
                : "Aún no existen usuarios para mostrar."
        }

                    </span>

                </td>

            </tr>

        `;

        return;

    }



    // ==================================================
    // 📋 CREAR FILAS
    // ==================================================

    tabla.innerHTML =
        filtrados
            .map(
                generarFilaUsuario
            )
            .join("");

}



// ======================================================
// 🧱 GENERAR FILA
// ======================================================

function generarFilaUsuario(
    usuario
) {

    const id =
        Number(usuario.id);


    const nombre =
        escaparHTML(
            usuario.nombre ||
            "Sin nombre"
        );


    const inicial =
        obtenerInicial(
            usuario.nombre
        );


    const tipo =
        escaparHTML(
            usuario.tipoIdentificacion ||
            ""
        );


    const documento =
        escaparHTML(
            usuario.numeroIdentificacion ||
            ""
        );


    const email =
        escaparHTML(
            usuario.email ||
            ""
        );


    const rol =
        normalizar(
            usuario.rol
        );


    const activo =
        obtenerEstadoBoolean(
            usuario.estado
        );


    const fecha =
        formatearFecha(
            usuario.fechaCreacion
        );



    // ==================================================
    // 🛡️ BADGE ROL
    // ==================================================

    const badgeRol =
        rol === "admin"

            ? `

                <span
                    class="badge-rol badge-admin">

                    <i
                        class="fa-solid fa-shield-halved">
                    </i>

                    Administrador

                </span>

              `

            : `

                <span
                    class="badge-rol badge-empleado">

                    <i
                        class="fa-solid fa-user-tie">
                    </i>

                    Empleado

                </span>

              `;



    // ==================================================
    // 🟢 BADGE ESTADO
    // ==================================================

    const badgeEstado =
        activo

            ? `

                <span
                    class="badge-estado badge-activo">

                    <i
                        class="fa-solid fa-circle-check">
                    </i>

                    Activo

                </span>

              `

            : `

                <span
                    class="badge-estado badge-inactivo">

                    <i
                        class="fa-solid fa-circle-xmark">
                    </i>

                    Inactivo

                </span>

              `;



    // ==================================================
    // ⚙️ ACCIONES ADMIN
    // ==================================================

    let acciones = "";


    if (esAdmin()) {

        acciones = `

            <td>

                <div class="acciones">

                    <button
                        type="button"
                        class="btn-accion btn-editar"
                        title="Editar usuario"
                        ${
            operacionUsuarioEnCurso
                ? "disabled"
                : ""
        }
                        onclick="editarUsuario(${id})">

                        <i
                            class="fa-solid fa-pen">
                        </i>

                    </button>


                    <button
                        type="button"
                        class="btn-accion btn-estado"
                        title="${
            activo
                ? "Desactivar usuario"
                : "Activar usuario"
        }"
                        ${
            operacionUsuarioEnCurso
                ? "disabled"
                : ""
        }
                        onclick="cambiarEstadoUsuario(${id})">

                        <i
                            class="fa-solid ${
            activo
                ? "fa-toggle-on"
                : "fa-toggle-off"
        }">
                        </i>

                    </button>


                    <button
                        type="button"
                        class="btn-accion btn-eliminar"
                        title="Eliminar usuario"
                        ${
            operacionUsuarioEnCurso
                ? "disabled"
                : ""
        }
                        onclick="confirmarEliminarUsuario(${id})">

                        <i
                            class="fa-solid fa-trash">
                        </i>

                    </button>

                </div>

            </td>

        `;

    }



    // ==================================================
    // 📋 FILA COMPLETA
    // ==================================================

    return `

        <tr>

            <td>

                <div class="usuario-nombre">

                    <div class="usuario-avatar">

                        ${inicial}

                    </div>

                    <span>
                        ${nombre}
                    </span>

                </div>

            </td>


            <td>

                ${
        tipo
            ? `${tipo} ${documento}`
            : documento || "—"
    }

            </td>


            <td>

                ${
        email || "—"
    }

            </td>


            <td>

                ${badgeRol}

            </td>


            <td>

                ${badgeEstado}

            </td>


            <td>

                ${fecha}

            </td>


            ${acciones}

        </tr>

    `;

}



// ======================================================
// ➕ NUEVO USUARIO
// ======================================================

function abrirModalUsuario() {

    if (!esAdmin()) {

        mostrarError(
            "No tienes permisos para crear usuarios."
        );

        return;

    }


    if (operacionUsuarioEnCurso) {

        return;

    }


    const modal =
        document.getElementById(
            "modalUsuario"
        );


    const form =
        document.getElementById(
            "formUsuario"
        );


    if (!modal || !form) {

        mostrarError(
            "No se encontró el formulario de usuarios."
        );

        return;

    }


    // --------------------------------------------------
    // Limpiar
    // --------------------------------------------------

    form.reset();


    document.getElementById(
        "usuarioId"
    ).value = "";


    // --------------------------------------------------
    // Valores por defecto
    // --------------------------------------------------

    const rol =
        document.getElementById(
            "rol"
        );


    if (rol) {

        rol.value =
            "EMPLEADO";

    }


    const estado =
        document.getElementById(
            "estado"
        );


    if (estado) {

        estado.value =
            "true";

    }


    // --------------------------------------------------
    // Contraseña
    // --------------------------------------------------

    const password =
        document.getElementById(
            "password"
        );


    if (password) {

        password.required =
            true;

        password.value = "";

    }


    const passwordObligatoria =
        document.getElementById(
            "passwordObligatoria"
        );


    if (passwordObligatoria) {

        passwordObligatoria.style.display =
            "inline";

    }


    const ayudaPassword =
        document.getElementById(
            "ayudaPassword"
        );


    if (ayudaPassword) {

        ayudaPassword.textContent =
            "La contraseña es obligatoria al crear.";

    }


    // --------------------------------------------------
    // Título
    // --------------------------------------------------

    const titulo =
        document.getElementById(
            "tituloModal"
        );


    if (titulo) {

        titulo.innerHTML = `

            <i class="fa-solid fa-user-plus"></i>

            Nuevo usuario

        `;

    }


    mostrarBotonGuardarUsuario();


    // --------------------------------------------------
    // Abrir
    // --------------------------------------------------

    modal.classList.add(
        "mostrar"
    );


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
// ✏️ EDITAR USUARIO
// ======================================================

async function editarUsuario(id) {

    if (!esAdmin()) {

        mostrarError(
            "No tienes permisos para editar usuarios."
        );

        return;

    }


    if (operacionUsuarioEnCurso) {

        return;

    }


    try {

        const respuesta =
            await fetch(
                `/api/usuarios/${encodeURIComponent(id)}`,
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
                await obtenerMensajeError(
                    respuesta
                )
            );

        }


        const usuario =
            await respuesta.json();


        // ------------------------------------------------
        // ID
        // ------------------------------------------------

        establecerValor(
            "usuarioId",
            usuario.id
        );


        // ------------------------------------------------
        // DATOS
        // ------------------------------------------------

        establecerValor(
            "nombre",
            usuario.nombre
        );


        establecerValor(
            "tipoIdentificacion",
            usuario.tipoIdentificacion
        );


        establecerValor(
            "numeroIdentificacion",
            usuario.numeroIdentificacion
        );


        establecerValor(
            "email",
            usuario.email
        );


        establecerValor(
            "rol",
            usuario.rol || "EMPLEADO"
        );


        establecerValor(
            "estado",
            obtenerEstadoBoolean(
                usuario.estado
            )
                ? "true"
                : "false"
        );


        // ------------------------------------------------
        // PASSWORD
        // ------------------------------------------------

        const password =
            document.getElementById(
                "password"
            );


        if (password) {

            password.value = "";

            password.required =
                false;

        }


        const passwordObligatoria =
            document.getElementById(
                "passwordObligatoria"
            );


        if (passwordObligatoria) {

            passwordObligatoria.style.display =
                "none";

        }


        const ayudaPassword =
            document.getElementById(
                "ayudaPassword"
            );


        if (ayudaPassword) {

            ayudaPassword.textContent =
                "Déjala vacía para conservar la contraseña actual.";

        }


        // ------------------------------------------------
        // TÍTULO
        // ------------------------------------------------

        const titulo =
            document.getElementById(
                "tituloModal"
            );


        if (titulo) {

            titulo.innerHTML = `

                <i class="fa-solid fa-user-pen"></i>

                Editar usuario

            `;

        }


        mostrarBotonActualizarUsuario();


        // ------------------------------------------------
        // MODAL
        // ------------------------------------------------

        const modal =
            document.getElementById(
                "modalUsuario"
            );


        if (modal) {

            modal.classList.add(
                "mostrar"
            );

            document.body.style.overflow =
                "hidden";

        }


        setTimeout(
            () => {

                document.getElementById(
                    "nombre"
                )?.focus();

            },
            100
        );


    } catch (error) {

        console.error(
            "❌ Error editando usuario:",
            error
        );


        mostrarError(
            error.message ||
            "No fue posible cargar el usuario."
        );

    }

}



// ======================================================
// 💾 MANEJAR SUBMIT
// ======================================================

async function manejarSubmitFormulario(
    event
) {

    event.preventDefault();


    if (!esAdmin()) {

        mostrarError(
            "No tienes permisos para realizar esta operación."
        );

        return;

    }


    if (operacionUsuarioEnCurso) {

        return;

    }


    const id =
        document.getElementById(
            "usuarioId"
        )?.value;


    if (id) {

        await actualizarUsuario();

    } else {

        await guardarUsuario();

    }

}



// ======================================================
// 💾 GUARDAR USUARIO
// ======================================================

async function guardarUsuario() {

    if (!esAdmin()) {

        return;

    }


    if (operacionUsuarioEnCurso) {

        return;

    }


    // --------------------------------------------------
    // Validar ANTES de bloquear
    // --------------------------------------------------

    if (
        !validarFormulario(false)
    ) {

        return;

    }


    const datos =
        obtenerDatosFormulario();


    // --------------------------------------------------
    // Bloquear
    // --------------------------------------------------

    bloquearOperacionUsuario();


    try {

        const respuesta =
            await fetch(
                "/api/usuarios",
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


        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta
                )
            );

        }


        // ------------------------------------------------
        // Leer respuesta si existe
        // ------------------------------------------------

        try {

            await respuesta.json();

        } catch {

            // El backend puede devolver
            // una respuesta sin cuerpo.

        }


        // ------------------------------------------------
        // Cerrar modal
        // ------------------------------------------------

        limpiarFormularioUsuario();

        cerrarModalUsuario();


        // ------------------------------------------------
        // Liberar bloqueo ANTES de refrescar
        // ------------------------------------------------

        desbloquearOperacionUsuario();


        // ------------------------------------------------
        // Confirmación
        // ------------------------------------------------

        await mostrarExito(
            "Usuario creado correctamente."
        );


        // ------------------------------------------------
        // Actualizar lista
        // ------------------------------------------------

        await cargarUsuarios();


    } catch (error) {

        console.error(
            "❌ Error creando usuario:",
            error
        );


        mostrarError(
            error.message ||
            "No fue posible crear el usuario."
        );


    } finally {

        desbloquearOperacionUsuario();

    }

}



// ======================================================
// ✏️ ACTUALIZAR USUARIO
// ======================================================

async function actualizarUsuario() {

    if (!esAdmin()) {

        return;

    }


    if (operacionUsuarioEnCurso) {

        return;

    }


    // --------------------------------------------------
    // Validar ANTES de bloquear
    // --------------------------------------------------

    if (
        !validarFormulario(true)
    ) {

        return;

    }


    const id =
        document.getElementById(
            "usuarioId"
        )?.value;


    if (!id) {

        mostrarError(
            "No se encontró el usuario a actualizar."
        );

        return;

    }


    const datos =
        obtenerDatosFormulario();


    bloquearOperacionUsuario();


    try {

        const respuesta =
            await fetch(
                `/api/usuarios/${encodeURIComponent(id)}`,
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


        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta
                )
            );

        }


        // ------------------------------------------------
        // Leer respuesta si existe
        // ------------------------------------------------

        try {

            await respuesta.json();

        } catch {

            // Puede venir vacío.

        }


        // ------------------------------------------------
        // Cerrar modal
        // ------------------------------------------------

        limpiarFormularioUsuario();

        cerrarModalUsuario();


        // ------------------------------------------------
        // Liberar antes de refrescar
        // ------------------------------------------------

        desbloquearOperacionUsuario();


        // ------------------------------------------------
        // Confirmación
        // ------------------------------------------------

        await mostrarExito(
            "Usuario actualizado correctamente."
        );


        // ------------------------------------------------
        // Actualizar lista
        // ------------------------------------------------

        await cargarUsuarios();


    } catch (error) {

        console.error(
            "❌ Error actualizando usuario:",
            error
        );


        mostrarError(
            error.message ||
            "No fue posible actualizar el usuario."
        );


    } finally {

        desbloquearOperacionUsuario();

    }

}



// ======================================================
// 🔄 CAMBIAR ESTADO
// ======================================================

async function cambiarEstadoUsuario(id) {

    if (!esAdmin()) {

        return;

    }


    if (operacionUsuarioEnCurso) {

        return;

    }


    const usuario =
        usuariosGlobal.find(
            item =>
                Number(item.id) ===
                Number(id)
        );


    if (!usuario) {

        mostrarError(
            "No se encontró el usuario."
        );

        return;

    }


    const activo =
        obtenerEstadoBoolean(
            usuario.estado
        );


    const accion =
        activo
            ? "desactivar"
            : "activar";


    const confirmacion =
        await Swal.fire({

            title:
                accion === "activar"
                    ? "Activar usuario"
                    : "Desactivar usuario",

            text:
                `¿Deseas ${accion} a ${usuario.nombre}?`,

            icon:
                "question",

            showCancelButton:
                true,

            confirmButtonText:
                accion === "activar"
                    ? "Sí, activar"
                    : "Sí, desactivar",

            cancelButtonText:
                "Cancelar",

            confirmButtonColor:
                "#047857",

            cancelButtonColor:
                "#6b7280",

            reverseButtons:
                true

        });


    if (
        !confirmacion.isConfirmed
    ) {

        return;

    }


    if (
        !bloquearOperacionUsuario()
    ) {

        return;

    }


    try {

        const respuesta =
            await fetch(
                `/api/usuarios/${encodeURIComponent(id)}/estado`,
                {

                    method: "PUT",

                    headers: {

                        "Accept":
                            "application/json"

                    }

                }
            );


        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta
                )
            );

        }


        // ------------------------------------------------
        // Liberar bloqueo
        // ------------------------------------------------

        desbloquearOperacionUsuario();


        await mostrarExito(
            accion === "activar"
                ? "Usuario activado correctamente."
                : "Usuario desactivado correctamente."
        );


        await cargarUsuarios();


    } catch (error) {

        console.error(
            "❌ Error cambiando estado:",
            error
        );


        mostrarError(
            error.message ||
            "No fue posible cambiar el estado del usuario."
        );


    } finally {

        desbloquearOperacionUsuario();

    }

}



// ======================================================
// 🗑️ CONFIRMAR ELIMINACIÓN
// ======================================================

async function confirmarEliminarUsuario(
    id
) {

    if (!esAdmin()) {

        return;

    }


    if (operacionUsuarioEnCurso) {

        return;

    }


    const usuario =
        usuariosGlobal.find(
            item =>
                Number(item.id) ===
                Number(id)
        );


    if (!usuario) {

        mostrarError(
            "No se encontró el usuario."
        );

        return;

    }


    const confirmacion =
        await Swal.fire({

            title:
                "¿Eliminar usuario?",

            html: `

                Se eliminará el usuario

                <strong>
                    ${escaparHTML(
                usuario.nombre
            )}
                </strong>.

            `,

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
                "#6b7280",

            reverseButtons:
                true

        });


    if (
        !confirmacion.isConfirmed
    ) {

        return;

    }


    await eliminarUsuario(id);

}



// ======================================================
// 🗑️ ELIMINAR USUARIO
// ======================================================

async function eliminarUsuario(id) {

    if (!esAdmin()) {

        return;

    }


    if (
        !bloquearOperacionUsuario()
    ) {

        return;

    }


    try {

        const respuesta =
            await fetch(
                `/api/usuarios/${encodeURIComponent(id)}`,
                {

                    method: "DELETE",

                    headers: {

                        "Accept":
                            "application/json"

                    }

                }
            );


        if (!respuesta.ok) {

            throw new Error(
                await obtenerMensajeError(
                    respuesta
                )
            );

        }


        // ------------------------------------------------
        // Liberar antes de actualizar
        // ------------------------------------------------

        desbloquearOperacionUsuario();


        await mostrarExito(
            "Usuario eliminado correctamente."
        );


        await cargarUsuarios();


    } catch (error) {

        console.error(
            "❌ Error eliminando usuario:",
            error
        );


        mostrarError(
            error.message ||
            "No fue posible eliminar el usuario."
        );


    } finally {

        desbloquearOperacionUsuario();

    }

}



// ======================================================
// 🧾 OBTENER DATOS DEL FORMULARIO
// ======================================================

function obtenerDatosFormulario() {

    const datos = {

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


        email:
            document.getElementById(
                "email"
            )?.value.trim() || "",


        rol:
            document.getElementById(
                "rol"
            )?.value || "",


        estado:
            document.getElementById(
                "estado"
            )?.value === "true"

    };


    // --------------------------------------------------
    // PASSWORD
    // --------------------------------------------------
    // Solo se envía si el usuario escribió una.
    //
    // Esto permite editar un usuario sin cambiar
    // su contraseña.
    // --------------------------------------------------

    const password =
        document.getElementById(
            "password"
        )?.value.trim() || "";


    if (password) {

        datos.password =
            password;

    }


    return datos;

}



// ======================================================
// ✅ VALIDAR FORMULARIO
// ======================================================

function validarFormulario(
    esEdicion = false
) {

    const nombre =
        document.getElementById(
            "nombre"
        )?.value.trim() || "";


    const tipo =
        document.getElementById(
            "tipoIdentificacion"
        )?.value || "";


    const numero =
        document.getElementById(
            "numeroIdentificacion"
        )?.value.trim() || "";


    const email =
        document.getElementById(
            "email"
        )?.value.trim() || "";


    const password =
        document.getElementById(
            "password"
        )?.value.trim() || "";


    const rol =
        document.getElementById(
            "rol"
        )?.value || "";



    // --------------------------------------------------
    // NOMBRE
    // --------------------------------------------------

    if (!nombre) {

        mostrarError(
            "El nombre es obligatorio."
        );


        document.getElementById(
            "nombre"
        )?.focus();


        return false;

    }



    // --------------------------------------------------
    // TIPO IDENTIFICACIÓN
    // --------------------------------------------------

    if (!tipo) {

        mostrarError(
            "Selecciona el tipo de identificación."
        );


        document.getElementById(
            "tipoIdentificacion"
        )?.focus();


        return false;

    }



    // --------------------------------------------------
    // NÚMERO
    // --------------------------------------------------

    if (!numero) {

        mostrarError(
            "El número de identificación es obligatorio."
        );


        document.getElementById(
            "numeroIdentificacion"
        )?.focus();


        return false;

    }



    // --------------------------------------------------
    // EMAIL
    // --------------------------------------------------

    if (!email) {

        mostrarError(
            "El correo electrónico es obligatorio."
        );


        document.getElementById(
            "email"
        )?.focus();


        return false;

    }


    if (!validarEmail(email)) {

        mostrarError(
            "Ingresa un correo electrónico válido."
        );


        document.getElementById(
            "email"
        )?.focus();


        return false;

    }



    // --------------------------------------------------
    // PASSWORD
    // --------------------------------------------------

    if (
        !esEdicion &&
        !password
    ) {

        mostrarError(
            "La contraseña es obligatoria al crear un usuario."
        );


        document.getElementById(
            "password"
        )?.focus();


        return false;

    }


    // --------------------------------------------------
    // PASSWORD MÍNIMO
    // --------------------------------------------------

    if (
        password &&
        password.length < 6
    ) {

        mostrarError(
            "La contraseña debe tener mínimo 6 caracteres."
        );


        document.getElementById(
            "password"
        )?.focus();


        return false;

    }



    // --------------------------------------------------
    // ROL
    // --------------------------------------------------

    if (!rol) {

        mostrarError(
            "Selecciona el rol del usuario."
        );


        document.getElementById(
            "rol"
        )?.focus();


        return false;

    }


    return true;

}



// ======================================================
// 📧 VALIDAR EMAIL
// ======================================================

function validarEmail(email) {

    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/
        .test(email);

}



// ======================================================
// ❌ CERRAR MODAL
// ======================================================

function cerrarModalUsuario() {

    const modal =
        document.getElementById(
            "modalUsuario"
        );


    if (!modal) {

        return;

    }


    modal.classList.remove(
        "mostrar"
    );


    document.body.style.overflow =
        "";

}



// ======================================================
// ❌ CANCELAR FORMULARIO
// ======================================================

function cancelarFormularioUsuario() {

    if (operacionUsuarioEnCurso) {

        return;

    }


    limpiarFormularioUsuario();

    cerrarModalUsuario();

}



// ======================================================
// 🧹 LIMPIAR FORMULARIO
// ======================================================

function limpiarFormularioUsuario() {

    const form =
        document.getElementById(
            "formUsuario"
        );


    if (form) {

        form.reset();

    }


    const usuarioId =
        document.getElementById(
            "usuarioId"
        );


    if (usuarioId) {

        usuarioId.value =
            "";

    }


    // --------------------------------------------------
    // Restaurar valores por defecto
    // --------------------------------------------------

    const rol =
        document.getElementById(
            "rol"
        );


    if (rol) {

        rol.value =
            "EMPLEADO";

    }


    const estado =
        document.getElementById(
            "estado"
        );


    if (estado) {

        estado.value =
            "true";

    }


    const password =
        document.getElementById(
            "password"
        );


    if (password) {

        password.value =
            "";

        password.required =
            true;

    }


    const obligatorio =
        document.getElementById(
            "passwordObligatoria"
        );


    if (obligatorio) {

        obligatorio.style.display =
            "inline";

    }


    const ayuda =
        document.getElementById(
            "ayudaPassword"
        );


    if (ayuda) {

        ayuda.textContent =
            "La contraseña es obligatoria al crear.";

    }


    // --------------------------------------------------
    // Título
    // --------------------------------------------------

    const titulo =
        document.getElementById(
            "tituloModal"
        );


    if (titulo) {

        titulo.innerHTML = `

            <i class="fa-solid fa-user-plus"></i>

            Nuevo usuario

        `;

    }


    mostrarBotonGuardarUsuario();

}



// ======================================================
// 💾 MOSTRAR BOTÓN GUARDAR
// ======================================================

function mostrarBotonGuardarUsuario() {

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
// 🔄 MOSTRAR BOTÓN ACTUALIZAR
// ======================================================

function mostrarBotonActualizarUsuario() {

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
// 🔒 BLOQUEAR OPERACIÓN
// ======================================================

function bloquearOperacionUsuario() {

    if (
        operacionUsuarioEnCurso
    ) {

        return false;

    }


    operacionUsuarioEnCurso =
        true;


    bloquearFormulario(true);


    return true;

}



// ======================================================
// 🔓 DESBLOQUEAR OPERACIÓN
// ======================================================

function desbloquearOperacionUsuario() {

    operacionUsuarioEnCurso =
        false;


    bloquearFormulario(false);

}



// ======================================================
// ⏳ BLOQUEAR FORMULARIO
// ======================================================

function bloquearFormulario(
    bloquear
) {

    const form =
        document.getElementById(
            "formUsuario"
        );


    if (!form) {

        return;

    }


    form.querySelectorAll(
        "input, select, button"
    ).forEach(
        elemento => {

            elemento.disabled =
                bloquear;

        }
    );

}



// ======================================================
// ⏳ CARGANDO TABLA
// ======================================================

function mostrarCargandoTabla() {

    const tabla =
        document.getElementById(
            "tablaUsuarios"
        );


    if (!tabla) {

        return;

    }


    tabla.innerHTML = `

        <tr>

            <td
                colspan="${esAdmin() ? 7 : 6}"
                class="tabla-vacia">

                <i
                    class="fa-solid fa-spinner fa-spin">
                </i>

                <strong>
                    Cargando usuarios...
                </strong>

            </td>

        </tr>

    `;

}



// ======================================================
// ❌ ERROR TABLA
// ======================================================

function mostrarErrorTabla(
    mensaje
) {

    const tabla =
        document.getElementById(
            "tablaUsuarios"
        );


    if (!tabla) {

        return;

    }


    tabla.innerHTML = `

        <tr>

            <td
                colspan="${esAdmin() ? 7 : 6}"
                class="tabla-vacia">

                <i
                    class="fa-solid fa-triangle-exclamation">
                </i>

                <strong>

                    ${escaparHTML(mensaje)}

                </strong>

            </td>

        </tr>

    `;

}



// ======================================================
// 📅 FORMATEAR FECHA
// ======================================================

function formatearFecha(
    fecha
) {

    if (!fecha) {

        return "—";

    }


    let fechaObjeto;


    // --------------------------------------------------
    // Fecha tipo YYYY-MM-DD
    // --------------------------------------------------

    if (
        typeof fecha === "string" &&
        /^\d{4}-\d{2}-\d{2}$/.test(fecha)
    ) {

        const partes =
            fecha.split("-");


        fechaObjeto =
            new Date(
                Number(partes[0]),
                Number(partes[1]) - 1,
                Number(partes[2])
            );

    } else {

        fechaObjeto =
            new Date(fecha);

    }


    if (
        isNaN(
            fechaObjeto.getTime()
        )
    ) {

        return "—";

    }


    return fechaObjeto.toLocaleDateString(
        "es-CO",
        {

            day: "2-digit",

            month: "2-digit",

            year: "numeric"

        }
    );

}



// ======================================================
// 🔤 NORMALIZAR
// ======================================================

function normalizar(
    valor
) {

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
// 🔤 OBTENER INICIAL
// ======================================================

function obtenerInicial(
    nombre
) {

    return String(
        nombre || "U"
    )
        .trim()
        .charAt(0)
        .toUpperCase();

}



// ======================================================
// 🟢 CONVERTIR ESTADO A BOOLEAN
// ======================================================

function obtenerEstadoBoolean(
    estado
) {

    if (
        typeof estado === "boolean"
    ) {

        return estado;

    }


    if (
        typeof estado === "number"
    ) {

        return estado !== 0;

    }


    if (
        typeof estado === "string"
    ) {

        const valor =
            estado
                .trim()
                .toLowerCase();


        return [
            "true",
            "1",
            "activo",
            "activa"
        ].includes(
            valor
        );

    }


    return false;

}



// ======================================================
// 🔄 ESTADO TEXTO
// ======================================================

function obtenerTextoEstado(
    estado
) {

    return obtenerEstadoBoolean(
        estado
    )
        ? "activo"
        : "inactivo";

}



// ======================================================
// 🛠️ ESTABLECER VALOR
// ======================================================

function establecerValor(
    id,
    valor
) {

    const elemento =
        document.getElementById(id);


    if (elemento) {

        elemento.value =
            valor ?? "";

    }

}



// ======================================================
// 🛡️ ESCAPAR HTML
// ======================================================

function escaparHTML(
    valor
) {

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
// ❌ OBTENER ERROR DEL BACKEND
// ======================================================

async function obtenerMensajeError(
    respuesta
) {

    try {

        const texto =
            await respuesta.text();


        if (!texto) {

            return `Error HTTP ${respuesta.status}`;

        }


        // ------------------------------------------------
        // Intentar JSON
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

        return `Error HTTP ${respuesta.status}`;

    }

}



// ======================================================
// ✅ SWEET ALERT - ÉXITO
// ======================================================

function mostrarExito(
    mensaje
) {

    if (
        typeof Swal === "undefined"
    ) {

        alert(mensaje);

        return Promise.resolve();

    }


    return Swal.fire({

        icon:
            "success",

        title:
            "Correcto",

        text:
        mensaje,

        confirmButtonText:
            "Aceptar",

        confirmButtonColor:
            "#047857"

    });

}



// ======================================================
// ❌ SWEET ALERT - ERROR
// ======================================================

function mostrarError(
    mensaje
) {

    if (
        typeof Swal === "undefined"
    ) {

        alert(mensaje);

        return;

    }


    Swal.fire({

        icon:
            "error",

        title:
            "Atención",

        text:
        mensaje,

        confirmButtonText:
            "Aceptar",

        confirmButtonColor:
            "#047857"

    });

}