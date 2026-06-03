package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class Token {

    static String getToken() throws Exception {

        Dotenv dotenv = Dotenv.load();

        String authBaseUrl = dotenv.get("AuthBaseUrl");
        String subjectId = dotenv.get("SubjectId");
        String apiKey = dotenv.get("ApiKey");
        String tenantId = dotenv.get("TenantId");
        int clientId = Integer.parseInt(dotenv.get("ClientId"));

        if (authBaseUrl == null || subjectId == null || apiKey == null || tenantId == null) {
            throw new RuntimeException("Klarte ikke å laste alle variabler fra .env-filen!");
        }

        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("subjectId", subjectId);
        bodyMap.put("apiKey", apiKey);
        bodyMap.put("clientId", clientId);
        bodyMap.put("tenantId", tenantId);

        String json = objectMapper.writeValueAsString(bodyMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(authBaseUrl + "/Token/GenerateAccessTokenAsJson"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());

        String body = response.body();
        System.out.println("AUTH RESPONSE (" + response.statusCode() + "):");
        System.out.println(body);

        JsonNode jsonNode = objectMapper.readTree(body);
        String token = jsonNode.path("accessToken").asText(null);

        if (token == null) {
            throw new RuntimeException("Failed to retrieve token");
        }
        return token;
    }
}
