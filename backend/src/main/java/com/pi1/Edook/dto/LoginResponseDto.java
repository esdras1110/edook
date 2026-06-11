package com.pi1.Edook.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
// utilizado para retornar apenas dados essenciais 
public class LoginResponseDto {
    private String nome;
    private String cargo;
    private String token;
}