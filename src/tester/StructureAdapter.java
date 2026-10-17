package tester;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public final class StructureAdapter<E> {

    private final Consumer<E> insert;
    private final Supplier<List<E>> getAll;
    private final IntSupplier size;

    public StructureAdapter(
            Consumer<E> insert,
            Supplier<List<E>> getAll,
            IntSupplier size) {
        this.insert = Objects.requireNonNull(insert, "Insert operation cannot be null");
        this.getAll = Objects.requireNonNull(getAll, "GetAll operation cannot be null");
        this.size = Objects.requireNonNull(size, "Size operation cannot be null");
    }

    public void insert(E object) {
        insert.accept(object);
    }

    public List<E> getAll() {
        return getAll.get();
    }

    public int size() {
        return size.getAsInt();
    }
}
