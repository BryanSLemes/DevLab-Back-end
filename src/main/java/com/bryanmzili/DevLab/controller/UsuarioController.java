package com.bryanmzili.DevLab.controller;

import com.bryanmzili.DevLab.data.EncryptedData;
import com.bryanmzili.DevLab.security.EncryptionDecryptionUtil;
import com.bryanmzili.DevLab.data.PartidaViewDTO;
import com.bryanmzili.DevLab.data.TrocaSenhaDTO;
import com.bryanmzili.DevLab.data.Usuario;
import com.bryanmzili.DevLab.service.PartidaService;
import com.bryanmzili.DevLab.service.TokenService;
import com.bryanmzili.DevLab.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("DevLab/usuario")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Value("${chave.criptografia.privada}")
    private String privateKeyPEM;

    private final EncryptionDecryptionUtil encryptionDecryptionUtil = new EncryptionDecryptionUtil();

    @Autowired
    public UsuarioController(Validator validator) {
        this.validator = validator;
    }

    private final Validator validator;

    @PostMapping("/cadastro")
    public ResponseEntity<String> adicionarUsuario(@RequestBody EncryptedData encryptedData) {
        try {
            Usuario usuario = decryptUser(encryptedData);

            return verificarCadastro(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Formato de parâmetro inválido");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> logarUsuario(@RequestBody EncryptedData encryptedData) {
        try {
            Usuario usuario = decryptUser(encryptedData);

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(usuario.getUsuario(), usuario.getSenha());

            Authentication authenticate = authenticationManager.authenticate(usernamePasswordAuthenticationToken);
            Usuario authenticatedUser = (Usuario) authenticate.getPrincipal();

            return new ResponseEntity<>(tokenService.gerarToken(authenticatedUser), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Login Falhou", HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/logado")
    public ResponseEntity<String> isUsuarioLogado(@RequestHeader("Authorization") String token) {
        try {
            String subject = this.tokenService.getSubject(token);
            Usuario usuarioLogado = this.usuarioService.listarInfoUsuario(subject);

            if (usuarioLogado != null) {
                usuarioLogado.setSenha(null);
                return new ResponseEntity<>(usuarioLogado.toJson(), HttpStatus.OK);
            }
        } catch (Exception e) {
        }

        return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("/historico")
    public ResponseEntity<?> historico(@RequestHeader("Authorization") String token) {
        try {
            String subject = this.tokenService.getSubject(token);
            Usuario usuarioLogado = this.usuarioService.listarInfoUsuario(subject);

            if (usuarioLogado != null) {
                List<PartidaViewDTO> historico = partidaService.historico(usuarioLogado.getId());
                return ResponseEntity.ok(historico);
            }
        } catch (Exception e) {
        }

        return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);
    }

    @PatchMapping("/trocar-senha")
    public ResponseEntity<String> trocarSenha(@RequestHeader("Authorization") String token, @RequestBody EncryptedData encryptedData) {
        try {
            String subject = this.tokenService.getSubject(token);
            Usuario usuario = this.usuarioService.listarInfoUsuario(subject);
            
            if (usuario != null) {
                TrocaSenhaDTO trocaSenhaDTO = decryptTrocaSenha(encryptedData);

                return usuarioService.atualizarSenhaUsuario(usuario, trocaSenhaDTO);
            }
        } catch (Exception e) {}
        return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);
    }

    private Usuario decryptUser(EncryptedData encryptedData) throws Exception {
        encryptionDecryptionUtil.setPrivateKeyPEM(privateKeyPEM);
        String decrypedData = encryptionDecryptionUtil.decryptMessage(encryptedData.getData());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(decrypedData, Usuario.class);
    }

    private TrocaSenhaDTO decryptTrocaSenha(EncryptedData encryptedData) throws Exception {
        encryptionDecryptionUtil.setPrivateKeyPEM(privateKeyPEM);
        String decrypedData = encryptionDecryptionUtil.decryptMessage(encryptedData.getData());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(decrypedData, TrocaSenhaDTO.class);
    }

    private ResponseEntity<String> verificarCadastro(Usuario usuario) {
        Set<ConstraintViolation<Usuario>> violations = validator.validate(usuario);
        if (violations.isEmpty()) {
            Pair<String, HttpStatus> resultado = usuarioService.criarUsuario(usuario);
            return new ResponseEntity<>(resultado.getFirst(), resultado.getSecond());
        } else {
            String mensagem = "";
            for (ConstraintViolation<Usuario> violation : violations) {
                if (!mensagem.contains(violation.getMessage())) {
                    mensagem += violation.getMessage() + ". ";
                }
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mensagem);
        }
    }

}
