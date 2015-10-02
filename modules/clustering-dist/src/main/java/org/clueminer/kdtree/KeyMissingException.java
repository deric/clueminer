// Key-size mismatch exception supporting KDTree class
package org.clueminer.kdtree;

public class KeyMissingException extends KDException {

    private static final long serialVersionUID = 3041350871482971387L;

    public KeyMissingException() {
        super("Key not found");
    }

}
