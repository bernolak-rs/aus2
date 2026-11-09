package tester;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import external.KDTrees.plamenabrezova.KdTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AI DOKUMENTACIA 3.3 
 */
public final class PlamenabrezovaKDTreeAdapter implements StructureAdapter<Point2D> {
    private final KdTree tree;

    /**
     * AI DOKUMENTACIA 3.3 
     */
    public PlamenabrezovaKDTreeAdapter(KdTree tree) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public void insert(Point2D object) {
        this.tree.insert(object);
    }

    /**
     * AI DOKUMENTACIA 3.4
     */
    @Override
    public List<Point2D> find(Point2D min, Point2D max) {
        Objects.requireNonNull(min, "Minimum boundaries cannot be null");
        Objects.requireNonNull(max, "Maximum boundaries cannot be null");
        RectHV area = new RectHV(min.x(), min.y(), max.x(), max.y());
        List<Point2D> result = new ArrayList<>();
        if (!this.tree.isEmpty()) {
            for (Point2D point : this.tree.range(area)) {
                result.add(point);
            }
        }
        return result;
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public List<Point2D> getAll() {
        List<Point2D> result = new ArrayList<>();
        if (!this.tree.isEmpty()) {
            for (Point2D point : this.tree.range(new RectHV(0, 0, 1, 1))) {
                result.add(point);
            }
        }
        return result;
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public int size() {
        return this.tree.size();
    }
}
