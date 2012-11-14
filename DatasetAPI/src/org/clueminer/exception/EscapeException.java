package org.clueminer.exception;

/**
 *
 * @author Tomas Barton
 */
public class EscapeException extends Exception {
    private static final long serialVersionUID = 1L;
    private String message;

    public EscapeException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
