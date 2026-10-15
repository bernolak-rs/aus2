package KDTree;

import java.util.ArrayList;
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

        List<E> sorted = new ArrayList<>(data);

        for (E item : sorted) {
            if (item == null)
                throw new IllegalArgumentException("Data inserted to KDTree cannot be null");
        }

        sorted.sort((a, b) -> compareToSort(a, b, 0));

        for (int i = 1; i < sorted.size(); i++) {
            if (compareToSort(sorted.get(i - 1), sorted.get(i), 0) == 0) {
                throw new IllegalArgumentException("Keys cannot be duplicated");
            }
        }

        KDNode newRoot = buildBalanced(sorted);

        root = newRoot;
        size = sorted.size();
    }

    private int compareToSort(E first, E second, int dimension) {
        for (int offset = 0; offset < dimensions; offset++) {
            int axis = (dimension + offset) % dimensions;
            int result = comparator.compare(first, second, axis);

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

        List<KDNode> nodes = new ArrayList<>();
        List<int[]> ranges = new ArrayList<>();

        nodes.add(newRoot);
        ranges.add(new int[] {0, data.size(), 0});

        for (int i = 0; i < nodes.size(); i++) {
            KDNode node = nodes.get(i);
            int[] range = ranges.get(i);

            int from = range[0];
            int to = range[1];
            int depth = range[2];
            int dimension = depth % dimensions;

            data.subList(from, to).sort(
                (a, b) -> compareToSort(a, b, dimension)
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

        if (root == null) {
            root = new KDNode(data);
            size++;
            return;
        }

        KDNode curr = root;
        int depth = 0;

        while (true) {
            if (this.contains(data, curr.data)) { throw new IllegalArgumentException("Keys cannot be duplicated"); }
            
            int dimension = depth % dimensions;
            int result = comparator.compare(data, curr.data, dimension);
            
            boolean goLeft = result <= 0;
            KDNode next = goLeft ? curr.left : curr.right;

            if (next == null) {
                KDNode node = new KDNode(data);

                if (goLeft) {
                    curr.left = node;
                } else {
                    curr.right = node;
                }
                size++;
                return;
            }

            curr = next;
            depth++;
        }
    }

    private boolean contains(E first, E second) {
        for (int i = 0; i < dimensions; i++) {
            if (comparator.compare(first, second, i) != 0)
                return false;
        }
        return true;
    }

    public List<E> find(E min, E max) {
        return new LinkedList<>();
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
}
