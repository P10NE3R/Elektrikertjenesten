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

public class GraphQLClient {
    private final HttpClient httpClient;
    private final AuthService authService;
    private final String endpoint;

    private final ObjectMapper objectMapper;

    public GraphQLClient(HttpClient httpClient, AuthService authService, Dotenv config) {
        this.httpClient = httpClient;
        this.authService = authService;
        this.objectMapper = new ObjectMapper();

        this.endpoint = config.get("GraphQLEndpoint");
        if (this.endpoint == null) {
            throw new RuntimeException("GraphQLEndpoint mangler i .env");
        }
    }


    public JsonNode sendQuery(String query, Object variables) throws Exception {


        String token = authService.getToken();

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("query", query);
        if (variables != null) {
            bodyMap.put("variables", variables);
        }


        String jsonRequestBody = objectMapper.writeValueAsString(bodyMap);

        //HTTP-request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .build();


        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseString = response.body();


        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("GraphQL error: " + responseString);
        }


        return objectMapper.readTree(responseString);
    }
}