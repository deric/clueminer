package org.clueminer.chart.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import org.clueminer.chart.api.AxisProperties;
import org.clueminer.gui.StrokeGenerator;

/**
 *
 * @author Tomas Barton
 */
public class AxisPropertiesImpl implements AxisProperties {

    private double majorTick = 6;
    private double padding = 5;
    private boolean logarithmic = false;
    private Color color = new Color(0x2e3436);
    private Font font = new Font("Dialog", Font.PLAIN, 12);
    private int strokeIndex = 0;
    private Color fontColor = new Color(0x2e3436);

    @Override
    public int getStrokeIndex() {
        return strokeIndex;
    }

    @Override
    public void setStrokeIndex(int i) {
        this.strokeIndex = i;
    }

    @Override
    public Stroke getStroke() {
        return StrokeGenerator.getStroke(strokeIndex);
    }

    @Override
    public void setStroke(Stroke s) {
        this.strokeIndex = StrokeGenerator.getStrokeIndex(s);
    }

    @Override
    public boolean isLogarithmic() {
        return logarithmic == true;
    }

    @Override
    public void setLogarithmic(boolean b) {
        this.logarithmic = b;
    }

    @Override
    public double getMajorTick() {
        return majorTick;
    }

    public void setMajorTick(double tick) {
        this.majorTick = tick;
    }

    @Override
    public double getMinorTick() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getPadding() {
        return padding;
    }

    public void setPadding(double padding) {
        this.padding = padding;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }

    @Override
    public Color getFontColor() {
        return fontColor;
    }

    @Override
    public void setFontColor(Color color) {
        this.fontColor = color;
    }

}
