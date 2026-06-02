package org.example;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import io.github.cdimascio.dotenv.Dotenv;



/*
URI.create("https://%s/token/generate".formatted(authHost))

String url = "https://%s/client/%s/graphql".formatted(graphQlHost, clientId);

*/
public class ContractingWorksApi {

    static String cwToken;
    static long tokenFetchedAt;

    static void fetchToken() throws Exception {
        Dotenv dotenv = Dotenv.load();
        
        
        String clientId = dotenv.get("clientId");
        String subjectId = dotenv.get("subjectId");
        String tenantId = dotenv.get("tenantId");
        
        
        String apiKey = dotenv.get("apiKey");
        String authHost = dotenv.get("authHost");
        
        String body = """
            {
                "apiKey": "%s",
                "clientId": "%s",
                "subjectId": "%s",
                "tenantId": "%s"
            }
            """.formatted(apiKey, clientId, subjectId, tenantId);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + authHost + "/token/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String json = response.body();
        cwToken = json.split("\"accessToken\":\"")[1].split("\"")[0];
        tokenFetchedAt = System.currentTimeMillis();
    }

    static String queryGraphQL(String query) throws Exception {
 
        Dotenv dotenv = Dotenv.load();
        if (System.currentTimeMillis() - tokenFetchedAt > 55 * 60 * 1000) {
            fetchToken();
        }

        String clientId = dotenv.get("clientId");
        String graphQlHost = dotenv.get("graphQlHost");
        String url = "https://" + graphQlHost + "/client/" + clientId + "/graphql";

        String body = "{\"query\": \"" + query + "\"}";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + cwToken)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

}
