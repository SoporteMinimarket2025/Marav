
document.addEventListener("DOMContentLoaded", function () {


    // ======================================================
    // 🔎 BUSCADOR
    // ======================================================

    const buscador =
        document.getElementById("buscarTopbar");


    // ------------------------------------------------------
    // Si no existe el buscador, no continuar
    // ------------------------------------------------------

    if (!buscador) {

        console.log(
            "Buscador del topbar no encontrado."
        );

        return;
    }


    // ======================================================
    // ✏️ PLACEHOLDER
    // ======================================================

    buscador.placeholder =
        "Buscar producto facturado...";


    // ======================================================
    // 📦 CONTAR PRODUCTOS
    // ======================================================

    actualizarCantidadProductos();


    // ======================================================
    // 🔍 EVENTO DE BÚSQUEDA
    // ======================================================

    buscador.addEventListener("input", function () {


        // --------------------------------------------------
        // Texto ingresado
        // --------------------------------------------------

        const texto =
            this.value
                .toLowerCase()
                .trim();


        // --------------------------------------------------
        // Obtener filas
        // --------------------------------------------------

        const filas =
            document.querySelectorAll(
                "#tablaProductos tr"
            );


        // --------------------------------------------------
        // Recorrer filas
        // --------------------------------------------------

        filas.forEach(function (fila) {


            // ==============================================
            // Ignorar fila vacía
            // ==============================================

            if (
                fila.querySelector(
                    ".factura-detalle-empty"
                )
            ) {

                return;

            }


            // ==============================================
            // Verificar que tenga celdas
            // ==============================================

            const celdas =
                fila.querySelectorAll("td");


            if (celdas.length === 0) {

                return;

            }


            // ==============================================
            // Obtener nombre del producto
            // ==============================================

            const producto =
                celdas[0]
                    .textContent
                    .toLowerCase()
                    .trim();


            // ==============================================
            // Mostrar / ocultar
            // ==============================================

            fila.style.display =
                producto.includes(texto)
                    ? ""
                    : "none";

        });

    });


});


// ==========================================================
// 📦 ACTUALIZAR CANTIDAD DE PRODUCTOS
// ==========================================================

function actualizarCantidadProductos() {


    const contador =
        document.getElementById(
            "cantidadProductos"
        );


    if (!contador) {

        return;

    }


    const filas =
        document.querySelectorAll(
            "#tablaProductos tr"
        );


    let cantidadFilas = 0;


    filas.forEach(function (fila) {


        // --------------------------------------------------
        // No contar la fila vacía
        // --------------------------------------------------

        if (
            fila.querySelector(
                ".factura-detalle-empty"
            )
        ) {

            return;

        }


        // --------------------------------------------------
        // Verificar que sea una fila válida
        // --------------------------------------------------

        if (
            fila.querySelectorAll("td").length >= 4
        ) {

            cantidadFilas++;

        }

    });


    // ======================================================
    // MOSTRAR RESULTADO
    // ======================================================

    contador.textContent =
        cantidadFilas === 1
            ? "1 producto"
            : `${cantidadFilas} productos`;

}