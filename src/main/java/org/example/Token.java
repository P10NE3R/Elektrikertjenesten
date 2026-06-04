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
 * Henter et tilgangstoken fra ContractingWorks sin autentiseringsserver.
 * Sender en POST-forespørsel med innloggingsinfo og returnerer access token som streng.
 *
 * Påkrevde variabler i .env:
 *      AuthBaseUrl – basis-URL til autentiseringstjenesten
 *      SubjectId   – identifikator for brukeren
 *      ApiKey      – API-nøkkel for autentisering
 *      TenantId    – leietaker-ID i multi-tenant-oppsett
 *      ClientId    – klient-ID (heltall)
 */
public class Token {

    private static String cachedToken = null;
    public static long expireTime = 0;

    /*
     * Henter og returnerer et access token fra autentiseringsserveren.
     * Kaster en feil hvis noen .env-variabler mangler eller token ikke returneres.
     */
    static String getToken() throws Exception {
        //Sjekker om token har gått ut enda
        if(cachedToken != null && System.currentTimeMillis() < expireTime){
            return cachedToken;
        }

        //Denne er brukt for å hente variabler fra .env-fil
        Dotenv dotenv = Dotenv.load();

        String authBaseUrl = dotenv.get("AuthBaseUrl");
        String subjectId = dotenv.get("SubjectId");
        String apiKey = dotenv.get("ApiKey");
        String tenantId = dotenv.get("TenantId");
        int clientId = Integer.parseInt(dotenv.get("ClientId"));

        //Sjekker at variablene i .env er tilstede
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

        //Request til token serveren
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
        expireTime = System.currentTimeMillis() + (55 * 60 * 1000);
        System.out.println(expireTime);
        return token;
    }
}
