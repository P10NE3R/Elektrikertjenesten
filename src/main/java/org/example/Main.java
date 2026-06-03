package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.cdimascio.dotenv.Dotenv;

import java.net.http.HttpClient;
import java.util.Map;

import java.sql.Connection;

/**
 * Startpunkt for applikasjonen.
 * Orkestrerer tilkobling til Azure SQL-database og ContractingWorks GraphQL-API.
 */
public class Main {
    public static void main(String[] args) {

        try {
            Connection conn = AzureClient.getConnection();
            System.out.println("Database tilkoblet: " + !conn.isClosed());
            conn.close();
        }catch (Exception e){
            System.out.println("Svar mottatt fra serveren:");
        }


        /*
        try {
            System.out.println("Starter applikasjonen...");



            HttpClient httpClient = HttpClient.newHttpClient();
            String token = Token.getToken();



            ContractingWorksClient contractingWorksClient = new ContractingWorksClient(httpClient, token);


            String query = "{ __typename }";
            Map<String, Object> variables = null;

            System.out.println("Henter token");

            JsonNode result = contractingWorksClient.sendQuery(query, variables);

            System.out.println("Svar mottatt fra serveren:");
            System.out.println(result.toPrettyString());

        } catch (Exception e) {
            System.err.println("Noe gikk galt under kjoring:");
            e.printStackTrace();
        }
        */
    }
}
