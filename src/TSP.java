import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by bowen on 23/01/16.
 */
public class TSP {
    public static void main(final String[] args) {
        final String dataFile = args[0];
        final Map<String, Position> cities = readCities(dataFile);
    }

    public static Map<String, Position> readCities(final String dataFile) {
        final Map<String, Position> cities = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            final int numCities = Integer.valueOf(br.readLine());
            for (int i = 0; i < numCities; i++) {
                final String line = br.readLine();
                final String[] tokens = line.split("\\s");
                cities.put(tokens[0], new Position(Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }

    public static List<String> search(final Map<String, Position> cities, final String startCity) {
        int numGenerated = 0;
        if (cities.size() == 1) {
            return Collections.singletonList(startCity);
        }
        final PriorityQueue<State> queue = new PriorityQueue<>(
                cities.size(),
                (Comparator<State>) (s1, s2) -> (s1.totalCost()).compareTo(s2.totalCost())
        );
        final List<String> visited = Collections.singletonList(startCity);
        queue.add(new State(startCity, visited, 0, computeHeuristic(cities, new HashSet<>(visited), startCity, startCity)));
        while (!queue.isEmpty()) {
            final State next = queue.poll();
            if (next.heuristic == 0) {
                System.out.println("Total generate nodes: " + numGenerated);
                return next.path;
            }
            if (next.path.equals(Arrays.asList("A", "H", "E", "C", "G", "I", "D", "B", "F", "J"))) {
                System.out.println("break here");
            }
            final Set<String> toBeVisited = new HashSet<>(cities.keySet());
            toBeVisited.removeAll(next.path);
            if (toBeVisited.isEmpty()) {
                toBeVisited.add(startCity);
            }
            for (final String s : toBeVisited) {
                if (!s.equals(next.currentCity)) {
                    numGenerated++;
                    final List<String> newPath = new ArrayList<>(next.path);
                    newPath.add(s);
                    final double heuristic = computeHeuristic(cities, new HashSet<>(newPath), s, startCity);
                    queue.add(new State(s, newPath, next.costToCurrent + distance(cities.get(next.currentCity), cities.get(s)), heuristic));
                }
            }
        }
        throw new IllegalStateException("Exhausted all nodes with no solution found");
    }

    public static double computeHeuristic(
            final Map<String, Position> cities,
            final Set<String> visited,
            final String currentCity,
            final String startCity
    ) {
        final Set<String> unvisited = new HashSet<>(cities.keySet());
        unvisited.removeAll(visited);
        if (unvisited.isEmpty()) {
            if (currentCity.equals(startCity)) {
                return 0;
            } else {
                return distance(cities.get(startCity), cities.get(currentCity));
            }
        }
        return computeSpanningCost(cities, unvisited, currentCity) +
                distance(cities.get(startCity), cities.get(findNearest(cities, unvisited, startCity)));
    }

    public static double computeSpanningCost(final Map<String, Position> cities, final Set<String> unvisited, final String currentCity) {
        final Set<String> toBeVisited = new HashSet<>(unvisited);
        final Map<String, Double> minDistanceToTree = new HashMap<>();
        for (final String s : toBeVisited) {
            minDistanceToTree.put(s, distance(cities.get(currentCity), cities.get(s)));
        }
        double cost = 0;
        while (!toBeVisited.isEmpty()) {
            final Map.Entry<String, Double> next = Collections.min(
                    minDistanceToTree.entrySet(),
                    (entry1, entry2) -> entry1.getValue().compareTo(entry2.getValue())
            );
            toBeVisited.remove(next.getKey());
            minDistanceToTree.remove(next.getKey());
            cost += next.getValue();
            for (final String s : toBeVisited) {
                final double distanceToNext = distance(cities.get(next.getKey()), cities.get(s));
                if (distance(cities.get(next.getKey()), cities.get(s)) < minDistanceToTree.get(s)) {
                    minDistanceToTree.put(s, distanceToNext);
                }
            }
        }
        return cost;
    }

    public static String findNearest(final Map<String, Position> cities, final Set<String> searchSet, final String currentCity) {
        String closest = "";
        double closestDist = Double.POSITIVE_INFINITY;
        for (final String city : searchSet) {
            if (!city.equals(currentCity)) {
                final double distance = distance(cities.get(currentCity), cities.get(city));
                if (distance < closestDist) {
                    closest = city;
                    closestDist = distance;
                }
            }
        }
        return closest;
    }

    public static double distance(final Position p1, final Position p2) {
        if (p1 == null || p2 == null) {
            throw new IllegalStateException("NPE");
        }
        return Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2));
    }

    private static class State {
        public final String currentCity;
        public final List<String> path;
        public final double costToCurrent;
        public final double heuristic;

        public State(final String currentCity, final List<String> path, final double costToCurrent, final double heuristic) {
            this.currentCity = currentCity;
            this.path = path;
            this.costToCurrent = costToCurrent;
            this.heuristic = heuristic;
        }

        public Double totalCost() {
            return costToCurrent + heuristic;
        }
    }
}
