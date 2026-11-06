package http;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import database.DatabaseSpese;
import java.util.Locale;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class WebServerSpese {
    private static final String WEB_DIR = "src/web";

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new StaticFileHandler());
        server.createContext("/aggiungi", new AggiungiHandler());
        server.createContext("/lista-dati", new ListaDatiHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("=== WEB SERVER AVVIATO ===");
        System.out.println("Apri il browser su: http://localhost:8080");
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestPath = exchange.getRequestURI().getPath();

            if (requestPath.equals("/") || requestPath.isEmpty()) {
                requestPath = "index.html";
            } else if (requestPath.startsWith("/")) {
                requestPath = requestPath.substring(1);
            }

            File file = new File(WEB_DIR, requestPath);

            if (!file.exists() || file.isDirectory()) {
                String notFound = "404 - File non trovato";
                exchange.sendResponseHeaders(404, notFound.length());
                OutputStream os = exchange.getResponseBody();
                os.write(notFound.getBytes());
                os.close();
                return;
            }

            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(bytes);
            os.close();
        }
    }

    static class AggiungiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseQuery(body);

                String descrizione = params.getOrDefault("descrizione", "");
                double importo = Double.parseDouble(params.getOrDefault("importo", "0"));
                String categoria = params.getOrDefault("categoria", "generale");

                DatabaseSpese db = new DatabaseSpese();
                db.aggiungiSpesa(descrizione, importo, categoria);

                exchange.sendResponseHeaders(200, -1);
            }
        }

        private Map<String, String> parseQuery(String query) {
            Map<String, String> map = new HashMap<>();
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    map.put(pair[0], URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
                } else {
                    map.put(pair[0], "");
                }
            }
            return map;
        }
    }

    // Endpoint per recuperare le spese in formato JSON per l'interfaccia web
    static class ListaDatiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("{\"spese\":[");
            double totale = 0;

            try {
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/expense_tracker", "root", "1234");
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM spese");

                boolean prima = true;
                while (rs.next()) {
                    if (!prima) json.append(",");

                    String rawDesc = rs.getString("descrizione");
                    String desc = (rawDesc != null) ? rawDesc.replace("\\", "\\\\")
                                                      .replace("\"", "\\\"")
                                                      .replace("\n", " ")
                                                      .replace("\r", " ") : "";

                    double imp = rs.getDouble("importo");

                    String rawCat = rs.getString("categoria");
                    String cat = (rawCat != null) ? rawCat.replace("\\", "\\\\")
                                                    .replace("\"", "\\\"")
                                                    .replace("\n", " ")
                                                    .replace("\r", " ") : "";

                    totale += imp;

                    //Locale.US per forzare il punto decimale (es. 89.99 anziché 89,99)
                    json.append(String.format(Locale.US, "{\"desc\":\"%s\",\"imp\":%.2f,\"cat\":\"%s\"}", desc, imp, cat));
                    prima = false;
                }

                rs.close();
                stmt.close();
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            //Locale.US anche per il totale complessivo
            json.append(String.format(Locale.US, "],\"totale\":%.2f}", totale));

            byte[] response = json.toString().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            OutputStream os = exchange.getResponseBody();
            os.write(response);
            os.close();
        }
    }
}