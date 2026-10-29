package com.br.unifor.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class UserController {

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> credencial){
        //Sumular login feita pelo filter
        return ResponseEntity.ok("Login : Bem-sucedido ! Use credencial no header");
    }

    @PostMapping("/protected")
    public ResponseEntity<String> protectedLogin(Authentication authentication){
        return ResponseEntity.ok("Acesso concedido ! Usuario: "+ authentication);
    }
}
