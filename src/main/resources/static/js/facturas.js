// ======================================================
// 🧾 FACTURAS | ECOMARKET PRO
// ======================================================
// Funciones:
// 1. Calcular KPI de facturas.
// 2. Buscar facturas desde el buscador del topbar.
// 3. Mantener los valores sincronizados con la tabla.
// ======================================================

document.addEventListener("DOMContentLoaded", function () {

    // ==================================================
    // 🚀 INICIALIZACIÓN
    // ==================================================

    inicializarBuscador();
    calcularEstadisticas();

});


// ======================================================
// 📊 CALCULAR ESTADÍSTICAS / KPI
// ======================================================

function calcularEstadisticas() {

    // --------------------------------------------------
    // Obtener todas las filas de la tabla
    // --------------------------------------------------

    const filas = document.querySelectorAll("#tablaFacturas tr");

    let totalFacturas = 0;
    let facturasCompletadas = 0;
    let totalFacturado = 0;

    // --------------------------------------------------
    // Recorrer las facturas
    // --------------------------------------------------

    filas.forEach(function (fila) {

        // Ignorar la fila de "No hay facturas"
        if (
            fila.classList.contains("facturas-empty") ||
            fila.querySelector(".facturas-empty")
        ) {
            return;
        }

        // Verificar que realmente sea una fila de factura
        const celdas = fila.querySelectorAll("td");

        if (celdas.length < 5) {
            return;
        }

        totalFacturas++;

        // --------------------------------------------------
        // Estado de la factura
        // --------------------------------------------------

        const estadoElemento =
            fila.querySelector(".estado-badge");

        const estado = estadoElemento
            ? estadoElemento.textContent.trim().toUpperCase()
            : "";

        if (estado === "COMPLETADA") {
            facturasCompletadas++;
        }

        // --------------------------------------------------
        // Total de la factura
        // --------------------------------------------------

        const totalElemento =
            fila.querySelector(".factura-total");

        if (totalElemento) {

            let valor = totalElemento.textContent.trim();

            // --------------------------------------------------
            // Convertir formato colombiano:
            //
            // $1.234.567
            // $25.000
            // --------------------------------------------------

            valor = valor
                .replace(/\$/g, "")
                .replace(/\s/g, "")
                .replace(/\./g, "")
                .replace(",", ".");

            const numero = parseFloat(valor);

            if (!isNaN(numero)) {
                totalFacturado += numero;
            }
        }

    });


    // ==================================================
    // 🖥️ MOSTRAR KPI
    // ==================================================

    const elementoTotal =
        document.getElementById("totalFacturas");

    const elementoCompletadas =
        document.getElementById("facturasCompletadas");

    const elementoFacturado =
        document.getElementById("totalFacturado");


    // --------------------------------------------------
    // Total de facturas
    // --------------------------------------------------

    if (elementoTotal) {
        elementoTotal.textContent =
            totalFacturas.toLocaleString("es-CO");
    }


    // --------------------------------------------------
    // Facturas completadas
    // --------------------------------------------------

    if (elementoCompletadas) {
        elementoCompletadas.textContent =
            facturasCompletadas.toLocaleString("es-CO");
    }


    // --------------------------------------------------
    // Total facturado
    // --------------------------------------------------

    if (elementoFacturado) {
        elementoFacturado.textContent =
            formatearMoneda(totalFacturado);
    }

}


// ======================================================
// 💰 FORMATEAR DINERO
// ======================================================

function formatearMoneda(valor) {

    return valor.toLocaleString("es-CO", {
        style: "currency",
        currency: "COP",
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
    });

}


// ======================================================
// 🔎 BUSCADOR DE FACTURAS
// ======================================================

function inicializarBuscador() {

    // --------------------------------------------------
    // Buscar el input del topbar
    // --------------------------------------------------

    const buscador =
        document.querySelector(".topbar .search input");

    if (!buscador) {
        return;
    }


    // --------------------------------------------------
    // Placeholder
    // --------------------------------------------------

    buscador.placeholder = "Buscar factura...";


    // --------------------------------------------------
    // Evento de búsqueda
    // --------------------------------------------------

    buscador.addEventListener("input", function () {

        const texto =
            this.value
                .trim()
                .toLowerCase();


        const filas =
            document.querySelectorAll("#tablaFacturas tr");


        filas.forEach(function (fila) {

            // ------------------------------------------
            // Ignorar fila vacía
            // ------------------------------------------

            if (
                fila.classList.contains("facturas-empty") ||
                fila.querySelector(".facturas-empty")
            ) {
                return;
            }


            // ------------------------------------------
            // Texto de la fila
            // ------------------------------------------

            const contenido =
                fila.innerText.toLowerCase();


            // ------------------------------------------
            // Mostrar / ocultar
            // ------------------------------------------

            if (contenido.includes(texto)) {
                fila.style.display = "";
            } else {
                fila.style.display = "none";
            }

        });

    });

}

