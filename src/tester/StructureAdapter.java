package tester;

import java.util.List;

/**
 * AI DOKUMENTACIA 3.4
 * 
 * Tree structure adapter
 * Defines operations that each tree adapter has to implement for testing
 * @param <E>
 */
public interface StructureAdapter<E> {
    void insert(E object);

    List<E> find(E min, E max);

    List<E> getAll();

    int size();
}
