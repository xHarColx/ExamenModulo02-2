const btnLogin = document.getElementById("btnLogin");
const clave = "12345678"; // Clave DES de exactamente 8 caracteres


btnLogin.addEventListener("click", async (event) => {
    event.preventDefault();

    const log = document.getElementById("log").value.trim();
    const pass = document.getElementById("pass").value.trim();

    if (!log || !pass) {
        alert("Por favor, complete todos los campos.");
        return;
    }

    // Asegúrate de que cifrar(pass, clave) esté definida e implemente AES
    const passCifrada = cifrar(pass, clave);

    const obj = {
        log: log,
        pass: passCifrada
    };
    console.log(obj);
    try {
        const response = await fetch("login-normal", {
            method: "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(obj)
        });

        const result = await response.json();
        console.log(result);

        if (result.result === "ok") {
            setCookie("token", result.token, 7); // función definida aparte
            alert("Inicio de sesión exitoso.");
            window.location.href = "dist/pages/index.html";
        } else {
            alert(result.message || "Credenciales incorrectas.");
        }
    } catch (error) {
        console.error("Error en la solicitud:", error);
        alert("Ocurrió un error al intentar iniciar sesión.");
    }
});

function setCookie(nombre, valor, dias) {
    const fecha = new Date();
    fecha.setTime(fecha.getTime() + (dias * 24 * 60 * 60 * 1000));
    const expira = "expires=" + fecha.toUTCString();
    document.cookie = nombre + "=" + valor + ";" + expira + ";path=/";
}




function cifrar(message, key) {
    var keyHex = CryptoJS.enc.Utf8.parse(key);

    var encrypted = CryptoJS.DES.encrypt(message, keyHex, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });
    return encrypted.toString();
}

function descifrar(ciphertext, key) {
    var keyHex = CryptoJS.enc.Utf8.parse(key);

    // direct decrypt ciphertext
    var decrypted = CryptoJS.DES.decrypt({
        ciphertext: CryptoJS.enc.Base64.parse(ciphertext)
    }, keyHex, {
        mode: CryptoJS.mode.ECB,
        padding: CryptoJS.pad.Pkcs7
    });

    return decrypted.toString(CryptoJS.enc.Utf8);
}

