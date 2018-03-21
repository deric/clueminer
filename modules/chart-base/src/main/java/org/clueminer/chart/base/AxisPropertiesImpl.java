/*
 * Copyright (C) 2011-2018 clueminer.org
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

    protected double majorTick = 6;
    protected double padding = 5;
    protected boolean logarithmic = false;
    protected Color color = new Color(0x2e3436);
    protected Font font = new Font("Dialog", Font.PLAIN, 12);
    protected int strokeIndex = 0;
    protected Color fontColor = new Color(0x2e3436);

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
