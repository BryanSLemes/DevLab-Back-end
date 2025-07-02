package com.bryanmzili.DevLab.webSocket;

import com.bryanmzili.DevLab.data.Usuario;
import com.bryanmzili.DevLab.service.TokenService;
import com.bryanmzili.DevLab.service.UsuarioService;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    @Autowired
    private TokenService tokenService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String query = request.getURI().getQuery();
        if (query != null && query.startsWith("token=")) {
            String token = query.substring("token=".length());
            
            try {
                String subject = this.tokenService.getSubject(token);
                Usuario usuarioLogado = this.usuarioService.listarInfoUsuario(subject);
                if (usuarioLogado != null) {
                    attributes.put("idUsuario", usuarioLogado.getId());
                    attributes.put("usuario", usuarioLogado.getUsuario());
                    if(usuarioLogado.isOnline()){
                        return false;
                    }
                    usuarioLogado.setOnline(true);
                    this.usuarioService.atualizarUsuario(usuarioLogado);
                    return true;
                }
            } catch (Exception e) {}
        }
        return false; 
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {}
    
}
