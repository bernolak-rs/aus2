package KDTree;

import java.util.LinkedList;
import java.util.List;

public class KDTree<E> {
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

    public KDTree(int dimensions, KDComparator<? super E> comparator) {
        if (dimensions <= 0) {
            throw new IllegalArgumentException("Dimensions cant be non-positive integer");
        }

        this.dimensions = dimensions;
        this.comparator = comparator;
    }

    public void insert(List<? extends E> data) throws IllegalArgumentException {
        if (data == null) {
            throw new IllegalArgumentException("Data inserted to KDTree cannot be null");
        }

        List<E> sorted = new LinkedList<>(data);

        for (E item : sorted) {
            if (item == null)
                throw new IllegalArgumentException("Data inserted to KDTree cannot be null");
        }

        sorted.sort((a, b) -> this.compareToSort(a, b, 0));

        E previous = null;
        for (E item : sorted) {
            if (previous != null && this.compareToSort(previous, item, 0) == 0) {
                throw new IllegalArgumentException("Keys cannot be duplicated");
            }
            previous = item;
        }

        KDNode newRoot = this.buildBalanced(sorted);

        this.root = newRoot;
        this.size = sorted.size();
    }

    private int compareToSort(E first, E second, int dimension) {
        for (int offset = 0; offset < this.dimensions; offset++) {
            int axis = (dimension + offset) % this.dimensions;
            int result = this.comparator.compare(first, second, axis);

            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    private KDNode buildBalanced(List<E> data) {
        if (data.isEmpty()) {
            return null;
        }

        KDNode newRoot = new KDNode(null);

        List<KDNode> nodes = new LinkedList<>();
        List<int[]> ranges = new LinkedList<>();

        nodes.add(newRoot);
        ranges.add(new int[] {0, data.size(), 0});

        while (!nodes.isEmpty()) {
            KDNode node = nodes.removeFirst();
            int[] range = ranges.removeFirst();

            int from = range[0];
            int to = range[1];
            int depth = range[2];
            int dimension = depth % this.dimensions;

            data.subList(from, to).sort(
                (a, b) -> this.compareToSort(a, b, dimension)
            );
            
            int middle = from + (to - from) / 2;
            node.data = data.get(middle);

            if (from < middle) {
                node.left = new KDNode(null);

                nodes.add(node.left);
                ranges.add(new int[] {from, middle, depth + 1});
            }

            if (middle + 1 < to) {
                node.right = new KDNode(null);

                nodes.add(node.right);
                ranges.add(new int[] {middle + 1, to, depth + 1});
            }
        }

        return newRoot;
    }

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
            if (this.contains(data, curr.data)) { 
                throw new IllegalArgumentException("Keys cannot be duplicated");
            }
            
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

    private boolean contains(E first, E second) {
        for (int i = 0; i < this.dimensions; i++) {
            if (this.comparator.compare(first, second, i) != 0)
                return false;
        }
        return true;
    }

    //TODO
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

        for (int i = 0; i < nodes.size(); i++) {
            KDNode current = nodes.get(i);
            int axis = axes.get(i);
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

    // TODO
    private boolean isInRange(E object, E min, E max) {
        for (int dimension = 0; dimension < this.dimensions; dimension++) {
            if (this.comparator.compare(object, min, dimension) < 0
                    || this.comparator.compare(object, max, dimension) > 0) {
                return false;
            }
        }

        return true;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

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
