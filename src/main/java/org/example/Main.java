package org.example;

import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpClient;

public class Main {
    public static void main(String[] args) {


            try {
                String token = Token.getToken();
            } catch (Exception e) {
                e.printStackTrace();
            }

        try {
            System.out.println("🚀 Starter applikasjonen...");


            Dotenv config = Dotenv.load();


            HttpClient httpClient = HttpClient.newHttpClient();
            String token = Token.getToken();
            GraphQLClient graphQLClient = new GraphQLClient(httpClient, token, config);


            String query = "query GetProject($id: Int!) { project(id: $id) { name } }";
            Map<String, Object> variables = Map.of("id", 12345);

            System.out.println("📡 Sender GraphQL-spørring til Devinco...");


            JsonNode result = graphQLClient.sendQuery(query, variables);


            System.out.println("Svar mottatt fra serveren:");
            System.out.println(result.toPrettyString());

        } catch (Exception e) {
            System.err.println("Noe gikk galt under kjøring:");
            e.printStackTrace();
        }

    }
}