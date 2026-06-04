package com.pi1.Edook.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class OpenApiExporter {

    @EventListener(ApplicationReadyEvent.class)
    public void exportOpenApi() {
        try {
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/v3/api-docs"))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            System.out.println("Status: " + response.statusCode());
            System.out.println("Resposta recebida:");
            System.out.println(response.body());

            Path output = Path.of("..", "docs", "openapi.json");

            Files.createDirectories(output.getParent());
            Files.writeString(output, response.body());

            System.out.println("OpenAPI exportado para: "
                    + output.toAbsolutePath());

        } catch (Exception e) {
            System.err.println("Erro ao exportar OpenAPI");
            e.printStackTrace();
        }
    }
}