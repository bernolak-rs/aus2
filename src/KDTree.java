import java.util.LinkedList;

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
    private KDimension<E> kDimension;

    public KDTree(int dimensions, KDimension<E> kDimension) {
        this.dimensions = dimensions;
        this.kDimension = kDimension;
    }

    public void insert(E data) {

    }

    public LinkedList<E> find(double[] min, double[] max) {
        return new LinkedList<>();
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }
}
