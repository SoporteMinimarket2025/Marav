// ======================================================
// 💰 CAJA | MARAV
// ======================================================
//
// Funciones:
// - Cargar cajas del negocio actual
// - Cargar usuarios del negocio actual
// - Mostrar KPIs de caja
// - Buscar cajas desde el Topbar
// - Abrir caja
// - Registrar ingresos
// - Registrar egresos
// - Cerrar caja
// - Control ADMIN / EMPLEADO
// - Evitar doble envío de operaciones
// - Manejo seguro de respuestas del backend
//
// IMPORTANTE:
// La seguridad real debe estar en Spring Security / backend.
// Este JS solamente controla la interfaz.
// ======================================================


// ======================================================
// VARIABLES GLOBALES
// ======================================================

let cajasGlobal = [];

let usuariosGlobal = [];

let cajaAbiertaGlobal = null;

let tipoMovimientoActual = null;


// ======================================================
// CONTROL DE OPERACIONES
// ======================================================
//
// Evita que el usuario pueda hacer doble clic y enviar
// dos veces la misma operación.
//
// Ejemplo:
// - doble clic en "Abrir caja"
// - doble clic en "Registrar ingreso"
// - doble clic en "Cerrar caja"
// ======================================================

let operacionCajaEnCurso = false;


// ======================================================
// INICIALIZACIÓN
// ======================================================

document.addEventListener("DOMContentLoaded", function () {

    console.log("✅ caja.js cargado correctamente");

    configurarBusqueda();

    configurarFormularioCaja();

    configurarFormularioMovimiento();

    configurarCierreModales();

    cargarUsuarios();

    cargarCajas();

});


// ======================================================
// CONTROL DE ROL
// ======================================================

function esAdmin() {

    const rol = String(
        typeof ROL_USUARIO !== "undefined"
            ? ROL_USUARIO
            : "EMPLEADO"
    )
        .trim()
        .toUpperCase();

    console.log("👤 Rol detectado en Caja:", rol);

    return rol === "ADMIN";

}


// ======================================================
// FORMATO MONEDA
// ======================================================

function formatearMoneda(valor) {

    const numero = Number(valor) || 0;

    return numero.toLocaleString("es-CO", {
        style: "currency",
        currency: "COP",
        maximumFractionDigits: 0
    });

}


// ======================================================
// FORMATO FECHA
// ======================================================

function formatearFecha(fecha) {

    if (!fecha) {
        return "-";
    }

    const fechaObj = new Date(fecha);

    if (isNaN(fechaObj.getTime())) {
        return "-";
    }

    return fechaObj.toLocaleString("es-CO", {
        day: "2-digit",
        month: "2-digit",
        year: "numeric",
        hour: "2-digit",
        minute: "2-digit"
    });

}


// ======================================================
// ESCAPAR HTML
// ======================================================

function escaparHTML(texto) {

    if (texto === null || texto === undefined) {
        return "";
    }

    return String(texto)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");

}


// ======================================================
// NORMALIZAR TEXTO
// ======================================================

function normalizarTexto(texto) {

    return String(texto ?? "")
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "");

}


// ======================================================
// OBTENER NOMBRE DEL USUARIO
// ======================================================

function obtenerNombreUsuario(idUsuario) {

    if (
        idUsuario === null ||
        idUsuario === undefined ||
        idUsuario === ""
    ) {
        return "Sin usuario";
    }

    const usuario = usuariosGlobal.find(
        u => Number(u.id) === Number(idUsuario)
    );

    if (!usuario) {

        return "Usuario #" + escaparHTML(idUsuario);

    }

    return usuario.nombre || "Sin nombre";

}


// ======================================================
// CARGAR USUARIOS
// ======================================================
//
// IMPORTANTE:
// Este endpoint debe devolver solamente los usuarios
// pertenecientes al negocio del usuario autenticado.
//
// No enviamos negocioId desde JavaScript.
//
// El backend debe determinar el negocio mediante
// el usuario autenticado.
// ======================================================

async function cargarUsuarios() {

    try {

        const respuesta = await fetch(
            "/api/usuarios/mios",
            {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                }
            }
        );

        if (!respuesta.ok) {

            throw new Error(
                "No fue posible cargar los usuarios."
            );

        }

        const datos = await leerRespuestaSegura(
            respuesta
        );

        usuariosGlobal =
            Array.isArray(datos)
                ? datos
                : [];

        console.log(
            "👥 Usuarios cargados para Caja:",
            usuariosGlobal
        );

        // Después de cargar los nombres,
        // actualizamos la tabla.
        renderizarTabla(cajasGlobal);

    } catch (error) {

        console.error(
            "❌ Error cargando usuarios:",
            error
        );

        // No mostramos SweetAlert aquí porque
        // la caja todavía puede funcionar.
        //
        // Simplemente dejamos el fallback:
        // Usuario #ID

    }

}


// ======================================================
// CARGAR CAJAS
// ======================================================

async function cargarCajas() {

    try {

        const respuesta = await fetch(
            "/api/caja",
            {
                method: "GET",
                headers: {
                    "Accept": "application/json"
                }
            }
        );

        if (!respuesta.ok) {

            const mensaje =
                await obtenerMensajeError(respuesta);

            throw new Error(mensaje);

        }

        const datos =
            await leerRespuestaSegura(respuesta);

        cajasGlobal =
            Array.isArray(datos)
                ? datos
                : [];

        console.log(
            "📦 Cajas recibidas:",
            cajasGlobal
        );

        actualizarCajaAbierta();

        actualizarKPIs();

        aplicarBusquedaActual();

    } catch (error) {

        console.error(
            "❌ Error cargando cajas:",
            error
        );

        mostrarErrorCaja(
            "No fue posible cargar la información de caja."
        );

    }

}


// ======================================================
// ACTUALIZAR CAJA ABIERTA
// ======================================================

function actualizarCajaAbierta() {

    cajaAbiertaGlobal =
        cajasGlobal.find(
            caja =>
                String(caja.estado || "")
                    .trim()
                    .toUpperCase() === "ABIERTA"
        ) || null;

}


// ======================================================
// ACTUALIZAR KPIs
// ======================================================

function actualizarKPIs() {

    const estado =
        document.getElementById("estadoCaja");

    const saldoInicial =
        document.getElementById("saldoInicial");

    const totalIngresos =
        document.getElementById("totalIngresos");

    const totalEgresos =
        document.getElementById("totalEgresos");

    const saldoFinal =
        document.getElementById("saldoFinal");


    if (!estado) {
        return;
    }


    // ==================================================
    // NO HAY CAJA ABIERTA
    // ==================================================

    if (!cajaAbiertaGlobal) {

        estado.textContent = "Sin caja";

        if (saldoInicial) {
            saldoInicial.textContent =
                formatearMoneda(0);
        }

        if (totalIngresos) {
            totalIngresos.textContent =
                formatearMoneda(0);
        }

        if (totalEgresos) {
            totalEgresos.textContent =
                formatearMoneda(0);
        }

        if (saldoFinal) {
            saldoFinal.textContent =
                formatearMoneda(0);
        }

        return;
    }


    // ==================================================
    // HAY CAJA ABIERTA
    // ==================================================

    estado.textContent = "ABIERTA";

    if (saldoInicial) {

        saldoInicial.textContent =
            formatearMoneda(
                cajaAbiertaGlobal.saldoInicial
            );

    }

    if (totalIngresos) {

        totalIngresos.textContent =
            formatearMoneda(
                cajaAbiertaGlobal.ingresos
            );

    }

    if (totalEgresos) {

        totalEgresos.textContent =
            formatearMoneda(
                cajaAbiertaGlobal.egresos
            );

    }

    if (saldoFinal) {

        saldoFinal.textContent =
            formatearMoneda(
                cajaAbiertaGlobal.saldoFinal
            );

    }

}


// ======================================================
// RENDERIZAR TABLA
// ======================================================

function renderizarTabla(lista) {

    const tabla =
        document.getElementById("tablaCaja");

    const empty =
        document.getElementById("cajaEmpty");


    if (!tabla) {

        console.warn(
            "⚠️ No existe #tablaCaja"
        );

        return;

    }


    tabla.innerHTML = "";


    // ==================================================
    // SIN RESULTADOS
    // ==================================================

    if (!lista || lista.length === 0) {

        if (empty) {
            empty.style.display = "block";
        }

        return;

    }


    if (empty) {
        empty.style.display = "none";
    }


    // ==================================================
    // CREAR FILAS
    // ==================================================

    lista.forEach(caja => {

        const tr =
            document.createElement("tr");


        const estado =
            String(caja.estado || "")
                .trim()
                .toUpperCase();


        const nombreUsuario =
            obtenerNombreUsuario(
                caja.idUsuario
            );


        let acciones = "";


        // ==================================================
        // ACCIONES ADMIN
        // ==================================================

        if (
            esAdmin() &&
            estado === "ABIERTA"
        ) {

            const idCaja =
                Number(caja.id);


            if (Number.isFinite(idCaja)) {

                acciones = `

                    <div class="caja-row-actions">

                        <button
                            type="button"
                            class="caja-action-btn caja-action-ingreso"
                            title="Registrar ingreso"
                            onclick="abrirMovimiento(${idCaja}, 'INGRESO')">

                            <i class="fa-solid fa-arrow-trend-up"></i>

                        </button>


                        <button
                            type="button"
                            class="caja-action-btn caja-action-egreso"
                            title="Registrar egreso"
                            onclick="abrirMovimiento(${idCaja}, 'EGRESO')">

                            <i class="fa-solid fa-arrow-trend-down"></i>

                        </button>


                        <button
                            type="button"
                            class="caja-action-btn caja-action-cerrar"
                            title="Cerrar caja"
                            onclick="cerrarCaja(${idCaja})">

                            <i class="fa-solid fa-lock"></i>

                        </button>

                    </div>

                `;

            }

        } else if (esAdmin()) {

            acciones = `

                <span class="caja-action-disabled">

                    <i class="fa-solid fa-lock"></i>

                    Cerrada

                </span>

            `;

        }


        // ==================================================
        // CREAR FILA
        // ==================================================

        tr.innerHTML = `

            <td>

                <div class="caja-usuario">

                    <span class="caja-avatar">

                        <i class="fa-solid fa-user"></i>

                    </span>

                    ${escaparHTML(nombreUsuario)}

                </div>

            </td>


            <td>

                <span class="caja-fecha">

                    ${formatearFecha(
            caja.fechaApertura
        )}

                </span>

            </td>


            <td>

                <span class="caja-fecha">

                    ${formatearFecha(
            caja.fechaCierre
        )}

                </span>

            </td>


            <td>

                <span class="caja-money">

                    ${formatearMoneda(
            caja.saldoInicial
        )}

                </span>

            </td>


            <td>

                <span class="caja-money caja-money-in">

                    ${formatearMoneda(
            caja.ingresos
        )}

                </span>

            </td>


            <td>

                <span class="caja-money caja-money-out">

                    ${formatearMoneda(
            caja.egresos
        )}

                </span>

            </td>


            <td>

                <span class="caja-money caja-money-final">

                    ${formatearMoneda(
            caja.saldoFinal
        )}

                </span>

            </td>


            <td>

                ${renderizarEstado(estado)}

            </td>


            ${
            esAdmin()
                ? `<td>${acciones}</td>`
                : ""
        }

        `;


        tabla.appendChild(tr);

    });

}


// ======================================================
// ESTADO DE CAJA
// ======================================================

function renderizarEstado(estado) {

    if (estado === "ABIERTA") {

        return `

            <span class="caja-status caja-status-abierta">

                <i class="fa-solid fa-circle"></i>

                ABIERTA

            </span>

        `;

    }


    return `

        <span class="caja-status caja-status-cerrada">

            <i class="fa-solid fa-lock"></i>

            ${escaparHTML(
        estado || "CERRADA"
    )}

        </span>

    `;

}


// ======================================================
// CONFIGURAR BÚSQUEDA
// ======================================================

function configurarBusqueda() {

    const buscador =
        document.getElementById("buscarTopbar");


    if (!buscador) {

        console.warn(
            "⚠️ No existe #buscarTopbar"
        );

        return;

    }


    buscador.placeholder =
        "Buscar caja...";


    // ==================================================
    // EVITAR LISTENER DUPLICADO
    // ==================================================

    if (
        buscador.dataset.cajaBusquedaConfigurada ===
        "true"
    ) {

        return;

    }


    buscador.dataset.cajaBusquedaConfigurada =
        "true";


    buscador.addEventListener(
        "input",
        function () {

            aplicarBusquedaActual();

        }
    );

}


// ======================================================
// APLICAR BÚSQUEDA ACTUAL
// ======================================================

function aplicarBusquedaActual() {

    const buscador =
        document.getElementById("buscarTopbar");


    if (!buscador) {

        renderizarTabla(cajasGlobal);

        return;

    }


    const termino =
        normalizarTexto(
            buscador.value
        );


    // ==================================================
    // SIN BÚSQUEDA
    // ==================================================

    if (!termino) {

        renderizarTabla(
            cajasGlobal
        );

        return;

    }


    // ==================================================
    // FILTRAR
    // ==================================================

    const filtradas =
        cajasGlobal.filter(caja => {

            const valores = [

                obtenerNombreUsuario(
                    caja.idUsuario
                ),

                caja.estado,

                formatearFecha(
                    caja.fechaApertura
                ),

                formatearFecha(
                    caja.fechaCierre
                ),

                formatearMoneda(
                    caja.saldoInicial
                ),

                formatearMoneda(
                    caja.ingresos
                ),

                formatearMoneda(
                    caja.egresos
                ),

                formatearMoneda(
                    caja.saldoFinal
                )

            ];


            return valores.some(valor =>
                normalizarTexto(valor)
                    .includes(termino)
            );

        });


    renderizarTabla(
        filtradas
    );

}


// ======================================================
// ABRIR MODAL DE CAJA
// ======================================================

function abrirModalCaja() {

    console.log(
        "🟢 CLICK EN ABRIR CAJA"
    );


    // ==================================================
    // EVITAR OPERACIÓN DUPLICADA
    // ==================================================

    if (operacionCajaEnCurso) {

        console.warn(
            "⚠️ Ya existe una operación de caja en curso."
        );

        return;

    }


    const modal =
        document.getElementById("modalCaja");

    const formulario =
        document.getElementById("formCaja");

    const input =
        document.getElementById(
            "saldoInicialInput"
        );


    if (!modal) {

        console.error(
            "❌ No existe #modalCaja en el HTML."
        );

        return;

    }


    if (!formulario) {

        console.error(
            "❌ No existe #formCaja en el HTML."
        );

        return;

    }


    formulario.reset();


    modal.classList.add("active");


    // Evita que el fondo se desplace
    // mientras el modal está abierto.
    document.body.style.overflow = "hidden";


    console.log(
        "✅ Modal de Caja abierto"
    );


    setTimeout(() => {

        if (input) {
            input.focus();
        }

    }, 150);

}


// ======================================================
// CERRAR MODAL CAJA
// ======================================================

function cerrarModalCaja(forzado = false) {

    // Si hay una operación en curso,
    // no permitimos cerrar accidentalmente
    // el modal mediante Escape o backdrop.
    if (
        operacionCajaEnCurso &&
        !forzado
    ) {

        return;

    }


    const modal =
        document.getElementById(
            "modalCaja"
        );


    if (!modal) {
        return;
    }


    modal.classList.remove("active");


    document.body.style.overflow = "";


    // Limpiar formulario
    const formulario =
        document.getElementById("formCaja");

    if (formulario) {
        formulario.reset();
    }

}


// ======================================================
// CONFIGURAR FORMULARIO CAJA
// ======================================================

function configurarFormularioCaja() {

    const boton =
        document.getElementById(
            "btnAbrirCaja"
        );


    console.log(
        "🔎 Botón Abrir Caja encontrado:",
        boton
    );


    // ==================================================
    // BOTÓN ABRIR CAJA
    // ==================================================

    if (
        boton &&
        boton.dataset.cajaEvento !== "true"
    ) {

        boton.dataset.cajaEvento = "true";

        boton.type = "button";


        boton.addEventListener(
            "click",
            function (event) {

                event.preventDefault();

                console.log(
                    "🖱️ Evento click detectado"
                );

                abrirModalCaja();

            }
        );

    }


    // ==================================================
    // CERRAR MODAL
    // ==================================================

    const btnCerrarModal =
        document.getElementById(
            "btnCerrarModal"
        );


    if (
        btnCerrarModal &&
        btnCerrarModal.dataset.cajaEvento !== "true"
    ) {

        btnCerrarModal.dataset.cajaEvento =
            "true";


        btnCerrarModal.addEventListener(
            "click",
            function () {

                cerrarModalCaja();

            }
        );

    }


    // ==================================================
    // CANCELAR
    // ==================================================

    const btnCancelarCaja =
        document.getElementById(
            "btnCancelarCaja"
        );


    if (
        btnCancelarCaja &&
        btnCancelarCaja.dataset.cajaEvento !== "true"
    ) {

        btnCancelarCaja.dataset.cajaEvento =
            "true";


        btnCancelarCaja.addEventListener(
            "click",
            function () {

                cerrarModalCaja();

            }
        );

    }


    // ==================================================
    // SUBMIT
    // ==================================================

    const formulario =
        document.getElementById(
            "formCaja"
        );


    if (
        formulario &&
        formulario.dataset.cajaEvento !== "true"
    ) {

        formulario.dataset.cajaEvento =
            "true";


        formulario.addEventListener(
            "submit",
            async function (event) {

                event.preventDefault();

                await guardarCaja();

            }
        );

    }

}


// ======================================================
// 💾 GUARDAR / ABRIR CAJA
// ======================================================

async function guardarCaja() {

    console.log(
        "📤 Intentando abrir caja..."
    );


    // ==================================================
    // EVITAR DOBLE ENVÍO
    // ==================================================

    if (operacionCajaEnCurso) {

        console.warn(
            "⚠️ Ya se está procesando una operación."
        );

        return;

    }


    const input =
        document.getElementById(
            "saldoInicialInput"
        );


    if (!input) {

        console.error(
            "❌ No existe #saldoInicialInput"
        );

        return;

    }


    const saldoInicial =
        Number(input.value);


    // ==================================================
    // VALIDAR SALDO
    // ==================================================

    if (
        !Number.isFinite(saldoInicial) ||
        saldoInicial < 0
    ) {

        Swal.fire({

            icon: "warning",

            title: "Valor inválido",

            text:
                "Ingresa un saldo inicial válido."

        });

        input.focus();

        return;

    }


    // ==================================================
    // VALIDAR CAJA ABIERTA
    // ==================================================

    if (cajaAbiertaGlobal) {

        Swal.fire({

            icon: "warning",

            title: "Caja ya abierta",

            text:
                "Ya existe una caja abierta."

        });

        return;

    }


    // ==================================================
    // INICIAR OPERACIÓN
    // ==================================================

    iniciarOperacionCaja();


    try {

        const respuesta =
            await fetch(
                "/api/caja/abrir",
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/x-www-form-urlencoded; charset=UTF-8",

                        "Accept":
                            "application/json"

                    },

                    body:
                        "saldoInicial=" +
                        encodeURIComponent(
                            saldoInicial
                        )

                }
            );


        console.log(
            "📡 Respuesta abrir caja:",
            respuesta.status
        );


        // ==================================================
        // ERROR DEL BACKEND
        // ==================================================

        if (!respuesta.ok) {

            const mensaje =
                await obtenerMensajeError(
                    respuesta
                );

            throw new Error(mensaje);

        }


        // ==================================================
        // LEER RESPUESTA SIN EXIGIR JSON
        // ==================================================

        await leerRespuestaSegura(
            respuesta
        );


        // ==================================================
        // LA OPERACIÓN YA FUE EXITOSA
        // ==================================================

        finalizarOperacionCaja();


        cerrarModalCaja(true);


        // Actualizamos inmediatamente la información
        // que ya tenemos en pantalla.
        //
        // La recarga completa se hace después.
        Swal.fire({

            icon: "success",

            title: "Caja abierta",

            text:
                "La caja fue abierta correctamente.",

            timer: 1800,

            showConfirmButton: false

        });


        // ==================================================
        // RECARGAR DATOS
        // ==================================================

        cargarCajas().catch(error => {

            console.error(
                "⚠️ La caja se abrió, pero no se pudo actualizar la tabla:",
                error
            );

        });


    } catch (error) {

        console.error(
            "❌ Error abriendo caja:",
            error
        );


        finalizarOperacionCaja();


        Swal.fire({

            icon: "error",

            title: "No se pudo abrir",

            text:
                error.message ||
                "Ocurrió un error al abrir la caja."

        });

    }

}


// ======================================================
// ABRIR MOVIMIENTO
// ======================================================

function abrirMovimiento(idCaja, tipo) {

    // ==================================================
    // VALIDAR OPERACIÓN
    // ==================================================

    if (operacionCajaEnCurso) {

        return;

    }


    // ==================================================
    // BUSCAR CAJA
    // ==================================================

    const caja =
        cajasGlobal.find(
            c =>
                Number(c.id) ===
                Number(idCaja)
        );


    if (!caja) {

        Swal.fire({

            icon: "error",

            title: "Caja no encontrada",

            text:
                "No fue posible encontrar la caja."

        });

        return;

    }


    // ==================================================
    // VALIDAR ESTADO
    // ==================================================

    if (
        String(caja.estado || "")
            .toUpperCase() !== "ABIERTA"
    ) {

        Swal.fire({

            icon: "info",

            title: "Caja cerrada",

            text:
                "No puedes registrar movimientos en una caja cerrada."

        });

        return;

    }


    // ==================================================
    // GUARDAR TIPO
    // ==================================================

    tipoMovimientoActual =
        tipo === "EGRESO"
            ? "EGRESO"
            : "INGRESO";


    const titulo =
        document.getElementById(
            "tituloMovimiento"
        );

    const descripcion =
        document.getElementById(
            "descripcionMovimiento"
        );

    const texto =
        document.getElementById(
            "textoMovimiento"
        );


    const esIngreso =
        tipoMovimientoActual === "INGRESO";


    // ==================================================
    // TÍTULO
    // ==================================================

    if (titulo) {

        titulo.innerHTML = `

            <i class="fa-solid ${
            esIngreso
                ? "fa-arrow-trend-up"
                : "fa-arrow-trend-down"
        }"></i>

            ${
            esIngreso
                ? "Registrar Ingreso"
                : "Registrar Egreso"
        }

        `;

    }


    // ==================================================
    // DESCRIPCIÓN
    // ==================================================

    if (descripcion) {

        descripcion.textContent =
            esIngreso
                ? "Agrega dinero a la caja."
                : "Registra una salida de dinero de la caja.";

    }


    if (texto) {

        texto.textContent =
            esIngreso
                ? "El valor se sumará a los ingresos de la caja."
                : "El valor se sumará a los egresos de la caja.";

    }


    // ==================================================
    // LIMPIAR FORMULARIO
    // ==================================================

    document
        .getElementById("formMovimiento")
        ?.reset();


    // ==================================================
    // ABRIR MODAL
    // ==================================================

    document
        .getElementById("modalMovimiento")
        ?.classList.add("active");


    document.body.style.overflow =
        "hidden";


    setTimeout(() => {

        document
            .getElementById(
                "valorMovimiento"
            )
            ?.focus();

    }, 150);

}


// ======================================================
// CERRAR MODAL MOVIMIENTO
// ======================================================

function cerrarModalMovimiento(
    forzado = false
) {

    if (
        operacionCajaEnCurso &&
        !forzado
    ) {

        return;

    }


    document
        .getElementById("modalMovimiento")
        ?.classList.remove("active");


    tipoMovimientoActual = null;


    document.body.style.overflow =
        "";

}


// ======================================================
// CONFIGURAR MOVIMIENTO
// ======================================================

function configurarFormularioMovimiento() {

    const btnCerrar =
        document.getElementById(
            "btnCerrarMovimiento"
        );


    if (
        btnCerrar &&
        btnCerrar.dataset.cajaEvento !== "true"
    ) {

        btnCerrar.dataset.cajaEvento =
            "true";


        btnCerrar.addEventListener(
            "click",
            function () {

                cerrarModalMovimiento();

            }
        );

    }


    const btnCancelar =
        document.getElementById(
            "btnCancelarMovimiento"
        );


    if (
        btnCancelar &&
        btnCancelar.dataset.cajaEvento !== "true"
    ) {

        btnCancelar.dataset.cajaEvento =
            "true";


        btnCancelar.addEventListener(
            "click",
            function () {

                cerrarModalMovimiento();

            }
        );

    }


    const formulario =
        document.getElementById(
            "formMovimiento"
        );


    if (
        formulario &&
        formulario.dataset.cajaEvento !== "true"
    ) {

        formulario.dataset.cajaEvento =
            "true";


        formulario.addEventListener(
            "submit",
            async function (event) {

                event.preventDefault();

                await guardarMovimiento();

            }
        );

    }

}


// ======================================================
// GUARDAR MOVIMIENTO
// ======================================================

async function guardarMovimiento() {

    // ==================================================
    // VALIDAR TIPO
    // ==================================================

    if (!tipoMovimientoActual) {
        return;
    }


    // ==================================================
    // EVITAR DOBLE ENVÍO
    // ==================================================

    if (operacionCajaEnCurso) {

        console.warn(
            "⚠️ Ya existe una operación en curso."
        );

        return;

    }


    const input =
        document.getElementById(
            "valorMovimiento"
        );


    const valor =
        Number(input?.value);


    // ==================================================
    // VALIDAR VALOR
    // ==================================================

    if (
        !Number.isFinite(valor) ||
        valor <= 0
    ) {

        Swal.fire({

            icon: "warning",

            title: "Valor inválido",

            text:
                "Ingresa un valor mayor que cero."

        });

        input?.focus();

        return;

    }


    // Guardamos el tipo antes de iniciar
    // la operación para no perderlo.
    const tipoOperacion =
        tipoMovimientoActual;


    const endpoint =
        tipoOperacion === "INGRESO"
            ? "/api/caja/ingreso"
            : "/api/caja/egreso";


    iniciarOperacionCaja();


    try {

        const respuesta =
            await fetch(
                endpoint +
                "?valor=" +
                encodeURIComponent(valor),
                {

                    method: "PUT",

                    headers: {

                        "Accept":
                            "application/json"

                    }

                }
            );


        if (!respuesta.ok) {

            const mensaje =
                await obtenerMensajeError(
                    respuesta
                );

            throw new Error(mensaje);

        }


        // ==================================================
        // LEER RESPUESTA DE FORMA SEGURA
        // ==================================================

        await leerRespuestaSegura(
            respuesta
        );


        // ==================================================
        // OPERACIÓN EXITOSA
        // ==================================================

        finalizarOperacionCaja();


        cerrarModalMovimiento(true);


        Swal.fire({

            icon: "success",

            title:
                tipoOperacion === "INGRESO"
                    ? "Ingreso registrado"
                    : "Egreso registrado",

            text:
                "El movimiento fue registrado correctamente.",

            timer: 1800,

            showConfirmButton: false

        });


        // ==================================================
        // ACTUALIZAR TABLA
        // ==================================================

        cargarCajas().catch(error => {

            console.error(
                "⚠️ Movimiento realizado, pero no se pudo actualizar la tabla:",
                error
            );

        });


    } catch (error) {

        console.error(
            "❌ Error registrando movimiento:",
            error
        );


        finalizarOperacionCaja();


        Swal.fire({

            icon: "error",

            title: "No se pudo registrar",

            text:
                error.message ||
                "Ocurrió un error."

        });

    }

}


// ======================================================
// CERRAR CAJA
// ======================================================

async function cerrarCaja(idCaja) {

    // ==================================================
    // EVITAR OPERACIONES SIMULTÁNEAS
    // ==================================================

    if (operacionCajaEnCurso) {
        return;
    }


    const caja =
        cajasGlobal.find(
            c =>
                Number(c.id) ===
                Number(idCaja)
        );


    if (!caja) {

        Swal.fire({

            icon: "error",

            title: "Caja no encontrada",

            text:
                "No fue posible encontrar la caja."

        });

        return;

    }


    // ==================================================
    // VALIDAR ESTADO
    // ==================================================

    if (
        String(caja.estado || "")
            .toUpperCase() !== "ABIERTA"
    ) {

        Swal.fire({

            icon: "info",

            title: "Caja cerrada",

            text:
                "Esta caja ya está cerrada."

        });

        return;

    }


    // ==================================================
    // CONFIRMACIÓN
    // ==================================================

    const confirmacion =
        await Swal.fire({

            icon: "warning",

            title: "¿Cerrar caja?",

            html: `

                <p>

                    La caja será marcada como
                    <strong>CERRADA</strong>.

                </p>

                <p>

                    <strong>Saldo final:</strong>

                    ${formatearMoneda(
                caja.saldoFinal
            )}

                </p>

            `,

            showCancelButton: true,

            confirmButtonText:
                "Sí, cerrar caja",

            cancelButtonText:
                "Cancelar",

            confirmButtonColor:
                "#ea580c",

            cancelButtonColor:
                "#6b7280"

        });


    if (!confirmacion.isConfirmed) {
        return;
    }


    // ==================================================
    // EVITAR QUE OTRO CLICK ENTRE DESPUÉS
    // DE LA CONFIRMACIÓN
    // ==================================================

    if (operacionCajaEnCurso) {
        return;
    }


    iniciarOperacionCaja();


    try {

        const respuesta =
            await fetch(
                "/api/caja/cerrar",
                {

                    method: "PUT",

                    headers: {

                        "Accept":
                            "application/json"

                    }

                }
            );


        if (!respuesta.ok) {

            const mensaje =
                await obtenerMensajeError(
                    respuesta
                );

            throw new Error(mensaje);

        }


        await leerRespuestaSegura(
            respuesta
        );


        // ==================================================
        // OPERACIÓN EXITOSA
        // ==================================================

        finalizarOperacionCaja();


        Swal.fire({

            icon: "success",

            title: "Caja cerrada",

            text:
                "La caja fue cerrada correctamente.",

            timer: 1800,

            showConfirmButton: false

        });


        // ==================================================
        // ACTUALIZAR DATOS
        // ==================================================

        cargarCajas().catch(error => {

            console.error(
                "⚠️ Caja cerrada, pero no se pudo actualizar la tabla:",
                error
            );

        });


    } catch (error) {

        console.error(
            "❌ Error cerrando caja:",
            error
        );


        finalizarOperacionCaja();


        Swal.fire({

            icon: "error",

            title: "No se pudo cerrar",

            text:
                error.message ||
                "Ocurrió un error al cerrar la caja."

        });

    }

}


// ======================================================
// INICIAR OPERACIÓN
// ======================================================

function iniciarOperacionCaja() {

    operacionCajaEnCurso = true;


    // ==================================================
    // DESHABILITAR BOTONES DE CAJA
    // ==================================================

    const ids = [

        "btnAbrirCaja",

        "btnGuardarCaja",

        "btnCancelarCaja",

        "btnCerrarModal",

        "btnGuardarMovimiento",

        "btnCancelarMovimiento",

        "btnCerrarMovimiento"

    ];


    ids.forEach(id => {

        const elemento =
            document.getElementById(id);

        if (elemento) {

            elemento.disabled = true;

        }

    });


    // ==================================================
    // DESHABILITAR CAMPOS DE LOS FORMULARIOS
    // ==================================================

    const formularios = [

        document.getElementById("formCaja"),

        document.getElementById("formMovimiento")

    ];


    formularios.forEach(formulario => {

        if (!formulario) {
            return;
        }


        formulario
            .querySelectorAll(
                "input, select, textarea"
            )
            .forEach(elemento => {

                elemento.disabled = true;

            });

    });


}


// ======================================================
// FINALIZAR OPERACIÓN
// ======================================================

function finalizarOperacionCaja() {

    operacionCajaEnCurso = false;


    const ids = [

        "btnAbrirCaja",

        "btnGuardarCaja",

        "btnCancelarCaja",

        "btnCerrarModal",

        "btnGuardarMovimiento",

        "btnCancelarMovimiento",

        "btnCerrarMovimiento"

    ];


    ids.forEach(id => {

        const elemento =
            document.getElementById(id);

        if (elemento) {

            elemento.disabled = false;

        }

    });


    const formularios = [

        document.getElementById("formCaja"),

        document.getElementById("formMovimiento")

    ];


    formularios.forEach(formulario => {

        if (!formulario) {
            return;
        }


        formulario
            .querySelectorAll(
                "input, select, textarea"
            )
            .forEach(elemento => {

                elemento.disabled = false;

            });

    });

}


// ======================================================
// LEER RESPUESTA SEGURA
// ======================================================
//
// Permite trabajar con:
// - JSON
// - texto
// - respuesta vacía
//
// Así no falla si Spring devuelve 200/201
// sin un cuerpo JSON.
// ======================================================

async function leerRespuestaSegura(respuesta) {

    const texto =
        await respuesta.text();


    if (!texto) {
        return null;
    }


    try {

        return JSON.parse(texto);

    } catch {

        return texto;

    }

}


// ======================================================
// MENSAJE DE ERROR DEL SERVIDOR
// ======================================================

async function obtenerMensajeError(
    respuesta
) {

    try {

        const texto =
            await respuesta.text();


        if (!texto) {

            return (
                "Ocurrió un error en el servidor."
            );

        }


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

        return (
            "Ocurrió un error en el servidor."
        );

    }

}


// ======================================================
// MOSTRAR ERROR DE CAJA
// ======================================================

function mostrarErrorCaja(mensaje) {

    const tabla =
        document.getElementById(
            "tablaCaja"
        );


    if (tabla) {

        tabla.innerHTML = `

            <tr>

                <td
                    colspan="${
            esAdmin()
                ? "9"
                : "8"
        }"
                    style="
                        text-align:center;
                        padding:30px;
                        color:#dc2626;
                    "
                >

                    <i class="fa-solid fa-triangle-exclamation"></i>

                    ${escaparHTML(mensaje)}

                </td>

            </tr>

        `;

    }


    const empty =
        document.getElementById(
            "cajaEmpty"
        );


    if (empty) {
        empty.style.display = "none";
    }

}


// ======================================================
// CONFIGURAR CIERRE DE MODALES
// ======================================================

function configurarCierreModales() {

    // ==================================================
    // EVITAR REGISTRAR EVENTOS DUPLICADOS
    // ==================================================

    if (
        document.documentElement.dataset
            .cajaModalesConfigurados === "true"
    ) {

        return;

    }


    document.documentElement.dataset
        .cajaModalesConfigurados = "true";


    // ==================================================
    // ESCAPE
    // ==================================================

    document.addEventListener(
        "keydown",
        function (event) {

            if (
                event.key !== "Escape" ||
                operacionCajaEnCurso
            ) {

                return;

            }


            cerrarModalCaja();

            cerrarModalMovimiento();

        }
    );


    // ==================================================
    // CLICK EN FONDO DEL MODAL
    // ==================================================

    document.addEventListener(
        "click",
        function (event) {

            if (operacionCajaEnCurso) {
                return;
            }


            const modalCaja =
                document.getElementById(
                    "modalCaja"
                );


            const modalMovimiento =
                document.getElementById(
                    "modalMovimiento"
                );


            // ------------------------------------------
            // MODAL CAJA
            // ------------------------------------------

            if (
                modalCaja &&
                event.target === modalCaja
            ) {

                cerrarModalCaja();

            }


            // ------------------------------------------
            // MODAL MOVIMIENTO
            // ------------------------------------------

            if (
                modalMovimiento &&
                event.target === modalMovimiento
            ) {

                cerrarModalMovimiento();

            }

        }
    );

}