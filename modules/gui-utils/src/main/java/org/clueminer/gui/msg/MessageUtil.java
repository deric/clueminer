/*
 * Copyright (C) 2011-2017 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.gui.msg;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;

/**
 *
 * @author Tomas Barton
 */
public class MessageUtil {

    private MessageUtil() {
    }

    /**
     * @return The dialog displayer used to show message boxes
     */
    public static DialogDisplayer getDialogDisplayer() {
        return DialogDisplayer.getDefault();
    }

    /**
     * Show a message of the specified type
     *
     * @param message
     * @param messageType As in {@link NotifyDescription} message type
     * constants.
     */
    public static void show(String message, MessageType messageType) {
        getDialogDisplayer().notify(new NotifyDescriptor.Message(message,
                messageType.getNotifyDescriptorType()));
    }

    /**
     * Show an exception message dialog
     *
     * @param message
     * @param exception
     */
    public static void showException(String message, Throwable exception) {
        Logger.getGlobal().log(Level.WARNING, "Exception: {0}", message);
        Exceptions.printStackTrace(exception);
        getDialogDisplayer().notify(new NotifyDescriptor.Message(message));
    }

    /**
     * Show an information dialog
     *
     * @param message
     */
    public static void info(String message) {
        show(message, MessageType.INFO);
    }

    /**
     * Show an error dialog
     *
     * @param message
     */
    public static void error(String message) {
        show(message, MessageType.ERROR);
    }

    /**
     * Show an error dialog for an exception
     *
     * @param message
     * @param exception
     */
    public static void error(String message, Throwable exception) {
        showException(message, exception);
    }

    /**
     * Show an question dialog
     *
     * @param message
     */
    public static void question(String message) {
        show(message, MessageType.QUESTION);
    }

    /**
     * Show an warning dialog
     *
     * @param message
     */
    public static void warn(String message) {
        show(message, MessageType.WARNING);
    }

    /**
     * Show an plain dialog
     *
     * @param message
     */
    public static void plain(String message) {
        show(message, MessageType.PLAIN);
    }
}
