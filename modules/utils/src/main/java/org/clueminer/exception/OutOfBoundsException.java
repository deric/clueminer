package org.clueminer.exception;

/**
 *
 * @author Tomas Barton
 */
public class OutOfBoundsException extends Exception {

    private static final long serialVersionUID = -4766356042758431023L;
    private String message;

    public OutOfBoundsException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
