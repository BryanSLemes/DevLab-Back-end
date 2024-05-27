package com.bryanmzili.Freela.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("Freela")
public class MainController {

    @GetMapping("")
    public ResponseEntity<String> index() {

        return new ResponseEntity<>("Retornar algo", HttpStatus.OK);
    }
}
