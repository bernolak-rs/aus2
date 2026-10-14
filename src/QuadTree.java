import java.util.LinkedList;
import java.util.Objects;

public class QuadTree<E, K> {
    private enum Quadrant {
        NW,
        NE,
        SE,
        SW
    }

    private class QTNode {
        private final Bounds<K> bounds;
        private final LinkedList<E> list = new LinkedList<>();

        private QTNode nw;
        private QTNode ne;
        private QTNode se;
        private QTNode sw;

        private K midX;
        private K midY;

        QTNode(Bounds<K> bounds) {
            this.bounds = bounds;
        }

        boolean isLeaf() {
            return nw == null && ne == null
                    && se == null && sw == null;
        }

        QTNode getChild(Quadrant quadrant) {
            return switch (quadrant) {
                case NW -> nw;
                case NE -> ne;
                case SE -> se;
                case SW -> sw;
            };
        }
    }

    private final SpatialAdapter<E, K> adapter;
    private final CoordinateOps<K> ops;
    private final int maxDepth;

    private final QTNode root;
    private int size;

    public QuadTree(Bounds<K> bounds, SpatialAdapter<E, K> adapter, CoordinateOps<K> ops, int maxDepth) {
        if (bounds == null || bounds.minX() == null || bounds.maxX() == null
                || bounds.minY() == null || bounds.maxY() == null) {
            throw new NullPointerException("Bounds and their coordinates cannot be null");
        }
        this.adapter = Objects.requireNonNull(adapter, "Spatial adapter cannot be null");
        this.ops = Objects.requireNonNull(ops, "Coordinate operations cannot be null");

        if (maxDepth < 0) {
            throw new IllegalArgumentException("Maximum depth cannot be negative");
        }
        if (ops.compare(bounds.minX(), bounds.maxX()) >= 0
                || ops.compare(bounds.minY(), bounds.maxY()) >= 0) {
            throw new IllegalArgumentException("Minimum boundaries must be smaller than maximum boundaries");
        }

        this.maxDepth = maxDepth;
        this.root = new QTNode(bounds);
    }

    public void insert(E object) {
        if (object == null) {
            throw new NullPointerException("Object cannot be null");
        }
        Bounds<K> bounds = adapter.boundsOf(object);

        if (bounds == null || bounds.minX() == null || bounds.maxX() == null
                || bounds.minY() == null || bounds.maxY() == null) {
            throw new NullPointerException("Bounds and their coordinates cannot be null");
        }

        if (ops.compare(bounds.minX(), bounds.maxX()) != 0
                || ops.compare(bounds.minY(), bounds.maxY()) != 0) {
            throw new IllegalArgumentException("Only points are supported");
        }

        K x = bounds.minX();
        K y = bounds.minY();

        Bounds<K> space = root.bounds;

        if (ops.compare(x, space.minX()) < 0
                || ops.compare(x, space.maxX()) > 0
                || ops.compare(y, space.minY()) < 0
                || ops.compare(y, space.maxY()) > 0) {
            throw new IllegalArgumentException("Point is outside tree bounds");
        }

        QTNode current = root;
        int depth = 0;

        while (true) {
            if (!current.isLeaf()) {
                Quadrant quadrant = getQuadrant(current, x, y);
                current = current.getChild(quadrant);
                depth++;
                continue;
            }

            if (current.list.isEmpty() || depth == maxDepth) {
                current.list.add(object);
                size++;
                return;
            }

            E previous = current.list.getFirst();
            Bounds<K> previousBounds = adapter.boundsOf(previous);

            split(current);

            Quadrant previousQuadrant = getQuadrant(
                    current, previousBounds.minX(), previousBounds.minY());

            current.getChild(previousQuadrant).list.add(previous);
            current.list.removeFirst();
        }
    }
    
    private Quadrant getQuadrant(QTNode node, K x, K y) {
        boolean east = ops.compare(x, node.midX) >= 0;
        boolean north = ops.compare(y, node.midY) >= 0;

        if (north) {
            return east ? Quadrant.NE : Quadrant.NW;
        }

        return east ? Quadrant.SE : Quadrant.SW;
    }

    private void split(QTNode node) {
        Bounds<K> b = node.bounds;

        node.midX = ops.midpoint(b.minX(), b.maxX());
        node.midY = ops.midpoint(b.minY(), b.maxY());

        node.nw = new QTNode(new Bounds<>(
                b.minX(), node.midX, node.midY, b.maxY()));

        node.ne = new QTNode(new Bounds<>(
                node.midX, b.maxX(), node.midY, b.maxY()));

        node.se = new QTNode(new Bounds<>(
                node.midX, b.maxX(), b.minY(), node.midY));

        node.sw = new QTNode(new Bounds<>(
                b.minX(), node.midX, b.minY(), node.midY));
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
}
