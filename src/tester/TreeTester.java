package tester;

import KDTree.KDTree;
import QuadTree.Bounds;
import QuadTree.CoordinateOps;
import QuadTree.QuadTree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public final class TreeTester {
    private record Point(int x, int y) {
    }

    public record Result(String name, long seed, int size, Throwable error) {
    }

    public List<Result> run(long seed, int seedCount, int pointCount, int coordinateLimit,
                            int checkEvery, int maxDepth, String tree) throws IllegalArgumentException {
        if (seedCount < 1) {
            throw new IllegalArgumentException("Pocet seedov musi byt aspon 1");
        }
        if (seed > Long.MAX_VALUE - (seedCount - 1L)) {
            throw new IllegalArgumentException("Posledny seed presahuje rozsah typu long");
        }

        List<Result> results = new LinkedList<>();
        for (int i = 0; i < seedCount; i++) {
            results.addAll(this.run(seed + i, pointCount, coordinateLimit, checkEvery, maxDepth, tree));
        }
        return results;
    }

    public List<Result> run(long seed, int pointCount, int coordinateLimit,
                            int checkEvery, int maxDepth, String tree) {
        if (pointCount > coordinateLimit) {
            throw new IllegalArgumentException("Pocet bodov size presahuje range");
        }

        List<Point> data = TreeTester.generatePoints(pointCount, coordinateLimit, seed);
        List<Result> results = new LinkedList<>();

        if (tree.equals("kd") || tree.equals("both")) {
            results.add(this.runKdTest(data, checkEvery, seed));
        }
        if (tree.equals("quad") || tree.equals("both")) {
            results.add(this.runQuadTest(data, coordinateLimit, maxDepth, checkEvery, seed));
        }
        return results;
    }

    private Result runKdTest(List<Point> data, int checkEvery, long seed) {
        KDTree<Point> kdTree = new KDTree<>(2, (first, second, axis) ->
                Integer.compare(axis == 0 ? first.x() : first.y(),
                        axis == 0 ? second.x() : second.y()));

        return this.runTest("KDTree", new KDTreeAdapter<>(kdTree), data, checkEvery, seed);
    }

    private Result runQuadTest(List<Point> data, int coordinateLimit, int maxDepth, int checkEvery, long seed) {
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

        return this.runTest("QuadTree", new QuadTreeAdapter<>(quadTree), data, checkEvery, seed);
    }

    private Result runTest(
            String name, StructureAdapter<Point> structure, List<Point> data, int checkEvery, long seed) {
        try {
            new Tester<Point>().run(structure, data, checkEvery);
            return new Result(name, seed, structure.size(), null);
        } catch (RuntimeException | AssertionError error) {
            return new Result(name, seed, structure.size(), error);
        }
    }

    private static List<Point> generatePoints(int count, int coordinateLimit, long seed) {
        if (coordinateLimit <= 0 || count < 0
                || count > (long) coordinateLimit * coordinateLimit) {
            throw new IllegalArgumentException("Invalid point count or coordinate limit");
        }

        Random random = new Random(seed);
        List<Point> points = new LinkedList<>();
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
