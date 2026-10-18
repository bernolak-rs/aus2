package QuadTree;

public interface SpatialAdapter<E, K> {
    Bounds<K> boundsOf(E object);
}
