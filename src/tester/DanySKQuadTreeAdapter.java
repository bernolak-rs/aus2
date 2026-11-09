package tester;

import org.danilopianini.util.SpatialIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

/**
 * AI DOKUMENTACIA 3.3
 * 
 * DanySKQuadTreeAdapter
 * @param <E>
 */
public final class DanySKQuadTreeAdapter<E> implements StructureAdapter<E> {
    private final SpatialIndex<E> tree;
    private final ToDoubleFunction<? super E> x;
    private final ToDoubleFunction<? super E> y;

    /**
     * AI DOKUMENTACIA 3.3 
     */
    public DanySKQuadTreeAdapter(SpatialIndex<E> tree,
                                 ToDoubleFunction<? super E> x,
                                 ToDoubleFunction<? super E> y) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
        this.x = Objects.requireNonNull(x, "X coordinate function cannot be null");
        this.y = Objects.requireNonNull(y, "Y coordinate function cannot be null");
        if (tree.getDimensions() != 2) {
            throw new IllegalArgumentException("Only two-dimensional trees are supported");
        }
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public void insert(E object) {
        Objects.requireNonNull(object, "Object cannot be null");
        this.tree.insert(object, this.x.applyAsDouble(object), this.y.applyAsDouble(object));
    }

    /**
     * AI DOKUMENTACIA 3.4
     */
    @Override
    public List<E> find(E min, E max) {
        Objects.requireNonNull(min, "Minimum boundaries cannot be null");
        Objects.requireNonNull(max, "Maximum boundaries cannot be null");
        double minX = this.x.applyAsDouble(min);
        double minY = this.y.applyAsDouble(min);
        double maxX = this.x.applyAsDouble(max);
        double maxY = this.y.applyAsDouble(max);
        if (minX > maxX || minY > maxY) {
            throw new IllegalArgumentException("Minimum boundaries cannot exceed maximum boundaries");
        }
        return new ArrayList<>(this.tree.query(
                new double[]{minX, minY}, new double[]{Math.nextUp(maxX), Math.nextUp(maxY)}));
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public List<E> getAll() {
        return new ArrayList<>(this.tree.query(
                new double[]{-Double.MAX_VALUE, -Double.MAX_VALUE},
                new double[]{Double.MAX_VALUE, Double.MAX_VALUE}));
    }

    /**
     * AI DOKUMENTACIA 3.3 
     */
    @Override
    public int size() {
        return this.getAll().size();
    }
}
