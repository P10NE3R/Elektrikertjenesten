# Arkutekturprinsipper verdt å inkorporere i Java-prosjektet

## Prepared statements

Prepared statements er en teknikk for å forbedre ytelsen og sikkerheten i databaseapplikasjoner. I stedet for å bygge SQL-spørringer som tekststrenger, kan du bruke prepared statements for å definere spørringen med plassholdere for parametere. Dette gir flere fordeler:

```Java

String sql = "INSERT INTO Projects (ProjectId, Name, CreatedDateUTC, UpdatedDateUTC) VALUES (?, ?, ?, ?)";
PreparedStatement statement = connection.prepareStatement(sql);

statement.setString(1, project.getProjectId());
statement.setString(2, project.getName());
statement.setTimestamp(3, project.getCreatedDateUTC() != null ? Timestamp.valueOf(project.getCreatedDateUTC()) : null);
statement.setTimestamp(4, project.getUpdatedDateUTC() != null ? Timestamp.valueOf(project.getUpdatedDateUTC()) : null);
statement.executeUpdate();
```

Ved å pakke inn SQL-spørringer i prepared statements, sikrer vi at SQLinjections ikke blir et problem, samtidig som vi forbedrer ytelsen ved at databasen kan optimalisere spørringene bedre. Prepared statements kan også bidra til å redusere datatrafikken mellom applikasjonen og databasen, siden spørringen bare trenger å sendes én gang, og deretter kan gjenbrukes med forskjellige parametere.

## Records

Records er en immutable dataklasse som er designet for å holde data (typisk for DTOer). De gir en enkel og konsis måte å definere datamodeller på, og kan være et godt alternativ til vanlige klasser når du bare trenger å holde data uten å implementere kompleks logikk.

```Java

// Istedenfor å skrive

Public class Project {
    private String projectId;
    private String name;
    private LocalDateTime createdDateUTC;
    private LocalDateTime updatedDateUTC;

    // Getters, setters, constructor, etc.
}

// Kan du bruke records

public record Project(String projectId, String name, LocalDateTime createdDateUTC, LocalDateTime updatedDateUTC) {}
```

Som du ser er alt forbeholdt en linje fremfor flere linjer med boilerplate-kode. Ulempemn er at siden der er immutable, kan du ikke endre verdiene etter at objektet er opprettet.

## IsertUTC og UpdatedUTC

Det er en god praksis å inkludere `InsertedUTC` og `UpdatedUTC` i databasetabellene dine for å holde styr på når dataen ble opprettet og sist oppdatert. Dette kan være spesielt nyttig for å spore endringer i dataen over tid, og for å implementere funksjonalitet som "soft deletes" eller historikksporing.

```sql
CREATE TABLE Projects (
    ProjectId NVARCHAR(50) PRIMARY KEY,
    Name NVARCHAR(255),
    CreatedDateUTC DATETIME2,
    UpdatedDateUTC DATETIME2
);
```

> Kan hende ikke all logikk blir logisk å basere på disse to alene, men de er gode sjekker å ha for å kunne spore dataen og når den ble hentet ned, og kan være nyttige for å implementere funksjonalitet som "soft deletes" eller historikksporing.