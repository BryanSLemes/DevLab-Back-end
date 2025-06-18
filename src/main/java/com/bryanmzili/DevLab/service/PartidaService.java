package com.bryanmzili.DevLab.service;

import com.bryanmzili.DevLab.data.Partida;
import com.bryanmzili.DevLab.data.PartidaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PartidaService {
 
    @Autowired
    PartidaRepository partidaRepository;
    
    public boolean salvarPartida(Partida partida) {
        partidaRepository.save(partida);
        return true;
    }
    
}
