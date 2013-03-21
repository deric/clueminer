package org.clueminer.exception;

/**
 *
 * @author Tomas Barton
 */
public class ReadingException extends Exception {

    private static final long serialVersionUID = -5921882494815980604L;
    private String message;

    public ReadingException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
