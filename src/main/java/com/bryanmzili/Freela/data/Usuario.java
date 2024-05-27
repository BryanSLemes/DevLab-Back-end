package com.bryanmzili.Freela.data;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Document(collection = "usuarios")
public class Usuario {

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
}
