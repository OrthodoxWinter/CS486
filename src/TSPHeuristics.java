import java.util.*;

/**
 * This class contains functions needed to compute the heuristic for TSP A* search
 */
public class TSPHeuristics {

    /**
     * Compute the heuristic for a city given a set of parameters
     * @param cities Set of all cities and their coordinates
     * @param visited Set of cities that has already been visited in the TSP
     * @param currentCity City for which the heuristic is calculated
     * @param startCity The start city of the TSP
     * @return An admissible heuristic for the specified city
     */
    public static double computeHeuristic(
            final Map<String, Position> cities,
            final Set<String> visited,
            final String currentCity,
            final String startCity
    ) {
        //figure out what city we still need to visit
        final Set<String> unvisited = new HashSet<>(cities.keySet());
        unvisited.removeAll(visited);
        if (unvisited.isEmpty()) {
            //if all cities has been visited and the current city is the start city, then it is a goal state
            if (currentCity.equals(startCity)) {
                return 0;
            } else {
                //otherwise the heuristic is simply the distance from current city to start city
                return TSPUtils.distance(cities.get(startCity), cities.get(currentCity));
            }
        }
        /**
         * assuming we have at least one unvisited city, then the heuristic of the current city is defined as follows:
         * total weight of the minimum spanning tree of all unvisited cities, with root being the current city +
         * the weight of the shortest edge from the MST to the start city
         **/
        return computeSpanningCost(cities, unvisited, currentCity) +
                TSPUtils.distance(cities.get(startCity), cities.get(TSPUtils.findNearest(cities, unvisited, startCity)));
    }

    /**
     * Computes the total weight of the minimum spanning tree of all unvisited cities, with the root being the current city
     * This is simply an implementation of Prim's algorithm.
     * @param cities Map from city to their coordinates
     * @param unvisited Set of unvisited cities which the tree will be spanning
     * @param currentCity root of the MST
     * @return Weight of the MST (sum of the edge weights in the tree)
     */
    private static double computeSpanningCost(final Map<String, Position> cities, final Set<String> unvisited, final String currentCity) {
        final Set<String> toBeVisited = new HashSet<>(unvisited);
        final Map<String, Double> minDistanceToTree = new HashMap<>();
        for (final String s : toBeVisited) {
            minDistanceToTree.put(s, TSPUtils.distance(cities.get(currentCity), cities.get(s)));
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
                final double distanceToNext = TSPUtils.distance(cities.get(next.getKey()), cities.get(s));
                if (distanceToNext < minDistanceToTree.get(s)) {
                    minDistanceToTree.put(s, distanceToNext);
                }
            }
        }
        return cost;
    }
}
