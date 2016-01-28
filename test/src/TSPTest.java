import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        cities.put("A", new Position(94, 43));
        cities.put("B", new Position(12, 44));
        cities.put("F", new Position(7, 25));
        cities.put("D", new Position(23, 36));
        cities.put("J", new Position(81, 15));
        final Set<String> unvisited = new HashSet<>(cities.keySet());
        unvisited.remove("B");
        unvisited.remove("A");
        Assert.assertTrue(TSP.computeSpanningCost(cities, unvisited, "B") == 14);
    }

    @Test
    public void testGeneratedCityData() {
        final URL testData = this.getClass().getClassLoader().getResource("randTSP/16/instance_1.txt");
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
    public void testAgainstBruteForce() throws InterruptedException {
        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                final String path = "randTSP/" + i + "/instance_" + j +".txt";
                System.out.println("Testing File " + path);
                final URL testData = this.getClass().getClassLoader().getResource(path);
                if (testData != null) {
                    final Map<String, Position> cities = TSPUtils.readCities(testData.getPath());
                    final List<String> smartPath = TSPSearch.search(testData.getPath());
                    final double totalCostSmart = totalCost(cities, smartPath);
                    System.out.println("Total Cost: " + totalCostSmart);
                    System.out.println("--------------------------------------------------");
                    final List<String> bruteForcePath = TSPBruteForce.bruteForce(testData.getPath());
                    final double totalCostBrute = totalCost(cities, bruteForcePath);
                    System.out.println("Total Cost: " + totalCostBrute);
                    System.out.println();
                    Assert.assertTrue(totalCostSmart == totalCostBrute);
                    final int numCities = cities.keySet().size();
                    Assert.assertTrue(smartPath.size() == (numCities == 1? 1 : numCities + 1));
                }
            }
        }
    }

    @Test
    public void testBruteForce() {
        final URL testData = this.getClass().getClassLoader().getResource("randTSP/4/instance_1.txt");
        if (testData != null) {
            final Map<String, Position> cities = TSP.readCities(testData.getPath());
            final List<String> bruteForcePath = TSPBruteForce.bruteForce(testData.getPath());
            final double totalCostBrute = totalCost(cities, bruteForcePath);
            System.out.println("Total Cost: " + totalCostBrute);
        }
    }

    @Test
    public void generateAdjacencyMatrix() {
        final URL testData = this.getClass().getClassLoader().getResource("filesToTest.txt");
        if (testData != null) {
            generateAdjacencyMatrix(testData.getPath());
        }
    }

    @Test
    public void testBruteForceTSP() {
        final URL testData = this.getClass().getClassLoader().getResource("filesToTest.txt");
        if (testData != null) {
            TSPBruteForce.bruteForce(testData.getPath());
        }
    }

    private double totalCost(final Map<String, Position> cities, final List<String> path) {
        double cost = 0;
        for (int i = 1; i < path.size(); i++) {
            cost += TSP.distance(cities.get(path.get(i-1)), cities.get(path.get(i)));
        }
        return cost;
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
