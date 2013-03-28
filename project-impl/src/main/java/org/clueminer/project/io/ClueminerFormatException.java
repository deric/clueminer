package org.clueminer.project.io;

import org.openide.util.NbBundle;

/**
 *
 * @author Tomas Barton
 */
public class ClueminerFormatException extends RuntimeException {

    private static final long serialVersionUID = -1996307221004820062L;
    private Throwable cause;
    private String message;
    private boolean isImport = false;

    public ClueminerFormatException(Class source, Throwable cause) {
        super(cause);
        this.cause = cause;
        if (source.equals(ClueminerReader.class)) {
            isImport = true;
        }
    }

    public ClueminerFormatException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        if (this.cause == null) {
            return message;
        }
        return getLocalizedMessage();
    }

    @Override
    public String getLocalizedMessage() {
        if (this.cause == null) {
            return message;
        }

        Object[] params = new Object[4];
        params[0] = cause.getClass().getSimpleName();
        params[1] = cause.getLocalizedMessage();
        params[2] = cause.getStackTrace()[0].getClassName();
        params[3] = cause.getStackTrace()[0].getLineNumber();

        if (isImport) {
            return String.format(NbBundle.getMessage(ClueminerFormatException.class, "clueminerFormatException_import"), params);
        } else //Export
        {
            return String.format(NbBundle.getMessage(ClueminerFormatException.class, "clueminerFormatException_export"), params);
        }

    }
}
