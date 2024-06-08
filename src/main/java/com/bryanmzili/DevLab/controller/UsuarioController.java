package com.bryanmzili.DevLab.controller;

import com.bryanmzili.DevLab.data.Usuario;
import com.bryanmzili.DevLab.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("DevLab/usuario")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;

    @PostMapping("/cadastro")
    public ResponseEntity<String> adicionarUsuario(@RequestBody @Valid Usuario usuario, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder mensagensDeErro = new StringBuilder();
            for (FieldError error : result.getFieldErrors()) {
                mensagensDeErro.append(error.getDefaultMessage()).append(". ");
            }
            return ResponseEntity.badRequest().body(mensagensDeErro.toString());
        }
        Pair<String, HttpStatus> resultado = usuarioService.criarUsuario(usuario);
        return new ResponseEntity<>(resultado.getFirst(), resultado.getSecond());
    }

    @PostMapping("/login")
    public ResponseEntity<String> logarUsuario(@RequestBody Usuario usuario) {
        Usuario usuarioLogado = usuarioService.listarUsuarioByUsuarioAndSenha(usuario);

        if (usuarioLogado != null) {
            return ResponseEntity.ok("{'usuario': '" + usuarioLogado.getUsuario() + "', 'senha': '" + usuarioLogado.getSenha() + "'}");
        }

        return new ResponseEntity<>("Login Falhou", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/logado")
    public ResponseEntity<String> isUsuarioLogado(@RequestHeader("Authorization") String token) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Usuario user = objectMapper.readValue(token, Usuario.class);
            Usuario usuarioLogado = usuarioService.listarUsuarioByUsuarioAndSenhaEncripted(user);

            if (usuarioLogado != null) {
                return new ResponseEntity<>("Usuário Logado", HttpStatus.OK);
            }

        } catch (Exception e) {}

        return new ResponseEntity<>("Usuário deve efetuar login", HttpStatus.UNAUTHORIZED);
    }

}