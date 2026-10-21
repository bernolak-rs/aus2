package QuadTree;

import java.util.LinkedList;
import java.util.List;
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
            return this.nw == null && this.ne == null
                    && this.se == null && this.sw == null;
        }

        QTNode getChild(Quadrant quadrant) {
            return switch (quadrant) {
                case NW -> this.nw;
                case NE -> this.ne;
                case SE -> this.se;
                case SW -> this.sw;
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
        if (this.ops.compare(bounds.minX(), bounds.maxX()) >= 0
                || this.ops.compare(bounds.minY(), bounds.maxY()) >= 0) {
            throw new IllegalArgumentException("Minimum boundaries must be smaller than maximum boundaries");
        }

        this.maxDepth = maxDepth;
        this.root = new QTNode(bounds);
    }

    public void insert(E object) {
        if (object == null) {
            throw new NullPointerException("Object cannot be null");
        }
        Bounds<K> bounds = this.adapter.boundsOf(object);

        if (bounds == null || bounds.minX() == null || bounds.maxX() == null
                || bounds.minY() == null || bounds.maxY() == null) {
            throw new NullPointerException("Bounds and their coordinates cannot be null");
        }

        if (this.ops.compare(bounds.minX(), bounds.maxX()) != 0
                || this.ops.compare(bounds.minY(), bounds.maxY()) != 0) {
            throw new IllegalArgumentException("Only points are supported");
        }

        K x = bounds.minX();
        K y = bounds.minY();

        Bounds<K> space = this.root.bounds;

        if (this.ops.compare(x, space.minX()) < 0
                || this.ops.compare(x, space.maxX()) > 0
                || this.ops.compare(y, space.minY()) < 0
                || this.ops.compare(y, space.maxY()) > 0) {
            throw new IllegalArgumentException("Point is outside tree bounds");
        }

        QTNode current = this.root;
        int depth = 0;

        while (true) {
            if (!current.isLeaf()) {
                Quadrant quadrant = this.getQuadrant(current, x, y);
                current = current.getChild(quadrant);
                depth++;
                continue;
            }

            if (current.list.isEmpty() || depth == this.maxDepth) {
                current.list.add(object);
                this.size++;
                return;
            }

            E previous = current.list.getFirst();
            Bounds<K> previousBounds = this.adapter.boundsOf(previous);

            this.split(current);

            Quadrant previousQuadrant = this.getQuadrant(
                    current, previousBounds.minX(), previousBounds.minY());

            current.getChild(previousQuadrant).list.add(previous);
            current.list.removeFirst();
        }
    }

    private Quadrant getQuadrant(QTNode node, K x, K y) {
        boolean west = this.ops.compare(x, node.midX) < 0;
        boolean north = this.ops.compare(y, node.midY) >= 0;

        if (north) {
            return west ? Quadrant.NW : Quadrant.NE;
        } else {
            return west ? Quadrant.SW : Quadrant.SE;
        }
    }

    private void split(QTNode node) {
        Bounds<K> b = node.bounds;

        node.midX = this.ops.midpoint(b.minX(), b.maxX());
        node.midY = this.ops.midpoint(b.minY(), b.maxY());

        node.nw = new QTNode(new Bounds<>(b.minX(), node.midX, node.midY, b.maxY()));

        node.ne = new QTNode(new Bounds<>(node.midX, b.maxX(), node.midY, b.maxY()));

        node.se = new QTNode(new Bounds<>(node.midX, b.maxX(), b.minY(), node.midY));

        node.sw = new QTNode(new Bounds<>(b.minX(), node.midX, b.minY(), node.midY));
    }

    /**
     * Find metody
     */

    public List<E> find(Bounds<K> area) {
        if (area == null || area.minX() == null || area.maxX() == null
                || area.minY() == null || area.maxY() == null) {
            throw new NullPointerException("Search bounds and their coordinates cannot be null");
        }

        if (this.ops.compare(area.minX(), area.maxX()) > 0
                || this.ops.compare(area.minY(), area.maxY()) > 0) {
            throw new IllegalArgumentException(
                    "Minimum boundaries cannot exceed maximum boundaries");
        }

        List<E> result = new LinkedList<>();
        LinkedList<QTNode> nodes = new LinkedList<>();
        nodes.add(this.root);

        while (!nodes.isEmpty()) {
            QTNode curr = nodes.removeFirst();

            if (!this.intersects(curr.bounds, area)) {
                continue;
            }

            for (E item : curr.list) {
                Bounds<K> objectBounds = this.adapter.boundsOf(item);

                if (this.intersects(objectBounds, area)) {
                    result.add(item);
                }
            }

            if (curr.nw != null) {
                nodes.add(curr.nw);
            }
            if (curr.ne != null) {
                nodes.add(curr.ne);
            }
            if (curr.se != null) {
                nodes.add(curr.se);
            }
            if (curr.sw != null) {
                nodes.add(curr.sw);
            }
        }
        return result;
    }

    private boolean intersects(Bounds<K> first, Bounds<K> second) {
        return this.ops.compare(first.minX(), second.maxX()) <= 0
            && this.ops.compare(first.maxX(), second.minX()) >= 0
            && this.ops.compare(first.minY(), second.maxY()) <= 0
            && this.ops.compare(first.maxY(), second.minY()) >= 0;
    }

    /**
     * Pomocne metody
     */
    public List<E> getAll() {
        return this.find(this.root.bounds);
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
}
