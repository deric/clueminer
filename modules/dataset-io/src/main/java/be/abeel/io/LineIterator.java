package be.abeel.io;

import be.abeel.net.URIFactory;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

/**
 * A class to iterate over all lines in a file. The methods and constructors of
 * this class will not throw an IO exception. Instead, they will throw a
 * RuntimeException if something goes wrong.
 *
 * This class transparently reads GZIP files.
 *
 * The IOException can be retrieved from this RuntimeException with the method
 * <code>.getCause()</code>.
 *
 *
 * @author Thomas Abeel
 *
 */
public class LineIterator implements Iterable<String>, Iterator<String>, Closeable {

    private BufferedReader in = null;
    private String next = null;

    public LineIterator(String input, boolean skipComments, boolean skipBlanks) {
        this(stream(input), skipComments, skipBlanks);

    }

    public LineIterator(String file) {
        this(file, false, false);
    }

    public LineIterator(File f) {
        this(stream(f));

    }

    public LineIterator(File f, boolean skipComments, boolean skipBlanks) {
        this(stream(f), skipComments, skipBlanks);

    }

    private static InputStream stream(File f) {
        try {
            return new FileInputStream(f);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static InputStream stream(String input) {
        try {
            if (input.startsWith("http://")) {
                return URIFactory.url(input).openStream();
            } else {
                return stream(new File(input));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private boolean skipBlanks = false;
    private boolean skipComments = false;
    private ArrayList<String> commentIdentifiers = new ArrayList<>();

    public LineIterator(InputStream stream) {
        /*
         * Check whether stream should be wrapped for GZIP compression
         */
        this(new InputStreamReader(wrap(stream)));
    }

    private static InputStream wrap(InputStream stream) {
        try {
            stream = new PushbackInputStream(stream, 2);
            PushbackInputStream tmp = (PushbackInputStream) stream;
            int a = tmp.read();
            int b = tmp.read();
            tmp.unread(b);
            tmp.unread(a);
            if (a == 0x1f && b == 0x8b) {
                stream = new GZIPInputStream(stream);
            }
            return stream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public LineIterator(Reader reader, boolean skipComments, boolean skipBlanks) {
        try {
            in = new BufferedReader(reader);
            next = in.readLine();
            if (next == null) {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        commentIdentifiers.add("#");
        commentIdentifiers.add("//");
        if (skipComments) {
            setSkipComments(true);
        }
        if (skipBlanks) {
            setSkipBlanks(true);
        }
    }

    public LineIterator(InputStream stream, boolean skipComments, boolean skipBlanks) {
        this(new InputStreamReader(wrap(stream)), skipComments, skipBlanks);
    }

    public LineIterator(Reader irs) {
        this(irs, false, false);
    }

    @Override
    public Iterator<String> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    private void getNext() {
        try {
            next = in.readLine();
            while (next != null && ((skipBlanks && next.length() == 0) || (skipComments && isComment(next)))) {
                next = in.readLine();
            }

            if (next == null) {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isComment(String line) {
        for (String prefix : commentIdentifiers) {
            if (line.startsWith(prefix)) {
                return true;
            }

        }
        return false;
    }

    @Override
    public String next() {
        String oldNext = next;

        getNext();
        return oldNext;

    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("This operation is not possible on a LineIterator");

    }

    @Override
    public void close() {
        try {
            in.close();
        } catch (IOException e) {
            // Be silent
        }
        next = null;

    }

    /**
     * Sets whether blank lines should be skipped.
     *
     * @param skipBlanks
     */
    public final void setSkipBlanks(boolean skipBlanks) {
        this.skipBlanks = skipBlanks;
        if (next != null && this.skipBlanks && next.length() == 0) {
            getNext();
        }
    }

    /**
     * Sets whether comment lines should be skipped. Comments lines are those
     * lines that start with a commentIdentifier.
     *
     * @param skipComments
     */
    public final void setSkipComments(boolean skipComments) {
        this.skipComments = skipComments;
        if (this.skipComments && next != null && isComment(next)) {
            getNext();
        }
    }

    /**
     * Set which regular expression should be used to identify comments.
     *
     * @param commentIdentifier the regular expression identifying the comment.
     */
    public final void setCommentIdentifier(String commentIdentifier) {
        this.commentIdentifiers.clear();
        addCommentIdentifier(commentIdentifier);
    }

    /**
     * Add an extra identifier for comments
     *
     * @param commentIdentifier the regular expression identifying a comment.
     */
    public final void addCommentIdentifier(String commentIdentifier) {
        this.commentIdentifiers.add(commentIdentifier);
        if (next != null && this.skipComments && isComment(next)) {
            getNext();
        }
    }

    public String peek() {
        return next;
    }
}
