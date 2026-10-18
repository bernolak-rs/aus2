package tester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Tester<E> {
    public void run(StructureAdapter<E> structure, List<E> data, int checkEvery) {
        if (checkEvery <= 0) {
            throw new IllegalArgumentException("Check interval must be positive");
        }

        List<E> expected = new ArrayList<>();
        int step = 0;

        try {
            this.check(structure, expected);

            for (E object : data) {
                step++;
                structure.insert(object);
                expected.add(object);

                if (step % checkEvery == 0 || step == data.size()) {
                    this.check(structure, expected);
                }
            }
        } catch (RuntimeException | AssertionError error) {
            throw new AssertionError("Test failed at step " + step, error);
        }
    }

    private void check(StructureAdapter<E> structure, List<E> expected) throws AssertionError {
        if (structure.size() != expected.size()) {
            throw new AssertionError("Incorrect size: expected=" + expected.size() + ", actual=" + structure.size());
        }

        if (!this.counts(expected).equals(this.counts(structure.getAll()))) {
            throw new AssertionError("GetAll contents differ from the reference data");
        }
    }

    private Map<E, Integer> counts(List<E> data) {
        Map<E, Integer> counts = new HashMap<>();
        for (E object : data) {
            counts.merge(object, 1, Integer::sum);
        }
        return counts;
    }
}
