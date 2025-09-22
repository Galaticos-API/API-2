package util;

import modelo.Usuario;

public class Session {
    private static Usuario usuarioAtual;

    public static Usuario getUsuarioAtual() {
        return usuarioAtual;
    }

    public static void setUsuarioAtual(Usuario usuario) {
        usuarioAtual = usuario;
    }
}
