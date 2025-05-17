package src;

import java.util.*;

public class Graph {
    private Set<String> cities = new HashSet<>();
    private Map<String, List<Flight>> flights = new HashMap<>();

    public boolean addCity(String cityName) {
        return cities.add(cityName);
    }

    public boolean addFlight(String from, String to, int duration, double cost) {
        if (!cities.contains(from) || !cities.contains(to)) {
            return false; // One or both cities not found
        }

        Flight flight = new Flight(to, duration, cost);
        flights.computeIfAbsent(from, k -> new ArrayList<>()).add(flight);
        return true;
    }

    public void showRoutes() {
        if (cities.isEmpty()) {
            System.out.println("No cities added.");
            return;
        }
        System.out.println("Cities:");
        for (String city : cities) {
            System.out.println("- " + city);
        }
        System.out.println("Flights:");
        for (String from : flights.keySet()) {
            for (Flight f : flights.get(from)) {
                System.out.printf("From %s to %s: Duration %d min, Cost %.2f\n", from, f.to, f.duration, f.cost);
            }
        }
    }

    // Getters to support RoutePlanner
    public Set<String> getCities() {
        return cities;
    }

    public Map<String, List<Flight>> getFlights() {
        return flights;
    }
}


