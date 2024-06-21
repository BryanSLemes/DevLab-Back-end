package com.bryanmzili.DevLab.exception;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex, WebRequest request) {
        if (IfContains(ex.getMessage())) {
            return new ResponseEntity<>("Recurso não encontrado", HttpStatus.NOT_FOUND);
        }

        System.out.println("Erro Interno no Servidor: \n" + ex.getMessage());
        return new ResponseEntity<>("Erro Interno no Servidor", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public final ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, WebRequest request) {
        return new ResponseEntity<>("Formato de parâmetro inválido", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestBodyException.class)
    public final ResponseEntity<String> handleMissingRequestBodyException(MissingRequestBodyException ex, WebRequest request) {
        return new ResponseEntity<>("Formato de parâmetro inválido", HttpStatus.BAD_REQUEST);
    }

    private boolean IfContains(String mensagemExcecao) {
        List<String> mensagens = new ArrayList();

        mensagens.add("No static resource");
        mensagens.add("Request method 'POST' is not supported");
        mensagens.add("Request method 'GET' is not supported");

        for (String mensagem : mensagens) {
            if (mensagemExcecao.contains(mensagem)) {
                return true;
            }
        }
        return false;
    }
    
}
