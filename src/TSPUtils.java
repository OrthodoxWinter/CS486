import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for some TSP related functions
 */
public class TSPUtils {

    /**
     * Read an input file from disk and returns a Map from city name to their coordinate
     * @param dataFile location of the input file on disk
     * @return Map from city name to their coordinate
     */
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

    /**
     * Find the nearest city to a user specified city from a search set
     * @param cities Map from city name to their coordinate
     * @param searchSet Set of cities that we'll look through
     * @param currentCity User specified city
     * @return The city in the search set that is closest to current city
     */
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

    /**
     * Calculate euclidean distance between 2 coordinates
     * @param p1 First coordinates
     * @param p2 Second coordinates
     * @return euclidean distance between coordinate p1 and p2
     */
    public static double distance(final Position p1, final Position p2) {
        if (p1 == null || p2 == null) {
            throw new IllegalStateException("NPE");
        }
        return Math.sqrt(Math.pow(Math.abs(p1.x - p2.x), 2) + Math.pow(Math.abs(p1.y - p2.y), 2));
    }
}
