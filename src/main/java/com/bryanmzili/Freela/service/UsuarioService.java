package com.bryanmzili.Freela.service;

import com.bryanmzili.Freela.data.Usuario;
import com.bryanmzili.Freela.data.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    public String criarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()) == null) {
            if (usuarioRepository.findByUsuario(usuario.getUsuario()) == null) {
                usuarioRepository.save(usuario);
                return "Usuário criado com sucesso";
            } else {
                return "Usuário Inválido";
            }
        }
        return "Email inválido";
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
