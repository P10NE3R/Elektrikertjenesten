package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;

import java.sql.*;
import java.util.*;

/**
 * Klient for å opprette tilkobling til Azure SQL-databasen.
 * Leser tilkoblingsinformasjon fra .env-filen og returnerer en aktiv Connection.
 *
 * Påkrevde variabler i .env:
 *      DB_HOST     – serveradressen til Azure SQL (f.eks. dinserver.database.windows.net)
 *      DB_NAME     – navnet på databasen
 *      DB_USER     – brukernavn
 *      DB_PASSWORD – passord
 *
 * Merk: IP-adressen din må være whitelistet i Azure Portal under Networking > Firewall rules.
 */
public class AzureClient {

    public static Connection cachedConnection = null;

    /*
     * Oppretter og returnerer en tilkobling til Azure SQL-databasen.
     * Henter tilkoblingsinformasjon fra .env-filen.
     * Kaster en feil hvis variabler mangler eller tilkoblingen feiler.
     */
    public static Connection getConnection() throws Exception {

        if (cachedConnection != null && !cachedConnection.isClosed()) {
            return cachedConnection;
        }

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

        return cachedConnection = DriverManager.getConnection(url);
    }

    /*
     * Lukker en åpen databasetilkobling.
     * Gjør ingenting hvis tilkoblingen allerede er null.
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                System.out.println("Klarte ikke å lukke tilkoblingen: " + e.getMessage());
            }
        }
    }

    public static void closeCache() {
        closeConnection(cachedConnection);
        cachedConnection = null;
    }

    /*
     * Tester at tilkoblingen til Azure SQL-databasen fungerer.
     * Skriver ut om tilkoblingen var vellykket eller ikke.
     */
    public void AzureTester() {
        try {
            Connection conn = AzureClient.getConnection();
            if(conn != null){
                System.out.println("Database tilkoblet: ");
            }
        } catch (Exception e) {
            System.out.println("Klarte ikke å koble til databasen: " + e.getMessage());
        }
    }

    /*
     * Kjører en INSERT, UPDATE eller DELETE mot Azure SQL-databasen.
     * Returnerer antall rader som ble påvirket.
     * Kaster en feil hvis tilkoblingen feiler eller spørringen er ugyldig.
     */
    public static int AzureUpdate(String query) throws Exception {
        Statement statement = getConnection().createStatement();
        return statement.executeUpdate(query);
    }




    }




