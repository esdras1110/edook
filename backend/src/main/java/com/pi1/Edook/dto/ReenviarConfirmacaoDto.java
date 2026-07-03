package com.pi1.Edook.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReenviarConfirmacaoDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String codigo;
}
