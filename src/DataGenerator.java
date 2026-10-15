import java.util.Random;

public interface DataGenerator<E> {
    E generate(Random random);

    E copy(E object);
}
