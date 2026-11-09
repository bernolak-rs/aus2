package QuadTree;

/**
 * AI DOKUMENTACIA 2.2, 2.3
 * 
 * Interface used to define operations for Quad tree
 * @param <K>
 */
public interface CoordinateOps<K> {
    /**
     * Compares 2 values
     * @param first
     * @param second
     * @return integer value: negative if first is lesser, 0 if they equal and positive if first is greater
     */
    int compare(K first, K second);

    /**
     * Calculates midpoint between 2 coordinates
     * @param min
     * @param max
     * @return midpoint coordinate between min and max
     */
    K midpoint(K min, K max);
}
