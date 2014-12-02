package org.clueminer.clustering.api.config;

/**
 *
 * @author Tomas Barton
 */
public class ConfigException extends RuntimeException {

    /**
     * Creates a new instance of <code>ConfigurationException</code> without
     * detail message.
     */
    public ConfigException() {
    }

    /**
     * Constructs an instance of <code>ConfigurationException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ConfigException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>ConfigurationException</code> with the
     * specified detail message and cause
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
