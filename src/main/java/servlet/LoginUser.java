package servlet;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dao.ClienteJpaController;
import dto.Cliente;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import util.DESCipher;
import util.JwtUtil;
import util.SHA256Util;

@WebServlet(name = "LoginUser", urlPatterns = {"/login-normal"})
public class LoginUser extends HttpServlet {

    private static final String CLAVE_DES = "12345678"; // clave DES (8 caracteres exactos)

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");

        try ( PrintWriter out = response.getWriter()) {
            Gson gson = new Gson();
            JsonObject jsonObject = new JsonObject();

            BufferedReader reader = request.getReader();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            String log = json.get("log").getAsString();
            String passCifrada = json.get("pass").getAsString();

            // DESCIFRAR contraseña con DES
            String passDescifrada = DESCipher.descifrar(passCifrada, CLAVE_DES);
            System.out.println("Contraseña descifrada con DES: " + passDescifrada);
            System.out.println("-----------------------------------------");
            String resultado = SHA256Util.hash(passDescifrada);
            System.out.println("Contraseña SHA256: " + resultado);
            System.out.println("-----------------------------------------");
            ClienteJpaController ujc = new ClienteJpaController();
            Cliente usuario = ujc.validarUsuario(new Cliente(log, resultado)); // Debes implementar este método
            // VALIDACIÓN CON BCRYPT
            if (usuario != null) {
                HttpSession session = request.getSession();

                session.setAttribute("codiClie", usuario.getCodiClie());
                session.setAttribute("logiClie", usuario.getLogiClie());
                session.setAttribute("passClie", usuario.getPassClie());
                session.setAttribute("usuario", log);

                String token = JwtUtil.generarToken(log);
                jsonObject.addProperty("result", "ok");
                jsonObject.addProperty("token", token);
                jsonObject.addProperty("logi", usuario.getLogiClie());
                jsonObject.addProperty("codiClie", usuario.getCodiClie());
                jsonObject.addProperty("logiClie", usuario.getLogiClie());
                jsonObject.addProperty("passClie", usuario.getPassClie());

            } else {
                jsonObject.addProperty("result", "not");
                jsonObject.addProperty("message", "credenciales inválidas");
            }
            out.print(gson.toJson(jsonObject));
        } catch (Exception ex) {
            Logger.getLogger(LoginUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getServletInfo() {
        return "Login de usuario con DES y BCrypt";
    }
}
