document.addEventListener("DOMContentLoaded", () => {
    const modalEditar = new bootstrap.Modal(document.getElementById("modalEditar"));
    const showModalChange = document.getElementById("showModalChange");
    const btnEditar = document.getElementById("btnEditar");

    showModalChange.addEventListener("click", () => {
        const logi = getCookie("logi");
        document.getElementById("user").value = logi;
        modalEditar.show();
    });

    btnEditar.addEventListener("click", async (event) => {
        const logi = document.getElementById("user").value;
        const pastPass = document.getElementById("pastPass").value;
        const pass = document.getElementById("newPass").value;
        const cpass = document.getElementById("cNewPass").value;
        event.preventDefault();
        const response = await fetch("login-user", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
        }).then(response => response.json());

    });



    function getCookie(nombre) {
        const valor = `; ${document.cookie}`;
        const partes = valor.split(`; ${nombre}=`);
        if (partes.length === 2) {
            return partes.pop().split(';').shift();
        }
    }
});
