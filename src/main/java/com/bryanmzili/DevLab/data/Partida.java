package com.bryanmzili.DevLab.data;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "partidas")
public class Partida {
    
    @Id
    private String id;

    private String jogador1;
    
    private String jogador2;
    
    private String vencedor;
    
    private LocalDateTime data;
    
    private String status;
    
    private String jogo;
}
