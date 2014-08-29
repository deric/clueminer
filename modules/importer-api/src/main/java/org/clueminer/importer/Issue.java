package org.clueminer.importer;

/**
 * Issue are logged and classified by
 * <code>Report</code> to describe a problem encountered during import process.
 * Fill issues as <code>Exceptions</code>.
 *
 * @author Mathieu Bastian
 */
public final class Issue {

    public enum Level {

        INFO(100),
        WARNING(200),
        SEVERE(500),
        CRITICAL(1000);
        private final int levelInt;

        Level(int levelInt) {
            this.levelInt = levelInt;
        }

        public int toInteger() {
            return levelInt;
        }
    }
    private final Throwable throwable;
    private final String message;
    private final Level level;

    public Issue(Throwable throwable, Level level) {
        this.throwable = throwable;
        this.level = level;
        this.message = throwable.getMessage();
    }

    public Issue(String message, Level level, Throwable throwable) {
        this.throwable = throwable;
        this.level = level;
        this.message = message;
    }

    public Issue(String message, Level level) {
        this.message = message;
        this.level = level;
        this.throwable = null;
    }

    public String getMessage() {
        return message;
    }

    public Level getLevel() {
        return level;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
