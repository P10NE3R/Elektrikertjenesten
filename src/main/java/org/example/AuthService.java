package org.example;
import io.github.cdimascio.dotenv.Dotenv;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {

    public void main(String[] args) throws Exception {
        String token = getToken();
        System.out.println("TOKEN: " + token);
    }

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


        String json = String.format(
                "{\"subjectId\":\"%s\",\"apiKey\":\"%s\",\"clientId\":%d,\"tenantId\":\"%s\"}",
                subjectId, apiKey, clientId, tenantId
        );

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

        String token = extract(body, "accessToken");
        if (token == null) {
            throw new RuntimeException("Failed to retrieve token");
        }
        return token;
    }

    static String extract(String json, String field) {
        String key = "\"" + field.toLowerCase() + "\"";
        int i = json.toLowerCase().indexOf(key);
        if (i < 0) return null;
        i = json.indexOf(':', i) + 1;
        int start = json.indexOf('"', i) + 1;
        int end = json.indexOf('"', start);
        if (start <= 0 || end < 0) return null;
        return json.substring(start, end);
    }
}