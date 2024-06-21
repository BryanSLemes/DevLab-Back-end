package com.bryanmzili.DevLab.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.bryanmzili.DevLab.data.Usuario;
import com.bryanmzili.DevLab.exception.InvalidTokenException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    
    @Value("${chave.criptografia.jwt}")
    private String encryptionKey;

    public String gerarToken(Usuario usuario) {
        return JWT.create()
                .withIssuer("Login")
                .withSubject(usuario.getUsuario())
                .withExpiresAt(LocalDateTime.now()
                        .plusHours(1)
                        .toInstant(ZoneOffset.of("-03:00"))
                ).sign(Algorithm.HMAC256(encryptionKey));
    }

    public String getSubject(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(encryptionKey))
                    .withIssuer("Login")
                    .build().verify(token).getSubject();
        } catch (JWTDecodeException exception) {
            throw new InvalidTokenException("Token Inválido");
        } catch (com.auth0.jwt.exceptions.TokenExpiredException exception) {
            throw new com.bryanmzili.DevLab.exception.TokenExpiredException("Token Expirado");
        } catch (com.auth0.jwt.exceptions.SignatureVerificationException exception) {
            throw new com.bryanmzili.DevLab.exception.SignatureVerificationException("Token Inválido");
        }
    }
}
