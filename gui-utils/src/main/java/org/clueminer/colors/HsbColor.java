package org.clueminer.colors;

import java.awt.Color;

/**
 *
 * @author Tomas Barton
 */
public class HsbColor {

    private int hue;
    private int saturation;
    private int brightness;

    public HsbColor(int saturation, int brightness) {
        this.saturation = saturation;
        this.brightness = brightness;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public Color toColor() {
        return new Color(Color.HSBtoRGB(hue / 255.0f, saturation / 255.0f, brightness / 255.0f));
    }
}
