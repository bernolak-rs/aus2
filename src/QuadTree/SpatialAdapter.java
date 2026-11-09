package QuadTree;

/**
 * AI DOKUMENTACIA 2.2, 2.3
 * 
 * Defines adapter that returns boundaries of object used in Quad trees
 * @param <E> type of object stored in tree
 * @param <K> type of coordinates
 */
public interface SpatialAdapter<E, K> {
    /**
     * @param object
     * @return bounds of element object
     */
    Bounds<K> boundsOf(E object);
}
