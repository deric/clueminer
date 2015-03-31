package org.clueminer.gui.msg;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import org.openide.NotifyDescriptor;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Tomas Barton
 */
public enum MessageType {

    PLAIN(NotifyDescriptor.PLAIN_MESSAGE, null),
    INFO(NotifyDescriptor.INFORMATION_MESSAGE, "info.png"),
    QUESTION(NotifyDescriptor.QUESTION_MESSAGE, "critical.png"),
    ERROR(NotifyDescriptor.ERROR_MESSAGE, "error.png"),
    WARNING(NotifyDescriptor.WARNING_MESSAGE, "warning.png");

    private final int notifyDescriptorType;

    private final ImageIcon icon;

    private MessageType(int notifyDescriptorType, String resourceName) {
        this.notifyDescriptorType = notifyDescriptorType;
        if (resourceName == null) {
            icon = new ImageIcon();
        } else {
            icon = ImageUtilities.loadImageIcon("org/clueminer/gui/utils/msg/" + resourceName, false);
        }
    }

    int getNotifyDescriptorType() {
        return notifyDescriptorType;
    }

    Icon getIcon() {
        return icon;
    }
}
