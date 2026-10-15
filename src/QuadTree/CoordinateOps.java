package QuadTree;

public interface CoordinateOps<K> {
    int compare(K first, K second);

    K midpoint(K min, K max);
}
