package com.bryanmzili.DevLab.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Document(collection = "usuarios")
public class Usuario implements UserDetails {

    @Id
    private String id;

    @NotNull(message = "Usuário é obrigatório")
    @NotBlank(message = "Usuário é obrigatório")
    private String usuario;

    @NotNull(message = "Senha é obrigatório")
    @NotBlank(message = "Senha é obrigatório")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    private String senha;

    @NotNull(message = "Email é obrigatório")
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    private boolean online = false;

    public void setUsuario(String usuario) {
        this.usuario = usuario.toLowerCase();
    }

    public void setEmail(String email) {
        this.email = email.toLowerCase();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return usuario;
    }
    
     public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String toJson() {
        return "{"
                + "\"usuario\":" + "\"" + this.usuario + "\","
                + "\"email\":" + "\"" + this.email + "\""
                + "}";
    }

}
