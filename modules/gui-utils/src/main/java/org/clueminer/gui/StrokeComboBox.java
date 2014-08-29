package org.clueminer.gui;

import java.awt.Dimension;
import java.awt.Stroke;
import javax.swing.JComboBox;

/**
 *
 * @author Tomas Barton
 */
public class StrokeComboBox extends JComboBox {
    private static final long serialVersionUID = -6259450920839549519L;

    public StrokeComboBox() { this(StrokeGenerator.getStrokes(), 100, 30); }

    public StrokeComboBox(Stroke[] strokes, int width, int height) {
        super(strokes);
        setRenderer(new StrokeComboBoxRenderer(width, height));
        Dimension prefSize = getPreferredSize();
        prefSize.height = height + getInsets().top + getInsets().bottom;
        setPreferredSize(prefSize);
        setMaximumRowCount(10);
    }

}
