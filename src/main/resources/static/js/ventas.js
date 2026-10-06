
// ======================================================
// VARIABLES GLOBALES
// ======================================================

let productos = [];

let clientes = [];

let carrito = [];


// ======================================================
// FORMATO DE MONEDA
// ======================================================

function dinero(valor) {

    const numero = Number(valor) || 0;

    return numero.toLocaleString(
        "es-CO",
        {
            style: "currency",
            currency: "COP",
            minimumFractionDigits: 0
        }
    );
}


// ======================================================
// ESCAPAR HTML
//
// Evita insertar directamente contenido de la base
// de datos dentro del HTML.
// ======================================================

function escapeHTML(valor) {

    if (
        valor === null ||
        valor === undefined
    ) {
        return "";
    }

    return String(valor)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


// ======================================================
// MENSAJES
//
// Utiliza SweetAlert si está disponible.
// ======================================================

function mostrarMensaje(
    titulo,
    texto,
    icono = "info"
) {

    if (typeof Swal !== "undefined") {

        return Swal.fire({

            title: titulo,

            text: texto,

            icon: icono,

            confirmButtonColor: "#16a34a"

        });

    }

    alert(
        titulo +
        (texto ? "\n\n" + texto : "")
    );
}


// ======================================================
// CARGAR PRODUCTOS
// ======================================================

async function cargarProductos() {

    try {

        const response =
            await fetch("/api/productos");


        // ==================================================
        // VALIDAR RESPUESTA
        // ==================================================

        if (!response.ok) {

            throw new Error(
                "Error cargando productos"
            );
        }


        // ==================================================
        // GUARDAR PRODUCTOS
        // ==================================================

        productos =
            await response.json();


        console.log(
            "PRODUCTOS CARGADOS:",
            productos
        );


        // ==================================================
        // MOSTRAR PRODUCTOS
        // ==================================================

        renderProductos(
            productos
        );


    }
    catch (error) {

        console.error(
            "Error productos:",
            error
        );


        const contenedor =
            document.getElementById(
                "listaProductos"
            );


        if (contenedor) {

            contenedor.innerHTML = `

                <div class="ventas-empty">

                    <i class="fa-solid fa-triangle-exclamation"></i>

                    <span>
                        Error cargando productos.
                    </span>

                </div>

            `;
        }

    }

}


// ======================================================
// CARGAR CLIENTES
// ======================================================

async function cargarClientes() {

    try {

        const response =
            await fetch("/api/clientes");


        // ==================================================
        // VALIDAR RESPUESTA
        // ==================================================

        if (!response.ok) {

            throw new Error(
                "Error cargando clientes"
            );
        }


        // ==================================================
        // GUARDAR CLIENTES
        // ==================================================

        clientes =
            await response.json();


        console.log(
            "CLIENTES CARGADOS:",
            clientes
        );


        // ==================================================
        // SELECT CLIENTES
        // ==================================================

        const select =
            document.getElementById(
                "cliente"
            );


        if (!select) {

            return;
        }


        // ==================================================
        // OPCIÓN INICIAL
        // ==================================================

        select.innerHTML = `

            <option value="">
                👥 Cliente opcional
            </option>

        `;


        // ==================================================
        // AGREGAR CLIENTES
        // ==================================================

        clientes.forEach(
            cliente => {

                select.innerHTML += `

                    <option value="${cliente.id}">

                        ${escapeHTML(
                    cliente.nombre
                )}

                    </option>

                `;

            }
        );


    }
    catch (error) {

        console.error(
            "Error clientes:",
            error
        );

    }

}


// ======================================================
// CONFIGURAR CLIENTE
//
// Al seleccionar un cliente se carga automáticamente
// su teléfono en el campo de WhatsApp.
// ======================================================

function configurarCliente() {

    const select =
        document.getElementById(
            "cliente"
        );


    if (!select) {

        return;
    }


    select.addEventListener(
        "change",
        function () {

            const id =
                this.value;


            const cliente =
                clientes.find(
                    c => c.id == id
                );


            console.log(
                "CLIENTE SELECCIONADO:",
                cliente
            );


            const telefono =
                document.getElementById(
                    "telefonoWhatsapp"
                );


            if (!telefono) {

                return;
            }


            // ==============================================
            // CLIENTE ENCONTRADO
            // ==============================================

            if (cliente) {

                telefono.value =
                    cliente.telefono || "";

            }


                // ==============================================
                // SIN CLIENTE
            // ==============================================

            else {

                telefono.value = "";

            }

        }
    );

}


// ======================================================
// RENDERIZAR PRODUCTOS
// ======================================================

function renderProductos(lista) {

    const contenedor =
        document.getElementById(
            "listaProductos"
        );


    if (!contenedor) {

        return;
    }


    // ==================================================
    // LIMPIAR
    // ==================================================

    contenedor.innerHTML = "";


    // ==================================================
    // SIN PRODUCTOS
    // ==================================================

    if (
        !lista ||
        lista.length === 0
    ) {

        contenedor.innerHTML = `

            <div class="ventas-empty">

                <i class="fa-solid fa-box-open"></i>

                <span>
                    No se encontraron productos.
                </span>

            </div>

        `;

        return;
    }


    // ==================================================
    // CREAR TARJETAS
    // ==================================================

    lista.forEach(
        producto => {

            contenedor.innerHTML += `

                <article
                    class="producto"
                    onclick="agregarCarrito(${producto.id})"
                    title="Agregar ${escapeHTML(producto.nombre)} al carrito">

                    <div>

                        <div class="producto-icono">

                            <i class="fa-solid fa-box"></i>

                        </div>


                        <h3>

                            ${escapeHTML(
                producto.nombre
            )}

                        </h3>

                    </div>


                    <div>

                        <div class="precio">

                            ${dinero(
                producto.precio
            )}

                        </div>


                        <div class="stock">

                            <i class="fa-solid fa-circle-check"></i>

                            Disponible

                        </div>

                    </div>

                </article>

            `;

        }
    );

}


// ======================================================
// AGREGAR PRODUCTO AL CARRITO
// ======================================================

function agregarCarrito(id) {

    // ==================================================
    // BUSCAR PRODUCTO
    // ==================================================

    const producto =
        productos.find(
            p => p.id == id
        );


    // ==================================================
    // VALIDAR
    // ==================================================

    if (!producto) {

        mostrarMensaje(
            "Producto no encontrado",
            "No fue posible agregar el producto al carrito.",
            "error"
        );

        return;
    }


    // ==================================================
    // BUSCAR SI YA EXISTE
    // ==================================================

    const existe =
        carrito.find(
            item => item.id == id
        );


    // ==================================================
    // SI YA EXISTE
    // ==================================================

    if (existe) {

        existe.cantidad++;

    }


        // ==================================================
        // SI ES NUEVO
    // ==================================================

    else {

        carrito.push({

            id:
            producto.id,

            nombre:
            producto.nombre,

            precio:
                Number(producto.precio) || 0,

            cantidad:
                1

        });

    }


    // ==================================================
    // ACTUALIZAR CARRITO
    // ==================================================

    renderCarrito();

}


// ======================================================
// RENDERIZAR CARRITO
//
// Actualiza:
// - Productos
// - Cantidades
// - Contador
// - Subtotal
// - IVA
// - Total
// - Cambio
// ======================================================

function renderCarrito() {

    // ==================================================
    // ELEMENTOS HTML
    // ==================================================

    const items =
        document.getElementById(
            "items"
        );


    const contador =
        document.getElementById(
            "cantidadCarrito"
        );


    const subtotalHTML =
        document.getElementById(
            "subtotal"
        );


    const ivaHTML =
        document.getElementById(
            "valorIVA"
        );


    const totalHTML =
        document.getElementById(
            "total"
        );


    // ==================================================
    // VALIDAR
    // ==================================================

    if (!items) {

        console.error(
            "No existe el elemento #items"
        );

        return;
    }


    // ==================================================
    // LIMPIAR
    // ==================================================

    items.innerHTML = "";


    // ==================================================
    // VARIABLES
    // ==================================================

    let subtotalGeneral = 0;

    let cantidadTotal = 0;


    // ==================================================
    // CARRITO VACÍO
    // ==================================================

    if (carrito.length === 0) {

        items.innerHTML = `

            <div class="carrito-vacio">

                <i class="fa-solid fa-cart-shopping"></i>

                <strong>
                    Carrito vacío
                </strong>

                <span>
                    Selecciona productos para comenzar una venta.
                </span>

            </div>

        `;

    }


    // ==================================================
    // RECORRER CARRITO
    // ==================================================

    carrito.forEach(
        item => {

            const precio =
                Number(item.precio) || 0;


            const cantidad =
                Number(item.cantidad) || 0;


            const subtotal =
                precio *
                cantidad;


            // ==============================================
            // ACUMULAR SUBTOTAL
            // ==============================================

            subtotalGeneral +=
                subtotal;


            // ==============================================
            // ACUMULAR CANTIDADES
            //
            // Ejemplo:
            // Arroz x2 + Aceite x3 = 5 productos
            // ==============================================

            cantidadTotal +=
                cantidad;


            // ==============================================
            // MOSTRAR ITEM
            // ==============================================

            items.innerHTML += `

                <div class="item">

                    <div class="item-top">

                        <strong>

                            ${escapeHTML(
                item.nombre
            )}

                        </strong>


                        <strong>

                            ${dinero(
                subtotal
            )}

                        </strong>

                    </div>


                    <div class="cantidad">

                        <button
                            type="button"
                            onclick="menos(${item.id})"
                            title="Disminuir cantidad">

                            <i class="fa-solid fa-minus"></i>

                        </button>


                        <span>

                            ${cantidad}

                        </span>


                        <button
                            type="button"
                            onclick="mas(${item.id})"
                            title="Aumentar cantidad">

                            <i class="fa-solid fa-plus"></i>

                        </button>

                    </div>

                </div>

            `;

        }
    );


    // ==================================================
    // ACTUALIZAR CONTADOR
    //
    // IMPORTANTE:
    // Este bloque es el encargado de cambiar
    // "0 productos" por "1 producto", "2 productos", etc.
    // ==================================================

    if (contador) {

        if (cantidadTotal === 0) {

            contador.textContent =
                "0 productos";

        }

        else if (cantidadTotal === 1) {

            contador.textContent =
                "1 producto";

        }

        else {

            contador.textContent =
                cantidadTotal +
                " productos";

        }

    }


    // ==================================================
    // IVA
    // ==================================================

    const checkIVA =
        document.getElementById(
            "aplicarIVA"
        );


    const aplicaIVA =
        checkIVA
            ? checkIVA.checked
            : false;


    let valorIVA = 0;


    if (aplicaIVA) {

        valorIVA =
            subtotalGeneral *
            0.19;

    }


    // ==================================================
    // TOTAL
    // ==================================================

    const totalFinal =
        subtotalGeneral +
        valorIVA;


    // ==================================================
    // MOSTRAR SUBTOTAL
    // ==================================================

    if (subtotalHTML) {

        subtotalHTML.textContent =
            dinero(
                subtotalGeneral
            );

    }


    // ==================================================
    // MOSTRAR IVA
    // ==================================================

    if (ivaHTML) {

        ivaHTML.textContent =
            dinero(
                valorIVA
            );

    }


    // ==================================================
    // MOSTRAR TOTAL
    // ==================================================

    if (totalHTML) {

        totalHTML.textContent =
            dinero(
                totalFinal
            );

    }


    // ==================================================
    // CALCULAR CAMBIO
    // ==================================================

    calcularCambio();

}


// ======================================================
// AUMENTAR CANTIDAD
// ======================================================

function mas(id) {

    const item =
        carrito.find(
            i => i.id == id
        );


    if (!item) {

        return;
    }


    item.cantidad++;


    renderCarrito();

}


// ======================================================
// DISMINUIR CANTIDAD
// ======================================================

function menos(id) {

    const item =
        carrito.find(
            i => i.id == id
        );


    if (!item) {

        return;
    }


    item.cantidad--;


    // ==================================================
    // ELIMINAR PRODUCTO SI LLEGA A CERO
    // ==================================================

    if (item.cantidad <= 0) {

        carrito =
            carrito.filter(
                i => i.id !== id
            );

    }


    renderCarrito();

}


// ======================================================
// CALCULAR CAMBIO
// ======================================================

function calcularCambio() {

    const metodoElement =
        document.getElementById(
            "metodoPago"
        );


    const cambioElement =
        document.getElementById(
            "cambio"
        );


    if (
        !metodoElement ||
        !cambioElement
    ) {

        return;
    }


    const metodo =
        metodoElement.value;


    // ==================================================
    // SI NO ES EFECTIVO
    // ==================================================

    if (metodo !== "EFECTIVO") {

        cambioElement.value =
            "$0";

        return;
    }


    // ==================================================
    // CALCULAR SUBTOTAL
    // ==================================================

    const subtotalGeneral =
        carrito.reduce(

            (acc, item) =>

                acc +
                (
                    Number(item.precio) *
                    Number(item.cantidad)
                ),

            0
        );


    // ==================================================
    // IVA
    // ==================================================

    const checkIVA =
        document.getElementById(
            "aplicarIVA"
        );


    const aplicaIVA =
        checkIVA
            ? checkIVA.checked
            : false;


    let valorIVA = 0;


    if (aplicaIVA) {

        valorIVA =
            subtotalGeneral *
            0.19;

    }


    // ==================================================
    // TOTAL
    // ==================================================

    const total =
        subtotalGeneral +
        valorIVA;


    // ==================================================
    // EFECTIVO RECIBIDO
    // ==================================================

    const efectivoElement =
        document.getElementById(
            "efectivo"
        );


    const efectivo =
        parseFloat(
            efectivoElement?.value || 0
        );


    // ==================================================
    // CALCULAR CAMBIO
    // ==================================================

    const cambio =
        efectivo -
        total;


    cambioElement.value =
        dinero(cambio);

}


// ======================================================
// MOSTRAR / OCULTAR CAMPOS DE EFECTIVO
// ======================================================

function cambiarMetodoPago() {

    const metodo =
        document.getElementById(
            "metodoPago"
        )?.value;


    const grupoEfectivo =
        document.getElementById(
            "grupoEfectivo"
        );


    const grupoCambio =
        document.getElementById(
            "grupoCambio"
        );


    const efectivo =
        document.getElementById(
            "efectivo"
        );


    // ==================================================
    // EFECTIVO
    // ==================================================

    if (metodo === "EFECTIVO") {

        if (grupoEfectivo) {

            grupoEfectivo.style.display =
                "flex";

        }


        if (grupoCambio) {

            grupoCambio.style.display =
                "flex";

        }


        if (efectivo) {

            efectivo.disabled =
                false;

        }

    }


        // ==================================================
        // OTROS MÉTODOS
    // ==================================================

    else {

        if (grupoEfectivo) {

            grupoEfectivo.style.display =
                "none";

        }


        if (grupoCambio) {

            grupoCambio.style.display =
                "none";

        }


        if (efectivo) {

            efectivo.disabled =
                true;

        }

    }


    calcularCambio();

}


// ======================================================
// CONFIGURAR EFECTIVO
// ======================================================

function configurarEfectivo() {

    const efectivo =
        document.getElementById(
            "efectivo"
        );


    if (!efectivo) {

        return;
    }


    // ==================================================
    // ACTUALIZAR CAMBIO MIENTRAS SE ESCRIBE
    // ==================================================

    efectivo.addEventListener(
        "input",
        calcularCambio
    );

}


// ======================================================
// FINALIZAR VENTA
// ======================================================

async function finalizarVenta() {

    try {

        // ==================================================
        // VALIDAR CARRITO
        // ==================================================

        if (carrito.length === 0) {

            await mostrarMensaje(
                "Carrito vacío",
                "Agrega al menos un producto antes de finalizar la venta.",
                "warning"
            );

            return;
        }


        // ==================================================
        // ELEMENTOS
        // ==================================================

        const clienteElement =
            document.getElementById(
                "cliente"
            );


        const metodoElement =
            document.getElementById(
                "metodoPago"
            );


        const efectivoElement =
            document.getElementById(
                "efectivo"
            );


        const telefonoElement =
            document.getElementById(
                "telefonoWhatsapp"
            );


        const whatsappElement =
            document.getElementById(
                "whatsapp"
            );


        const ivaElement =
            document.getElementById(
                "aplicarIVA"
            );


        // ==================================================
        // OBTENER VALORES
        // ==================================================

        const metodoPago =
            metodoElement.value;


        const efectivoRecibido =
            parseFloat(
                efectivoElement.value || 0
            );


        const telefonoEnvio =
            telefonoElement.value.trim();


        const enviarWhatsapp =
            whatsappElement.checked;


        const aplicarIva =
            ivaElement.checked;


        // ==================================================
        // VALIDAR EFECTIVO
        // ==================================================

        if (
            metodoPago === "EFECTIVO" &&
            efectivoRecibido < 0
        ) {

            await mostrarMensaje(
                "Efectivo inválido",
                "El valor recibido no puede ser negativo.",
                "warning"
            );

            return;
        }


        // ==================================================
        // VALIDAR WHATSAPP
        // ==================================================

        if (
            enviarWhatsapp &&
            telefonoEnvio === ""
        ) {

            await mostrarMensaje(
                "Falta el número",
                "Ingresa un número de WhatsApp para enviar la factura.",
                "warning"
            );

            return;
        }


        // ==================================================
        // CREAR OBJETO DE VENTA
        //
        // NO CAMBIAR ESTOS NOMBRES:
        //
        // clienteId
        // metodoPago
        // efectivoRecibido
        // telefonoEnvio
        // enviarWhatsapp
        // aplicarIva
        // porcentajeIva
        // detalles
        // ==================================================

        const venta = {

            clienteId:
                clienteElement.value ||
                null,


            metodoPago:
            metodoPago,


            efectivoRecibido:
            efectivoRecibido,


            telefonoEnvio:
            telefonoEnvio,


            enviarWhatsapp:
            enviarWhatsapp,


            aplicarIva:
            aplicarIva,


            porcentajeIva:
                19,


            detalles:

                carrito.map(
                    item => ({

                        productoId:
                        item.id,

                        cantidad:
                        item.cantidad,

                        precio:
                        item.precio

                    })
                )

        };


        console.log(
            "VENTA A ENVIAR:",
            venta
        );


        // ==================================================
        // BOTÓN
        // ==================================================

        const boton =
            document.getElementById(
                "btnFinalizarVenta"
            );


        if (boton) {

            boton.disabled =
                true;


            boton.innerHTML = `

                <i class="fa-solid fa-spinner fa-spin"></i>

                Procesando venta...

            `;

        }


        // ==================================================
        // ENVIAR VENTA AL BACKEND
        // ==================================================

        const response =
            await fetch(
                "/api/ventas",
                {

                    method:
                        "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body:
                        JSON.stringify(
                            venta
                        )

                }
            );


        // ==================================================
        // ERROR
        // ==================================================

        if (!response.ok) {

            const error =
                await response.text();


            console.error(
                "ERROR BACKEND:",
                error
            );


            await mostrarMensaje(
                "No se pudo realizar la venta",
                error ||
                "El servidor rechazó la operación.",
                "error"
            );


            return;
        }


        // ==================================================
        // RESPUESTA
        // ==================================================

        const factura =
            await response.json();


        console.log(
            "FACTURA GENERADA:",
            factura
        );


        // ==================================================
        // WHATSAPP
        // ==================================================

        if (
            venta.enviarWhatsapp &&
            venta.telefonoEnvio !== ""
        ) {

            const mensaje =
                encodeURIComponent(

                    `🧾 *MARAV*\n\n` +

                    `Factura: ${
                        factura.numeroFactura
                    }\n` +

                    `Fecha: ${
                        new Date()
                            .toLocaleDateString(
                                "es-CO"
                            )
                    }\n\n` +

                    `🛒 Productos:\n` +

                    carrito
                        .map(
                            item =>

                                `• ${
                                    item.nombre
                                } x${
                                    item.cantidad
                                } = ${
                                    dinero(
                                        item.precio *
                                        item.cantidad
                                    )
                                }`
                        )
                        .join("\n") +

                    `\n\n💰 Subtotal: ${
                        dinero(
                            factura.subtotal
                        )
                    }\n` +

                    `IVA: ${
                        dinero(
                            factura.iva
                        )
                    }\n` +

                    `TOTAL: ${
                        dinero(
                            factura.total
                        )
                    }\n\n` +

                    `Solicite su factura PDF en el local.\n` +

                    `Gracias por su compra 🙏`

                );


            window.open(

                `https://wa.me/57${
                    venta.telefonoEnvio
                }?text=${mensaje}`,

                "_blank"

            );

        }


        // ==================================================
        // CONFIRMACIÓN
        // ==================================================

        await mostrarMensaje(
            "Venta realizada",
            `La venta fue registrada correctamente. Factura: ${
                factura.numeroFactura || "generada"
            }`,
            "success"
        );


        // ==================================================
        // LIMPIAR CARRITO
        // ==================================================

        carrito = [];


        renderCarrito();


        // ==================================================
        // LIMPIAR CLIENTE
        // ==================================================

        document.getElementById(
            "cliente"
        ).value = "";


        // ==================================================
        // RESTABLECER MÉTODO DE PAGO
        // ==================================================

        document.getElementById(
            "metodoPago"
        ).value =
            "EFECTIVO";


        // ==================================================
        // LIMPIAR EFECTIVO
        // ==================================================

        document.getElementById(
            "efectivo"
        ).value = "";


        // ==================================================
        // LIMPIAR CAMBIO
        // ==================================================

        document.getElementById(
            "cambio"
        ).value = "$0";


        // ==================================================
        // DESACTIVAR WHATSAPP
        // ==================================================

        document.getElementById(
            "whatsapp"
        ).checked =
            false;


        // ==================================================
        // LIMPIAR TELEFONO
        // ==================================================

        document.getElementById(
            "telefonoWhatsapp"
        ).value = "";


        // ==================================================
        // DESACTIVAR IVA
        // ==================================================

        document.getElementById(
            "aplicarIVA"
        ).checked =
            false;


        // ==================================================
        // RESTABLECER FORMA DE PAGO
        // ==================================================

        cambiarMetodoPago();


        // ==================================================
        // RECARGAR PRODUCTOS
        // ==================================================

        await cargarProductos();


    }
    catch (error) {

        console.error(
            "ERROR REALIZANDO VENTA:",
            error
        );


        await mostrarMensaje(
            "Error",
            "Ocurrió un error al realizar la venta.",
            "error"
        );

    }
    finally {

        // ==================================================
        // RESTAURAR BOTÓN
        // ==================================================

        const boton =
            document.getElementById(
                "btnFinalizarVenta"
            );


        if (boton) {

            boton.disabled =
                false;


            boton.innerHTML = `

                <i class="fa-solid fa-circle-check"></i>

                Finalizar Venta

            `;

        }

    }

}


// ======================================================
// BUSCADOR GLOBAL
//
// Utiliza el buscador del TOPBAR:
// #buscarTopbar
// ======================================================

function configurarBuscador() {

    const buscador =
        document.getElementById(
            "buscarTopbar"
        );


    if (!buscador) {

        return;
    }


    buscador.addEventListener(
        "input",
        function () {

            const texto =
                this.value
                    .toLowerCase()
                    .trim();


            // ==============================================
            // SIN TEXTO
            // ==============================================

            if (texto === "") {

                renderProductos(
                    productos
                );

                return;
            }


            // ==============================================
            // BUSCAR POR NOMBRE O PRECIO
            // ==============================================

            const filtrados =
                productos.filter(
                    producto => {

                        const nombre =
                            String(
                                producto.nombre ||
                                ""
                            ).toLowerCase();


                        const precio =
                            String(
                                producto.precio ||
                                ""
                            ).toLowerCase();


                        return (

                            nombre.includes(
                                texto
                            )

                            ||

                            precio.includes(
                                texto
                            )

                        );

                    }
                );


            // ==============================================
            // MOSTRAR RESULTADOS
            // ==============================================

            renderProductos(
                filtrados
            );

        }
    );

}


// ======================================================
// INICIALIZAR POS
// ======================================================

async function iniciarPOS() {

    console.log(
        "======================================"
    );


    console.log(
        "🛒 INICIANDO POS ECOMARKET PRO"
    );


    console.log(
        "======================================"
    );


    // ==================================================
    // CONFIGURAR EVENTOS
    // ==================================================

    configurarCliente();

    configurarEfectivo();

    configurarBuscador();


    // ==================================================
    // CARGAR PRODUCTOS
    // ==================================================

    await cargarProductos();


    // ==================================================
    // CARGAR CLIENTES
    // ==================================================

    await cargarClientes();


    // ==================================================
    // MOSTRAR CARRITO INICIAL
    // ==================================================

    renderCarrito();


    // ==================================================
    // CONFIGURAR MÉTODO DE PAGO
    // ==================================================

    cambiarMetodoPago();


    console.log(
        "✅ POS LISTO"
    );

}


// ======================================================
// INICIO
//
// Esperamos a que todo el HTML esté disponible antes
// de buscar elementos como #cliente, #items, etc.
// ======================================================

document.addEventListener(
    "DOMContentLoaded",
    iniciarPOS
);