package database;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseSpese {
    private static String serverUrl = "jdbc:mysql://localhost:3306/";
    private static String dbName = "expense_tracker";
    private static String username = "root";
    private static String password = "1234";

    // Blocco statico per caricare la configurazione esterna all'avvio
    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            File jarPath = new File(DatabaseSpese.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File parent = jarPath.isDirectory() ? jarPath : jarPath.getParentFile();
            File configFile = new File(parent, "config.properties");

            if (configFile.exists()) {
                try (InputStream input = new FileInputStream(configFile)) {
                    Properties prop = new Properties();
                    prop.load(input);
                    serverUrl = prop.getProperty("db.server", serverUrl);
                    dbName = prop.getProperty("db.name", dbName);
                    username = prop.getProperty("db.user", username);
                    password = prop.getProperty("db.password", password);
                    System.out.println("✅ Configurazione database caricata da file esterno.");
                }
            } else {
                System.out.println("⚠️ File config.properties non trovato, uso i valori di default.");
            }
        } catch (Exception e) {
            System.out.println("⚠️ Errore nel caricamento del file di configurazione: " + e.getMessage());
        }
    }

    public DatabaseSpese() {
        try {
            // 1. Connessione al server per creare il database se non esiste
            Connection con = DriverManager.getConnection(serverUrl, username, password);
            Statement stmt = con.createStatement();
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
            stmt.close();
            con.close();

            // 2. Connessione al database specifico per creare la tabella
            Connection conDb = DriverManager.getConnection(serverUrl + dbName, username, password);
            Statement stmtDb = conDb.createStatement();

            String sqlTable = "CREATE TABLE IF NOT EXISTS spese (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "descrizione LONGTEXT NOT NULL, " +
                    "importo DOUBLE NOT NULL, " +
                    "categoria VARCHAR(100) NOT NULL)";

            stmtDb.executeUpdate(sqlTable);
            stmtDb.close();
            conDb.close();

            System.out.println("Database e tabella 'spese' pronti a funzionare!");
        } catch (SQLException e) {
            System.out.println("Errore di connessione al database: " + e.getMessage());
        }
    }

    public void stampaTutteLeSpese() {
        try {
            Connection con = DriverManager.getConnection(serverUrl + dbName, username, password);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM spese");

            System.out.println("\n--- ELENCO DELLE SPESE REGISTRATE ---");
            while (rs.next()) {
                int id = rs.getInt("id");
                String desc = rs.getString("descrizione");
                double imp = rs.getDouble("importo");
                String cat = rs.getString("categoria");
                System.out.println("ID: " + id + " | " + cat.toUpperCase() + " | " + desc + " - " + imp + " €");
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void calcolaTotale() {
        try {
            Connection con = DriverManager.getConnection(serverUrl + dbName, username, password);
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT SUM(importo) AS totale FROM spese");

            if (rs.next()) {
                double totale = rs.getDouble("totale");
                System.out.println("\n--- TOTALE COMPLESSIVO SPESO: " + totale + " €");
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void aggiungiSpesa(String descrizione, double importo, String categoria) {
        try {
            Connection con = DriverManager.getConnection(serverUrl + dbName, username, password);
            String sql = "INSERT INTO spese (descrizione, importo, categoria) VALUES (?, ?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);

            pstmt.setString(1, descrizione);
            pstmt.setDouble(2, importo);
            pstmt.setString(3, categoria);

            pstmt.executeUpdate();
            System.out.println("Spesa aggiunta con successo!");

            pstmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}