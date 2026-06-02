package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ContractingWorksApi {

    static String cwToken;
    static long tokenFetchedAt;

    public static void main(String[] args) throws Exception {
        fetchToken();
        String result = queryGraphQL("{ customers(top: 10) { items { name } } }");
        System.out.println(result);
    }

    static void fetchToken() throws Exception {
        String clientId = "1";
        String subjectId = ;
        String apiKey = "";
        String authHost = ";

        String body = """
            {
                "apiKey": "%s",
                "clientId": 1,
                "subjectId": "%s",
                "tenantId": "%s"
            }
            """.formatted(apiKey, subjectId, clientId);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://" + authHost + "/token/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // parse the accessToken from the JSON response
        // simplest way without a library:
        String json = response.body();
        cwToken = json.split("\"accessToken\":\"")[1].split("\"")[0];
        tokenFetchedAt = System.currentTimeMillis();
    }

    static String queryGraphQL(String query) throws Exception {
        // refresh token if older than 55 minutes
        if (System.currentTimeMillis() - tokenFetchedAt > 55 * 60 * 1000) {
            fetchToken();
        }

        String clientId = "";
        String graphQlHost = "";
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
