package edu.hawaii.jmotif.logic.apriori;

import java.util.Locale;

/**
 * Implements a token used in the data mining algorithms. It converts any given
 * payload into upper case.
 *
 * @author Pavel Senin
 *
 */
public class Token implements Comparable<Token> {

    private String payload;

    /**
     * Constructor.
     *
     * @param payload The payload to set.
     */
    public Token(String payload) {
        if (null == payload) {
            this.payload = null;
        } else {
            this.payload = payload.toUpperCase(Locale.US);
        }
    }

    /**
     * Get the payload value.
     *
     * @return The payload value.
     */
    public String getPayload() {
        return this.payload;
    }

    /**
     * Set the payload value.
     *
     * @param payload The new payload value.
     */
    public void setPayload(String payload) {
        if (null == payload) {
            this.payload = null;
        } else {
            this.payload = payload.toUpperCase(Locale.US);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((payload == null) ? 0 : payload.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Token other = (Token) obj;
        if (payload == null) {
            if (other.payload != null) {
                return false;
            }
        } else if (!payload.equals(other.payload)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Token o) {
        if (o == null) {
            throw new NullPointerException("Unable to compare with a null object.");
        }
        if ((payload == null) || (o.payload == null)) {
            throw new NullPointerException("Unable to compare with a null payload.");
        }
        return payload.compareTo(o.payload);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return this.payload;
    }
}
