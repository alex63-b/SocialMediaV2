package Repository.Helpers;

/**
 * Provides common predicate implementations used by repositories.
 */
public final class DefaultFilters{
    /**
     * Accepts every element.
     *
     * @param element the element to test
     * @return always {@code true}
     */
    public static boolean filterTrue(Object element) {
        return true;
    }

    /**
     * Rejects every element.
     *
     * @param element the element to test
     * @return always {@code false}
     */
    public static boolean filterFalse(Object element) {
        return false;
    }

    /**
     * Checks whether a text line contains at least one character.
     *
     * @param line the text line to test
     * @return {@code true} if the line is not empty
     */
    public static boolean filterNotEmptyLines(String line){
        return !line.isEmpty();
    }
}
