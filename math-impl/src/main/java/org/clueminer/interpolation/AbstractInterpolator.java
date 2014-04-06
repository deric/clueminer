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
        int i = 0;
        double pos, last;
        if (hasData()) {
            curve = new Point2D.Double[axisX.size() * steps + 1];
            pos = axisX.get(0);
            last = axisX.get(axisX.size() - 1);
            double inc = (last - pos) / (double) (axisX.size() * steps + 1);

            int idx = search(axisX, 0, axisX.size() - 1, pos);
            int low, up;
            if (axisX.get(idx) > pos) {
                up = idx;
                low = idx - 1;
            } else {
                low = idx;
                up = idx + 1;
            }

            while (pos <= last && i < curve.length) {
                curve[i++] = new Point2D.Double(pos, value(pos, low, up));
                pos += inc;
            }
        } else {
            return new Point2D.Double[0];
        }
        return curve;
    }

    /**
     * Search for number closes to toFind
     *
     * @param a
     * @param l lower index in array to start from
     * @param h upper index limit in array
     * @param x
     * @return index of closest number in sortedArray
     */
    public static int search(NumericBox a, int l, int h, double x) {
        int low = l;
        int high = h;
        if (low < 0) {
            low = 0;
        }
        if (high >= a.size()) {
            high = a.size() - 1;
        }
        int mid;
        while (a.get(low) < x && a.get(high) >= x) {
            mid = (int) (low + Math.ceil(((x - a.get(low)) * (high - low)) / (a.get(high) - a.get(low))));
            if (a.get(mid) < x) {
                low = mid + 1;
            } else if (a.get(mid) > x) {
                high = mid - 1;
            } else {
                return mid;
            }
        }
        if (Math.abs(x - a.get(low)) < Math.abs(a.get(high) - x)) {
            return low;
        }
        return high;
    }

    @Override
    public String toString() {
        return getName();
    }
}
