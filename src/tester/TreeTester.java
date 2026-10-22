package tester;

import KDTree.KDTree;
import QuadTree.Bounds;
import QuadTree.CoordinateOps;
import QuadTree.QuadKDTree;
import QuadTree.QuadTree;
import edu.princeton.cs.algs4.Point2D;
import external.KDTrees.morkev.a05.KdTreeST;
import external.QuadTrees.mimicat13.com.github.ryanp102694.geometry.RectangleObject;
import external.QuadTrees.mimicat13.com.github.ryanp102694.geometry.SearchRectangleObject;
import org.danilopianini.util.FlexibleQuadTree;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.quadtree.Quadtree;

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

        if (this.selected("kd") || this.tree.equals("both")) {
            results.add(this.runKdTest(data, seed));
        }
        if (this.selected("quad") || this.tree.equals("both")) {
            results.add(this.runQuadTest(data, seed));
        }
        if (this.selected("quadkd")) {
            results.add(this.runQuadKdTest(data, seed));
        }
        if (this.selected("danysk")) {
            results.add(this.runTest("DanySK QuadTree", new DanySKQuadTreeAdapter<>(
                    new FlexibleQuadTree<Point>(), Point::x, Point::y), data, seed));
        }
        if (this.selected("jts")) {
            results.add(this.runTest("JTS QuadTree", new JTSQuadTreeAdapter<Point>(
                    new Quadtree(), point -> new Envelope(point.x(), point.x(), point.y(), point.y())),
                    data, seed));
        }
        if (this.selected("mimicat13")) {
            results.add(this.runMimicat13Test(data, seed));
        }
        if (this.selected("morkev")) {
            results.add(this.runTest("Morkev KDTree", new MorkevKDTreeAdapter<>(
                    new KdTreeST<Point>(), point -> new Point2D(point.x(), point.y())), data, seed));
        }
        if (this.selected("plamenabrezova") || this.selected("mgruben")) {
            // Both implementations partition the unit square. Scaling preserves unique points.
            double scale = this.coordinateLimit - 1.0;
            List<Point2D> unitPoints = data.stream()
                    .map(point -> new Point2D(point.x() / scale, point.y() / scale))
                    .toList();
            if (this.selected("plamenabrezova")) {
                results.add(this.runTest("Plamenabrezova KDTree", new PlamenabrezovaKDTreeAdapter(
                        new external.KDTrees.plamenabrezova.KdTree()), unitPoints, seed));
            }
            if (this.selected("mgruben")) {
                results.add(this.runTest("Mgruben KDTree", new MgrubenKDTreeAdapter(
                        new external.KDTrees.mgruben.KdTree()), unitPoints, seed));
            }
        }
        return results;
    }

    private boolean selected(String name) {
        return this.tree.equals(name) || this.tree.equals("all");
    }

    private Result runKdTest(List<Point> data, long seed) {
        KDTree<Point> kdTree = new KDTree<>(2, (first, second, axis) ->
                Integer.compare(axis == 0 ? first.x() : first.y(),
                        axis == 0 ? second.x() : second.y()));

        return this.runTest("KDTree", new KDTreeAdapter<>(kdTree), data, seed);
    }

    private Result runQuadTest(List<Point> data, long seed) {
        QuadTree<Point, Integer> quadTree = new QuadTree<>(
                new Bounds<>(0, this.coordinateLimit - 1, 0, this.coordinateLimit - 1),
                point -> new Bounds<>(point.x(), point.x(), point.y(), point.y()),
                coordinateOps(), this.maxDepth);

        return this.runTest("QuadTree", new QuadTreeAdapter<>(quadTree), data, seed);
    }

    private Result runQuadKdTest(List<Point> data, long seed) {
        QuadKDTree<Point, Integer> quadKdTree = new QuadKDTree<>(
                new Bounds<>(0, this.coordinateLimit - 1, 0, this.coordinateLimit - 1),
                point -> new Bounds<>(point.x(), point.x(), point.y(), point.y()),
                coordinateOps(), this.maxDepth, (first, second, axis) ->
                        Integer.compare(axis == 0 ? first.x() : first.y(),
                                axis == 0 ? second.x() : second.y()));

        return this.runTest("QuadKDTree", new QuadKDTreeAdapter<>(quadKdTree), data, seed);
    }

    private Result runMimicat13Test(List<Point> data, long seed) {
        var quadTree = new external.QuadTrees.mimicat13.com.github.ryanp102694.QuadTree(
                10, this.maxDepth, 0, 0, 0, this.coordinateLimit, this.coordinateLimit, null);
        List<RectangleObject> rectangles = data.stream()
                .<RectangleObject>map(point -> new SearchRectangleObject(
                        (double) point.x(), (double) point.y(), 0.0, 0.0,
                        point.x() + ":" + point.y()))
                .toList();

        return this.runTest("Mimicat13 QuadTree", new Mimicat13QuadTreeAdapter(quadTree), rectangles, seed);
    }

    private static CoordinateOps<Integer> coordinateOps() {
        return new CoordinateOps<>() {
            @Override
            public int compare(Integer first, Integer second) {
                return Integer.compare(first, second);
            }

            @Override
            public Integer midpoint(Integer min, Integer max) {
                return min + (max - min) / 2;
            }
        };
    }

    private <E> Result runTest(
            String name, StructureAdapter<E> structure, List<E> data, long seed) {
        try {
            new Tester<E>().run(structure, data, this.checkEvery);
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
        //Set<Point> used = new HashSet<>();

        while (points.size() < this.pointCount) {
            Point point = new Point(random.nextInt(this.coordinateLimit), random.nextInt(this.coordinateLimit));
            //if (used.add(point)) {
                points.add(point);
            //}
        }
        return points;
    }
}
