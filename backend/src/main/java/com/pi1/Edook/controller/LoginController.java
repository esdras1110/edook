package com.pi1.Edook.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi1.Edook.dto.LoginDto;
import com.pi1.Edook.dto.LoginResponseDto;
import com.pi1.Edook.service.LoginService;

@RestController
@RequestMapping("/auth")
public class LoginController {

    //objeto de loginService para usar o metodo logar
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }
    //metodo responsavel por dar a resposta ao frontend se o login foi valido ou nao
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginDto dto) {

        // o objeto response recebe o login valido se passar pelas validações do service
        LoginResponseDto response = loginService.logar(dto);

        /* se chegou aqui, é por que esta tudo certo e devolve ao frontend a mensagem de login com sucesso la do
         loginService*/
        return ResponseEntity.ok(response);
    }
}