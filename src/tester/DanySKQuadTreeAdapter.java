package tester;

import org.danilopianini.util.SpatialIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

public final class DanySKQuadTreeAdapter<E> implements StructureAdapter<E> {
    private final SpatialIndex<E> tree;
    private final ToDoubleFunction<? super E> x;
    private final ToDoubleFunction<? super E> y;

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

    @Override
    public void insert(E object) {
        Objects.requireNonNull(object, "Object cannot be null");
        this.tree.insert(object, this.x.applyAsDouble(object), this.y.applyAsDouble(object));
    }

    @Override
    public List<E> getAll() {
        return new ArrayList<>(this.tree.query(
                new double[]{-Double.MAX_VALUE, -Double.MAX_VALUE},
                new double[]{Double.MAX_VALUE, Double.MAX_VALUE}));
    }

    @Override
    public int size() {
        // SpatialIndex has no size method; count the objects returned by the tree.
        return this.getAll().size();
    }
}
