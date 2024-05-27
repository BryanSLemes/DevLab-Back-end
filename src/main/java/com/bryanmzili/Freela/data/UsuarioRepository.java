package com.bryanmzili.Freela.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    
    Usuario findByUsuarioAndSenha(String usuario, String senha);
    
    Usuario findByEmail(String email);
    
    Usuario findByUsuario(String usuario);
}
