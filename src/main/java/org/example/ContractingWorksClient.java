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

/**
 * Klient for å sende GraphQL-spørringer til ContractingWorks sitt API.
 * Henter automatisk et nytt access token før hver spørring.
 * Krever at GraphQLEndpoint er satt i .env-filen.
 */

public class ContractingWorksClient {
    private final HttpClient httpClient;
    private final String token;
    private final String endpoint;

    private final ObjectMapper objectMapper;

    public ContractingWorksClient(HttpClient httpClient, String token) {
        Dotenv config = Dotenv.load();
        this.httpClient = httpClient;
        this.token = token;
        this.objectMapper = new ObjectMapper();
        this.endpoint = config.get("GraphQLEndpoint");
        if (this.endpoint == null) {
            throw new RuntimeException("GraphQLEndpoint mangler i .env");
        }
    }


    /**
     * Sender en GraphQL-spørring og returnerer svaret fra serveren.
     * variables gjør bare at denne er gjennbrukbar med flere queries.
     * Kaster en feil hvis serveren svarer med feilkode (ikke 200-299).
     */

    public JsonNode sendQuery(String query, Object variables) throws Exception {

        Map<String, Object> bodyMap = new HashMap<>();
        bodyMap.put("query", query);
        if (variables != null) {
            bodyMap.put("variables", variables);
        }

        String jsonRequestBody = objectMapper.writeValueAsString(bodyMap);
        //HTTP-request til kontracting works
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Bearer " + this.token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .build();


        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseString = response.body();


        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("Får ikke respons fra Contracting Works: " + responseString);
        }
        return objectMapper.readTree(responseString);
    }
}