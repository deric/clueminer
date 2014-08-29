package org.clueminer.gui;

import java.awt.Insets;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 *
 * @author Tomas Barton
 */
public class ToolbarButton extends JButton {

    private static final long serialVersionUID = -6983942848067934704L;

    public static ToolbarButton getButton(Action action) {
        return new ToolbarButton(action);
    }

    public ToolbarButton(Action action) {
        super(action);

        setVerticalAlignment(SwingConstants.TOP);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);

        setMargin(new Insets(6, 6, 6, 6));
        setBorderPainted(false);
    }

    public void toggleLabel(boolean show) {
        if (show) {
            showText();
        } else {
            hideText();
        }
    }

    public void hideText() {
        setText("");
    }

    public void showText() {
        setText((String) getAction().getValue(Action.NAME));
    }

    public void toggleIcon(boolean small) {
        if (small) {
            showSmallIcon();
        } else {
            showBigIcon();
        }
    }

    public void showSmallIcon() {
        setIcon((ImageIcon) getAction().getValue(Action.SMALL_ICON));
    }

    public void showBigIcon() {
        setIcon((ImageIcon) getAction().getValue(Action.LARGE_ICON_KEY));
    }
}