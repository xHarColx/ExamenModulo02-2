document.addEventListener("DOMContentLoaded",  () => {
    const modalEditar = new bootstrap.Modal(document.getElementById("modalEditar"));
    const showModalChange = document.getElementById("showModalChange");

    showModalChange.addEventListener("click", () => {
        modalEditar.show();
    });
});
