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

    @Autowired
    private EncryptionService encryptionService;

    public Pair<String, HttpStatus> criarUsuario(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()) == null) {
            if (usuarioRepository.findByUsuario(usuario.getUsuario()) == null) {
                usuario.setSenha(encryptionService.encrypt(usuario.getSenha()));
                usuarioRepository.save(usuario);
                return Pair.of("Usuário criado com sucesso", HttpStatus.CREATED);
            } else {
                return Pair.of("Usuário Inválido", HttpStatus.CONFLICT);
            }
        }
        return Pair.of("Email inválido", HttpStatus.CONFLICT);
    }

    public Usuario listarUsuarioByUsuarioAndSenha(Usuario usuario) {
        Usuario nomeUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());

        if (nomeUsuario != null) {
            String decryptedSenha = encryptionService.decrypt(nomeUsuario.getSenha());

            if (usuario.getSenha().equals(decryptedSenha)) {
                return nomeUsuario;
            }
        }

        return null;
    }

    public Usuario listarUsuarioByUsuarioAndSenhaEncripted(Usuario usuario) {
        usuario.setSenha(encryptionService.decrypt(usuario.getSenha()));

        return listarUsuarioByUsuarioAndSenha(usuario);
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario listarUsuarioById(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }
    
}
