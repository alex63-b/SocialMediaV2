package util.datatypes;

import java.util.Objects;

/**
 * Stores two values of potentially different types.
 *
 * @param <K> the type of the first value
 * @param <V> the type of the second value
 */
public class Pair<K,V> {
    private final K first;
    private final V second;

    /**
     * Creates a pair from two values.
     *
     * @param first  the first value
     * @param second the second value
     */
    public Pair(K first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first value.
     *
     * @return the first value
     */
    public K getFirst() {
        return first;
    }

    /**
     * Returns the second value.
     *
     * @return the second value
     */
    public V getSecond() {
        return second;
    }

    @Override
    /**
     * Checks whether another pair contains the same two values.
     *
     * @param obj the object to compare
     * @return {@code true} if the pairs are equal; {@code false} otherwise
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof Pair<?, ?> pair)){
            return false;
        }
        return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
    }

    @Override
    /**
     * Returns a hash code derived from both values.
     *
     * @return the hash code
     */
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
