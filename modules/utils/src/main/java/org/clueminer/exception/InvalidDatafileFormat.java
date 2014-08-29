package org.clueminer.exception;

/**
 *
 * @author Tomas Barton
 */
public class InvalidDatafileFormat extends Exception {

    private static final long serialVersionUID = -7315071204775959347L;
    private String message;

    public InvalidDatafileFormat(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
