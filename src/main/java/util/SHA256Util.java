package util;

import java.security.MessageDigest;

public class SHA256Util {

    public static String hash(String texto) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256"); // Cambio aquí
        byte[] bytes = md.digest(texto.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b)); // a hexadecimal
        }
        return sb.toString();
    }
}
