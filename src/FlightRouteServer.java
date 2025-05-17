import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.*;

public class FlightRouteServer {

    private static final Set<String> cities = new HashSet<>();
    private static final Map<String, List<Flight>> flights = new HashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/add_city", new AddCityHandler());
        server.createContext("/add_flight", new AddFlightHandler());
        server.createContext("/show_routes", new ShowRoutesHandler());

        server.setExecutor(null); // creates a default executor
        System.out.println("Server started at http://localhost:8080");
        server.start();
    }

    static class AddCityHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String city = params.get("name");
            if (city == null || city.isEmpty()) {
                sendResponse(exchange, 400, "Missing city name");
                return;
            }
            boolean added = cities.add(city);
            if (added) {
                sendResponse(exchange, 200, "City added: " + city);
            } else {
                sendResponse(exchange, 200, "City already exists: " + city);
            }
        }
    }

    static class AddFlightHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            Map<String, String> params = queryToMap(exchange.getRequestURI().getQuery());
            String from = params.get("from");
            String to = params.get("to");
            String durationStr = params.get("duration");
            String costStr = params.get("cost");

            if (from == null || to == null || durationStr == null || costStr == null) {
                sendResponse(exchange, 400, "Missing parameters");
                return;
            }
            if (!cities.contains(from) || !cities.contains(to)) {
                sendResponse(exchange, 400, "Both cities must exist");
                return;
            }
            try {
                int duration = Integer.parseInt(durationStr);
                double cost = Double.parseDouble(costStr);

                flights.computeIfAbsent(from, k -> new ArrayList<>())
                        .add(new Flight(to, duration, cost));

                sendResponse(exchange, 200, "Flight added from " + from + " to " + to);
            } catch (NumberFormatException e) {
                sendResponse(exchange, 400, "Invalid duration or cost");
            }
        }
    }

    static class ShowRoutesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!exchange.getRequestMethod().equalsIgnoreCase("GET")) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Cities:\n");
            for (String city : cities) {
                sb.append("- ").append(city).append("\n");
            }
            sb.append("\nFlights:\n");
            for (Map.Entry<String, List<Flight>> entry : flights.entrySet()) {
                String from = entry.getKey();
                for (Flight f : entry.getValue()) {
                    sb.append("From ").append(from)
                            .append(" to ").append(f.to)
                            .append(": Duration ").append(f.duration).append(" min, Cost ").append(f.cost)
                            .append("\n");
                }
            }
            sendResponse(exchange, 200, sb.toString());
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");  // CORS header
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private static Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();
        if (query == null) return result;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length > 1) {
                result.put(pair[0], pair[1]);
            } else {
                result.put(pair[0], "");
            }
        }
        return result;
    }

    static class Flight {
        String to;
        int duration;
        double cost;

        Flight(String to, int duration, double cost) {
            this.to = to;
            this.duration = duration;
            this.cost = cost;
        }
    }
}

