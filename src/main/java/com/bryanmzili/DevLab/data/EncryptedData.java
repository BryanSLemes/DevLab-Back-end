package com.bryanmzili.DevLab.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Data;

@Data
public class EncryptedData {
    
    @NotNull(message = "Data é obrigatório")
    @NotBlank(message = "Data é obrigatório")
    private List<String> data;
    
}
