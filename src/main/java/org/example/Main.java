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
        AzureClient azure = new AzureClient();
        azure.AzureTester();
        

        AzureClient.closeCache();
    }
}
