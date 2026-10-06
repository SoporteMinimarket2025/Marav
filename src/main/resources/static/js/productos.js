// ============================================================
// PRODUCTOS.JS - MARAV
// ============================================================
//
// Módulo completo de productos.
//
// FUNCIONES:
//
// - Listar productos
// - Buscar productos
// - Crear productos
// - Editar productos
// - Actualizar productos
// - Eliminar productos
// - Cargar proveedores
// - Control ADMIN / EMPLEADO
// - KPIs
// - Modal
// - SweetAlert2
// - Protección contra doble registro
// - Protección contra doble actualización
//
// ============================================================


// ============================================================
// VARIABLES GLOBALES
// ============================================================

let productos = [];

let proveedores = [];


// ============================================================
// CONTROL DE OPERACIONES
// ============================================================
//
// Esta variable evita que una misma operación se ejecute
// varias veces al mismo tiempo.
//
// Ejemplo:
//
// Usuario hace doble clic en Guardar.
//
// Primer clic:
//      POST /api/productos
//
// Segundo clic:
//      BLOQUEADO
//
// Lo mismo ocurre con Actualizar.
//
// ============================================================

let operacionProductoEnCurso = false;


// ============================================================
// INICIO DEL MODULO
// ============================================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        console.log(
            "📦 Productos | MARAV iniciado"
        );


        // ----------------------------------------------------
        // Configurar buscador global.
        // ----------------------------------------------------

        configurarBuscadorTopbar();


        // ----------------------------------------------------
        // Configurar tecla ESC.
        // ----------------------------------------------------

        document.addEventListener(
            "keydown",
            function (event) {

                if (event.key === "Escape") {

                    cerrarModalProducto();

                }

            }
        );


        // ----------------------------------------------------
        // Cerrar modal haciendo clic fuera.
        // ----------------------------------------------------

        const modal =
            document.getElementById(
                "modalProducto"
            );


        if (modal) {

            modal.addEventListener(
                "click",
                function (event) {

                    if (
                        event.target === modal
                    ) {

                        cerrarModalProducto();

                    }

                }
            );

        }


        // ----------------------------------------------------
        // Control de rol.
        // ----------------------------------------------------

        if (esAdmin()) {

            // ADMIN puede crear y editar.
            cargarProveedores();

        } else {

            // EMPLEADO solamente consulta.
            ocultarFormularioAdmin();

        }


        // ----------------------------------------------------
        // Configurar formulario.
        // ----------------------------------------------------

        configurarFormularioProducto();


        // ----------------------------------------------------
        // Configurar botones.
        // ----------------------------------------------------

        configurarBotonesProducto();


        // ----------------------------------------------------
        // Cargar productos.
        // ----------------------------------------------------

        cargarProductos();

    }
);


// ============================================================
// CONTROL DE ROL
// ============================================================

function esAdmin() {

    return String(
        ROL_USUARIO || ""
    )
        .trim()
        .toUpperCase() === "ADMIN";

}


// ============================================================
// CONFIGURAR BUSCADOR GLOBAL
// ============================================================
//
// Utiliza:
//
// #buscarTopbar
//
// ============================================================

function configurarBuscadorTopbar() {

    const buscador =
        document.getElementById(
            "buscarTopbar"
        );


    // --------------------------------------------------------
    // Validar.
    // --------------------------------------------------------

    if (!buscador) {

        console.warn(
            "⚠️ No se encontró #buscarTopbar"
        );

        return;

    }


    // --------------------------------------------------------
    // Evitar registrar evento dos veces.
    // --------------------------------------------------------

    if (
        buscador.dataset.productosConfigurado === "true"
    ) {

        return;

    }


    buscador.dataset.productosConfigurado =
        "true";


    // --------------------------------------------------------
    // Placeholder.
    // --------------------------------------------------------

    buscador.placeholder =
        "Buscar productos...";


    // --------------------------------------------------------
    // Evento.
    // --------------------------------------------------------

    buscador.addEventListener(
        "input",
        function () {

            filtrarProductos(
                this.value
            );

        }
    );

}


// ============================================================
// OCULTAR CONTROLES PARA EMPLEADO
// ============================================================

function ocultarFormularioAdmin() {

    const botonNuevo =
        document.getElementById(
            "btnNuevoProducto"
        );


    if (botonNuevo) {

        botonNuevo.style.display =
            "none";

    }

}


// ============================================================
// CONFIGURAR FORMULARIO
// ============================================================
//
// Aquí se controla:
//
// NUEVO:
//      Guardar producto
//
// EDITAR:
//      Actualizar producto
//
// ============================================================

function configurarFormularioProducto() {

    const formulario =
        document.getElementById(
            "formProducto"
        );


    // --------------------------------------------------------
    // Si no existe el formulario.
    // --------------------------------------------------------

    if (!formulario) {

        console.warn(
            "⚠️ No se encontró #formProducto"
        );

        return;

    }


    // --------------------------------------------------------
    // Evento submit.
    //
    // Este evento solamente se utiliza para Guardar.
    //
    // La actualización tendrá su propio botón.
    // --------------------------------------------------------

    formulario.addEventListener(
        "submit",
        function (event) {

            event.preventDefault();

            event.stopPropagation();


            // ------------------------------------------------
            // Si ya hay operación, no hacer nada.
            // ------------------------------------------------

            if (
                operacionProductoEnCurso
            ) {

                return;

            }


            // ------------------------------------------------
            // Verificar si estamos editando.
            // ------------------------------------------------

            const id =
                document.getElementById(
                    "productoId"
                )
                    ?.value
                    ?.trim();


            // ------------------------------------------------
            // Si hay ID, actualizar.
            // ------------------------------------------------

            if (id) {

                actualizarProducto();

                return;

            }


            // ------------------------------------------------
            // Si no hay ID, guardar.
            // ------------------------------------------------

            guardarProducto();

        }
    );

}


// ============================================================
// CONFIGURAR BOTONES
// ============================================================
//
// Se utiliza un listener específico para Actualizar.
//
// Esto permite que funcione incluso si el botón no está
// configurado como submit.
//
// La protección operacionProductoEnCurso evita duplicados.
//
// ============================================================

function configurarBotonesProducto() {

    // --------------------------------------------------------
    // BOTÓN CANCELAR
    // --------------------------------------------------------

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

                cerrarModalProducto();

            }
        );

    }


    // --------------------------------------------------------
    // BOTÓN ACTUALIZAR
    // --------------------------------------------------------

    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    if (btnActualizar) {

        btnActualizar.addEventListener(
            "click",
            function (event) {

                // ------------------------------------------------
                // Evitar comportamiento automático del botón.
                // ------------------------------------------------

                event.preventDefault();

                event.stopPropagation();


                // ------------------------------------------------
                // Si ya hay una operación, no repetir.
                // ------------------------------------------------

                if (
                    operacionProductoEnCurso
                ) {

                    return;

                }


                // ------------------------------------------------
                // Ejecutar actualización.
                // ------------------------------------------------

                actualizarProducto();

            }
        );

    }

}


// ============================================================
// CARGAR PROVEEDORES
// ============================================================
//
// Endpoint:
//
// GET /proveedores/api
//
// ============================================================

async function cargarProveedores() {

    try {

        const response =
            await fetch(
                "/proveedores/api"
            );


        // ----------------------------------------------------
        // Validar.
        // ----------------------------------------------------

        if (!response.ok) {

            throw new Error(
                "No se pudieron cargar los proveedores."
            );

        }


        // ----------------------------------------------------
        // Convertir JSON.
        // ----------------------------------------------------

        proveedores =
            await response.json();


        console.log(
            "🚚 Proveedores cargados:",
            proveedores
        );


        // ----------------------------------------------------
        // Llenar select.
        // ----------------------------------------------------

        llenarSelectProveedores();


    } catch (error) {

        console.error(
            "❌ Error cargando proveedores:",
            error
        );


        Swal.fire({

            icon:
                "error",

            title:
                "Error",

            text:
                "No fue posible cargar los proveedores."

        });

    }

}


// ============================================================
// LLENAR SELECT DE PROVEEDORES
// ============================================================

function llenarSelectProveedores() {

    const select =
        document.getElementById(
            "proveedor"
        );


    if (!select) {

        return;

    }


    // --------------------------------------------------------
    // Limpiar.
    // --------------------------------------------------------

    select.innerHTML =
        "";


    // --------------------------------------------------------
    // Opción inicial.
    // --------------------------------------------------------

    const opcionInicial =
        document.createElement(
            "option"
        );


    opcionInicial.value =
        "";


    opcionInicial.textContent =
        "Seleccione un proveedor";


    select.appendChild(
        opcionInicial
    );


    // --------------------------------------------------------
    // Agregar proveedores.
    // --------------------------------------------------------

    proveedores.forEach(
        function (proveedor) {

            const option =
                document.createElement(
                    "option"
                );


            option.value =
                proveedor.id;


            option.textContent =
                proveedor.nombre ||
                "Sin nombre";


            select.appendChild(
                option
            );

        }
    );

}


// ============================================================
// CARGAR PRODUCTOS
// ============================================================
//
// Endpoint:
//
// GET /api/productos
//
// ============================================================

async function cargarProductos() {

    try {

        const response =
            await fetch(
                "/api/productos"
            );


        // ----------------------------------------------------
        // Validar.
        // ----------------------------------------------------

        if (!response.ok) {

            throw new Error(
                "No se pudieron cargar los productos."
            );

        }


        // ----------------------------------------------------
        // Obtener JSON.
        // ----------------------------------------------------

        productos =
            await response.json();


        console.log(
            "📦 Productos cargados:",
            productos
        );


        // ----------------------------------------------------
        // Renderizar.
        // ----------------------------------------------------

        renderProductos(
            productos
        );


        // ----------------------------------------------------
        // KPIs.
        // ----------------------------------------------------

        actualizarKPIs();


    } catch (error) {

        console.error(
            "❌ Error cargando productos:",
            error
        );


        const tabla =
            document.getElementById(
                "tablaProductos"
            );


        if (tabla) {

            tabla.innerHTML =
                "";

        }


        const vacio =
            document.getElementById(
                "productosVacio"
            );


        if (vacio) {

            vacio.style.display =
                "block";


            vacio.innerHTML = `

                <i class="fa-solid fa-triangle-exclamation"></i>

                <span>
                    No fue posible cargar los productos.
                </span>

            `;

        }


        Swal.fire({

            icon:
                "error",

            title:
                "Error",

            text:
                "No fue posible cargar los productos."

        });

    }

}


// ============================================================
// RENDERIZAR PRODUCTOS
// ============================================================

function renderProductos(lista) {

    const tabla =
        document.getElementById(
            "tablaProductos"
        );


    const vacio =
        document.getElementById(
            "productosVacio"
        );


    // --------------------------------------------------------
    // Validar tabla.
    // --------------------------------------------------------

    if (!tabla) {

        return;

    }


    // --------------------------------------------------------
    // Limpiar.
    // --------------------------------------------------------

    tabla.innerHTML =
        "";


    // --------------------------------------------------------
    // Ocultar mensaje vacío.
    // --------------------------------------------------------

    if (vacio) {

        vacio.style.display =
            "none";

    }


    // --------------------------------------------------------
    // Sin resultados.
    // --------------------------------------------------------

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


    // --------------------------------------------------------
    // Crear filas.
    // --------------------------------------------------------

    lista.forEach(
        function (producto) {

            const id =
                producto.id;


            const nombre =
                producto.nombre ||
                "Sin nombre";


            const descripcion =
                producto.descripcion ||
                "Sin descripción";


            const proveedorNombre =
                producto.proveedor &&
                producto.proveedor.nombre

                    ? producto.proveedor.nombre

                    : "Sin proveedor";


            const precio =
                Number(
                    producto.precio || 0
                );


            const fila =
                document.createElement(
                    "tr"
                );


            // ------------------------------------------------
            // Construir HTML.
            // ------------------------------------------------

            let html = `

                <td>

                    <div class="producto-nombre">

                        <div class="producto-avatar">

                            <i class="fa-solid fa-box"></i>

                        </div>

                        <span>
                            ${escapeHTML(nombre)}
                        </span>

                    </div>

                </td>


                <td>

                    <div
                        class="producto-descripcion"
                        title="${escapeHTML(descripcion)}"
                    >

                        ${escapeHTML(descripcion)}

                    </div>

                </td>


                <td>

                    <span class="producto-precio">

                        ${formatearPrecio(precio)}

                    </span>

                </td>


                <td>

                    <span class="producto-proveedor">

                        ${escapeHTML(proveedorNombre)}

                    </span>

                </td>

            `;


            // ------------------------------------------------
            // Acciones ADMIN.
            // ------------------------------------------------

            if (esAdmin()) {

                html += `

                    <td>

                        <div class="producto-actions">

                            <button
                                type="button"
                                class="producto-action-btn producto-btn-edit"
                                title="Editar producto"
                                onclick="editarProducto(${Number(id)})"
                            >

                                <i class="fa-solid fa-pen"></i>

                            </button>


                            <button
                                type="button"
                                class="producto-action-btn producto-btn-delete"
                                title="Eliminar producto"
                                onclick="eliminarProducto(${Number(id)})"
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
// ACTUALIZAR KPIs
// ============================================================

function actualizarKPIs() {

    const totalProductos =
        document.getElementById(
            "totalProductos"
        );


    if (totalProductos) {

        totalProductos.textContent =
            productos.length;

    }


    // --------------------------------------------------------
    // Proveedores únicos.
    // --------------------------------------------------------

    const idsProveedores =
        new Set();


    productos.forEach(
        function (producto) {

            if (
                producto.proveedor &&
                producto.proveedor.id != null
            ) {

                idsProveedores.add(
                    producto.proveedor.id
                );

            }

        }
    );


    const totalProveedores =
        document.getElementById(
            "totalProveedores"
        );


    if (totalProveedores) {

        totalProveedores.textContent =
            idsProveedores.size;

    }


    // --------------------------------------------------------
    // Precio promedio.
    // --------------------------------------------------------

    let suma =
        0;


    productos.forEach(
        function (producto) {

            suma += Number(
                producto.precio || 0
            );

        }
    );


    const promedio =
        productos.length > 0

            ? suma / productos.length

            : 0;


    const precioPromedio =
        document.getElementById(
            "precioPromedio"
        );


    if (precioPromedio) {

        precioPromedio.textContent =
            formatearPrecio(
                promedio
            );

    }

}


// ============================================================
// BUSCAR PRODUCTOS
// ============================================================

function filtrarProductos(texto) {

    const termino =
        normalizarTexto(
            texto
        );


    // --------------------------------------------------------
    // Sin búsqueda.
    // --------------------------------------------------------

    if (!termino) {

        renderProductos(
            productos
        );

        return;

    }


    // --------------------------------------------------------
    // Filtrar.
    // --------------------------------------------------------

    const resultados =
        productos.filter(
            function (producto) {

                const nombre =
                    normalizarTexto(
                        producto.nombre
                    );


                const descripcion =
                    normalizarTexto(
                        producto.descripcion
                    );


                const proveedor =
                    normalizarTexto(
                        producto.proveedor &&
                        producto.proveedor.nombre
                    );


                const precio =
                    normalizarTexto(
                        producto.precio
                    );


                return (

                    nombre.includes(
                        termino
                    ) ||

                    descripcion.includes(
                        termino
                    ) ||

                    proveedor.includes(
                        termino
                    ) ||

                    precio.includes(
                        termino
                    )

                );

            }
        );


    // --------------------------------------------------------
    // Renderizar.
    // --------------------------------------------------------

    renderProductos(
        resultados
    );


    // --------------------------------------------------------
    // Sin resultados.
    // --------------------------------------------------------

    if (
        resultados.length === 0
    ) {

        const vacio =
            document.getElementById(
                "productosVacio"
            );


        if (vacio) {

            vacio.style.display =
                "block";


            vacio.innerHTML = `

                <i class="fa-solid fa-magnifying-glass"></i>

                <span>

                    No se encontraron productos para

                    "<strong>
                        ${escapeHTML(texto)}
                    </strong>".

                </span>

            `;

        }

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
// FORMATEAR PRECIO
// ============================================================

function formatearPrecio(valor) {

    return Number(
        valor || 0
    ).toLocaleString(
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
    );

}


// ============================================================
// OBTENER DATOS DEL FORMULARIO
// ============================================================

function obtenerDatosFormulario() {

    // --------------------------------------------------------
    // Nombre.
    // --------------------------------------------------------

    const nombre =
        document.getElementById(
            "nombre"
        )
            ?.value
            .trim() || "";


    // --------------------------------------------------------
    // Descripción.
    // --------------------------------------------------------

    const descripcion =
        document.getElementById(
            "descripcion"
        )
            ?.value
            .trim() || "";


    // --------------------------------------------------------
    // Precio.
    // --------------------------------------------------------

    const precioTexto =
        document.getElementById(
            "precio"
        )
            ?.value
            .trim() || "";


    // --------------------------------------------------------
    // Proveedor.
    // --------------------------------------------------------

    const proveedorTexto =
        document.getElementById(
            "proveedor"
        )
            ?.value || "";


    const precio =
        Number(
            precioTexto
        );


    const proveedorId =
        proveedorTexto

            ? parseInt(
                proveedorTexto,
                10
            )

            : null;


    // --------------------------------------------------------
    // Objeto para Spring Boot.
    // --------------------------------------------------------

    return {

        nombre:
        nombre,

        descripcion:
        descripcion,

        precio:
        precio,

        proveedor:
            proveedorId

                ? {
                    id:
                    proveedorId
                }

                : null

    };

}


// ============================================================
// VALIDAR FORMULARIO
// ============================================================

function validarFormulario(datos) {

    // --------------------------------------------------------
    // Nombre.
    // --------------------------------------------------------

    if (!datos.nombre) {

        Swal.fire({

            icon:
                "warning",

            title:
                "Nombre requerido",

            text:
                "Ingrese el nombre del producto."

        });

        return false;

    }


    // --------------------------------------------------------
    // Precio.
    // --------------------------------------------------------

    if (

        datos.precio === null ||

        Number.isNaN(
            datos.precio
        ) ||

        datos.precio < 0

    ) {

        Swal.fire({

            icon:
                "warning",

            title:
                "Precio inválido",

            text:
                "Ingrese un precio válido."

        });

        return false;

    }


    // --------------------------------------------------------
    // Proveedor.
    // --------------------------------------------------------

    if (!datos.proveedor) {

        Swal.fire({

            icon:
                "warning",

            title:
                "Proveedor requerido",

            text:
                "Seleccione un proveedor."

        });

        return false;

    }


    return true;

}


// ============================================================
// ABRIR MODAL NUEVO
// ============================================================

function abrirModalProducto() {

    // --------------------------------------------------------
    // Solo ADMIN.
    // --------------------------------------------------------

    if (!esAdmin()) {

        return;

    }


    // --------------------------------------------------------
    // No abrir mientras se procesa otra operación.
    // --------------------------------------------------------

    if (
        operacionProductoEnCurso
    ) {

        return;

    }


    const modal =
        document.getElementById(
            "modalProducto"
        );


    if (!modal) {

        return;

    }


    // --------------------------------------------------------
    // Limpiar.
    // --------------------------------------------------------

    limpiarFormularioProducto();


    // --------------------------------------------------------
    // Modo nuevo.
    // --------------------------------------------------------

    cambiarModoModal(
        "nuevo"
    );


    // --------------------------------------------------------
    // Abrir.
    // --------------------------------------------------------

    modal.classList.add(
        "active"
    );


    modal.setAttribute(
        "aria-hidden",
        "false"
    );


    // --------------------------------------------------------
    // Bloquear scroll.
    // --------------------------------------------------------

    document.body.style.overflow =
        "hidden";


    // --------------------------------------------------------
    // Enfocar.
    // --------------------------------------------------------

    setTimeout(
        function () {

            document.getElementById(
                "nombre"
            )?.focus();

        },
        100
    );

}


// ============================================================
// EDITAR PRODUCTO
// ============================================================
//
// Endpoint:
//
// GET /api/productos/{id}
//
// ============================================================

async function editarProducto(id) {

    // --------------------------------------------------------
    // Solo ADMIN.
    // --------------------------------------------------------

    if (!esAdmin()) {

        return;

    }


    // --------------------------------------------------------
    // No permitir mientras haya operación.
    // --------------------------------------------------------

    if (
        operacionProductoEnCurso
    ) {

        return;

    }


    try {

        const response =
            await fetch(
                `/api/productos/${id}`
            );


        // ----------------------------------------------------
        // Validar.
        // ----------------------------------------------------

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        // ----------------------------------------------------
        // Obtener producto.
        // ----------------------------------------------------

        const producto =
            await response.json();


        console.log(
            "✏️ Producto para editar:",
            producto
        );


        // ----------------------------------------------------
        // ID.
        // ----------------------------------------------------

        const productoId =
            document.getElementById(
                "productoId"
            );


        if (productoId) {

            productoId.value =
                producto.id || "";

        }


        // ----------------------------------------------------
        // Nombre.
        // ----------------------------------------------------

        const nombre =
            document.getElementById(
                "nombre"
            );


        if (nombre) {

            nombre.value =
                producto.nombre || "";

        }


        // ----------------------------------------------------
        // Descripción.
        // ----------------------------------------------------

        const descripcion =
            document.getElementById(
                "descripcion"
            );


        if (descripcion) {

            descripcion.value =
                producto.descripcion || "";

        }


        // ----------------------------------------------------
        // Precio.
        // ----------------------------------------------------

        const precio =
            document.getElementById(
                "precio"
            );


        if (precio) {

            precio.value =
                producto.precio ?? "";

        }


        // ----------------------------------------------------
        // Proveedor.
        // ----------------------------------------------------

        const proveedorSelect =
            document.getElementById(
                "proveedor"
            );


        if (proveedorSelect) {

            proveedorSelect.value =
                producto.proveedor?.id

                    ? String(
                        producto.proveedor.id
                    )

                    : "";

        }


        // ----------------------------------------------------
        // Cambiar modal a EDITAR.
        // ----------------------------------------------------

        cambiarModoModal(
            "editar"
        );


        // ----------------------------------------------------
        // Abrir modal.
        // ----------------------------------------------------

        abrirModalProductoEdicion();


    } catch (error) {

        console.error(
            "❌ Error obteniendo producto:",
            error
        );


        Swal.fire({

            icon:
                "error",

            title:
                "Error",

            text:
                error.message ||
                "No fue posible cargar el producto."

        });

    }

}


// ============================================================
// ABRIR MODAL EDICION
// ============================================================

function abrirModalProductoEdicion() {

    const modal =
        document.getElementById(
            "modalProducto"
        );


    if (!modal) {

        return;

    }


    modal.classList.add(
        "active"
    );


    modal.setAttribute(
        "aria-hidden",
        "false"
    );


    document.body.style.overflow =
        "hidden";


    // --------------------------------------------------------
    // Enfocar nombre.
    // --------------------------------------------------------

    setTimeout(
        function () {

            document.getElementById(
                "nombre"
            )?.focus();

        },
        100
    );

}


// ============================================================
// CAMBIAR MODO DEL MODAL
// ============================================================

function cambiarModoModal(modo) {

    const titulo =
        document.getElementById(
            "tituloFormulario"
        );


    const subtitulo =
        document.getElementById(
            "subtituloFormulario"
        );


    const btnGuardar =
        document.getElementById(
            "btnGuardar"
        );


    const btnActualizar =
        document.getElementById(
            "btnActualizar"
        );


    // --------------------------------------------------------
    // MODO NUEVO.
    // --------------------------------------------------------

    if (
        modo === "nuevo"
    ) {

        if (titulo) {

            titulo.innerHTML = `

                <i class="fa-solid fa-box"></i>

                Nuevo Producto

            `;

        }


        if (subtitulo) {

            subtitulo.textContent =
                "Registra un nuevo producto en MARAV";

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


    // --------------------------------------------------------
    // MODO EDITAR.
    // --------------------------------------------------------

    if (titulo) {

        titulo.innerHTML = `

            <i class="fa-solid fa-box"></i>

            Editar Producto

        `;

    }


    if (subtitulo) {

        subtitulo.textContent =
            "Actualiza la información del producto";

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


// ============================================================
// GUARDAR PRODUCTO
// ============================================================
//
// Endpoint:
//
// POST /api/productos
//
// ============================================================

async function guardarProducto() {

    // --------------------------------------------------------
    // PROTECCIÓN CONTRA DOBLE REGISTRO.
    // --------------------------------------------------------

    if (
        operacionProductoEnCurso
    ) {

        console.warn(
            "⚠️ Ya existe un registro en curso."
        );

        return;

    }


    // --------------------------------------------------------
    // Obtener datos.
    // --------------------------------------------------------

    const datos =
        obtenerDatosFormulario();


    // --------------------------------------------------------
    // Validar.
    // --------------------------------------------------------

    if (
        !validarFormulario(
            datos
        )
    ) {

        return;

    }


    // --------------------------------------------------------
    // BLOQUEAR.
    // --------------------------------------------------------

    operacionProductoEnCurso =
        true;


    cambiarEstadoBotonesProducto(
        true
    );


    try {

        const response =
            await fetch(
                "/api/productos",
                {

                    method:
                        "POST",

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
        // Validar respuesta.
        // ----------------------------------------------------

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        // ----------------------------------------------------
        // IMPORTANTE:
        //
        // El backend ya confirmó.
        //
        // Cerramos modal.
        // ----------------------------------------------------

        limpiarFormularioProducto();

        cerrarModalProducto();


        // ----------------------------------------------------
        // Liberar bloqueo.
        // ----------------------------------------------------

        operacionProductoEnCurso =
            false;


        cambiarEstadoBotonesProducto(
            false
        );


        // ----------------------------------------------------
        // Mensaje.
        // ----------------------------------------------------

        await Swal.fire({

            icon:
                "success",

            title:
                "Producto registrado",

            text:
                "El producto fue registrado correctamente.",

            confirmButtonText:
                "Aceptar"

        });


        // ----------------------------------------------------
        // Actualizar lista.
        //
        // No usamos await.
        // ----------------------------------------------------

        cargarProductos()
            .catch(
                function (error) {

                    console.error(
                        "❌ Error actualizando lista:",
                        error
                    );

                }
            );


    } catch (error) {

        console.error(
            "❌ Error guardando producto:",
            error
        );


        // ----------------------------------------------------
        // Liberar bloqueo.
        // ----------------------------------------------------

        operacionProductoEnCurso =
            false;


        cambiarEstadoBotonesProducto(
            false
        );


        Swal.fire({

            icon:
                "error",

            title:
                "No se pudo guardar",

            text:
                error.message ||
                "Ocurrió un error al guardar el producto."

        });

    }

}


// ============================================================
// ACTUALIZAR PRODUCTO
// ============================================================
//
// Endpoint:
//
// PUT /api/productos/{id}
//
// ============================================================

async function actualizarProducto() {

    // --------------------------------------------------------
    // PROTECCIÓN CONTRA DOBLE ACTUALIZACIÓN.
    // --------------------------------------------------------

    if (
        operacionProductoEnCurso
    ) {

        console.warn(
            "⚠️ Ya existe una actualización en curso."
        );

        return;

    }


    // --------------------------------------------------------
    // Obtener ID.
    // --------------------------------------------------------

    const id =
        document.getElementById(
            "productoId"
        )
            ?.value
            ?.trim();


    // --------------------------------------------------------
    // Validar ID.
    // --------------------------------------------------------

    if (!id) {

        Swal.fire({

            icon:
                "error",

            title:
                "Producto no encontrado",

            text:
                "No se encontró el producto que deseas actualizar."

        });

        return;

    }


    // --------------------------------------------------------
    // Obtener datos.
    // --------------------------------------------------------

    const datos =
        obtenerDatosFormulario();


    // --------------------------------------------------------
    // Validar.
    // --------------------------------------------------------

    if (
        !validarFormulario(
            datos
        )
    ) {

        return;

    }


    // --------------------------------------------------------
    // BLOQUEAR OPERACIÓN.
    // --------------------------------------------------------

    operacionProductoEnCurso =
        true;


    cambiarEstadoBotonesProducto(
        true
    );


    try {

        console.log(
            "✏️ Actualizando producto:",
            id,
            datos
        );


        const response =
            await fetch(
                `/api/productos/${id}`,
                {

                    method:
                        "PUT",

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
        // Validar.
        // ----------------------------------------------------

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        // ----------------------------------------------------
        // MUY IMPORTANTE:
        //
        // El backend confirmó el PUT.
        //
        // Cerramos el modal inmediatamente.
        // ----------------------------------------------------

        limpiarFormularioProducto();

        cerrarModalProducto();


        // ----------------------------------------------------
        // Liberar bloqueo.
        // ----------------------------------------------------

        operacionProductoEnCurso =
            false;


        cambiarEstadoBotonesProducto(
            false
        );


        // ----------------------------------------------------
        // Mostrar éxito.
        // ----------------------------------------------------

        await Swal.fire({

            icon:
                "success",

            title:
                "Producto actualizado",

            text:
                "El producto fue actualizado correctamente.",

            confirmButtonText:
                "Aceptar"

        });


        // ----------------------------------------------------
        // Actualizar tabla.
        //
        // No bloqueamos el modal esperando el GET.
        // ----------------------------------------------------

        cargarProductos()
            .catch(
                function (error) {

                    console.error(
                        "❌ Error actualizando la lista:",
                        error
                    );

                }
            );


    } catch (error) {

        console.error(
            "❌ Error actualizando producto:",
            error
        );


        // ----------------------------------------------------
        // Liberar bloqueo.
        // ----------------------------------------------------

        operacionProductoEnCurso =
            false;


        cambiarEstadoBotonesProducto(
            false
        );


        Swal.fire({

            icon:
                "error",

            title:
                "No se pudo actualizar",

            text:
                error.message ||
                "Ocurrió un error al actualizar el producto."

        });

    }

}


// ============================================================
// CONTROL DE BOTONES
// ============================================================

function cambiarEstadoBotonesProducto(
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


    // --------------------------------------------------------
    // Guardar.
    // --------------------------------------------------------

    if (btnGuardar) {

        btnGuardar.disabled =
            bloquear;


        btnGuardar.style.pointerEvents =
            bloquear
                ? "none"
                : "";


        btnGuardar.style.opacity =
            bloquear
                ? "0.6"
                : "";

    }


    // --------------------------------------------------------
    // Actualizar.
    // --------------------------------------------------------

    if (btnActualizar) {

        btnActualizar.disabled =
            bloquear;


        btnActualizar.style.pointerEvents =
            bloquear
                ? "none"
                : "";


        btnActualizar.style.opacity =
            bloquear
                ? "0.6"
                : "";

    }

}


// ============================================================
// ELIMINAR PRODUCTO
// ============================================================
//
// Endpoint:
//
// DELETE /api/productos/{id}
//
// ============================================================

async function eliminarProducto(id) {

    // --------------------------------------------------------
    // Solo ADMIN.
    // --------------------------------------------------------

    if (!esAdmin()) {

        return;

    }


    // --------------------------------------------------------
    // Evitar operación simultánea.
    // --------------------------------------------------------

    if (
        operacionProductoEnCurso
    ) {

        return;

    }


    // --------------------------------------------------------
    // Buscar producto.
    // --------------------------------------------------------

    const producto =
        productos.find(
            function (item) {

                return Number(
                    item.id
                ) === Number(id);

            }
        );


    const nombre =
        producto?.nombre ||
        "este producto";


    // --------------------------------------------------------
    // Confirmación.
    // --------------------------------------------------------

    const confirmacion =
        await Swal.fire({

            icon:
                "warning",

            title:
                "¿Eliminar producto?",

            html:
                `¿Deseas eliminar <strong>${escapeHTML(nombre)}</strong>?`,

            showCancelButton:
                true,

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


    // --------------------------------------------------------
    // Bloquear.
    // --------------------------------------------------------

    operacionProductoEnCurso =
        true;


    try {

        const response =
            await fetch(
                `/api/productos/${id}`,
                {

                    method:
                        "DELETE"

                }
            );


        // ----------------------------------------------------
        // Validar.
        // ----------------------------------------------------

        if (!response.ok) {

            throw new Error(
                await obtenerMensajeError(
                    response
                )
            );

        }


        // ----------------------------------------------------
        // Liberar.
        // ----------------------------------------------------

        operacionProductoEnCurso =
            false;


        // ----------------------------------------------------
        // Éxito.
        // ----------------------------------------------------

        await Swal.fire({

            icon:
                "success",

            title:
                "Producto eliminado",

            text:
                "El producto fue eliminado correctamente.",

            confirmButtonText:
                "Aceptar"

        });


        // ----------------------------------------------------
        // Actualizar tabla.
        // ----------------------------------------------------

        cargarProductos()
            .catch(
                function (error) {

                    console.error(
                        "❌ Error actualizando productos:",
                        error
                    );

                }
            );


    } catch (error) {

        console.error(
            "❌ Error eliminando producto:",
            error
        );


        operacionProductoEnCurso =
            false;


        Swal.fire({

            icon:
                "error",

            title:
                "No se pudo eliminar",

            text:
                error.message ||
                "Ocurrió un error al eliminar el producto."

        });

    }

}


// ============================================================
// LIMPIAR FORMULARIO
// ============================================================

function limpiarFormularioProducto() {

    const formulario =
        document.getElementById(
            "formProducto"
        );


    // --------------------------------------------------------
    // Reset.
    // --------------------------------------------------------

    if (formulario) {

        formulario.reset();

    }


    // --------------------------------------------------------
    // Limpiar ID.
    // --------------------------------------------------------

    const id =
        document.getElementById(
            "productoId"
        );


    if (id) {

        id.value =
            "";

    }


    // --------------------------------------------------------
    // Modo nuevo.
    // --------------------------------------------------------

    cambiarModoModal(
        "nuevo"
    );


    // --------------------------------------------------------
    // Restaurar botones.
    // --------------------------------------------------------

    cambiarEstadoBotonesProducto(
        false
    );

}


// ============================================================
// CERRAR MODAL
// ============================================================

function cerrarModalProducto() {

    const modal =
        document.getElementById(
            "modalProducto"
        );


    // --------------------------------------------------------
    // Si no existe.
    // --------------------------------------------------------

    if (!modal) {

        document.body.style.overflow =
            "";

        return;

    }


    // --------------------------------------------------------
    // Cerrar.
    // --------------------------------------------------------

    modal.classList.remove(
        "active"
    );


    // --------------------------------------------------------
    // Accesibilidad.
    // --------------------------------------------------------

    modal.setAttribute(
        "aria-hidden",
        "true"
    );


    // --------------------------------------------------------
    // Restaurar scroll.
    // --------------------------------------------------------

    document.body.style.overflow =
        "";

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
// OBTENER MENSAJE DE ERROR
// ============================================================

async function obtenerMensajeError(
    response
) {

    try {

        const texto =
            await response.text();


        // ----------------------------------------------------
        // Sin respuesta.
        // ----------------------------------------------------

        if (!texto) {

            return `Error HTTP ${response.status}`;

        }


        // ----------------------------------------------------
        // Intentar JSON.
        // ----------------------------------------------------

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

            // ------------------------------------------------
            // Respuesta de texto plano.
            // ------------------------------------------------

            return texto;

        }

    } catch (error) {

        return `Error HTTP ${response.status}`;

    }

}


// ============================================================
// FIN DEL MODULO
// ============================================================

console.log(
    "📦 productos.js de MARAV cargado correctamente"
);