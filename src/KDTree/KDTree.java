package KDTree;

import java.util.LinkedList;
import java.util.List;

/**
 * AI DOKUMENTACIA 2.1
 * 
 * KD Tree implementation
 * @param <E> type of object inserted into a tree
 */
public class KDTree<E> {
    /**
     * KDNode represents one node in KDTree
     */
    private class KDNode {
        E data;
        KDNode left;
        KDNode right;

        KDNode(E data) {
            this.data = data;
        }
    }

    private KDNode root;
    private final int dimensions;
    private int size;
    private KDComparator<? super E> comparator;

    /**
     * Creates empty tree
     * @param dimensions number of dimensions in a tree
     * @param comparator comparator used to compare nodes for each dimension
     */
    public KDTree(int dimensions, KDComparator<? super E> comparator) {
        if (dimensions <= 0) {
            throw new IllegalArgumentException("Dimensions cant be non-positive integer");
        }

        this.dimensions = dimensions;
        this.comparator = comparator;
    }

    /**
     * AI DOKUMENTACIA 2.1
     * 
     * Inserts element into KD tree, alternates dimensions for each level
     * @param data to insert
     * @throws IllegalArgumentException
     */
    public void insert(E data) throws IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("Data inserted to KDTree cannot be null");
        }

        if (this.root == null) {
            this.root = new KDNode(data);
            this.size++;
            return;
        }

        KDNode curr = this.root;
        int depth = 0;

        while (true) {
            int dimension = depth % this.dimensions;
            int result = this.comparator.compare(data, curr.data, dimension);

            boolean goLeft = result <= 0;
            KDNode next = goLeft ? curr.left : curr.right;

            if (next == null) {
                KDNode node = new KDNode(data);

                if (goLeft) {
                    curr.left = node;
                } else {
                    curr.right = node;
                }
                this.size++;
                return;
            }

            curr = next;
            depth++;
        }
    }

    /**
     * Return element that are within bounds
     * Using modified BFS and ignores irrelevant subtrees
     * @param min represents minimum point for valid result
     * @param max represents maximum point for valid result
     * @return
     */
    public List<E> find(E min, E max) {
        if (min == null || max == null) {
            throw new NullPointerException("Search boundaries cannot be null");
        }

        for (int dimension = 0; dimension < this.dimensions; dimension++) {
            if (this.comparator.compare(min, max, dimension) > 0) {
                throw new IllegalArgumentException(
                        "Minimum boundaries cannot exceed maximum boundaries");
            }
        }

        List<E> result = new LinkedList<>();

        if (this.root == null) {
            return result;
        }

        List<KDNode> nodes = new LinkedList<>();
        List<Integer> axes = new LinkedList<>();

        nodes.add(this.root);
        axes.add(0);

        while (!nodes.isEmpty()) {
            KDNode current = nodes.removeFirst();
            int axis = axes.removeFirst();
            int nextAxis = (axis + 1) % this.dimensions;

            if (this.isInRange(current.data, min, max)) {
                result.add(current.data);
            }

            if (current.left != null
                    && this.comparator.compare(min, current.data, axis) <= 0) {
                nodes.add(current.left);
                axes.add(nextAxis);
            }

            if (current.right != null
                    && this.comparator.compare(max, current.data, axis) >= 0) {
                nodes.add(current.right);
                axes.add(nextAxis);
            }
        }
        return result;
    }

    /**
     * Checks wheter object is within boundries
     * @param object to check
     * @param min minimal boundry
     * @param max maximal boundry
     * @return true if lies within range, otherwise returns false
     */
    private boolean isInRange(E object, E min, E max) {
        for (int dimension = 0; dimension < this.dimensions; dimension++) {
            if (this.comparator.compare(object, min, dimension) < 0
                    || this.comparator.compare(object, max, dimension) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return size of a tree
     */
    public int size() {
        return this.size;
    }

    /**
     * @return true if tree is empty otherwise returns false
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    /**
     * Helper function for testing
     * Returns all elements using BFS
     */
    public List<E> getAll() {
        List<E> result = new LinkedList<>();

        if (this.root == null) {
            return result;
        }

        List<KDNode> nodes = new LinkedList<>();
        nodes.add(this.root);

        while (!nodes.isEmpty()) {
            KDNode current = nodes.removeFirst();
            result.add(current.data);

            if (current.left != null) {
                nodes.add(current.left);
            }
            if (current.right != null) {
                nodes.add(current.right);
            }
        }

        return result;
    }
}
