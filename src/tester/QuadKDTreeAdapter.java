package tester;

import QuadTree.QuadKDTree;

import java.util.List;
import java.util.Objects;

public final class QuadKDTreeAdapter<E, K> implements StructureAdapter<E> {
    private final QuadKDTree<E, K> tree;

    public QuadKDTreeAdapter(QuadKDTree<E, K> tree) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
    }

    @Override
    public void insert(E object) {
        this.tree.insert(object);
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
