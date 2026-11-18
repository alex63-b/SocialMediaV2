package util.paging;

/**
 * Represents one page of repository elements and the total element count.
 *
 * @param <E> the type of elements stored on the page
 */
public class Page<E> {

    private final Iterable<E> elementsOnPage;
    private final int totalNumberOfElements;

    /**
     * Creates a page from its elements and total result count.
     *
     * @param elementsOnPage       the elements contained on this page
     * @param totalNumberOfElements the total number of elements across all pages
     */
    public Page(Iterable<E> elementsOnPage, int totalNumberOfElements) {
        this.elementsOnPage = elementsOnPage;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    /**
     * Returns the elements contained on this page.
     *
     * @return the page elements
     */
    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    /**
     * Returns the total number of elements before pagination.
     *
     * @return the total element count
     */
    public int getTotalNumberOfElements() {

        return totalNumberOfElements;
    }
}
