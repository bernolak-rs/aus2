package tester;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import external.KDTrees.morkev.a05.KdTreeST;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * AI DOKUMENTACIA 3.3 
 */
public final class MorkevKDTreeAdapter<E> implements StructureAdapter<E> {
    private final KdTreeST<E> tree;
    private final Function<? super E, ? extends Point2D> pointOf;

    /**
     * AI DOKUMENTACIA 3.3 
     */
    public MorkevKDTreeAdapter(KdTreeST<E> tree,
                               Function<? super E, ? extends Point2D> pointOf) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
        this.pointOf = Objects.requireNonNull(pointOf, "Point function cannot be null");
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public void insert(E object) {
        Objects.requireNonNull(object, "Object cannot be null");
        this.tree.put(this.pointOf.apply(object), object);
    }

    /**
     * AI DOKUMENTACIA 3.4
     */
    @Override
    public List<E> find(E min, E max) {
        Point2D lower = this.pointOf.apply(Objects.requireNonNull(min, "Minimum boundaries cannot be null"));
        Point2D upper = this.pointOf.apply(Objects.requireNonNull(max, "Maximum boundaries cannot be null"));
        RectHV area = new RectHV(lower.x(), lower.y(), upper.x(), upper.y());
        List<E> result = new ArrayList<>();
        for (Point2D point : this.tree.range(area)) {
            result.add(this.tree.get(point));
        }
        return result;
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public List<E> getAll() {
        List<E> result = new ArrayList<>();
        for (Point2D point : this.tree.points()) {
            result.add(this.tree.get(point));
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
