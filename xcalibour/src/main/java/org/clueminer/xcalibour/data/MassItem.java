package org.clueminer.xcalibour.data;

import java.io.Serializable;
import org.clueminer.math.Numeric;

/**
 * On x-axis we display time, the mass numbers might differ for each instance
 * (consists of several - 100 to 200 - MassItems)
 *
 * @author Tomas Barton
 */
public class MassItem extends Number implements Serializable, Numeric {

    private static final long serialVersionUID = -3055157236651229852L;
    /**
     *
     * shown on z-axis
     */
    private long intensity;
    /**
     * Interpolation is computed on mass value
     *
     * @TODO precision of read values is 1e-6, so storing in float might be
     * enough
     *
     * shown on y-axis
     */
    private double mass;
    /**
     * not sure what to do with this yet
     */
    private double total_intensity;

    public MassItem(long intensity, double mass) {
        this.intensity = intensity;
        this.mass = mass;
    }

    public MassItem(long intensity, double mass, double total_intensity) {
        this.intensity = intensity;
        this.mass = mass;
        this.total_intensity = total_intensity;
    }

    public long getIntensity() {
        return intensity;
    }

    public void setIntensity(long intensity) {
        this.intensity = intensity;
    }

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getTotalIntensity() {
        return total_intensity;
    }

    public void setTotalIntensity(double total_intensity) {
        this.total_intensity = total_intensity;
    }

    @Override
    public int intValue() {
        return (int) mass;
    }

    @Override
    public long longValue() {
        return (long) mass;
    }

    /**
     * Possible lost of precision
     *
     * @return mass value
     */
    @Override
    public float floatValue() {
        return (float) mass;
    }

    @Override
    public double doubleValue() {
        return mass;
    }

    @Override
    public double getValue() {
        return mass;
    }

    @Override
    public int compareTo(double d) {
        if (this.getValue() == d) {
            return 0;
        } else if (this.getValue() > d) {
            return 1;
        }
        return -1;
    }

    @Override
    public int compareTo(Numeric that) {
        if (this.getValue() == that.getValue()) {
            return 0;
        } else if (this.getValue() > that.getValue()) {
            return 1;
        }
        return -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof MassItem)) {
            return false;
        }
        MassItem that = (MassItem) obj;

        if (this.mass != that.mass || this.intensity != that.intensity) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (int) (this.intensity ^ (this.intensity >>> 32));
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.mass) ^ (Double.doubleToLongBits(this.mass) >>> 32));
        return hash;
    }
}
