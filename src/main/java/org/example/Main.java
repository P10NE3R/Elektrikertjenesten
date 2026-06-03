package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpClient;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starter applikasjonen...");

            Dotenv config = Dotenv.load();

            HttpClient httpClient = HttpClient.newHttpClient();
            Token token = new Token();
            GraphQLClient graphQLClient = new GraphQLClient(httpClient, token, config);

            String query = "query GetProject($id: Int!) { project(id: $id) { name } }";
            Map<String, Object> variables = Map.of("id", 12345);

            System.out.println("Sender GraphQL-sporring til Devinco...");

            JsonNode result = graphQLClient.sendQuery(query, variables);

            System.out.println("Svar mottatt fra serveren:");
            System.out.println(result.toPrettyString());

        } catch (Exception e) {
            System.err.println("Noe gikk galt under kjoring:");
            e.printStackTrace();
        }
    }
}
