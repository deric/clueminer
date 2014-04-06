package org.clueminer.interpolation;

import java.util.List;
import org.clueminer.math.Interpolator;
import org.clueminer.math.Numeric;
import org.clueminer.math.NumericBox;

/**
 *
 * @author Tomas Barton
 */
public abstract class AbstractInterpolator implements Interpolator {

    protected NumericBox axisX;
    protected NumericBox axisY;

    @Override
    public void setX(double[] x) {
        axisX = new DoubleBox(x);
    }

    @Override
    public void setY(double[] y) {
        axisY = new DoubleBox(y);
    }

    @Override
    public void setX(List x) {
        axisX = new ListBox(x);
    }

    @Override
    public void setY(List y) {
        axisY = new ListBox(y);
    }

    @Override
    public void setX(Numeric[] x) {
        axisX = new NumBox(x);
    }

    @Override
    public void setY(Numeric[] y) {
        axisY = new NumBox(y);
    }

    @Override
    public void setX(Number[] x) {
        axisX = new NumberBox(x);
    }

    @Override
    public void setY(Number[] y) {
        axisY = new NumberBox(y);
    }

    @Override
    public void setX(NumericBox x) {
        axisX = x;
    }

    @Override
    public void setY(NumericBox y) {
        axisY = y;
    }

    @Override
    public boolean hasData() {
        if (axisX != null && axisY != null) {
            if (axisX.size() != axisY.size()) {
                throw new RuntimeException("axis X and Y must have same size! "
                        + "x: " + axisX.size() + ", y: " + axisY.size());
            }
            return true;
        }
        return false;
    }

    @Override
    public double value(double x) {
        if (!hasData()) {
            throw new RuntimeException("missing data for x and y, can't interpolate");
        }

        return value(x, 0, axisX.size());
    }

    @Override
    public String toString() {
        return getName();
    }
}
