/* ========================================= */
/* SIMULADOR SOLAR DASHBOARD */
/* ========================================= */

function calcularSolar() {


const ciudad =
    document.getElementById("ciudad");

const consumo =
    Number(
        document.getElementById("consumo").value
    );

const factura =
    Number(
        document.getElementById("factura").value
    );



if (consumo <= 0 || factura <= 0) {

    document.getElementById("resultadoSolar").innerHTML = `

    <div class="alerta-solar">

            ⚠️ Complete todos los campos para realizar la simulación.

</div>

    `;

    return;
}



const nombreCiudad =
    ciudad.options[
        ciudad.selectedIndex
    ].text;



const ahorroMensual =
    factura * 0.80;



const ahorroAnual =
    ahorroMensual * 12;



document.getElementById("resultadoSolar").innerHTML = `

    <div class="card-solar">

        <h3>

            ☀️ Resultado Preliminar

    </h3>

    <p>

        📍 Ciudad:

        <strong>

            ${nombreCiudad}

        </strong>

    </p>

    <p>

        ⚡ Consumo reportado:

        <strong>

            ${consumo.toLocaleString("es-CO")} kWh/mes

        </strong>

    </p>

    <p>

        💰 Ahorro mensual estimado:

        <strong>

            $${ahorroMensual.toLocaleString("es-CO")} COP

        </strong>

    </p>

    <p>

        📅 Ahorro anual estimado:

        <strong>

            $${ahorroAnual.toLocaleString("es-CO")} COP

        </strong>

    </p>

    <div class="solar-info">

        Esta es una simulación informativa.

        Para conocer la cantidad de paneles,
        inversión requerida, retorno económico
        e impacto ambiental consulta el
        simulador especializado.

    </div>

    <a href="https://soporteminimarket2025.github.io/simulador-solar/"
       target="_blank"
       class="btn-cotizar">

        ☀️ Ver Simulador Completo

    </a>

</div>

    `;


}

/* ========================================= */
/* SLIDER AUTOMÁTICO */
/* ========================================= */

document.addEventListener("DOMContentLoaded", () => {


const slides =
    document.querySelectorAll(".slide");

let currentSlide = 0;

if (slides.length > 0) {

    setInterval(() => {

        slides[currentSlide]
            .classList.remove("active");

        currentSlide =
            (currentSlide + 1)
            % slides.length;

        slides[currentSlide]
            .classList.add("active");

    }, 4000);

}


});
