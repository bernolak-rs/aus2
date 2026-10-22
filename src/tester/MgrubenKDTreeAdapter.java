package tester;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import external.KDTrees.mgruben.KdTree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Points must lie in the unit square [0, 1] x [0, 1]. */
public final class MgrubenKDTreeAdapter implements StructureAdapter<Point2D> {
    private final KdTree tree;

    public MgrubenKDTreeAdapter(KdTree tree) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
    }

    @Override
    public void insert(Point2D object) {
        this.tree.insert(object);
    }

    @Override
    public List<Point2D> getAll() {
        List<Point2D> result = new ArrayList<>();
        for (Point2D point : this.tree.range(new RectHV(0, 0, 1, 1))) {
            result.add(point);
        }
        return result;
    }

    @Override
    public int size() {
        return this.tree.size();
    }
}
