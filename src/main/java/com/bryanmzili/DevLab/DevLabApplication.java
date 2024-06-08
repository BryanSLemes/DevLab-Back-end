package com.bryanmzili.DevLab;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class DevLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevLabApplication.class, args);
    }

}
