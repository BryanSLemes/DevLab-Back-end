package com.bryanmzili.DevLab;

import com.bryanmzili.DevLab.data.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements ApplicationRunner{

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        mongoTemplate.updateMulti(
            new Query() {},
            new Update().unset("online"),
            Usuario.class
        );
    }
}
