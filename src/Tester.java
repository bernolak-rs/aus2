import java.util.List;

public interface Tester<E> {
    void insert(E object);

    boolean delete(E object);

    List<E> getAll();

    int size();

    default void validateStructure() {
    }
}