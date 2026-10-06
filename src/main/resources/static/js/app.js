// 🔹 Gráfico
const ctx = document.getElementById('grafico');

new Chart(ctx, {
    type: 'line',
    data: {
        labels: ['Ene', 'Feb', 'Mar', 'Abr'],
        datasets: [{
            label: 'Ventas',
            data: [100, 200, 150, 300],
        }]
    }
});

// 🔹 Simulador solar
function calcular() {
    let consumo = document.getElementById("consumo").value;

    if (consumo === "") {
        alert("Ingresa consumo");
        return;
    }

    let paneles = consumo / 150;
    let inversion = paneles * 800000;

    document.getElementById("resultado").innerHTML =
        "Necesitas " + paneles.toFixed(1) + " paneles<br>" +
        "Inversión: $" + inversion.toLocaleString();
}