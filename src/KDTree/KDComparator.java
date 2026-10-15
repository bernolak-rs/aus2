package KDTree;

public interface KDComparator<E> {
    int compare(E first, E second, int dimensions);
}
