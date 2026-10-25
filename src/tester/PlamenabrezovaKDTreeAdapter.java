package tester;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import external.KDTrees.plamenabrezova.KdTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Points must lie in the unit square [0, 1] x [0, 1]. */
public final class PlamenabrezovaKDTreeAdapter implements StructureAdapter<Point2D> {
    private final KdTree tree;

    public PlamenabrezovaKDTreeAdapter(KdTree tree) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
    }

    @Override
    public void insert(Point2D object) {
        this.tree.insert(object);
    }

    @Override
    public List<Point2D> find(Point2D min, Point2D max) {
        Objects.requireNonNull(min, "Minimum boundaries cannot be null");
        Objects.requireNonNull(max, "Maximum boundaries cannot be null");
        RectHV area = new RectHV(min.x(), min.y(), max.x(), max.y());
        List<Point2D> result = new ArrayList<>();
        // This implementation returns null for an empty tree.
        if (!this.tree.isEmpty()) {
            for (Point2D point : this.tree.range(area)) {
                result.add(point);
            }
        }
        return result;
    }

    @Override
    public List<Point2D> getAll() {
        List<Point2D> result = new ArrayList<>();
        // This implementation returns null for an empty tree.
        if (!this.tree.isEmpty()) {
            for (Point2D point : this.tree.range(new RectHV(0, 0, 1, 1))) {
                result.add(point);
            }
        }
        return result;
    }

    @Override
    public int size() {
        return this.tree.size();
    }
}
