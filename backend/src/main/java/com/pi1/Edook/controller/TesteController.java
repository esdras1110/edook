package com.pi1.Edook.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/teste")
public class TesteController{
    @GetMapping("/backend")
    public String teste() {
        return "Esdras e Icaro vulgo TheBigAmorim";
    }
}