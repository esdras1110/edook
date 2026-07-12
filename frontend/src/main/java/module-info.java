// Descritor do Módulo da aplicação
// Define quais bibliotecas externas este projeto precisa para funcionar e quais pacotes internos ele expõe ou permite acesso
module com.edook.frontend {
    requires javafx.controls;
    requires javafx.fxml;
    // Módulo para requisições HTTP
    requires java.net.http;
    // Bibliotecas do Jackson para manipular JSON
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;

    // 'opens' permite que bibliotecas externas, como o JavaFX acessem atributos privados
    opens com.edook.frontend to javafx.fxml;
    // 'exports' torna os pacotes visíveis para quem for executar
    exports com.edook.frontend;
    exports com.edook.frontend.controllers;
    opens com.edook.frontend.controllers to javafx.fxml;
    opens com.edook.frontend.models to javafx.base, javafx.fxml, com.fasterxml.jackson.databind;
    opens com.edook.frontend.components to javafx.fxml;
}