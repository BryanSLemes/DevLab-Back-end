package com.bryanmzili.DevLab.security;

import com.bryanmzili.DevLab.data.Usuario;
import com.bryanmzili.DevLab.data.UsuarioRepository;
import com.bryanmzili.DevLab.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class FilterToken extends OncePerRequestFilter{

    @Autowired
    TokenService tokenService;
    
    @Autowired
    UsuarioRepository usuarioRepository;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        String token;
        
        var authorizationHeader = request.getHeader("Authorization");
        
        if(authorizationHeader != null){
            token = authorizationHeader.replace("Bearer ", "");
            
            String subject = this.tokenService.getSubject(token);  
            Usuario usuario = this.usuarioRepository.findByUsuario(subject);
            Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
    
}
