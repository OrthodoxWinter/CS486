import java.util.*;

/**
 * Created by bowen on 27/01/16.
 */
public class TSPSearch {

    public static void main(final String[] args) {
        search(args[0]);
    }

    /**
     * Performs A* search to solve the TSP problem with the given input file
     * @param file location of input file on disk
     * @return The optimal path the salesman should take
     */
    public static List<String> search(final String file) {
        final Map<String, Position> cities = TSPUtils.readCities(file);
        final List<String> path = search(cities, "A");
        //print the optimal path
        path.forEach(System.out::println);
        return path;
    }

    /**
     * Performs A* search to solve the TSP problem with the given cities coordinates and start city
     * @param cities Map from city name to coordinates
     * @param startCity Start city
     * @return The optimal path the salesman should take
     */
    public static List<String> search(final Map<String, Position> cities, final String startCity) {
        //counter for number of nodes generated
        int numGenerated = 0;
        //check for trivial case where there is only one city
        if (cities.size() == 1) {
            return Collections.singletonList(startCity);
        }
        //initialize a PriorityQueue for storing the states we generate
        //priority is based on the lowest (distance so far to current city + heuristic for current city)
        final PriorityQueue<State> queue = new PriorityQueue<>(
                cities.size(),
                (Comparator<State>) (s1, s2) -> (s1.totalCost()).compareTo(s2.totalCost())
        );
        //keep track of visited cities. initialized to only being the start city
        final List<String> visited = Collections.singletonList(startCity);
        //expand start city
        queue.add(new State(startCity, visited, 0, TSPHeuristics.computeHeuristic(cities, new HashSet<>(visited), startCity, startCity)));
        //Loop until queue is empty. We should never hit the point where the queue is empty unless something went terribly wrong
        while (!queue.isEmpty()) {
            //pop state with the top priority off the queue in order to expand it
            final State expand = queue.poll();
            //check if it is a goal state. If so, return path taken
            if (expand.heuristic == 0) {
                System.out.println("Total generate nodes: " + numGenerated);
                return expand.path;
            }
            //if state is not goal state, then we expand it by computing the list of cities that we still need
            //to visit in alphabetical order. This set is basically the list of successors/possible moves.
            final List<String> successorCities = new ArrayList<>(cities.keySet());
            successorCities.removeAll(expand.path);
            successorCities.sort(String::compareTo);
            //if we visited all the cities, then the only successor is to return to start city
            if (successorCities.isEmpty()) {
                successorCities.add(startCity);
            }
            //for each successor we have, we add it onto the priority queue
            for (final String s : successorCities) {
                if (!s.equals(expand.currentCity)) {
                    //increment our counter
                    numGenerated++;
                    //update the path by appending each successor city to the path we have taken to get
                    //to the node that we expanded
                    final List<String> newPath = new ArrayList<>(expand.path);
                    newPath.add(s);
                    //compute the heuristic of each of the successor city
                    final double heuristic = TSPHeuristics.computeHeuristic(cities, new HashSet<>(newPath), s, startCity);
                    //enqueue the new state
                    queue.add(new State(s, newPath, expand.costToCurrent + TSPUtils.distance(cities.get(expand.currentCity), cities.get(s)), heuristic));
                }
            }
        }
        throw new IllegalStateException("Exhausted all nodes with no solution found! How is this possible?");
    }

    /**
     * Represents states that is to be added to the queue during the A* search
     */
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
