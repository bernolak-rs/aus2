package KDTree;

/**
 * KD tree comparator used to compare 2 elements of a tree for each dimension
 * @param <E> type of object that is being compared
 */
public interface KDComparator<E> {
    /**
     * Compares 2 objects for a specified dimension
     * @param first object to compare
     * @param second object to compare
     * @param dimensions number of dimensions of a KD tree used for comparison
     * @return negative integer for first being less than second, 0 for equality and positive for first being bigger
     */
    int compare(E first, E second, int dimensions);
}
