package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Oppretter en tilkobling til Azure SQL-databasen.
 * Leser DB_HOST, DB_NAME, DB_USER og DB_PASSWORD fra .env-filen.
 */

public class AzureClient {

    public static Connection getConnection() throws Exception {
        Dotenv dotenv = Dotenv.load();

        String host     = dotenv.get("DB_HOST");
        String dbName   = dotenv.get("DB_NAME");
        String user     = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        if (host == null || dbName == null || user == null || password == null) {
            throw new RuntimeException("Klarte ikke å laste alle DB-variabler fra .env-filen!");
        }

        String url = "jdbc:sqlserver://" + host + ":1433;"
                   + "database=" + dbName + ";"
                   + "user=" + user + ";"
                   + "password=" + password + ";"
                   + "encrypt=true;"
                   + "trustServerCertificate=false;";

        return DriverManager.getConnection(url);
    }




}
