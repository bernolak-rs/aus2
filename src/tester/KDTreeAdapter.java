package tester;

import KDTree.KDTree;

import java.util.List;
import java.util.Objects;

public final class KDTreeAdapter<E> implements StructureAdapter<E> {
    private final KDTree<E> tree;

    public KDTreeAdapter(KDTree<E> tree) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
    }

    @Override
    public void insert(E object) {
        this.tree.insert(object);
    }

    /**
     * AI DOKUMENTACIA 3.4
     */
    @Override
    public List<E> find(E min, E max) {
        return this.tree.find(min, max);
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
