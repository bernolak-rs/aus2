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

    private final long seed;
    private final int seedCount;
    private final int pointCount;
    private final int coordinateLimit;
    private final int checkEvery;
    private final int maxDepth;
    private final String tree;

    public TreeTester(long seed, int seedCount, int pointCount, int coordinateLimit,
                      int checkEvery, int maxDepth, String tree) {
        this.seed = seed;
        this.seedCount = seedCount;
        this.pointCount = pointCount;
        this.coordinateLimit = coordinateLimit;
        this.checkEvery = checkEvery;
        this.maxDepth = maxDepth;
        this.tree = tree;
    }

    public List<Result> run() {
        if (this.seedCount < 1) {
            throw new IllegalArgumentException("Pocet seedov musi byt aspon 1");
        }
        if (this.seed > Long.MAX_VALUE - (this.seedCount - 1L)) {
            throw new IllegalArgumentException("Posledny seed presahuje rozsah typu long");
        }

        List<Result> results = new LinkedList<>();
        for (int i = 0; i < this.seedCount; i++) {
            results.addAll(this.runForSeed(this.seed + i));
        }
        return results;
    }

    private List<Result> runForSeed(long seed) {
        if (this.pointCount > this.coordinateLimit) {
            throw new IllegalArgumentException("Pocet bodov size presahuje range");
        }

        List<Point> data = this.generatePoints(seed);
        List<Result> results = new LinkedList<>();

        if (this.tree.equals("kd") || this.tree.equals("both")) {
            results.add(this.runKdTest(data, seed));
        }
        if (this.tree.equals("quad") || this.tree.equals("both")) {
            results.add(this.runQuadTest(data, seed));
        }
        return results;
    }

    private Result runKdTest(List<Point> data, long seed) {
        KDTree<Point> kdTree = new KDTree<>(2, (first, second, axis) ->
                Integer.compare(axis == 0 ? first.x() : first.y(),
                        axis == 0 ? second.x() : second.y()));

        return this.runTest("KDTree", new KDTreeAdapter<>(kdTree), data, seed);
    }

    private Result runQuadTest(List<Point> data, long seed) {
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
                new Bounds<>(0, this.coordinateLimit - 1, 0, this.coordinateLimit - 1),
                point -> new Bounds<>(point.x(), point.x(), point.y(), point.y()),
                ops, this.maxDepth);

        return this.runTest("QuadTree", new QuadTreeAdapter<>(quadTree), data, seed);
    }

    private Result runTest(
            String name, StructureAdapter<Point> structure, List<Point> data, long seed) {
        try {
            new Tester<Point>().run(structure, data, this.checkEvery);
            return new Result(name, seed, structure.size(), null);
        } catch (RuntimeException | AssertionError error) {
            return new Result(name, seed, structure.size(), error);
        }
    }

    private List<Point> generatePoints(long seed) {
        if (this.coordinateLimit <= 0 || this.pointCount < 0
                || this.pointCount > (long) this.coordinateLimit * this.coordinateLimit) {
            throw new IllegalArgumentException("Invalid point count or coordinate limit");
        }

        Random random = new Random(seed);
        List<Point> points = new LinkedList<>();
        Set<Point> used = new HashSet<>();

        while (points.size() < this.pointCount) {
            Point point = new Point(random.nextInt(this.coordinateLimit), random.nextInt(this.coordinateLimit));
            if (used.add(point)) {
                points.add(point);
            }
        }
        return points;
    }
}
