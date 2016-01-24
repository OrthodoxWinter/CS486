import javafx.geometry.Pos;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class TSPTest {

    @Test
    public void testEncludeanDistance() {
        final Position p1 = new Position(0, 0);
        final Position p2 = new Position(3 ,4);
        Assert.assertTrue(TSP.distance(p1, p2) == 5.0);
    }

    @Test
    public void testFindNearest() {
        final Map<String, Position> cities = new HashMap<>();
        cities.put("A", new Position(0, 0));
        cities.put("B", new Position(3, 1));
        cities.put("C", new Position(4, 4));
        cities.put("D", new Position(1, 0));
        cities.put("E", new Position(8, 8));
        Assert.assertTrue(TSP.findNearest(cities, cities.keySet(), "A").equals("D"));
    }

    @Test
    public void testSpanningCost() {
        final Map<String, Position> cities = new HashMap<>();
        cities.put("A", new Position(86, 95));
        cities.put("B", new Position(88, 62));
        cities.put("C", new Position(94, 81));
        cities.put("D", new Position(19, 9));
        cities.put("E", new Position(19, 52));
        final Set<String> unvisited = new HashSet<>(cities.keySet());
        unvisited.remove("A");
        Assert.assertTrue(TSP.computeSpanningCost(cities, unvisited, "A") == 14);
    }

    @Test
    public void testGeneratedCityData() throws URISyntaxException {
        final URL testData = this.getClass().getClassLoader().getResource("16Cities.txt");
        if (testData != null) {
            final Map<String, Position> cities = TSP.readCities(testData.getPath());
            final long startTime = System.currentTimeMillis();
            final List<String> path = TSP.search(cities, "A");
            final long totalTime = System.currentTimeMillis() - startTime;
            System.out.println("" + totalTime);
            path.forEach(System.out::println);
            Assert.assertTrue(TSP.search(cities, "A").equals(Collections.singletonList("A")));
        }
    }

    @Test
    public void generateAdjacencyMatrix() {
        final URL testData = this.getClass().getClassLoader().getResource("16Cities.txt");
        if (testData != null) {
            generateAdjacencyMatrix(testData.getPath());
        }
    }

    private List<Position> readCities(final String dataFile) {
        final List<Position> cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(dataFile))) {
            final int numCities = Integer.valueOf(br.readLine());
            for (int i = 0; i < numCities; i++) {
                final String line = br.readLine();
                final String[] tokens = line.split("\\s");
                cities.add(new Position(Integer.valueOf(tokens[1]), Integer.valueOf(tokens[2])));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }

    private void generateAdjacencyMatrix(final String dataFile) {
        final List<Position> cities = readCities(dataFile);
        cities.forEach(a -> {
            cities.forEach(b -> {
                final double distance = TSP.distance(a, b);
                final long roundedDist = Math.round(distance*1000);
                System.out.print("" + roundedDist + " ");
            });
            System.out.println();
        });
    }
}
