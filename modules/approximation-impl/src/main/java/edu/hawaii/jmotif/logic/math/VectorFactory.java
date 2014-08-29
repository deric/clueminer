package edu.hawaii.jmotif.logic.math;

/**
 * Implements vector methods.
 * 
 * @author Pavel Senin
 * 
 */
public final class VectorFactory {

  /**
   * Constructor.
   */
  private VectorFactory() {
    super();
  }

  /**
   * Get the vector minimal value.
   * 
   * @param values the vector.
   * @return the vector's minimal value.
   */
  public static double getMinValue(double[] values) {
    double[] tmp_options = new double[values.length];
    int num = 0;

    for (int i = 0; i < values.length; i++) {
      if (Double.isNaN(values[i])) {
        continue;
      }
      else {
        tmp_options[num] = values[i];
        num++;
      }
    }

    double minDistance = Double.NaN;
    if (num != 0) {
      minDistance = Double.MAX_VALUE;
      for (int i = 0; i < num; i++) {
        if (tmp_options[i] < minDistance) {
          minDistance = tmp_options[i];
        }
      }
    }

    return minDistance;
  }

}
