package tester;

import QuadTree.Bounds;
import QuadTree.QuadKDTree;
import QuadTree.SpatialAdapter;

import java.util.List;
import java.util.Objects;

public final class QuadKDTreeAdapter<E, K> implements StructureAdapter<E> {
    private final QuadKDTree<E, K> tree;
    private final SpatialAdapter<E, K> spatialAdapter;

    public QuadKDTreeAdapter(QuadKDTree<E, K> tree, SpatialAdapter<E, K> spatialAdapter) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
        this.spatialAdapter = Objects.requireNonNull(spatialAdapter, "Spatial adapter cannot be null");
    }

    @Override
    public void insert(E object) {
        this.tree.insert(object);
    }

    @Override
    public List<E> find(E min, E max) {
        Bounds<K> lower = Objects.requireNonNull(this.spatialAdapter.boundsOf(
                Objects.requireNonNull(min, "Minimum boundaries cannot be null")), "Bounds cannot be null");
        Bounds<K> upper = Objects.requireNonNull(this.spatialAdapter.boundsOf(
                Objects.requireNonNull(max, "Maximum boundaries cannot be null")), "Bounds cannot be null");
        return this.tree.find(new Bounds<>(lower.minX(), upper.maxX(), lower.minY(), upper.maxY()));
    }

    @Override
    public List<E> getAll() {
        return this.tree.getAll();
    }

    @Override
    public int size() {
        return this.tree.size();
    }
}
