import java.util.*;

/**
 * Created by bowen on 24/01/16.
 */
public class TSPBruteForce {
    private static Map<String, Position> cities;
    private static double minCost;
    private static ListWrapper minPath;

    public static List<String> bruteForce(final String file) {
        minCost = Double.POSITIVE_INFINITY;
        minPath = new ListWrapper();
        cities = TSPUtils.readCities(file);
        if (cities.size() == 1) {
            System.out.println("A");
            return Collections.singletonList("A");
        } else {
            runBruteForce("A", Collections.singletonList("A"), 0);
            minPath.list.add("A");
            minPath.list.forEach(System.out::println);
            return minPath.list;
        }
    }

    private static class ListWrapper {
        public List<String> list;
    }

    public static void runBruteForce(final String current, final List<String> explored, final double cost) {
        final Set<String> unexplored = new HashSet<>(cities.keySet());
        unexplored.removeAll(explored);
        if (unexplored.isEmpty()) {
            final double newCost = cost + TSPUtils.distance(cities.get(current), cities.get("A"));
            if (newCost < minCost) {
                minPath.list = explored;
                minCost = newCost;
            }
        } else {
            for (final String s : unexplored) {
                final List<String> newExplored = new ArrayList<>(explored);
                newExplored.add(s);
                final double newCost = cost + TSPUtils.distance(cities.get(current), cities.get(s));
                runBruteForce(s, newExplored, newCost);
            }
        }
    }
}
