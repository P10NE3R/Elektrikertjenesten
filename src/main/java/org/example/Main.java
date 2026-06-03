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
            String token = Token.getToken();
            ContractingWorksClient contractingWorksClient = new ContractingWorksClient(httpClient, token, config);


            //String query = "{ __typename }";
            String query = "{ __schema { queryType { fields { name description } } } }";
            Map<String, Object> variables = null;

            System.out.println("Sender GraphQL-sporring til Devinco");

            JsonNode result = contractingWorksClient.sendQuery(query, variables);

            System.out.println("Svar mottatt fra serveren:");
            System.out.println(result.toPrettyString());

        } catch (Exception e) {
            System.err.println("Noe gikk galt under kjoring:");
            e.printStackTrace();
        }
    }
}
