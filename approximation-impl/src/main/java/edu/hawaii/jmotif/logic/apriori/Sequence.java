package edu.hawaii.jmotif.logic.apriori;

import java.util.Vector;

/**
 * Implements a sequence of tokens.
 *
 * @author Pavel Senin
 *
 */
public class Sequence {

    // payload
    private Vector<Token> sequence;

    /**
     * Constructor.
     */
    public Sequence() {
        this.sequence = new Vector<Token>();
    }

    /**
     * Constructor.
     *
     * @param seq The seed sequence.
     */
    @SuppressWarnings("unchecked")
    public Sequence(Vector<Token> seq) {
        if (null == seq) {
            this.sequence = new Vector<Token>();
        } else {
            this.sequence = (Vector<Token>) seq.clone();
        }
    }

    /**
     * Returns the length of the sequence.
     *
     * @return The sequence length.
     */
    public int length() {
        return sequence.size();
    }

    /**
     * Reports token at the index specified.
     *
     * @param index The position.
     * @return The token value.
     *
     * @throws ArrayIndexOutOfBoundsException if wrong index supplied.
     */
    public Token tokenAt(int index) throws ArrayIndexOutOfBoundsException {
        return sequence.elementAt(index);
    }

    /**
     * Test if the sequence has the prefix.
     *
     * @param prefix The prefix.
     * @return True if the sequence starts with the prefix.
     */
    public boolean startsWith(Sequence prefix) {
        if (prefix.length() > this.length()) {
            return false;
        } else {
            for (int i = 0; i < prefix.length(); i++) {
                if (!this.tokenAt(i).equals(prefix.tokenAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Test if the the sequence has the suffix.
     *
     * @param suffix The suffix.
     * @return True if the sequence ends with the suffix.
     */
    public boolean endsWith(Sequence suffix) {
        if (suffix.length() > this.length()) {
            return false;
        } else {
            for (int i = 0; i < suffix.length(); i++) {
                if (!this.tokenAt(this.length() - i - 1).equals(suffix.tokenAt(suffix.length() - i - 1))) {
                    return false;
                }
            }
            return true;
        }
    }

    /**
     * Test if the sequence contains subsequence.
     *
     * @param query The query sequence.
     * @return True if the sequence ends with the suffix.
     */
    public int indexOf(Sequence query) {
        if (null != query && query.length() > 0) {
            Token t = query.tokenAt(0);
            int tPos = this.sequence.indexOf(t);
            if (-1 == tPos || this.length() < tPos + query.length()) {
                return -1;
            } else {
                for (int i = 0; i < query.length(); i++) {
                    if (!this.sequence.elementAt(i + tPos).equals(query.tokenAt(i))) {
                        return -1;
                    }
                }
                return tPos;
            }
        }
        return -1;
    }

    /**
     * Test if the sequence contains subsequence.
     *
     * @param query The query sequence.
     * @return True if the sequence contains the sub-sequence similar to the
     * query one.
     */
    public boolean contains(Sequence query) {
        if (null != query && query.length() > 0) {
            Token t = query.tokenAt(0);
            int tPos = this.sequence.indexOf(t);
            if (-1 == tPos || this.length() < tPos + query.length()) {
                return false;
            } else {
                for (int i = 0; i < query.length(); i++) {
                    if (!this.sequence.elementAt(i + tPos).equals(query.tokenAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Appends a token to the end of the sequence.
     *
     * @param t The token to append.
     */
    public void append(Token t) {
        sequence.add(t);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + sequence.hashCode();
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Sequence)) {
            return false;
        }
        Sequence other = (Sequence) obj;
        if (sequence == null) {
            if (other.sequence != null) {
                return false;
            }
        } else if (!sequence.equals(other.sequence)) {
            return false;
        }
        return true;
    }
}
