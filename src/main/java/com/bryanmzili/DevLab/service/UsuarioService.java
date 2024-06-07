package com.bryanmzili.DevLab.service;

import com.bryanmzili.DevLab.data.Usuario;
import com.bryanmzili.DevLab.data.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public Pair<String, HttpStatus> criarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()) == null) {
            if (usuarioRepository.findByUsuario(usuario.getUsuario()) == null) {
                usuarioRepository.save(usuario);
                return Pair.of("Usuário criado com sucesso", HttpStatus.CREATED);
            } else {
                return Pair.of("Usuário Inválido", HttpStatus.CONFLICT);
            }
        }
        return Pair.of("Email inválido", HttpStatus.CONFLICT);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario listarUsuarioById(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario listarUsuarioByUsuarioAndSenha(Usuario usuario) {
        return usuarioRepository.findByUsuarioAndSenha(usuario.getUsuario(), usuario.getSenha());
    }
}
