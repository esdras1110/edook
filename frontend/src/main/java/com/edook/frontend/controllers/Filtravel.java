package com.edook.frontend.controllers;

import com.edook.frontend.models.FiltroReservaDTO;

// Interface que define um contrato para controladores que suportam filtros de pesquisa. InicioController e ReservasController
public interface Filtravel {
    // Recebe um objeto (DTO) contendo os critérios de pesquisa escolhidos pelo utilizador.
    void setFiltrosAvancados(FiltroReservaDTO filtros);
}