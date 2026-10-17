package tester;

import KDTree.KDTree;
import QuadTree.Bounds;
import QuadTree.CoordinateOps;
import QuadTree.QuadTree;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public final class TesterCli {
    private long seed = 42L;
    private int pointCount = 10_100;
    private int coordinateLimit = 1_000_000;
    private int checkEvery = 100;
    private int maxDepth = 20;
    private String tree = "both";

    private record Point(int x, int y) {
    }

    public void start() {
        System.out.println("Tester stromov");
        printHelp();
        printSettings();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("> ");
                System.out.flush();
                if (!scanner.hasNextLine()) {
                    return;
                }

                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                try {
                    if (!handleCommand(line)) {
                        return;
                    }
                } catch (NumberFormatException error) {
                    System.out.println("Chyba: zadaj celé číslo v povolenom číselnom rozsahu.");
                } catch (IllegalArgumentException error) {
                    System.out.println("Chyba: " + error.getMessage());
                }
            }
        }
    }

    private boolean handleCommand(String line) {
        String[] parts = line.split("\\s+");
        String command = parts[0].toLowerCase(Locale.ROOT);
        if (Set.of("run", "show", "help", "exit").contains(command) && parts.length != 1) {
            throw new IllegalArgumentException("Príkaz " + command + " nemá argumenty.");
        }

        switch (command) {
            case "seed" -> {
                String value = argument(parts);
                seed = value.equalsIgnoreCase("random")
                        ? new Random().nextLong() : Long.parseLong(value);
            }
            case "size" -> pointCount = integerArgument(parts, 0);
            case "check" -> checkEvery = integerArgument(parts, 1);
            case "range" -> coordinateLimit = integerArgument(parts, 2);
            case "depth" -> maxDepth = integerArgument(parts, 0);
            case "tree" -> {
                String value = argument(parts).toLowerCase(Locale.ROOT);
                if (!Set.of("kd", "quad", "both").contains(value)) {
                    throw new IllegalArgumentException("Vyber strom: kd, quad alebo both.");
                }
                tree = value;
            }
            case "run" -> {
                runTests();
                return true;
            }
            case "show" -> {
                printSettings();
                return true;
            }
            case "help" -> {
                printHelp();
                return true;
            }
            case "exit" -> {
                return false;
            }
            default -> throw new IllegalArgumentException("Neznámy príkaz. Použi help.");
        }

        printSettings();
        return true;
    }

    private static String argument(String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("Použitie: " + parts[0] + " <hodnota>");
        }
        return parts[1];
    }

    private static int integerArgument(String[] parts, int minimum) {
        int value = Integer.parseInt(argument(parts));
        if (value < minimum) {
            throw new IllegalArgumentException("Hodnota pre " + parts[0]
                    + " musí byť aspoň " + minimum + ".");
        }
        return value;
    }

    private void printSettings() {
        System.out.println("seed=" + seed + ", size=" + pointCount
                + ", check=" + checkEvery + ", range=" + coordinateLimit
                + ", depth=" + maxDepth + ", tree=" + tree);
    }

    private static void printHelp() {
        System.out.println("""
                run              Spusti test s aktuálnymi nastaveniami
                seed N|random    Nastav seed alebo vyber náhodný
                size N           Počet unikátnych bodov (N >= 0)
                check N          Kontrola každých N vložení (N >= 1)
                range N          Súradnice od 0 do N-1 (N >= 2)
                depth N          Maximálna hĺbka QuadTree (N >= 0)
                tree kd|quad|both Vyber testované stromy
                show             Zobraz nastavenia
                help             Zobraz príkazy
                exit             Ukonči tester
                """);
    }

    private void runTests() {
        if (pointCount > (long) coordinateLimit * coordinateLimit) {
            throw new IllegalArgumentException("Počet bodov presahuje range × range."
                    + " Zníž size alebo zväčši range.");
        }

        printSettings();
        List<Point> data = generatePoints(pointCount, coordinateLimit, seed);

        if (tree.equals("kd") || tree.equals("both")) {
            runKdTest(data);
        }
        if (tree.equals("quad") || tree.equals("both")) {
            runQuadTest(data);
        }
    }

    private void runKdTest(List<Point> data) {
        KDTree<Point> kdTree = new KDTree<>(2, (first, second, axis) ->
                Integer.compare(axis == 0 ? first.x() : first.y(),
                        axis == 0 ? second.x() : second.y()));

        runTest("KDTree", new StructureAdapter<Point>(
                kdTree::insert, kdTree::getAll, kdTree::size), data);
    }

    private void runQuadTest(List<Point> data) {
        CoordinateOps<Integer> ops = new CoordinateOps<>() {
            @Override
            public int compare(Integer first, Integer second) {
                return Integer.compare(first, second);
            }

            @Override
            public Integer midpoint(Integer min, Integer max) {
                return min + (max - min) / 2;
            }
        };

        QuadTree<Point, Integer> quadTree = new QuadTree<>(
                new Bounds<>(0, coordinateLimit - 1, 0, coordinateLimit - 1),
                point -> new Bounds<>(point.x(), point.x(), point.y(), point.y()),
                ops, maxDepth);

        runTest("QuadTree", new StructureAdapter<Point>(
                quadTree::insert, quadTree::getAll, quadTree::size), data);
    }

    private void runTest(
            String name, StructureAdapter<Point> structure, List<Point> data) {
        try {
            new Tester<Point>().run(structure, data, checkEvery);
            System.out.println(name + " OK: seed=" + seed + ", size=" + structure.size());
        } catch (RuntimeException | AssertionError error) {
            System.out.println(name + " ZLYHAL: seed=" + seed + ", " + error.getMessage());
            for (Throwable cause = error.getCause(); cause != null; cause = cause.getCause()) {
                System.out.println("  " + cause);
            }
        }
    }

    private static List<Point> generatePoints(int count, int coordinateLimit, long seed) {
        if (coordinateLimit <= 0 || count < 0
                || count > (long) coordinateLimit * coordinateLimit) {
            throw new IllegalArgumentException("Invalid point count or coordinate limit");
        }

        Random random = new Random(seed);
        List<Point> points = new ArrayList<>();
        Set<Point> used = new HashSet<>();

        while (points.size() < count) {
            Point point = new Point(random.nextInt(coordinateLimit), random.nextInt(coordinateLimit));
            if (used.add(point)) {
                points.add(point);
            }
        }
        return points;
    }
}
