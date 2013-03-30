package org.clueminer.exception;

/**
 *
 * @author Tomas Barton
 */
public class OperatorException extends Exception {
    private static final long serialVersionUID = 1L;
    private String message;

    public OperatorException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
