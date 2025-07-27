package com.bryanmzili.DevLab.service;

import com.bryanmzili.DevLab.security.EncryptionDecryptionUtil;
import com.bryanmzili.DevLab.data.TrocaSenhaDTO;
import com.bryanmzili.DevLab.data.Usuario;
import com.bryanmzili.DevLab.data.UsuarioRepository;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    private final EncryptionDecryptionUtil encryptionService;

    public UsuarioService(@Value("${jasypt.encryptor.password}") String encryptionKey) {
        encryptionService = new EncryptionDecryptionUtil(encryptionKey);
    }

    public Pair<String, HttpStatus> criarUsuario(Usuario usuario) {
        String regexUser = "^[a-z0-9_-]+$";
        Pattern patternUsuario = Pattern.compile(regexUser);
        Matcher matcherUser = patternUsuario.matcher(usuario.getUsuario());

        if (!matcherUser.matches()) {
            return Pair.of("Usuário contém caracteres inválidos", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if (usuarioRepository.findByEmail(usuario.getEmail()) == null) {
            if (usuarioRepository.findByUsuario(usuario.getUsuario()) == null) {
                usuario.setSenha(encryptionService.encode(usuario.getSenha()));
                usuario.setOnline(false);
                usuario.setId(null);
                usuarioRepository.save(usuario);
                return Pair.of("Usuário criado com sucesso", HttpStatus.CREATED);
            }
            return Pair.of("Usuário Inválido", HttpStatus.CONFLICT);
        }
        return Pair.of("Email inválido", HttpStatus.CONFLICT);
    }

    public Usuario listarUsuarioByUsuarioAndSenhaEncripted(Usuario usuario) {
        Usuario nomeUsuario = usuarioRepository.findByUsuario(usuario.getUsuario());
        if (nomeUsuario != null) {
            if (encryptionService.matches(nomeUsuario.getSenha(), usuario.getSenha())) {
                return nomeUsuario;
            }
        }
        return null;
    }

    public Usuario atualizarUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public ResponseEntity<String> atualizarSenhaUsuario(Usuario usuario, TrocaSenhaDTO trocaSenhaDTO) {
        if (!encryptionService.decrypt(usuario.getSenha()).equals(trocaSenhaDTO.getSenhaAtual())) {
            return new ResponseEntity<>("Senha Atual Inválida", HttpStatus.UNAUTHORIZED);
        }

        if (trocaSenhaDTO.getNovaSenha().replace(" ", "").length() < 8) {
            return ResponseEntity.badRequest().body("Senha Nova deve possuir pelo menos 8 caracteres");
        }

        if (encryptionService.decrypt(usuario.getSenha()).equals(trocaSenhaDTO.getNovaSenha())) {
            return ResponseEntity.badRequest().body("Nova senha não pode ser igual à atual.");
        }

        usuario.setSenha(encryptionService.encode(trocaSenhaDTO.getNovaSenha()));
        usuarioRepository.save(usuario);

        return ResponseEntity.ok("Senha atualizada com sucesso.");
    }

    public List<Usuario> listarTodosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario listarUsuarioById(String id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    public Usuario listarInfoUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario);
    }

}
