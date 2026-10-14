@FunctionalInterface
public interface SpatialAdapter<E, K> {
    Bounds<K> boundsOf(E object);
}
