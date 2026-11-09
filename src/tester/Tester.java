package tester;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Generic tester for testing data structures
 * Checks if elements were correctly inserted against List structure
 * @param <E> type of element stored in structure
 */
public final class Tester<E> {
    /**
     * Inserts elements into structure and periodically checks if they match List structure
     * @param structure structure adapter to test
     * @param data list of elements to insert into structure
     * @param checkEvery period at which structure should be checked
     */
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

    /**
     * Compares structure element counts and their occurrences to list structure
     * if they don't match AssertionError is thrown
     * @param structure
     * @param expected
     * @throws AssertionError
     */
    private void check(StructureAdapter<E> structure, List<E> expected) throws AssertionError {
        if (structure.size() != expected.size()) {
            throw new AssertionError("Incorrect size: expected=" + expected.size() + ", actual=" + structure.size());
        }

        if (!this.counts(expected).equals(this.counts(structure.getAll()))) {
            throw new AssertionError("GetAll contents differ from the reference data");
        }
    }

    /**
     * Counts the occurrences in passed list
     * @param data
     * @return a HashMap of each element as a key and number of its occurrences as value
     */
    private Map<E, Integer> counts(List<E> data) {
        Map<E, Integer> counts = new HashMap<>();
        for (E object : data) {
            counts.merge(object, 1, Integer::sum);
        }
        return counts;
    }
}
