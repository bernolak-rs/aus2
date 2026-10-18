package tester;

import QuadTree.QuadTree;

import java.util.List;
import java.util.Objects;

public final class QuadTreeAdapter<E, K> implements StructureAdapter<E> {
    private final QuadTree<E, K> tree;

    public QuadTreeAdapter(QuadTree<E, K> tree) {
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
