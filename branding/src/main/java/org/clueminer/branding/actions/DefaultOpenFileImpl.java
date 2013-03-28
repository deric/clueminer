package org.clueminer.branding.actions;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import javax.swing.Action;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.actions.FileSystemAction;
import org.openide.actions.ToolsAction;
import org.openide.awt.StatusDisplayer;
import org.openide.cookies.EditCookie;
import org.openide.cookies.OpenCookie;
import org.openide.cookies.ViewCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.nodes.NodeOperation;
import org.openide.util.ContextAwareAction;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;
import static java.util.logging.Level.FINER;
import static java.util.logging.Level.FINEST;
import javax.swing.SwingUtilities;
import org.clueminer.openfile.OpenFileImpl;
//import static org.openide.cookies.EditorCookie.Observable.PROP_OPENED_PANES;

/**
 * Opens files when requested. Main functionality.
 *
 * @author Jaroslav Tulach, Jesse Glick, Marian Petras, David Konecny
 */
@org.openide.util.lookup.ServiceProvider(service = org.clueminer.openfile.OpenFileImpl.class, position = 100)
public class DefaultOpenFileImpl implements OpenFileImpl, Runnable {

    private final Logger log = Logger.getLogger(getClass().getName());
    /**
     * parameter of this
     * <code>Runnable</code> - file to open
     */
    private final FileObject fileObject;

    /**
     * Creates an instance of this class. It is used only as an instance of
     * <code>Runnable</code> used for rescheduling to the AWT thread. The
     * arguments are stored to local variables and when the
     * <code>run()</code> method gets executed (in the AWT thread), they are
     * passed to the
     * <code>open(...)</code> method.
     *
     * @param file file to open (must exist)
     * @param waiter double-callback or <code>null</code>
     */
    private DefaultOpenFileImpl(FileObject fileObject) {
        this.fileObject = fileObject;
    }

    /**
     * Creates a new instance of OpenFileImpl
     */
    public DefaultOpenFileImpl() {

        /* These fields are not used in the default instance. */
        this.fileObject = null;
    }

    /**
     * Sets the specified text into the status line.
     *
     * @param text text to be displayed
     */
    protected final void setStatusLine(String text) {
        StatusDisplayer.getDefault().setStatusText(text);
    }

    /**
     * Displays a dialog that the file cannot be open. This method is to be used
     * in cases that the file was open via the Open File Server. The message
     * also informs that the launcher will be notified as if the file was closed
     * immediately.
     *
     * @param fileName name of file that could not be opened
     */
    protected void notifyCannotOpen(String fileName) {
        assert EventQueue.isDispatchThread();

        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                NbBundle.getMessage(DefaultOpenFileImpl.class,
                "MSG_cannotOpenWillClose", //NOI18N
                fileName)));
    }

    /**
     * Activates the specified cookie, thus opening a file. The file is
     * specified by the cookie, because the cookie was obtained from it. The
     * cookie must be one of
     * <code>EditorCookie</code>
     * <code>OpenCookie</code>,
     * <code>EditCookie</code>,
     * <code>ViewCookie</code>.
     *
     * @param cookie cookie to activate
     * @param cookieClass type of the cookie - specifies action to activate
     * @return  <code>true</code> if the cookie was successfully activated,
     * <code>false</code> if some error occured
     * @exception java.lang.IllegalArgumentException if <code>cookieClass</code>
     * is not any of <code>EditorCookie</code>, <code>OpenCookie</code>,
     * <code>ViewCookie</code>
     * @exception java.lang.ClassCastException if the <code>cookie</code> is not
     * an instance of the specified cookie class
     */
    protected boolean openByCookie(Node.Cookie cookie,
            Class cookieClass) {
        assert EventQueue.isDispatchThread();

        if (cookieClass == OpenCookie.class) {
            ((OpenCookie) cookie).open();
        } else if (cookieClass == EditCookie.class) {
            ((EditCookie) cookie).edit();
        } else if (cookieClass == ViewCookie.class) {
            ((ViewCookie) cookie).view();
        } else {
            throw new IllegalArgumentException();
        }
        return true;
    }

    /**
     * Tries to open the specified file, using one of
     * <code>EditorCookie</code>,
     * <code>OpenCookie</code>,
     * <code>EditCookie</code>,
     * <code>ViewCookie</code> (in the same order). If the client of the open
     * file server wants, waits until the file is closed and notifies the
     * client.
     *
     * @param dataObject  <code>DataObject</code> representing the file
     * @return  <code>true</code> if the file was successfully open,
     * <code>false</code> otherwise
     */
    private boolean openDataObjectByCookie(DataObject dataObject) {

        Class<? extends Node.Cookie> cookieClass;
        Node.Cookie cookie;
        if ((cookie = dataObject.getCookie(cookieClass = OpenCookie.class)) != null
                || (cookie = dataObject.getCookie(cookieClass = EditCookie.class)) != null
                || (cookie = dataObject.getCookie(cookieClass = ViewCookie.class)) != null) {
            return openByCookie(cookie, cookieClass);
        }
        return false;
    }

    /**
     * This method is called when it is rescheduled to the AWT thread. (from a
     * different thread). It is always run in the AWT thread.
     */
    @Override
    public void run() {
        assert EventQueue.isDispatchThread();

        open(fileObject);
    }

    /**
     * Opens the
     * <code>FileObject</code> either by calling {@link EditorCookie} (or
     * {@link OpenCookie} or {@link ViewCookie}), or by showing it in the
     * Explorer.
     */
    @Override
    public boolean open(final FileObject fileObject) {
        if (log.isLoggable(FINER)) {
            log.log(FINER, "open({0}, line={1}) called from thread {2}",//NOI18N
                    new Object[]{fileObject.getNameExt(),
                        Thread.currentThread().getName()});
        }
        if (!EventQueue.isDispatchThread()) {
            log.finest(" - rescheduling to EDT using invokeLater(...)");//NOI18N
            EventQueue.invokeLater(
                    new DefaultOpenFileImpl(fileObject));
            return true;
        }


        assert EventQueue.isDispatchThread();
        log.finest(" - yes, it is an EDT");                             //NOI18N

        String fileName = fileObject.getNameExt();

        /* Find a DataObject for the FileObject: */
        final DataObject dataObject;
        try {
            dataObject = DataObject.find(fileObject);
        } catch (DataObjectNotFoundException ex) {
            ErrorManager.getDefault().notify(ex);
            return false;
        }

        /* try to open the object using the default action */
        Node dataNode = dataObject.getNodeDelegate();
        Action action = dataNode.getPreferredAction();
        if ((action != null)
                && !(action instanceof FileSystemAction)
                && !(action instanceof ToolsAction)) {
            if (log.isLoggable(FINEST)) {
                log.log(FINEST, " - using preferred action " //NOI18N
                        + "(\"{0}\" - {1}) for opening the file", //NOI18N
                        new Object[]{action.getValue(Action.NAME),
                            action.getClass().getName()});
            }

            if (action instanceof ContextAwareAction) {
                action = ((ContextAwareAction) action)
                        .createContextAwareInstance(dataNode.getLookup());
                if (log.isLoggable(FINEST)) {
                    log.finest("    - it is a ContextAwareAction");     //NOI18N
                    log.log(FINEST, "    - using a context-aware " //NOI18N
                            + "instance instead (\"{0}\" - {1})", //NOI18N
                            new Object[]{action.getValue(Action.NAME),
                                action.getClass().getName()});
                }
            }

            log.finest("   - will call action.actionPerformed(...)");   //NOI18N
            final Action a = action;
            final Node n = dataNode;
            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
                @Override
                public void run() {
                    a.actionPerformed(new ActionEvent(n, 0, ""));
                }
            });

            return true;
        }

        /* Try to grab an editor/open/edit/view cookie and open the object: */
        StatusDisplayer.getDefault().setStatusText(
                NbBundle.getMessage(DefaultOpenFileImpl.class,
                "MSG_opening", //NOI18N
                fileName));
        boolean success = openDataObjectByCookie(dataObject);
        if (success) {
            return true;
        }
        if (fileObject.isFolder() || FileUtil.isArchiveFile(fileObject)) {
            // select it in explorer:
            final Node node = dataObject.getNodeDelegate();
            if (node != null) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        NodeOperation.getDefault().explore(node);
                    }
                });


              /*  WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
                    @Override
                    public void run() {
                        NodeOperation.getDefault().explore(node);
                    }
                });*/
                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}
