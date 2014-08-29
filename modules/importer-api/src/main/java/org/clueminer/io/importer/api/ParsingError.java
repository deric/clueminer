package org.clueminer.io.importer.api;

/**
 * Thrown when value can't be converted into requested type
 *
 * @author Tomas Barton
 */
public class ParsingError extends Exception {

    private static final long serialVersionUID = 5546904325453911214L;
    private final String message;

    public ParsingError(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return this.message;
    }
}
