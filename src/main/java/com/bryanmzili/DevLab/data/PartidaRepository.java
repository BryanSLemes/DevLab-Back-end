package com.bryanmzili.DevLab.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends MongoRepository<Partida, String>{
    
}
