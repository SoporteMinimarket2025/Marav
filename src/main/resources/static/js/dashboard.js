
const buscador =
    document.getElementById(
        "buscadorModulo"
    );

buscador.addEventListener(
    "keyup",
    function(){

        let filtro =
            this.value.toLowerCase();

        let enlaces =
            document.querySelectorAll(
                ".menu a"
            );

        enlaces.forEach(link => {

            let texto =
                link.textContent.toLowerCase();

            if(texto.includes(filtro)){

                link.style.display =
                    "flex";

            }else{

                link.style.display =
                    "none";

            }

        });

    });

