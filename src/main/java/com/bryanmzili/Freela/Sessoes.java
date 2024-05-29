package com.bryanmzili.Freela;

import com.bryanmzili.Freela.data.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class Sessoes {
    
    public static Usuario lerSessaoUsuario(HttpServletRequest request) {
        HttpSession ses = request.getSession();
        Usuario sesProp = null;
        if (ses != null && ses.getAttribute("usuario") != null) {
            sesProp = (Usuario) ses.getAttribute("usuario");
        }

        return sesProp;
    }

}
