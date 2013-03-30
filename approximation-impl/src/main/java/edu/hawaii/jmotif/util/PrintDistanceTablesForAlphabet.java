package edu.hawaii.jmotif.util;

import edu.hawaii.jmotif.datatype.TSException;
import edu.hawaii.jmotif.logic.math.MatrixFactory;
import edu.hawaii.jmotif.logic.sax.alphabet.Alphabet;
import edu.hawaii.jmotif.logic.sax.alphabet.NormalAlphabet;

/**
 * Helps to calculate a distance matrix for the alphabet.
 *
 * @author Pavel Senin
 *
 */
public final class PrintDistanceTablesForAlphabet {

  // the alphabet we run over
  private static Alphabet a = new NormalAlphabet();

  /**
   * Silence constructor.
   */
  private PrintDistanceTablesForAlphabet() {
    assert true;
  }

  /**
   * Main runnable method.
   *
   * @param args None accepted.
   * @throws TSException If error occurs.
   */
  public static void main(String[] args) throws TSException {

    for (int i = 2; i <= a.getMaxSize(); i++) {
      double[][] distanceMatrix = new double[i][i];
      double[] cuts = a.getCuts(i);
      for (int j = 0; j < i; j++) {
        // the min_dist for adjacent symbols are 0, so we start with j+2
        for (int k = j + 2; k < i; k++) {
          distanceMatrix[j][k] = Math.sqrt((cuts[j] - cuts[k - 1]) * (cuts[j] - cuts[k - 1]));
          // since the distance matrix is symmetric
          distanceMatrix[k][j] = distanceMatrix[j][k];
        }
      }
      String s = "Size " + i + ": \n" + MatrixFactory.toString(distanceMatrix);
      System.out.println(s);
    }
  }
}
