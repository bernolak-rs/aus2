package QuadTree;

/**
 * AI DOKUMENTACIA 2.2, 2.3
 * 
 * Represents boundaries for Quad tree
 * @param minX
 * @param maxX
 * @param minY
 * @param maxY
 * @param <K>
 */
public record Bounds<K>(K minX, K maxX, K minY, K maxY) {
}
