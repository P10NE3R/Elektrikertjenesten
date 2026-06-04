package org.example;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;

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

    public void AzureTester(){
        try {
            Connection conn = AzureClient.getConnection();
            System.out.println("Database tilkoblet: " + !conn.isClosed());
            conn.close();
        }catch (Exception e){
            System.out.println("Svar mottatt fra serveren:");
        }
    }



}
