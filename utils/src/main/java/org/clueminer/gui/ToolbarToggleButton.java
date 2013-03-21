package org.clueminer.gui;

import java.awt.Insets;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

/**
 *
 * @author Tomas Barton
 */
public class ToolbarToggleButton extends JToggleButton {

    private static final long serialVersionUID = -7777962861410809512L;

    public static ToolbarToggleButton getButton(Action action) {
        return new ToolbarToggleButton(action);
    }

    public ToolbarToggleButton(Action action) {
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
        this.setText("");
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