package src;

import java.util.*;

public class FlightRouteCLI {
    private Graph graph = new Graph();
    private RoutePlanner planner = new RoutePlanner(graph);

    public void run() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Flight Route Optimization System");
        String cmd;

        while (true) {
            System.out.print("> ");
            cmd = sc.nextLine().trim();

            if (cmd.isEmpty()) {
                System.out.println("Please enter a command.");
                continue;
            }

            String[] parts = cmd.split("\\s+");
            String command = parts[0];

            try {
                switch (command) {
                    case "add_city":
                        if (parts.length != 2) {
                            System.out.println("Usage: add_city <CityName>");
                            break;
                        }
                        boolean added = graph.addCity(parts[1]);
                        if (added) {
                            System.out.println("City " + parts[1] + " added.");
                        } else {
                            System.out.println("City " + parts[1] + " already exists.");
                        }
                        break;

                    case "add_flight":
                        if (parts.length != 5) {
                            System.out.println("Usage: add_flight <FromCity> <ToCity> <Duration> <Cost>");
                            break;
                        }
                        String from = parts[1];
                        String to = parts[2];
                        int duration = Integer.parseInt(parts[3]);
                        double cost = Double.parseDouble(parts[4]);
                        boolean flightAdded = graph.addFlight(from, to, duration, cost);
                        if (flightAdded) {
                            System.out.println("Flight added from " + from + " to " + to);
                        } else {
                            System.out.println("Failed to add flight. Check city names.");
                        }
                        break;

                    case "show_routes":
                        graph.showRoutes();
                        break;

                    case "cheapest_route":
                        if (parts.length != 3) {
                            System.out.println("Usage: cheapest_route <FromCity> <ToCity>");
                            break;
                        }
                        planner.findCheapestRoute(parts[1], parts[2]);
                        break;

                    case "fastest_route":
                        if (parts.length != 3) {
                            System.out.println("Usage: fastest_route <FromCity> <ToCity>");
                            break;
                        }
                        planner.findFastestRoute(parts[1], parts[2]);
                        break;

                    case "exit":
                        System.out.println("Exiting program.");
                        sc.close();
                        return;

                    default:
                        System.out.println("Unknown command");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new FlightRouteCLI().run();
    }
}
