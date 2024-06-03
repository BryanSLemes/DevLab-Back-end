package com.bryanmzili.Freela.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("Freela")
public class MainController {

    @GetMapping("")
    public ResponseEntity<String> index() {

        return new ResponseEntity<>("Retornar algo", HttpStatus.OK);
    }
    
}
