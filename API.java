
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

public class API {

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/student_management";

    private static final String DB_USER = "root";

    // Password is read from an environment variable.
    private static final String DB_PASSWORD =
            System.getenv("DB_PASSWORD");

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress(8080), 0
        );

        server.createContext("/api/students", API::handleStudents);

        server.setExecutor(null);
        server.start();

        System.out.println("Student API is running at http://localhost:8080");
    }

    private static void handleStudents(HttpExchange exchange)
            throws IOException {

        addCorsHeaders(exchange);

        String method = exchange.getRequestMethod();

        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        if (!"POST".equalsIgnoreCase(method)) {
            sendResponse(exchange, 405,
                    "Method not allowed. Use POST.");
            return;
        }

        if (DB_PASSWORD == null || DB_PASSWORD.isBlank()) {
            sendResponse(exchange, 500,
                    "Database password is not configured.");
            return;
        }

        try {
            String requestBody;

            try (InputStream input = exchange.getRequestBody()) {
                requestBody = new String(
                        input.readAllBytes(),
                        StandardCharsets.UTF_8
                );
            }

            Map<String, String> formData = parseFormData(requestBody);

            String name = formData.get("name");
            String ageText = formData.get("age");

            if (name == null || name.isBlank()
                    || ageText == null || ageText.isBlank()) {
                sendResponse(exchange, 400,
                        "Name and age are required.");
                return;
            }

            name = name.trim();

            if (name.length() > 100) {
                sendResponse(exchange, 400,
                        "Name must be 100 characters or fewer.");
                return;
            }

            int age;

            try {
                age = Integer.parseInt(ageText.trim());
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400,
                        "Age must be a valid number.");
                return;
            }

            if (age < 1 || age > 120) {
                sendResponse(exchange, 400,
                        "Age must be between 1 and 120.");
                return;
            }

            String sql =
                    "INSERT INTO students (name, age) VALUES (?, ?)";

            try (Connection connection = DriverManager.getConnection(
                    DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement statement =
                         connection.prepareStatement(sql)) {

                statement.setString(1, name);
                statement.setInt(2, age);

                int rowsInserted = statement.executeUpdate();

                if (rowsInserted > 0) {
                    sendResponse(exchange, 201,
                            "Student saved successfully!");
                } else {
                    sendResponse(exchange, 500,
                            "Student could not be saved.");
                }
            }

        } catch (Exception e) {
            // Log details on the server; don't expose them to the browser.
            System.err.println("Student API error: " + e.getMessage());

            sendResponse(exchange, 500,
                    "Unable to save student. Check the server configuration.");
        }
    }

    private static Map<String, String> parseFormData(String body) {

        Map<String, String> formData = new HashMap<>();

        if (body == null || body.isBlank()) {
            return formData;
        }

        for (String pair : body.split("&")) {
            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(
                    parts[0], StandardCharsets.UTF_8
            );

            String value = parts.length > 1
                    ? URLDecoder.decode(
                    parts[1], StandardCharsets.UTF_8
            )
                    : "";

            formData.put(key, value);
        }

        return formData;
    }

    private static void addCorsHeaders(HttpExchange exchange) {

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Origin", "*"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Methods", "POST, OPTIONS"
        );

        exchange.getResponseHeaders().set(
                "Access-Control-Allow-Headers", "Content-Type"
        );
    }

    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String message
    ) throws IOException {

        byte[] response = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(statusCode, response.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
