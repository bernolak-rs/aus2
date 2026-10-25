package tester;

import external.QuadTrees.mimicat13.com.github.ryanp102694.QuadTree;
import external.QuadTrees.mimicat13.com.github.ryanp102694.geometry.RectangleObject;
import external.QuadTrees.mimicat13.com.github.ryanp102694.geometry.SearchRectangleObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class Mimicat13QuadTreeAdapter implements StructureAdapter<RectangleObject> {
    private final QuadTree tree;

    public Mimicat13QuadTreeAdapter(QuadTree tree) {
        this.tree = Objects.requireNonNull(tree, "Tree cannot be null");
    }

    @Override
    public void insert(RectangleObject object) {
        this.tree.insert(Objects.requireNonNull(object, "Object cannot be null"));
    }

    @Override
    public List<RectangleObject> find(RectangleObject min, RectangleObject max) {
        Objects.requireNonNull(min, "Minimum boundaries cannot be null");
        Objects.requireNonNull(max, "Maximum boundaries cannot be null");
        if (min.getX() > max.getX() || min.getY() > max.getY()) {
            throw new IllegalArgumentException("Minimum boundaries cannot exceed maximum boundaries");
        }
        return this.tree.search(new SearchRectangleObject(
                min.getX(), min.getY(), max.getX() - min.getX(), max.getY() - min.getY()));
    }

    @Override
    public List<RectangleObject> getAll() {
        List<RectangleObject> result = new ArrayList<>();
        LinkedList<QuadTree> nodes = new LinkedList<>();
        nodes.add(this.tree);

        while (!nodes.isEmpty()) {
            QuadTree current = nodes.removeFirst();
            result.addAll(current.getRectangleObjects());
            for (QuadTree child : current.getChildren()) {
                if (child != null) {
                    nodes.add(child);
                }
            }
        }
        return result;
    }

    @Override
    public int size() {
        return this.tree.getTotalObjects();
    }
}
