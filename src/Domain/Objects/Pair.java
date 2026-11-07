package Domain.Objects;

import java.util.Objects;

/**
 * This class keeps a pair of objects of a generic type that is comparable, so the pairs can be easily compared and hashed.
 * @param <E> A generic parameter that is the type of the elements in the pair.
 */
public class Pair<E extends Comparable<E>> {
    private final E firstElement;
    private final E secondElement;

    /**
     * This function returns the first object in the pair(the smaller one).
     * @return An element of type E, which is the smallest object in the pair.
     */
    public E getFirstElement() {
        return firstElement;
    }

    /**
     * This function returns the second object in the pair(the greater one).
     * @return An element of type E, which is the greatest object in the pair.
     */
    public E getSecondElement() {
        return secondElement;
    }

    /**
     * This function is the constructor of the class, and it receives the two elements in the pair and keeps them
     * in a certain order, so the pair is (N1,N2), where N1 is smaller than N2.
     * @param firstElement an element of type E, the first element of the pair;
     * @param secondElement an element of type E, the second element of the pair;
     */
    public Pair(E firstElement, E secondElement){
        if(firstElement.compareTo(secondElement) <= 0) {
            this.firstElement = firstElement;
            this.secondElement = secondElement;
        }
        else{
            this.secondElement= firstElement;
            this.firstElement = secondElement;
        }
    }

    /**
     * This function returns the hash code of the Pair, which is the hashing of the two elements in the pair;
     * @return An integer, which represents the hash code of the pair.
     */
    @Override
    public int hashCode() {
        return Objects.hash(firstElement,secondElement);
    }

    /**
     * This functions checks if an object is equal to a pair.
     * @param other the reference object with which to compare.
     * @return {@code true} if the objects are equal;
     *         {@code false} otherwise;
     */
    @Override
    public boolean equals(Object other){
        if(other == null){
            return false;
        }
        if(other instanceof Pair<?> pair){
            return pair.firstElement == this.firstElement && pair.secondElement == this.secondElement;
        }
        return false;
    }
}
