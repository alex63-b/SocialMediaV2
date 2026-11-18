package util.paging;

/**
 * Describes the number and size of a requested result page.
 */
public class Pageable {
    private final int pageNumber;
    private final int pageSize;

    /**
     * Creates paging parameters.
     *
     * @param pageNumber the zero-based page number
     * @param pageSize   the maximum number of elements on a page
     */
    public Pageable(int pageNumber, int pageSize) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    /**
     * Returns the zero-based page number.
     *
     * @return the page number
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Returns the maximum number of elements on a page.
     *
     * @return the page size
     */
    public int getPageSize() {
        return pageSize;
    }
}
