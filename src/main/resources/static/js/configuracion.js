
/* =========================================================
   1. DATOS DEL MANUAL
   =========================================================

   Cada módulo contiene:

   - id
   - titulo
   - icono
   - descripcion
   - video
   - rol
   - pasos

   Si video = ""
   significa que todavía no tiene tutorial.
========================================================= */

const MANUAL_MODULOS = [

    /* =====================================================
       1. LOGIN
    ====================================================== */

    {
        id: "login",

        titulo: "Inicio de sesión",

        icono: "fa-solid fa-right-to-bracket",

        descripcion:
            "Aprende cómo ingresar correctamente a la app utilizando el correo y contraseña de tu cuenta.",

        video: "https://youtu.be/VRmEtDQdPOY?si=J2BRyaFLznAUsuPv",

        rol:
            "El inicio de sesión está disponible para todos los usuarios registrados y activos.",

        pasos: [

            {
                titulo: "Abrir la app",

                descripcion:
                    "Ingresa a la página de inicio de sesión de la app."
            },

            {
                titulo: "Ingresar el correo",

                descripcion:
                    "Escribe el correo electrónico asociado a tu cuenta."
            },

            {
                titulo: "Ingresar la contraseña",

                descripcion:
                    "Escribe la contraseña correspondiente a tu usuario."
            },

            {
                titulo: "Iniciar sesión",

                descripcion:
                    "Presiona el botón de ingreso para acceder al sistema."
            },

            {
                titulo: "Verificar el Dashboard",

                descripcion:
                    "Si los datos son correctos, EcoMarket PRO te llevará al Dashboard."
            }

        ]

    },


    /* =====================================================
       2. PRIMEROS PASOS
    ====================================================== */

    {
        id: "primeros-pasos",

        titulo: "Primeros pasos",

        icono: "fa-solid fa-rocket",

        descripcion:
            "Conoce el proceso inicial recomendado para comenzar a utilizar la app correctamente.",

        video: "https://youtu.be/y6fDvSlL4Og",

        rol:
            "Este proceso está orientado principalmente al administrador que crea y configura el negocio.",

        pasos: [

            {
                titulo: "Crear la cuenta del negocio",

                descripcion:
                    "Registra el nombre del negocio y los datos del administrador."
            },

            {
                titulo: "Iniciar sesión",

                descripcion:
                    "Ingresa con el correo y contraseña creados durante el registro."
            },

            {
                titulo: "Ingresar al Dashboard",

                descripcion:
                    "Conoce los principales indicadores y accesos del sistema."
            },

            {
                titulo: "Configurar la información",

                descripcion:
                    "Antes de vender, registra proveedores, productos, inventario y clientes."
            }

        ]

    },


    /* =====================================================
       3. DASHBOARD
    ====================================================== */

    {
        id: "dashboard",

        titulo: "Dashboard",

        icono: "fa-solid fa-chart-line",

        descripcion:
            "Consulta los principales indicadores y la información general del negocio.",

        video: "https://youtu.be/KEr-8ax2BNk",

        rol:
            "Los indicadores corresponden exclusivamente a la información del negocio del usuario autenticado.",

        pasos: [

            {
                titulo: "Ingresar al Dashboard",

                descripcion:
                    "Después de iniciar sesión, accede al panel principal."
            },

            {
                titulo: "Revisar los indicadores",

                descripcion:
                    "Consulta las estadísticas disponibles sobre ventas y operación."
            },

            {
                titulo: "Consultar información reciente",

                descripcion:
                    "Revisa las operaciones recientes mostradas en el panel."
            },

            {
                titulo: "Utilizar el Dashboard",

                descripcion:
                    "Utiliza los indicadores como apoyo para consultar el estado general del negocio."
            }

        ]

    },


    /* =====================================================
       4. PROVEEDORES
    ====================================================== */

    {
        id: "proveedores",

        titulo: "Proveedores",

        icono: "fa-solid fa-truck-field",

        descripcion:
            "Permite registrar y consultar los proveedores que suministran productos al negocio.",

        video: "https://youtu.be/GV6THxTiKxo?si=NPu1vqjjXXUhv5xS",

        rol:
            "El administrador puede gestionar proveedores. El empleado puede consultar según sus permisos.",

        pasos: [

            {
                titulo: "Ingresar a Proveedores",

                descripcion:
                    "Selecciona Proveedores desde el menú lateral."
            },

            {
                titulo: "Crear un proveedor",

                descripcion:
                    "Registra los datos principales del proveedor."
            },

            {
                titulo: "Verificar los datos",

                descripcion:
                    "Comprueba que la información ingresada sea correcta."
            },

            {
                titulo: "Guardar el proveedor",

                descripcion:
                    "Presiona Guardar para registrar la información."
            },

            {
                titulo: "Buscar proveedores",

                descripcion:
                    "Utiliza el buscador para encontrar rápidamente un proveedor."
            }

        ]

    },


    /* =====================================================
       5. PRODUCTOS
    ====================================================== */

    {
        id: "productos",

        titulo: "Productos",

        icono: "fa-solid fa-box-open",

        descripcion:
            "Permite registrar los productos que serán vendidos y controlados por EcoMarket PRO.",

        video: "https://youtu.be/yvfUtHcBvZI?si=ErBhVDZCLs5LYue4",

        rol:
            "El administrador puede gestionar productos. El empleado puede consultar la información disponible.",

        pasos: [

            {
                titulo: "Abrir Productos",

                descripcion:
                    "Selecciona Productos desde el menú lateral."
            },

            {
                titulo: "Crear un producto",

                descripcion:
                    "Ingresa el nombre, categoría, precio y demás información solicitada."
            },

            {
                titulo: "Revisar la información",

                descripcion:
                    "Verifica que el nombre y precio sean correctos."
            },

            {
                titulo: "Guardar el producto",

                descripcion:
                    "Presiona Guardar para registrar el producto."
            },

            {
                titulo: "Buscar productos",

                descripcion:
                    "Utiliza el buscador para encontrar productos registrados."
            }

        ]

    },


    /* =====================================================
       6. INVENTARIO
    ====================================================== */

    {
        id: "inventario",

        titulo: "Inventario",

        icono: "fa-solid fa-warehouse",

        descripcion:
            "Permite controlar las existencias, stock mínimo, ubicación y vencimiento de los productos.",

        video: "https://youtu.be/b2NtBmq6h4c?si=A5ioeBQlnC6YxV1b",

        rol:
            "El administrador gestiona la información del inventario. El empleado puede consultar según sus permisos.",

        pasos: [

            {
                titulo: "Ingresar a Inventario",

                descripcion:
                    "Selecciona Inventario desde el menú lateral."
            },

            {
                titulo: "Seleccionar el producto",

                descripcion:
                    "Busca el producto que deseas controlar."
            },

            {
                titulo: "Registrar existencias",

                descripcion:
                    "Indica la cantidad disponible y el stock mínimo."
            },

            {
                titulo: "Registrar ubicación",

                descripcion:
                    "Si corresponde, registra la ubicación física del producto."
            },

            {
                titulo: "Registrar lote y vencimiento",

                descripcion:
                    "Para productos que lo requieran, registra lote y fecha de vencimiento."
            }

        ]

    },


    /* =====================================================
       7. CLIENTES
    ====================================================== */

    {
        id: "clientes",

        titulo: "Clientes",

        icono: "fa-solid fa-users",

        descripcion:
            "Permite registrar y consultar los clientes del negocio.",

        video: "https://youtu.be/PigBNBksDYk?si=Tnc3_TX5QVU96Akt",

        rol:
            "El administrador puede crear y modificar clientes. El empleado dispone principalmente de funciones de consulta.",

        pasos: [

            {
                titulo: "Abrir Clientes",

                descripcion:
                    "Selecciona Clientes desde el menú lateral."
            },

            {
                titulo: "Crear cliente",

                descripcion:
                    "Presiona Nuevo Cliente para abrir el formulario."
            },

            {
                titulo: "Registrar los datos",

                descripcion:
                    "Ingresa nombre, identificación, teléfono y dirección."
            },

            {
                titulo: "Guardar",

                descripcion:
                    "Verifica la información y guarda el cliente."
            },

            {
                titulo: "Buscar",

                descripcion:
                    "Utiliza el buscador para localizar un cliente."
            }

        ]

    },


    /* =====================================================
       8. USUARIOS
    ====================================================== */

    {
        id: "usuarios",

        titulo: "Usuarios",

        icono: "fa-solid fa-user-gear",

        descripcion:
            "Permite crear y administrar los usuarios empleados que tendrán acceso al negocio.",

        video: "https://youtu.be/4Zv3lSoV0DM?si=1AciLD5Ml4e4v2FY",

        rol:
            "Este módulo está orientado principalmente al administrador.",

        pasos: [

            {
                titulo: "Ingresar a Usuarios",

                descripcion:
                    "Desde el menú lateral abre el módulo Usuarios."
            },

            {
                titulo: "Crear un empleado",

                descripcion:
                    "Presiona Nuevo Usuario para abrir el formulario."
            },

            {
                titulo: "Ingresar los datos",

                descripcion:
                    "Registra nombre, identificación, correo y contraseña."
            },

            {
                titulo: "Seleccionar el rol",

                descripcion:
                    "Selecciona EMPLEADO cuando el usuario tendrá funciones operativas."
            },

            {
                titulo: "Guardar usuario",

                descripcion:
                    "Comprueba los datos y guarda el nuevo usuario."
            }

        ]

    },


    /* =====================================================
       9. VENTAS
    ====================================================== */

    {
        id: "ventas",

        titulo: "Ventas",

        icono: "fa-solid fa-cart-shopping",

        descripcion:
            "Permite realizar ventas, seleccionar clientes, agregar productos y registrar formas de pago.",

        video: "https://youtu.be/Txac6Jc8Tus?si=cTSwRTF86tdjhYwe",

        rol:
            "Administrador y empleado pueden realizar las operaciones de venta permitidas.",

        pasos: [

            {
                titulo: "Abrir Ventas",

                descripcion:
                    "Selecciona Ventas desde el menú lateral."
            },

            {
                titulo: "Seleccionar cliente",

                descripcion:
                    "Selecciona el cliente correspondiente o Consumidor Final."
            },

            {
                titulo: "Agregar productos",

                descripcion:
                    "Busca los productos y agrega las cantidades."
            },

            {
                titulo: "Revisar el total",

                descripcion:
                    "Comprueba subtotal, IVA cuando aplique y total."
            },

            {
                titulo: "Seleccionar forma de pago",

                descripcion:
                    "Selecciona efectivo, Nequi, Daviplata, transferencia o deuda según corresponda."
            },

            {
                titulo: "Finalizar venta",

                descripcion:
                    "Verifica la información y confirma la venta."
            }

        ]

    },


    /* =====================================================
       10. FACTURAS
    ====================================================== */

    {
        id: "facturas",

        titulo: "Ver factura",

        icono: "fa-solid fa-file-invoice",

        descripcion:
            "Permite consultar las facturas generadas a partir de las ventas realizadas.",

        video: "https://youtu.be/QewcGTTZhYY",

        rol:
            "La consulta de facturas depende de los permisos asignados al usuario.",

        pasos: [

            {
                titulo: "Ingresar a Facturas",

                descripcion:
                    "Abre el módulo Facturas desde el menú."
            },

            {
                titulo: "Buscar la factura",

                descripcion:
                    "Localiza la factura que deseas consultar."
            },

            {
                titulo: "Revisar la información",

                descripcion:
                    "Comprueba número de factura, fecha, cliente y total."
            },

            {
                titulo: "Abrir el detalle",

                descripcion:
                    "Selecciona la opción correspondiente para consultar los productos y valores."
            },

            {
                titulo: "Consultar el PDF",

                descripcion:
                    "Utiliza la opción disponible para visualizar o generar la factura en PDF."
            }

        ]

    },


    /* =====================================================
       11. DEVOLUCIONES
    ====================================================== */

    {
        id: "devoluciones",

        titulo: "Devoluciones",

        icono: "fa-solid fa-arrow-rotate-left",

        descripcion:
            "Permite registrar devoluciones relacionadas con las ventas realizadas.",

        video: "https://youtu.be/vxVQAop2P_M?si=0nUHm7FCVBRzy0jb",

        rol:
            "El administrador puede gestionar devoluciones. El empleado puede consultar según sus permisos.",

        pasos: [

            {
                titulo: "Ingresar a Devoluciones",

                descripcion:
                    "Selecciona el módulo Devoluciones."
            },

            {
                titulo: "Seleccionar la venta",

                descripcion:
                    "Busca la venta o factura relacionada con la devolución."
            },

            {
                titulo: "Seleccionar el producto",

                descripcion:
                    "Selecciona el producto que será devuelto."
            },

            {
                titulo: "Indicar cantidad",

                descripcion:
                    "Registra la cantidad que será devuelta."
            },

            {
                titulo: "Indicar el motivo",

                descripcion:
                    "Escribe el motivo correspondiente."
            },

            {
                titulo: "Guardar devolución",

                descripcion:
                    "Comprueba la información y confirma la operación."
            }

        ]

    },


    /* =====================================================
       12. CAJA
    ====================================================== */

    {
        id: "caja",

        titulo: "Abrir caja",

        icono: "fa-solid fa-cash-register",

        descripcion:
            "Permite consultar y controlar los movimientos relacionados con la caja del negocio.",

        video: "https://youtu.be/uozCX0W_Y8k?si=0Wp5s9CFVb5D6iHc",

        rol:
            "Las funciones disponibles dependen del rol y de los permisos configurados.",

        pasos: [

            {
                titulo: "Ingresar a Caja",

                descripcion:
                    "Selecciona Caja desde el menú lateral."
            },

            {
                titulo: "Abrir la caja",

                descripcion:
                    "Registra la información solicitada para iniciar la jornada de caja."
            },

            {
                titulo: "Revisar movimientos",

                descripcion:
                    "Consulta los movimientos registrados."
            },

            {
                titulo: "Verificar el saldo",

                descripcion:
                    "Comprueba el saldo de acuerdo con los movimientos realizados."
            }

        ]

    },


    /* =====================================================
       13. ABONOS
    ====================================================== */

    {
        id: "abonos",

        titulo: "Crear abono",

        icono: "fa-solid fa-money-bill-transfer",

        descripcion:
            "Permite registrar pagos parciales relacionados con ventas realizadas a crédito.",

        video: "https://youtu.be/xjoQm0j_p_w?si=l3T5QhoVsBVUAVLe",

        rol:
            "Los permisos dependen del rol asignado al usuario.",

        pasos: [

            {
                titulo: "Ingresar a Abonos",

                descripcion:
                    "Abre el módulo Abonos."
            },

            {
                titulo: "Buscar la venta",

                descripcion:
                    "Localiza la venta que tiene un saldo pendiente."
            },

            {
                titulo: "Registrar el valor",

                descripcion:
                    "Indica cuánto dinero está abonando el cliente."
            },

            {
                titulo: "Revisar el saldo",

                descripcion:
                    "Comprueba el nuevo saldo pendiente."
            },

            {
                titulo: "Guardar el abono",

                descripcion:
                    "Confirma el abono para registrar el movimiento."
            }

        ]

    },


    /* =====================================================
       14. SIMULADOR SOLAR
    ====================================================== */

    {
        id: "simulador-solar",

        titulo: "Simulador Solar",

        icono: "fa-solid fa-solar-panel",

        descripcion:
            "Permite realizar una estimación relacionada con generación de energía solar.",

        video: "https://youtu.be/jwXaGb5_WB8",

        rol:
            "Módulo de consulta y simulación.",

        pasos: [

            {
                titulo: "Abrir el Simulador Solar",

                descripcion:
                    "Selecciona el módulo desde el menú."
            },

            {
                titulo: "Seleccionar ubicación",

                descripcion:
                    "Selecciona la ciudad o ubicación disponible."
            },

            {
                titulo: "Ingresar los datos",

                descripcion:
                    "Completa los valores solicitados."
            },

            {
                titulo: "Ejecutar simulación",

                descripcion:
                    "Presiona el botón para realizar el cálculo."
            },

            {
                titulo: "Consultar resultado",

                descripcion:
                    "Analiza la información generada."
            }

        ]

    }

];


/* =========================================================
   2. CONFIGURACIÓN DEL STORAGE
   =========================================================

   El progreso se guarda en el navegador.

   Ejemplo:

   localStorage
       ↓
   ecomarket_manual_progreso
       ↓
   {
       "clientes": [true, false, true],
       "productos": [true, true, false]
   }

   Esto permite que al cerrar el navegador el progreso
   permanezca guardado.
========================================================= */

const STORAGE_KEY = "ecomarket_manual_progreso";


/* =========================================================
   3. VARIABLES GLOBALES
========================================================= */

let progresoManual = {};

let moduloActual = null;


/* =========================================================
   4. INICIAR MANUAL
========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    console.log(" Manual iniciado.");

    cargarProgreso();

    prepararElementos();

    renderizarRuta();

    renderizarModulos(MANUAL_MODULOS);

    actualizarProgresoGeneral();

    configurarBusqueda();

    configurarBotonReiniciar();

    configurarModalVideo();

});


/* =========================================================
   5. OBTENER ELEMENTOS DEL HTML
========================================================= */

function prepararElementos() {

    window.manualElementos = {

        grid:
            document.getElementById("manualGrid"),

        detalle:
            document.getElementById("manualDetalle"),

        buscador:
            document.getElementById("buscarManual"),

        btnReiniciar:
            document.getElementById("btnReiniciarProgreso"),

        progresoTexto:
            document.getElementById("progresoTexto"),

        progresoPorcentaje:
            document.getElementById("progresoPorcentaje"),

        barraProgreso:
            document.getElementById("barraProgreso"),

        pasosCompletados:
            document.getElementById("pasosCompletados"),

        totalPasos:
            document.getElementById("totalPasos"),

        ruta:
            document.querySelector(".ruta-list")

    };

}


/* =========================================================
   6. CARGAR PROGRESO
========================================================= */

function cargarProgreso() {

    try {

        const datosGuardados =
            localStorage.getItem(STORAGE_KEY);

        if (datosGuardados) {

            progresoManual =
                JSON.parse(datosGuardados);

        } else {

            progresoManual = {};

        }

    } catch (error) {

        console.error(
            "No fue posible cargar el progreso:",
            error
        );

        progresoManual = {};

    }

}


/* =========================================================
   7. GUARDAR PROGRESO
========================================================= */

function guardarProgreso() {

    try {

        localStorage.setItem(
            STORAGE_KEY,
            JSON.stringify(progresoManual)
        );

    } catch (error) {

        console.error(
            "No fue posible guardar el progreso:",
            error
        );

    }

}


/* =========================================================
   8. OBTENER ESTADO DE UN PASO
========================================================= */

function pasoCompletado(moduloId, indicePaso) {

    if (!progresoManual[moduloId]) {

        return false;

    }

    return progresoManual[moduloId][indicePaso] === true;

}


/* =========================================================
   9. CAMBIAR ESTADO DE UN PASO
========================================================= */

function cambiarPaso(moduloId, indicePaso, completado) {

    if (!progresoManual[moduloId]) {

        progresoManual[moduloId] = [];

    }

    progresoManual[moduloId][indicePaso] =
        completado;

    guardarProgreso();

    actualizarProgresoGeneral();

    actualizarProgresoModulo(moduloId);

}


/* =========================================================
   10. CALCULAR PROGRESO
========================================================= */

function obtenerEstadisticasProgreso() {

    let total = 0;

    let completados = 0;


    MANUAL_MODULOS.forEach(function (modulo) {

        modulo.pasos.forEach(function (_, indice) {

            total++;

            if (
                pasoCompletado(
                    modulo.id,
                    indice
                )
            ) {

                completados++;

            }

        });

    });


    let porcentaje = 0;

    if (total > 0) {

        porcentaje =
            Math.round(
                (completados / total) * 100
            );

    }


    return {

        total: total,

        completados: completados,

        porcentaje: porcentaje

    };

}


/* =========================================================
   11. ACTUALIZAR PROGRESO GENERAL
========================================================= */

function actualizarProgresoGeneral() {

    const estadisticas =
        obtenerEstadisticasProgreso();


    const elementos =
        window.manualElementos || {};


    if (elementos.progresoTexto) {

        elementos.progresoTexto.textContent =
            estadisticas.porcentaje + "%";

    }


    if (elementos.progresoPorcentaje) {

        elementos.progresoPorcentaje.textContent =
            estadisticas.porcentaje + "%";

    }


    if (elementos.pasosCompletados) {

        elementos.pasosCompletados.textContent =
            estadisticas.completados;

    }


    if (elementos.totalPasos) {

        elementos.totalPasos.textContent =
            estadisticas.total;

    }


    if (elementos.barraProgreso) {

        elementos.barraProgreso.style.width =
            estadisticas.porcentaje + "%";

        elementos.barraProgreso.setAttribute(
            "aria-valuenow",
            estadisticas.porcentaje
        );

    }

}


/* =========================================================
   12. RUTA RECOMENDADA
========================================================= */

function renderizarRuta() {

    const ruta =
        document.querySelector(".ruta-list");

    if (!ruta) {

        console.warn(
            "No se encontró .ruta-list en configuracion.html"
        );

        return;

    }


    /*
     * Limpiamos el contenido anterior.
     */

    ruta.innerHTML = "";


    /*
     * La ruta utiliza los primeros 12 módulos.
     *
     * El Simulador Solar queda fuera de la ruta
     * principal porque es un módulo complementario.
     */

    const rutaModulos =
        MANUAL_MODULOS.filter(function (modulo) {

            return modulo.id !== "simulador-solar";

        });


    rutaModulos.forEach(function (modulo, indice) {

        const item =
            document.createElement("button");

        item.type = "button";

        item.className = "ruta-item";

        item.setAttribute(
            "data-modulo",
            modulo.id
        );


        item.innerHTML = `

            <span class="ruta-numero">
                ${indice + 1}
            </span>

            <span class="ruta-nombre">
                ${modulo.titulo}
            </span>

            <span class="ruta-flecha">
                <i class="fa-solid fa-chevron-right"></i>
            </span>

        `;


        item.addEventListener(
            "click",
            function () {

                abrirModulo(modulo.id);

            }
        );


        ruta.appendChild(item);

    });

}


/* =========================================================
   13. RENDERIZAR TARJETAS DE MÓDULOS
========================================================= */

function renderizarModulos(modulos) {

    const grid =
        window.manualElementos?.grid ||
        document.getElementById("manualGrid");


    if (!grid) {

        console.error(
            "No se encontró #manualGrid."
        );

        return;

    }


    grid.innerHTML = "";


    if (!modulos || modulos.length === 0) {

        grid.innerHTML = `

            <div class="manual-empty">

                <div class="manual-empty-icon">

                    <i class="fa-solid fa-magnifying-glass"></i>

                </div>

                <h3>No se encontraron módulos</h3>

                <p>
                    Intenta realizar otra búsqueda.
                </p>

            </div>

        `;

        return;

    }


    modulos.forEach(function (modulo) {

        const tarjeta =
            document.createElement("article");


        tarjeta.className =
            "manual-card";


        tarjeta.setAttribute(
            "data-modulo",
            modulo.id
        );


        const estadisticas =
            obtenerEstadisticasModulo(modulo);


        tarjeta.innerHTML = `

            <div class="manual-card-icon">

                <i class="${modulo.icono}"></i>

            </div>


            <div class="manual-card-content">

                <div class="manual-card-top">

                    <span class="manual-card-number">
                        ${obtenerNumeroModulo(modulo.id)}
                    </span>

                    ${
            modulo.video
                ? `
                            <span class="manual-video-badge">
                                <i class="fa-solid fa-circle-play"></i>
                                Tutorial
                            </span>
                          `
                : `
                            <span class="manual-no-video">
                                <i class="fa-solid fa-book"></i>
                                Guía
                            </span>
                          `
        }

                </div>


                <h3>
                    ${modulo.titulo}
                </h3>


                <p>
                    ${modulo.descripcion}
                </p>


                <div class="manual-card-progress">

                    <div class="manual-progress-info">

                        <span>
                            Progreso
                        </span>

                        <strong>
                            ${estadisticas.porcentaje}%
                        </strong>

                    </div>


                    <div class="manual-mini-progress">

                        <span
                            style="width:${estadisticas.porcentaje}%"
                        ></span>

                    </div>

                </div>


                <button
                    type="button"
                    class="manual-card-button"
                    data-abrir-modulo="${modulo.id}"
                >

                    Ver guía

                    <i class="fa-solid fa-arrow-right"></i>

                </button>

            </div>

        `;


        /*
         * Toda la tarjeta puede abrir el módulo.
         */

        tarjeta.addEventListener(
            "click",
            function (evento) {

                if (
                    evento.target.closest(
                        "[data-abrir-modulo]"
                    )
                ) {

                    abrirModulo(modulo.id);

                    return;

                }


                abrirModulo(modulo.id);

            }
        );


        grid.appendChild(tarjeta);

    });

}


/* =========================================================
   14. OBTENER NÚMERO DEL MÓDULO
========================================================= */

function obtenerNumeroModulo(id) {

    const indice =
        MANUAL_MODULOS.findIndex(function (modulo) {

            return modulo.id === id;

        });


    if (indice === -1) {

        return "";

    }


    return String(indice + 1).padStart(2, "0");

}


/* =========================================================
   15. ESTADÍSTICAS DE UN MÓDULO
========================================================= */

function obtenerEstadisticasModulo(modulo) {

    const total =
        modulo.pasos.length;

    let completados = 0;


    modulo.pasos.forEach(function (_, indice) {

        if (
            pasoCompletado(
                modulo.id,
                indice
            )
        ) {

            completados++;

        }

    });


    const porcentaje =
        total > 0
            ? Math.round(
                (completados / total) * 100
            )
            : 0;


    return {

        total: total,

        completados: completados,

        porcentaje: porcentaje

    };

}


/* =========================================================
   16. ACTUALIZAR TARJETA DE MÓDULO
========================================================= */

function actualizarProgresoModulo(moduloId) {

    const modulo =
        MANUAL_MODULOS.find(function (item) {

            return item.id === moduloId;

        });


    if (!modulo) {

        return;

    }


    const tarjeta =
        document.querySelector(
            `.manual-card[data-modulo="${moduloId}"]`
        );


    if (!tarjeta) {

        return;

    }


    const estadisticas =
        obtenerEstadisticasModulo(modulo);


    const porcentaje =
        tarjeta.querySelector(
            ".manual-progress-info strong"
        );


    const barra =
        tarjeta.querySelector(
            ".manual-mini-progress span"
        );


    if (porcentaje) {

        porcentaje.textContent =
            estadisticas.porcentaje + "%";

    }


    if (barra) {

        barra.style.width =
            estadisticas.porcentaje + "%";

    }

}


/* =========================================================
   17. ABRIR MÓDULO
========================================================= */

function abrirModulo(moduloId) {

    const modulo =
        MANUAL_MODULOS.find(function (item) {

            return item.id === moduloId;

        });


    if (!modulo) {

        console.error(
            "Módulo no encontrado:",
            moduloId
        );

        return;

    }


    moduloActual =
        modulo;


    const detalle =
        window.manualElementos?.detalle ||
        document.getElementById("manualDetalle");


    if (!detalle) {

        console.error(
            "No se encontró #manualDetalle."
        );

        return;

    }


    const estadisticas =
        obtenerEstadisticasModulo(modulo);


    detalle.innerHTML = `

        <div class="manual-detail-card">


            <!-- =========================================
                 ENCABEZADO DEL DETALLE
            ========================================== -->

            <div class="manual-detail-header">


                <div class="manual-detail-icon">

                    <i class="${modulo.icono}"></i>

                </div>


                <div class="manual-detail-title">

                    <span>
                        Guía del módulo
                    </span>

                    <h2>
                        ${modulo.titulo}
                    </h2>

                    <p>
                        ${modulo.descripcion}
                    </p>

                </div>


                <button
                    type="button"
                    class="manual-detail-close"
                    id="cerrarManualDetalle"
                    aria-label="Cerrar guía"
                >

                    <i class="fa-solid fa-xmark"></i>

                </button>

            </div>


            <!-- =========================================
                 INFORMACIÓN DEL ROL
            ========================================== -->

            <div class="manual-role-info">

                <div class="manual-role-info-icon">

                    <i class="fa-solid fa-user-shield"></i>

                </div>


                <div>

                    <strong>
                        Permisos y orientación
                    </strong>

                    <p>
                        ${modulo.rol}
                    </p>

                </div>

            </div>


            <!-- =========================================
                 PROGRESO DEL MÓDULO
            ========================================== -->

            <div class="manual-detail-progress">


                <div class="manual-detail-progress-top">

                    <div>

                        <strong>
                            Tu progreso
                        </strong>

                        <span id="detalleProgresoTexto">
                            ${estadisticas.completados}
                            de
                            ${estadisticas.total}
                            pasos completados
                        </span>

                    </div>


                    <strong
                        id="detalleProgresoPorcentaje"
                    >
                        ${estadisticas.porcentaje}%
                    </strong>

                </div>


                <div class="manual-detail-progress-bar">

                    <span
                        id="detalleBarraProgreso"
                        style="width:${estadisticas.porcentaje}%"
                    ></span>

                </div>

            </div>


            <!-- =========================================
                 PASOS
            ========================================== -->

            <div class="manual-detail-steps">


                <div class="manual-detail-steps-title">

                    <div>

                        <h3>
                            Paso a paso
                        </h3>

                        <p>
                            Marca cada paso cuando lo hayas completado.
                        </p>

                    </div>

                </div>


                <div
                    class="manual-steps-list"
                    id="manualStepsList"
                >

                    ${generarPasosHTML(modulo)}

                </div>

            </div>


            <!-- =========================================
                 VIDEO
            ========================================== -->

            ${
        modulo.video
            ? `

                    <div class="manual-video-section">

                        <div class="manual-video-info">

                            <div class="manual-video-icon">

                                <i class="fa-brands fa-youtube"></i>

                            </div>

                            <div>

                                <h3>
                                    Tutorial en video
                                </h3>

                                <p>
                                    Puedes complementar esta guía
                                    con el tutorial disponible.
                                </p>

                            </div>

                        </div>


                        <button
                            type="button"
                            class="manual-video-button"
                            id="btnVerVideo"
                        >

                            <i class="fa-solid fa-play"></i>

                            Ver tutorial

                        </button>

                    </div>

                  `
            : `

                    <div class="manual-no-video-section">

                        <i class="fa-solid fa-circle-info"></i>

                        <div>

                            <strong>
                                Tutorial próximamente
                            </strong>

                            <p>
                                Este módulo actualmente cuenta
                                con la guía paso a paso.
                            </p>

                        </div>

                    </div>

                  `
    }


        </div>

    `;


    /*
     * Mostrar detalle.
     */

    detalle.classList.add(
        "manual-detalle-visible"
    );


    /*
     * Botón cerrar.
     */

    const cerrar =
        document.getElementById(
            "cerrarManualDetalle"
        );


    if (cerrar) {

        cerrar.addEventListener(
            "click",
            cerrarDetalle
        );

    }


    /*
     * Botón video.
     */

    const btnVideo =
        document.getElementById(
            "btnVerVideo"
        );


    if (btnVideo) {

        btnVideo.addEventListener(
            "click",
            function () {

                abrirVideo(modulo.video);

            }
        );

    }


    /*
     * Eventos de los checkboxes.
     */

    configurarCheckboxes();


    /*
     * Llevar al usuario hasta el detalle.
     */

    setTimeout(function () {

        detalle.scrollIntoView({

            behavior: "smooth",

            block: "start"

        });

    }, 100);

}


/* =========================================================
   18. GENERAR PASOS HTML
========================================================= */

function generarPasosHTML(modulo) {

    return modulo.pasos.map(function (paso, indice) {

        const completado =
            pasoCompletado(
                modulo.id,
                indice
            );


        return `

            <label
                class="manual-step ${
            completado
                ? "completed"
                : ""
        }"
                data-step-index="${indice}"
            >


                <div class="manual-step-check">

                    <input
                        type="checkbox"
                        class="manual-step-checkbox"
                        data-modulo-id="${modulo.id}"
                        data-paso-index="${indice}"
                        ${completado ? "checked" : ""}
                    >

                    <span class="manual-checkmark">

                        <i class="fa-solid fa-check"></i>

                    </span>

                </div>


                <div class="manual-step-number">

                    ${indice + 1}

                </div>


                <div class="manual-step-content">

                    <h4>
                        ${paso.titulo}
                    </h4>

                    <p>
                        ${paso.descripcion}
                    </p>

                </div>


            </label>

        `;

    }).join("");

}


/* =========================================================
   19. CONFIGURAR CHECKBOXES
========================================================= */

function configurarCheckboxes() {

    const checkboxes =
        document.querySelectorAll(
            ".manual-step-checkbox"
        );


    checkboxes.forEach(function (checkbox) {

        checkbox.addEventListener(
            "change",
            function () {

                const moduloId =
                    checkbox.dataset.moduloId;


                const indicePaso =
                    Number(
                        checkbox.dataset.pasoIndex
                    );


                cambiarPaso(
                    moduloId,
                    indicePaso,
                    checkbox.checked
                );


                const paso =
                    checkbox.closest(
                        ".manual-step"
                    );


                if (paso) {

                    paso.classList.toggle(
                        "completed",
                        checkbox.checked
                    );

                }


                actualizarDetalleProgreso(
                    moduloId
                );

            }
        );

    });

}


/* =========================================================
   20. ACTUALIZAR PROGRESO DEL DETALLE
========================================================= */

function actualizarDetalleProgreso(moduloId) {

    const modulo =
        MANUAL_MODULOS.find(function (item) {

            return item.id === moduloId;

        });


    if (!modulo) {

        return;

    }


    const estadisticas =
        obtenerEstadisticasModulo(modulo);


    const texto =
        document.getElementById(
            "detalleProgresoTexto"
        );


    const porcentaje =
        document.getElementById(
            "detalleProgresoPorcentaje"
        );


    const barra =
        document.getElementById(
            "detalleBarraProgreso"
        );


    if (texto) {

        texto.textContent =
            `${estadisticas.completados} de ${estadisticas.total} pasos completados`;

    }


    if (porcentaje) {

        porcentaje.textContent =
            estadisticas.porcentaje + "%";

    }


    if (barra) {

        barra.style.width =
            estadisticas.porcentaje + "%";

    }

}


/* =========================================================
   21. CERRAR DETALLE
========================================================= */

function cerrarDetalle() {

    const detalle =
        window.manualElementos?.detalle ||
        document.getElementById("manualDetalle");


    if (!detalle) {

        return;

    }


    detalle.classList.remove(
        "manual-detalle-visible"
    );


    /*
     * Dejamos un pequeño tiempo para limpiar el HTML.
     */

    setTimeout(function () {

        if (
            !detalle.classList.contains(
                "manual-detalle-visible"
            )
        ) {

            detalle.innerHTML = "";

        }

    }, 300);


    moduloActual = null;

}


/* =========================================================
   22. BUSCADOR
========================================================= */

function configurarBusqueda() {

    const buscador =
        window.manualElementos?.buscador ||
        document.getElementById("buscarManual");


    if (!buscador) {

        console.warn(
            "No se encontró #buscarManual."
        );

        return;

    }


    buscador.addEventListener(
        "input",
        function () {

            const texto =
                buscador.value
                    .trim()
                    .toLowerCase();


            if (!texto) {

                renderizarModulos(
                    MANUAL_MODULOS
                );

                return;

            }


            const resultados =
                MANUAL_MODULOS.filter(
                    function (modulo) {

                        const contenido =
                            (
                                modulo.titulo +
                                " " +
                                modulo.descripcion +
                                " " +
                                modulo.rol
                            )
                                .toLowerCase();


                        return contenido.includes(
                            texto
                        );

                    }
                );


            renderizarModulos(
                resultados
            );

        }
    );

}


/* =========================================================
   23. BOTÓN REINICIAR PROGRESO
========================================================= */

function configurarBotonReiniciar() {

    const boton =
        window.manualElementos?.btnReiniciar ||
        document.getElementById(
            "btnReiniciarProgreso"
        );


    if (!boton) {

        console.warn(
            "No se encontró #btnReiniciarProgreso."
        );

        return;

    }


    boton.addEventListener(
        "click",
        function () {

            confirmarReinicio();

        }
    );

}


/* =========================================================
   24. CONFIRMAR REINICIO
========================================================= */

function confirmarReinicio() {

    /*
     * Si SweetAlert2 está disponible,
     * utilizamos una ventana bonita.
     */

    if (
        typeof Swal !== "undefined"
    ) {

        Swal.fire({

            title: "¿Reiniciar progreso?",

            text:
                "Se eliminará el progreso guardado del manual.",

            icon: "warning",

            showCancelButton: true,

            confirmButtonText:
                "Sí, reiniciar",

            cancelButtonText:
                "Cancelar",

            reverseButtons: true

        }).then(function (resultado) {

            if (
                resultado.isConfirmed
            ) {

                reiniciarProgreso();

            }

        });

        return;

    }


    /*
     * Alternativa si SweetAlert2 no está cargado.
     */

    const confirmar =
        window.confirm(
            "¿Deseas reiniciar todo el progreso del manual?"
        );


    if (confirmar) {

        reiniciarProgreso();

    }

}


/* =========================================================
   25. REINICIAR PROGRESO
========================================================= */

function reiniciarProgreso() {

    progresoManual = {};


    try {

        localStorage.removeItem(
            STORAGE_KEY
        );

    } catch (error) {

        console.error(
            "No se pudo eliminar el progreso:",
            error
        );

    }


    actualizarProgresoGeneral();


    /*
     * Si actualmente hay un módulo abierto,
     * lo volvemos a dibujar.
     */

    if (moduloActual) {

        const id =
            moduloActual.id;

        abrirModulo(id);

    }


    /*
     * Actualizamos todas las tarjetas.
     */

    renderizarModulos(
        obtenerModulosVisibles()
    );


    /*
     * Mensaje de confirmación.
     */

    if (
        typeof Swal !== "undefined"
    ) {

        Swal.fire({

            title: "Progreso reiniciado",

            text:
                "El progreso del manual fue reiniciado correctamente.",

            icon: "success",

            confirmButtonText:
                "Continuar"

        });

    }

}


/* =========================================================
   26. OBTENER MÓDULOS VISIBLES
========================================================= */

function obtenerModulosVisibles() {

    const buscador =
        window.manualElementos?.buscador ||
        document.getElementById(
            "buscarManual"
        );


    if (!buscador) {

        return MANUAL_MODULOS;

    }


    const texto =
        buscador.value
            .trim()
            .toLowerCase();


    if (!texto) {

        return MANUAL_MODULOS;

    }


    return MANUAL_MODULOS.filter(
        function (modulo) {

            const contenido =
                (
                    modulo.titulo +
                    " " +
                    modulo.descripcion +
                    " " +
                    modulo.rol
                )
                    .toLowerCase();


            return contenido.includes(
                texto
            );

        }
    );

}


/* =========================================================
   27. MODAL DE VIDEO
========================================================= */

function configurarModalVideo() {

    /*
     * El modal puede no existir inicialmente
     * porque lo creamos dinámicamente cuando
     * se necesita.
     */

    let modal =
        document.getElementById(
            "modalVideoManual"
        );


    if (!modal) {

        modal =
            document.createElement("div");

        modal.id =
            "modalVideoManual";

        modal.className =
            "manual-video-modal";


        modal.innerHTML = `

            <div class="manual-video-overlay"></div>


            <div class="manual-video-dialog">


                <button
                    type="button"
                    class="manual-video-close"
                    id="cerrarVideoManual"
                    aria-label="Cerrar video"
                >

                    <i class="fa-solid fa-xmark"></i>

                </button>


                <div class="manual-video-frame">

                    <iframe
                        id="iframeVideoManual"
                        src=""
                        title="Tutorial EcoMarket PRO"
                        allow="
                            accelerometer;
                            autoplay;
                            clipboard-write;
                            encrypted-media;
                            gyroscope;
                            picture-in-picture;
                            web-share
                        "
                        allowfullscreen
                    ></iframe>

                </div>

            </div>

        `;


        document.body.appendChild(
            modal
        );

    }


    const cerrar =
        document.getElementById(
            "cerrarVideoManual"
        );


    const overlay =
        modal.querySelector(
            ".manual-video-overlay"
        );


    if (cerrar) {

        cerrar.addEventListener(
            "click",
            cerrarVideo
        );

    }


    if (overlay) {

        overlay.addEventListener(
            "click",
            cerrarVideo
        );

    }


    document.addEventListener(
        "keydown",
        function (evento) {

            if (
                evento.key === "Escape"
            ) {

                cerrarVideo();

            }

        }
    );

}


/* =========================================================
   28. ABRIR VIDEO
========================================================= */

function abrirVideo(url) {

    if (!url) {

        return;

    }


    const modal =
        document.getElementById(
            "modalVideoManual"
        );


    const iframe =
        document.getElementById(
            "iframeVideoManual"
        );


    if (!modal || !iframe) {

        console.error(
            "No se encontró el modal del video."
        );

        return;

    }


    const videoId =
        obtenerYoutubeId(url);


    if (!videoId) {

        console.error(
            "URL de YouTube no válida:",
            url
        );

        return;

    }


    iframe.src =
        "https://www.youtube.com/embed/" +
        videoId +
        "?rel=0";


    modal.classList.add(
        "active"
    );


    document.body.classList.add(
        "manual-video-open"
    );

}


/* =========================================================
   29. CERRAR VIDEO
========================================================= */

function cerrarVideo() {

    const modal =
        document.getElementById(
            "modalVideoManual"
        );


    const iframe =
        document.getElementById(
            "iframeVideoManual"
        );


    if (!modal) {

        return;

    }


    modal.classList.remove(
        "active"
    );


    document.body.classList.remove(
        "manual-video-open"
    );


    /*
     * Detener el video al cerrar el modal.
     */

    if (iframe) {

        iframe.src = "";

    }

}


/* =========================================================
   30. OBTENER ID DE YOUTUBE
========================================================= */

function obtenerYoutubeId(url) {

    if (!url) {

        return null;

    }


    /*
     * Soporta:

     * https://youtu.be/ABC123
     * https://www.youtube.com/watch?v=ABC123
     * https://youtube.com/watch?v=ABC123
     * https://www.youtube.com/embed/ABC123
     */

    const expresion =
        /(?:youtube\.com\/(?:watch\?v=|embed\/|shorts\/)|youtu\.be\/)([A-Za-z0-9_-]{11})/;


    const resultado =
        url.match(expresion);


    if (
        resultado &&
        resultado[1]
    ) {

        return resultado[1];

    }


    return null;

}


/* =========================================================
   31. EXPONER FUNCIONES
=========================================================

   Estas funciones quedan disponibles en window.

   Esto permite que también puedan ser utilizadas
   desde otros elementos del HTML si posteriormente
   se necesitan.
========================================================= */

window.MANUAL_MODULOS =
    MANUAL_MODULOS;

window.abrirModulo =
    abrirModulo;

window.cerrarDetalle =
    cerrarDetalle;

window.abrirVideo =
    abrirVideo;

window.cerrarVideo =
    cerrarVideo;

window.reiniciarProgreso =
    reiniciarProgreso;


/* =========================================================
   FIN DE configuracion.js
========================================================= */