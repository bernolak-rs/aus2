package tester;

import java.util.List;

public interface StructureAdapter<E> {
    void insert(E object);

    List<E> getAll();

    int size();
}
