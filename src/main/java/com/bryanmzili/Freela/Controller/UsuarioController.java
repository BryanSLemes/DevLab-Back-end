package com.bryanmzili.Freela.controller;

import com.bryanmzili.Freela.data.Usuario;
import com.bryanmzili.Freela.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuario")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UsuarioController {

    @Autowired
    UsuarioService usuarioService;
    
    @GetMapping("/listar")
    public ResponseEntity<List<Usuario>> listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodosUsuarios();
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }
    
    @PostMapping("/cadastro")
    public ResponseEntity<String> adicionarUsuario(@RequestBody Usuario usuario) {
        String result = usuarioService.criarUsuario(usuario);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<String> logarUsuario(@RequestBody Usuario usuario, HttpServletRequest request) {
        Usuario usuarioLogado = usuarioService.listarUsuarioByUsuarioAndSenha(usuario);

        if (usuarioLogado != null) {
            HttpSession ses = request.getSession(true);
            ses.setAttribute("usuario", usuario);
            return new ResponseEntity<>("Login Feito com sucesso", HttpStatus.OK);
        }

        return new ResponseEntity<>("Login Falhou", HttpStatus.OK);
    }
}
