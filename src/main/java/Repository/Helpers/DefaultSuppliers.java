package Repository.Helpers;

/**
 * Provides common supplier implementations used when loading repository data.
 */
public class DefaultSuppliers {
    /**
     * Supplies the numeric value zero.
     *
     * @param o ignored supplier argument
     * @return {@code 0}
     */
    static public long get0(Object o){
        return 0;
    }

    /**
     * Returns the supplied object unchanged.
     *
     * @param o the object to return
     * @return the supplied object
     */
    static public Object getIdentity(Object o){
        return o;
    }
}
