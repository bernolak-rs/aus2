package tester;

import KDTree.KDTree;
import QuadTree.Bounds;
import QuadTree.CoordinateOps;
import QuadTree.QuadKDTree;
import QuadTree.QuadTree;
import QuadTree.SpatialAdapter;
import edu.princeton.cs.algs4.Point2D;
import external.KDTrees.morkev.a05.KdTreeST;
import external.QuadTrees.mimicat13.com.github.ryanp102694.geometry.RectangleObject;
import external.QuadTrees.mimicat13.com.github.ryanp102694.geometry.SearchRectangleObject;
import org.danilopianini.util.FlexibleQuadTree;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.index.quadtree.Quadtree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

public final class TreeTester {
    private record Point(int x, int y) {
    }

    private record DataPoint(long id, double y, double x) {
    }

    private record TestArea(long id, double min_y, double min_x, double max_y, double max_x) { }

    public record Result(String name, long seed, int size, Throwable error) {
    }

    private final long seed;
    private final int seedCount;
    private final int pointCount;
    private final int areaCount;
    private final int coordinateLimit;
    private final int checkEvery;
    private final int maxDepth;
    private final String tree;
    private final boolean useData;

    public TreeTester(long seed, int seedCount, int pointCount, int areaCount, int coordinateLimit,
                      int checkEvery, int maxDepth, String tree, boolean useData) {
        this.seed = seed;
        this.seedCount = seedCount;
        this.pointCount = pointCount;
        this.areaCount = areaCount;
        this.coordinateLimit = coordinateLimit;
        this.checkEvery = checkEvery;
        this.maxDepth = maxDepth;
        this.tree = tree;
        this.useData = useData;
    }

    public List<Result> run() {
        if (this.useData) {
            return this.runForData();
        }

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

    private List<Result> runForData() {
        try {
            return this.runForData("data/slovakia_nodes.csv", "data/testovacie_oblasti.csv");
        } catch (IOException error) {
            throw new IllegalArgumentException("Nepodarilo sa nacitat CSV: " + error.getMessage(), error);
        }
    }

    public List<Result> runForData(String pointsPath, String areasPath) throws IOException {
        List<TestArea> areas = this.loadAreasFromData(areasPath);

        System.out.println(" Areas nacitane");
        List<DataPoint> data = this.loadPointFromData(pointsPath);

        System.out.println(" Body nacitane");
        double minX = data.stream().mapToDouble(DataPoint::x).min().orElse(0.0);
        double maxX = data.stream().mapToDouble(DataPoint::x).max().orElse(1.0);
        double minY = data.stream().mapToDouble(DataPoint::y).min().orElse(0.0);
        double maxY = data.stream().mapToDouble(DataPoint::y).max().orElse(1.0);
        Bounds<Double> bounds = new Bounds<>(
                minX, minX == maxX ? minX + 1.0 : maxX,
                minY, minY == maxY ? minY + 1.0 : maxY);
        SpatialAdapter<DataPoint, Double> spatialAdapter = point ->
                new Bounds<>(point.x(), point.x(), point.y(), point.y());
        CoordinateOps<Double> ops = new CoordinateOps<>() {
            @Override
            public int compare(Double first, Double second) {
                return Double.compare(first, second);
            }

            @Override
            public Double midpoint(Double min, Double max) {
                return min + (max - min) / 2.0;
            }
        };

        List<Result> results = new LinkedList<>();
        if (this.selected("kd") || this.tree.equals("both")) {
            KDTree<DataPoint> kdTree = new KDTree<>(2, (first, second, axis) ->
                    Double.compare(axis == 0 ? first.x() : first.y(),
                            axis == 0 ? second.x() : second.y()));
            results.add(this.runDataTest("KDTree", new KDTreeAdapter<>(kdTree),
                    data, areas, Function.identity()));
        }
        if (this.selected("quad") || this.tree.equals("both")) {
            QuadTree<DataPoint, Double> quadTree = new QuadTree<>(
                    bounds, spatialAdapter, ops, this.maxDepth);
            results.add(this.runDataTest("QuadTree", new QuadTreeAdapter<>(quadTree, spatialAdapter),
                    data, areas, Function.identity()));
        }
        if (this.selected("quadkd")) {
            QuadKDTree<DataPoint, Double> quadKdTree = new QuadKDTree<>(
                    bounds, spatialAdapter, ops, this.maxDepth, (first, second, axis) ->
                    Double.compare(axis == 0 ? first.x() : first.y(),
                            axis == 0 ? second.x() : second.y()));
            results.add(this.runDataTest("QuadKDTree", new QuadKDTreeAdapter<>(quadKdTree, spatialAdapter),
                    data, areas, Function.identity()));
        }
        if (this.selected("danysk")) {
            results.add(this.runDataTest("DanySK QuadTree", new DanySKQuadTreeAdapter<>(
                    new FlexibleQuadTree<DataPoint>(), DataPoint::x, DataPoint::y),
                    data, areas, Function.identity()));
        }
        if (this.selected("jts")) {
            results.add(this.runDataTest("JTS QuadTree", new JTSQuadTreeAdapter<DataPoint>(
                    new Quadtree(), point -> new Envelope(point.x(), point.x(), point.y(), point.y())),
                    data, areas, Function.identity()));
        }
        if (this.selected("mimicat13")) {
            int extent = 1 << 30;
            var quadTree = new external.QuadTrees.mimicat13.com.github.ryanp102694.QuadTree(
                    10, this.maxDepth, 0, 0, 0, extent, extent, null);
            Function<DataPoint, RectangleObject> rectangleOf = point -> new SearchRectangleObject(
                    (point.x() - bounds.minX()) / (bounds.maxX() - bounds.minX()) * extent,
                    (point.y() - bounds.minY()) / (bounds.maxY() - bounds.minY()) * extent,
                    0.0, 0.0, Long.toString(point.id()));
            results.add(this.runDataTest("Mimicat13 QuadTree", new Mimicat13QuadTreeAdapter(quadTree),
                    data, areas, rectangleOf));
        }
        if (this.selected("morkev")) {
            results.add(this.runDataTest("Morkev KDTree", new MorkevKDTreeAdapter<>(
                    new KdTreeST<DataPoint>(), point -> new Point2D(point.x(), point.y())),
                    data, areas, Function.identity()));
        }
        Function<DataPoint, Point2D> unitPointOf = point -> new Point2D(
                (point.x() - bounds.minX()) / (bounds.maxX() - bounds.minX()),
                (point.y() - bounds.minY()) / (bounds.maxY() - bounds.minY()));
        if (this.selected("plamenabrezova")) {
            results.add(this.runDataTest("Plamenabrezova KDTree", new PlamenabrezovaKDTreeAdapter(
                    new external.KDTrees.plamenabrezova.KdTree()), data, areas, unitPointOf));
        }
        if (this.selected("mgruben")) {
            results.add(this.runDataTest("Mgruben KDTree", new MgrubenKDTreeAdapter(
                    new external.KDTrees.mgruben.KdTree()), data, areas, unitPointOf));
        }
        return results;
    }

    private <E> Result runDataTest(String name, StructureAdapter<E> structure,
                                    List<DataPoint> points, List<TestArea> areas,
                                    Function<DataPoint, E> convert) {
        try {
            List<E> objects = points.stream().map(convert).toList();
            new Tester<E>().run(structure, objects, this.checkEvery);
            // for (E object : objects) {
            //     structure.insert(object);
            // }
            System.out.println(" Body vlozene do stromu");
            this.checkAreas(structure, points, objects, areas, convert);
            return new Result(name, this.seed, structure.size(), null);
        } catch (RuntimeException | AssertionError error) {
            return new Result(name, this.seed, structure.size(), error);
        }
    }

    private <E> void checkAreas(StructureAdapter<E> structure, List<DataPoint> points,
                                List<E> objects, List<TestArea> areas,
                                Function<DataPoint, E> convert) {
        for (TestArea area : areas) {
            try {
                E min = convert.apply(new DataPoint(0L, area.min_y(), area.min_x()));
                E max = convert.apply(new DataPoint(0L, area.max_y(), area.max_x()));
                Map<E, Integer> expected = new HashMap<>();
                int expectedSize = 0;
                Iterator<E> iterator = objects.iterator();

                for (DataPoint point : points) {
                    E object = iterator.next();
                    if (point.x() >= area.min_x() && point.x() <= area.max_x()
                            && point.y() >= area.min_y() && point.y() <= area.max_y()) {
                        expected.merge(object, 1, Integer::sum);
                        expectedSize++;
                    }
                }

                List<E> found = structure.find(min, max);
                if (found == null) {
                    throw new AssertionError("Find vratil null namiesto zoznamu");
                }
                Map<E, Integer> actual = new HashMap<>();
                for (E object : found) {
                    actual.merge(object, 1, Integer::sum);
                }
                if (!expected.equals(actual)) {
                    throw new AssertionError("Nespravny vysledok find: expected="
                            + expectedSize + ", actual=" + found.size());
                }
            } catch (RuntimeException | AssertionError error) {
                throw new AssertionError("Test oblasti id=" + area.id()
                        + " zlyhal: " + error.getMessage(), error);
            }
        }
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
        SpatialAdapter<Point, Integer> spatialAdapter =
                point -> new Bounds<>(point.x(), point.x(), point.y(), point.y());
        QuadTree<Point, Integer> quadTree = new QuadTree<>(
                new Bounds<>(0, this.coordinateLimit - 1, 0, this.coordinateLimit - 1),
                spatialAdapter,
                coordinateOps(), this.maxDepth);

        return this.runTest("QuadTree", new QuadTreeAdapter<>(quadTree, spatialAdapter), data, seed);
    }

    private Result runQuadKdTest(List<Point> data, long seed) {
        SpatialAdapter<Point, Integer> spatialAdapter =
                point -> new Bounds<>(point.x(), point.x(), point.y(), point.y());
        QuadKDTree<Point, Integer> quadKdTree = new QuadKDTree<>(
                new Bounds<>(0, this.coordinateLimit - 1, 0, this.coordinateLimit - 1),
                spatialAdapter,
                coordinateOps(), this.maxDepth, (first, second, axis) ->
                        Integer.compare(axis == 0 ? first.x() : first.y(),
                                axis == 0 ? second.x() : second.y()));

        return this.runTest("QuadKDTree", new QuadKDTreeAdapter<>(quadKdTree, spatialAdapter), data, seed);
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

        while (points.size() < this.pointCount) {
            Point point = new Point(random.nextInt(this.coordinateLimit), random.nextInt(this.coordinateLimit));
            points.add(point);
        }
        return points;
    }

    private List<DataPoint> loadPointFromData(String path) throws IOException {
        List<DataPoint> points = new ArrayList<>(this.pointCount);

        try (BufferedReader reader = new BufferedReader(new FileReader(path), 64 * 1024)) {
            reader.readLine();

            String line;
            long i = this.pointCount;
            while ((line = reader.readLine()) != null && i > 0L) {
                String[] values = line.split(";");

                points.add(new DataPoint(
                    Long.parseLong(values[0]),
                    Double.parseDouble(values[1]),
                    Double.parseDouble(values[2])
                ));
                i--;
            }
        }

        return points;
    }

    private List<TestArea> loadAreasFromData(String path) throws IOException {
        List<TestArea> areas = new ArrayList<>(this.areaCount);

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            reader.readLine();

            String line;
            long i = this.areaCount;
            while ((line = reader.readLine()) != null && i > 0L) {
                String[] values = line.split(";");

                areas.add(new TestArea(
                    Long.parseLong(values[0]),
                    Double.parseDouble(values[1]),
                    Double.parseDouble(values[2]),
                    Double.parseDouble(values[3]),
                    Double.parseDouble(values[4])
                ));
                i--;
            }
        }

        return areas;
    }
}
