package org.clueminer.gui;

import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author Mathieu Bastian
 */
public class BusyUtils {

    /**
     * Creates a new <code>JXBusyLabel</code> wrapper and set it at the center
     * of <code>scrollPane</code>. When users calls
     * <code>BusyLabel.setBusy(false)</code>, the label is removed from
     * <code>scrollPanel</code> and <code>component</code> is set instead.
     *
     * @param scrollPane the scroll Panel where the label is to be put
     * @param text the text set to the newly created label
     * @param component the component to set in <code>scrollPane</code> when it
     * is not busy anymore
     * @return the newly created <code>JXBusyLabel</code> wrapper
     */
    public static BusyLabel createCenteredBusyLabel(JScrollPane scrollPane, String text, JComponent component) {
        return new BusyLabel(scrollPane, text, component);
    }

    public static class BusyLabel {

        private final JScrollPane scrollPane;
        private final JXBusyLabel busyLabel;
        private JComponent component;

        private BusyLabel(JScrollPane scrollpane, String text, JComponent component) {
            this.scrollPane = scrollpane;
            this.component = component;
            busyLabel = new JXBusyLabel(new Dimension(20, 20));
            busyLabel.setText(text);
            busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        }

        public void setBusy(boolean busy) {
            if (busy) {
                if (scrollPane != null) {
                    scrollPane.setViewportView(busyLabel);
                }

                busyLabel.setBusy(true);
            } else {
                busyLabel.setBusy(false);
                if (scrollPane != null) {
                    scrollPane.setViewportView(component);
                }
            }
        }

        public void setBusy(boolean busy, JComponent component) {
            this.component = component;
            setBusy(busy);
        }
    }
}
