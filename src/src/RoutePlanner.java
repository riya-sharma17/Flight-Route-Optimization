package src;

public class RoutePlanner {
    private Graph graph;

    public RoutePlanner(Graph graph) {
        this.graph = graph;
    }

    public void findCheapestRoute(String from, String to) {
        // For demo, just print a placeholder
        if (!graph.getCities().contains(from) || !graph.getCities().contains(to)) {
            System.out.println("One or both cities do not exist.");
            return;
        }
        System.out.println("Finding cheapest route from " + from + " to " + to + "...");
        // TODO: Implement real Dijkstra or other algorithm
        System.out.println("Cheapest route feature not implemented yet.");
    }

    public void findFastestRoute(String from, String to) {
        if (!graph.getCities().contains(from) || !graph.getCities().contains(to)) {
            System.out.println("One or both cities do not exist.");
            return;
        }
        System.out.println("Finding fastest route from " + from + " to " + to + "...");
        // TODO: Implement real algorithm
        System.out.println("Fastest route feature not implemented yet.");
    }
}
