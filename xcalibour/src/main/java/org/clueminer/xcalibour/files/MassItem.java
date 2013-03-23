package org.clueminer.xcalibour.files;

import java.io.Serializable;
import org.clueminer.dataset.row.DataItem;
import org.clueminer.math.Numeric;

/**
 *
 * @author Tomas Barton
 */
public class MassItem extends DataItem implements Serializable, Numeric {

    private static final long serialVersionUID = -3055157236651229852L;
    private long intensity;
    private double mass;
    private double total_intensity;

    public MassItem(long intensity, double mass) {
        this.intensity = intensity;
        this.mass = mass;
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

    public double getTotal_intensity() {
        return total_intensity;
    }

    public void setTotalIntensity(double total_intensity) {
        this.total_intensity = total_intensity;
    }
}
