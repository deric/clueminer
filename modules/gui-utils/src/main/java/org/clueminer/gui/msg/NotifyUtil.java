/*
 * Copyright (C) 2011-2016 clueminer.org
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.ErrorManager;
import org.openide.awt.Notification;
import org.openide.awt.NotificationDisplayer;

/**
 *
 * @author Tomas Barton
 */
public class NotifyUtil {

    private NotifyUtil() {
    }

    /**
     * Show message with the specified type and action listener
     *
     * @param title
     * @param message
     * @param type
     * @param actionListener
     * @param clear
     */
    public static void show(String title, String message, MessageType type, ActionListener actionListener, boolean clear) {
        Notification n = (Notification) NotificationDisplayer.getDefault().notify(title, type.getIcon(), message, actionListener);
        if (clear == true) {
            n.clear();
        }
    }

    /**
     * Show message with the specified type and a default action which displays
     * the message using {@link MessageUtil} with the same message type
     *
     * @param title
     * @param message
     * @param type
     * @param clear
     */
    public static void show(String title, final String message, final MessageType type, boolean clear) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MessageUtil.show(message, type);
            }
        };

        show(title, message, type, actionListener, clear);
    }

    /**
     * Show an information notification
     *
     * @param title
     * @param message
     * @param clear
     */
    public static void info(String title, String message, boolean clear) {
        show(title, message, MessageType.INFO, clear);
    }

    /**
     * Show an error notification
     *
     * @param title
     * @param message
     * @param clear
     */
    public static void error(String title, String message, boolean clear) {
        show(title, message, MessageType.ERROR, clear);
    }

    /**
     * Show an error notification for an exception
     *
     * @param title
     * @param message
     * @param exception
     * @param clear
     */
    public static void error(String title, final String message, final Throwable exception, boolean clear) {
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ErrorManager.getDefault().notify(exception);
                //MessageUtil.showException(message, exception);
            }
        };

        show(title, message, MessageType.ERROR, actionListener, clear);
    }

    /**
     * Show an warning notification
     *
     * @param title
     * @param message
     * @param clear
     */
    public static void warn(String title, String message, boolean clear) {
        show(title, message, MessageType.WARNING, clear);
    }

    /**
     * Show an plain notification
     *
     * @param title
     * @param message
     * @param clear
     */
    public static void plain(String title, String message, boolean clear) {
        show(title, message, MessageType.PLAIN, clear);
    }

}
