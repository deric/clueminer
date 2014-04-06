package org.clueminer.interpolation;

import java.awt.geom.Point2D;
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
        changedX();
    }

    @Override
    public void setY(double[] y) {
        axisY = new DoubleBox(y);
        changedY();
    }

    @Override
    public void setX(List x) {
        axisX = new ListBox(x);
        changedX();
    }

    @Override
    public void setY(List y) {
        axisY = new ListBox(y);
        changedY();
    }

    @Override
    public void setX(Numeric[] x) {
        axisX = new NumBox(x);
        changedX();
    }

    @Override
    public void setY(Numeric[] y) {
        axisY = new NumBox(y);
        changedY();
    }

    @Override
    public void setX(Number[] x) {
        axisX = new NumberBox(x);
        changedX();
    }

    @Override
    public void setY(Number[] y) {
        axisY = new NumberBox(y);
        changedY();
    }

    @Override
    public void setX(NumericBox x) {
        axisX = x;
        changedX();
    }

    @Override
    public void setY(NumericBox y) {
        axisY = y;
        changedY();
    }

    @Override
    public boolean hasData() {
        if (axisX != null && axisY != null) {
            if (axisX.size() != axisY.size()) {
                throw new RuntimeException("axis X and Y must have same size! "
                        + "x: " + axisX.size() + ", y: " + axisY.size());
            }
            if (axisX.size() >= 2) {
                return true;
            }
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

    /**
     * Pre-generate set of interpolated points
     *
     * @param steps number of points between two points
     * @return
     */
    @Override
    public Point2D.Double[] curvePoints(int steps) {
        Point2D.Double[] curve;
        double u;
        int i = 0;
        double pos, last;
        if (hasData()) {
            curve = new Point2D.Double[axisX.size() * steps + 1];
            pos = axisX.get(0);
            last = axisX.get(axisX.size() - 1);
            double inc = (last - pos) / (double) (axisX.size() * steps + 1);
            while (pos <= last && i < curve.length) {
                curve[i++] = new Point2D.Double(pos, value(pos));
                pos += inc;
            }
        } else {
            return new Point2D.Double[0];
        }
        return curve;
    }

    @Override
    public String toString() {
        return getName();
    }
}
