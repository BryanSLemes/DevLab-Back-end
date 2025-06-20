package com.bryanmzili.DevLab.webSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;
    
    @Autowired
    public WebSocketConfig(JwtHandshakeInterceptor jwtHandshakeInterceptor) {
        this.jwtHandshakeInterceptor = jwtHandshakeInterceptor;
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new JogoDaVelhaWebSocketHandler(), "/DevLab/jogo-da-velha").addInterceptors(jwtHandshakeInterceptor).setAllowedOrigins("*");
    }
}
