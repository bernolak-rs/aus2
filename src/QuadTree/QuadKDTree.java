package QuadTree;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import KDTree.KDComparator;
import KDTree.KDTree;

/**
 * Implementation of a QuadKDTree
 * Hybrid tree structure that uses KD tree on each node of a QuadTree instead of List structure
 * @param <E> type of elements stored in tree
 * @param <K> type of coordinate values
 */
public class QuadKDTree<E, K> {
    /**
     * Represents 4 sons of each Quad tree node
     */
    private enum Quadrant {
        NW,
        NE,
        SE,
        SW
    }

    /**
     * Represents node in QuadKDTree
     * Owns its boundaries, KDTree for data and its children Node references
     */
    private class QTNode {
        private final Bounds<K> bounds;
        private KDTree<E> kdTree;

        private QTNode nw;
        private QTNode ne;
        private QTNode se;
        private QTNode sw;

        private K midX;
        private K midY;

        /**
         * Creates QTNode
         * @param bounds boundaries of new node
         * @param comparator used in KDTree
         */
        QTNode(Bounds<K> bounds, KDComparator<? super E> comparator) {
            this.bounds = bounds;
            this.kdTree = new KDTree<>(2, comparator);
        }

        /**
         * @return true if node is leaf otherwise false
         */
        boolean isLeaf() {
            return this.nw == null && this.ne == null
                    && this.se == null && this.sw == null;
        }

        /**
         * @param quadrant of the requested child
         * @return reference to child at quadrant position
         */
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

    private KDComparator<? super E> comparator;

    /**
     * Creates QuadKDTree structure
     * @param bounds boundaries of tree
     * @param adapter used to get coordinates of an element
     * @param ops operations to compare coordinates and calculate midpoint
     * @param maxDepth of tree
     * @param comparator used by KDTree structure to compare coordinates
     */
    public QuadKDTree(Bounds<K> bounds, 
                    SpatialAdapter<E, K> adapter, 
                    CoordinateOps<K> ops, 
                    int maxDepth, 
                    KDComparator<? super E> comparator) {
        if (bounds == null || bounds.minX() == null || bounds.maxX() == null
                || bounds.minY() == null || bounds.maxY() == null) {
            throw new NullPointerException("Bounds and their coordinates cannot be null");
        }
        this.adapter = Objects.requireNonNull(adapter, "Spatial adapter cannot be null");
        this.ops = Objects.requireNonNull(ops, "Coordinate operations cannot be null");
        // AI DOKUMENTACIA 3.3 opravila chybajuce nastavenie komparatora
        this.comparator = Objects.requireNonNull(comparator, "Comparator cannot be null");

        if (maxDepth < 0) {
            throw new IllegalArgumentException("Maximum depth cannot be negative");
        }
        if (this.ops.compare(bounds.minX(), bounds.maxX()) >= 0
                || this.ops.compare(bounds.minY(), bounds.maxY()) >= 0) {
            throw new IllegalArgumentException("Minimum boundaries must be smaller than maximum boundaries");
        }

        this.maxDepth = maxDepth;
        this.root = new QTNode(bounds, comparator);
    }

    /**
     * Inserts a point into QuadKDTree
     * Goes down tree structure through appropriate nodes until finds empty list node or reaches max depth and puts data into KDTree
     * @param object to insert
     */
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

            if (current.kdTree.isEmpty() || depth == this.maxDepth) {
                current.kdTree.insert(object);
                this.size++;
                return;
            }

            List<E> previousObjects = current.kdTree.getAll();

            this.split(current);

            for (E previous : previousObjects) {
                Bounds<K> previousBounds = this.adapter.boundsOf(previous);

                Quadrant previousQuadrant = this.getQuadrant(
                        current,
                        previousBounds.minX(),
                        previousBounds.minY()
                );

                current.getChild(previousQuadrant).kdTree.insert(previous);
            }

            current.kdTree = null;
        }
    }

    /**
     * Finds quadrant that matches coordinates
     * @param node which quadrants are being evaluated
     * @param x coordinate of a point we are trying to insert
     * @param y coordinate of a point we are trying to insert
     * @return Quadrant that matches our coordinates in node
     */
    private Quadrant getQuadrant(QTNode node, K x, K y) {
        boolean west = this.ops.compare(x, node.midX) < 0;
        boolean north = this.ops.compare(y, node.midY) >= 0;

        if (north) {
            return west ? Quadrant.NW : Quadrant.NE;
        } else {
            return west ? Quadrant.SW : Quadrant.SE;
        }
    }

    /**
     * Splits node into 4 children using midpoint
     * @param node to split
     */
    private void split(QTNode node) {
        Bounds<K> b = node.bounds;

        node.midX = this.ops.midpoint(b.minX(), b.maxX());
        node.midY = this.ops.midpoint(b.minY(), b.maxY());

        node.nw = new QTNode(new Bounds<>(b.minX(), node.midX, node.midY, b.maxY()), this.comparator);

        node.ne = new QTNode(new Bounds<>(node.midX, b.maxX(), node.midY, b.maxY()), this.comparator);

        node.se = new QTNode(new Bounds<>(node.midX, b.maxX(), b.minY(), node.midY), this.comparator);

        node.sw = new QTNode(new Bounds<>(b.minX(), node.midX, b.minY(), node.midY), this.comparator);
    }

    /**
     * Find methods
     */

    /**
     * Finds elements in defined rectangular area, uses modified BFS search that skips nodes that don't intersect searched area
     * @param area we are searching
     * @return list of elements in area
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
            if (curr.isLeaf()) {
                for (E item : curr.kdTree.getAll()) {
                    Bounds<K> objectBounds = this.adapter.boundsOf(item);

                    if (this.intersects(objectBounds, area)) {
                        result.add(item);
                    }
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

    /**
     * Checks if 2 boundaries intersect each other
     * @param first area
     * @param second area
     * @return true if they intersect and false if they don't
     */
    private boolean intersects(Bounds<K> first, Bounds<K> second) {
        return this.ops.compare(first.minX(), second.maxX()) <= 0
            && this.ops.compare(first.maxX(), second.minX()) >= 0
            && this.ops.compare(first.minY(), second.maxY()) <= 0
            && this.ops.compare(first.maxY(), second.minY()) >= 0;
    }

    // Helper methods
    /**
     * @return all elements from tree
     */
    public List<E> getAll() {
        return this.find(this.root.bounds);
    }

    /**
     * @return size of tree
     */
    public int size() {
        return this.size;
    }

    /**
     * @return true if tree is empty otherwise returns false
     */
    public boolean isEmpty() {
        return this.size == 0;
    }
}
