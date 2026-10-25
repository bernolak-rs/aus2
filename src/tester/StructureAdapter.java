package tester;

import java.util.List;

public interface StructureAdapter<E> {
    void insert(E object);

    List<E> find(E min, E max);

    List<E> getAll();

    int size();
}
