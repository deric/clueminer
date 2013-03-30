package edu.hawaii.jmotif.datatype;

import edu.hawaii.jmotif.logic.math.MatrixFactory;
import edu.hawaii.jmotif.logic.sax.alphabet.Alphabet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Implements some auxillary methods for the timeseries manipulation.
 *
 * @author Pavel Senin
 *
 */
public final class TSUtils {

  /** The alphabet. */
  static final char[] ALPHABET = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
      'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

  /**
   * Constructor.
   */
  private TSUtils() {
    super();
  }

  /**
   * Reads test timeseries from the file.
   *
   * @param filename the file to read from.
   * @param sizeLimit the amount of values to read.
   * @return the timeseries.
   * @throws NumberFormatException if error occurs.
   * @throws IOException if error occurs.
   * @throws TSException if error occurs.
   */
  public static Timeseries readTS(String filename, int sizeLimit) throws NumberFormatException,
      IOException, TSException {
    BufferedReader br = new BufferedReader(new FileReader(new File(filename)));
    String line = null;
    double[] values = new double[sizeLimit];
    long[] tstamps = new long[sizeLimit];
    int i = 0;
    while ((line = br.readLine()) != null) {
      values[i] = Double.valueOf(line);
      tstamps[i] = (long) i;
      i++;
    }
    br.close();
    return new Timeseries(values, tstamps);
  }

  /**
   * Compute the mean of the timeseries.
   *
   * @param series The input timeseries.
   * @return the mean of values.
   */
  public static double mean(Timeseries series) {
    double res = 0D;
    int count = 0;
    for (TPoint tp : series) {
      if (Double.isNaN(tp.value()) || Double.isInfinite(tp.value())) {
        continue;
      }
      else {
        res += tp.value();
        count += 1;
      }
    }
    if (count > 0) {
      return res / ((Integer) count).doubleValue();
    }
    return Double.NaN;
  }

  /**
   * Compute the mean of the timeseries.
   *
   * @param series The input timeseries.
   * @return the mean of values.
   */
  public static double mean(double[] series) {
    double res = 0D;
    int count = 0;
    for (double tp : series) {
      if (Double.isNaN(tp) || Double.isInfinite(tp)) {
        continue;
      }
      else {
        res += tp;
        count += 1;
      }
    }
    if (count > 0) {
      return res / ((Integer) count).doubleValue();
    }
    return Double.NaN;
  }

  /**
   * Compute the variance of the timeseries.
   *
   * @param series The input timeseries.
   * @return the variance of values.
   */
  public static double var(Timeseries series) {
    double res = 0D;
    double mean = mean(series);
    if (Double.isNaN(mean) || Double.isInfinite(mean)) {
      return Double.NaN;
    }
    int count = 0;
    for (TPoint tp : series) {
      if (Double.isNaN(tp.value()) || Double.isInfinite(tp.value())) {
        continue;
      }
      else {
        res += (tp.value() - mean) * (tp.value() - mean);
        count += 1;
      }
    }
    if (count > 0) {
      return res / ((Integer) (count - 1)).doubleValue();
    }
    return Double.NaN;
  }

  /**
   * Compute the variance of the timeseries.
   *
   * @param series The input timeseries.
   * @return the variance of values.
   */
  public static double var(double[] series) {
    double res = 0D;
    double mean = mean(series);
    if (Double.isNaN(mean) || Double.isInfinite(mean)) {
      return Double.NaN;
    }
    int count = 0;
    for (double tp : series) {
      if (Double.isNaN(tp) || Double.isInfinite(tp)) {
        continue;
      }
      else {
        res += (tp - mean) * (tp - mean);
        count += 1;
      }
    }
    if (count > 0) {
      return res / ((Integer) (count - 1)).doubleValue();
    }
    return Double.NaN;
  }

  /**
   * Compute the standard deviation of the timeseries.
   *
   * @param series The input timeseries.
   * @return the standard deviation of values.
   */
  public static double stDev(Timeseries series) {
    double num0 = 0D;
    double sum = 0D;
    int count = 0;
    for (TPoint tp : series) {
      if (Double.isNaN(tp.value()) || Double.isInfinite(tp.value())) {
        continue;
      }
      else {
        num0 = num0 + tp.value() * tp.value();
        sum = sum + tp.value();
        count += 1;
      }
    }
    if (count > 0) {
      double len = ((Integer) count).doubleValue();
      return Math.sqrt((len * num0 - sum * sum) / (len * (len - 1)));
    }
    return Double.NaN;
  }

  /**
   * Compute the standard deviation of the timeseries.
   *
   * @param series The input timeseries.
   * @return the standard deviation of values.
   */
  public static double stDev(double[] series) {
    double num0 = 0D;
    double sum = 0D;
    int count = 0;
    for (double tp : series) {
      if (Double.isNaN(tp) || Double.isInfinite(tp)) {
        continue;
      }
      else {
        num0 = num0 + tp * tp;
        sum = sum + tp;
        count += 1;
      }
    }
    if (count > 0) {
      double len = ((Integer) count).doubleValue();
      return Math.sqrt((len * num0 - sum * sum) / (len * (len - 1)));
    }
    return Double.NaN;
  }

  /**
   * Z-Normalize timeseries to the mean ~0 and standard deviation ~1.
   *
   * @param series The input timeseries.
   * @return the normalized time-series.
   * @throws TSException if error occurs.
   */
  public static Timeseries normalize(Timeseries series) throws TSException {

    // get values and timestamps out of there
    //
    double[] res = new double[series.size()];
    long[] tstamps = new long[series.size()];

    // get mean and sdev, NaN's will be removed.
    //
    double mean = mean(series);
    double sd = stDev(series);

    // check if we hit special case, where something got NaN
    //
    if (Double.isInfinite(mean) || Double.isNaN(mean)
        || Double.isInfinite(sd) || Double.isNaN(sd)) {
      //
      // case[1] single value within the timeseries, normalize this value
      // to 1, whatever
      int nanNum = countNaN(series);
      if ((series.size() - nanNum) == 1) {
        for (int i = 0; i < res.length; i++) {
          if (Double.isInfinite(series.elementAt(i).value())
              || Double.isNaN(series.elementAt(i).value())) {
            res[i] = series.elementAt(i).value();
          }
          else {
            res[i] = 1.0D;
          }
          tstamps[i] = series.elementAt(i).tstamp();
        }
      }
      //
      // case[2] all values are NaN's
      else if (series.size() == nanNum) {
        for (int i = 0; i < res.length; i++) {
          res[i] = series.elementAt(i).value();
          tstamps[i] = series.elementAt(i).tstamp();
        }
      }
    }
    //
    // case[3] SD happens to be a zero, i.e. they all are the same
    else if (sd == 0.0D) {
      //
      // now - check the case when SD is playing the trick, like
      // [1.0, 1.0, NaN, NaN, NaN, NaN, NaN] ->
      // subsection: [1.0, 1.0, NaN, NaN, NaN, NaN, NaN]
      // m: 1.0 sd: 0.0 ->
      // normal: [NaN, NaN, NaN, NaN, NaN, NaN, NaN] ->
      // NaN,NaN,NaN,NaN,NaN,NaN,NaN, -> "_______"
      // ??
      for (int i = 0; i < res.length; i++) {
        if (Double.isInfinite(series.elementAt(i).value())
            || Double.isNaN(series.elementAt(i).value())) {
          res[i] = series.elementAt(i).value();
        }
        else {
          res[i] = 1.0D;
        }
        tstamps[i] = series.elementAt(i).tstamp();
      }
    }
    //
    // normal case, everything seems to be fine
    else {
      // sd and mean here, - go-go-go
      for (int i = 0; i < res.length; i++) {
        res[i] = (series.elementAt(i).value() - mean) / sd;
        tstamps[i] = series.elementAt(i).tstamp();
      }
    }
    return new Timeseries(res, tstamps);
  }

  /**
   * Z-Normalize timeseries to the mean ~0 and standard deviation ~1.
   *
   * @param series The input timeseries.
   * @return the normalized time-series.
   * @throws TSException if error occurs.
   */
  public static double[] normalize(double[] series) throws TSException {
    // get values and timestamps out of there
    //
    double[] res = new double[series.length];

    // get mean and sdev, NaN's will be removed.
    //
    double mean = mean(series);
    double sd = stDev(series);

    // check if we hit special case, where something got NaN
    //
    if (Double.isInfinite(mean) || Double.isNaN(mean)
        || Double.isInfinite(sd) || Double.isNaN(sd)) {
      //
      // case[1] single value within the timeseries, normalize this value
      // to 1, whatever
      int nanNum = countNaN(series);
      if ((series.length - nanNum) == 1) {
        for (int i = 0; i < res.length; i++) {
          if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
            res[i] = series[i];
          }
          else {
            res[i] = 1.0D;
          }
        }
      }
      //
      // case[2] all values are NaN's
      else if (series.length == nanNum) {
        for (int i = 0; i < res.length; i++) {
          res[i] = series[i];
        }
      }
    }
    //
    // case[3] SD happens to be a zero, i.e. they all are the same
    else if (sd == 0.0D) {
      //
      // now - check the case when SD is playing the trick, like
      // [1.0, 1.0, NaN, NaN, NaN, NaN, NaN] ->
      // subsection: [1.0, 1.0, NaN, NaN, NaN, NaN, NaN]
      // m: 1.0 sd: 0.0 ->
      // normal: [NaN, NaN, NaN, NaN, NaN, NaN, NaN] ->
      // NaN,NaN,NaN,NaN,NaN,NaN,NaN, -> "_______"
      // ??
      for (int i = 0; i < res.length; i++) {
        if (Double.isInfinite(series[i]) || Double.isNaN(series[i])) {
          res[i] = series[i];
        }
        else {
          res[i] = 1.0D;
        }
      }
    }
    //
    // normal case, everything seems to be fine
    else {
      // sd and mean here, - go-go-go
      for (int i = 0; i < res.length; i++) {
        res[i] = (series[i] - mean) / sd;
      }
    }
    return res;
  }

  /**
   * Max value in timeseries.
   *
   * @param series The input timeseries.
   * @return the max value.
   */
  public static double max(Timeseries series) {
    if (countNaN(series) == series.size()) {
      return Double.NaN;
    }
    double[] values = series.values();
    double max = Double.MIN_VALUE;
    for (int i = 0; i < values.length; i++) {
      if (max < values[i]) {
        max = values[i];
      }
    }
    return max;
  }

  /**
   * Max value in timeseries.
   *
   * @param series The input timeseries.
   * @return the max value.
   */
  public static double max(double[] series) {
    if (countNaN(series) == series.length) {
      return Double.NaN;
    }
    double max = Double.MIN_VALUE;
    for (int i = 0; i < series.length; i++) {
      if (max < series[i]) {
        max = series[i];
      }
    }
    return max;
  }

  /**
   * Min value in timeseries.
   *
   * @param series The input timeseries.
   * @return the min value.
   */
  public static double min(Timeseries series) {
    if (countNaN(series) == series.size()) {
      return Double.NaN;
    }
    double[] values = series.values();
    double min = Double.MAX_VALUE;
    for (int i = 0; i < values.length; i++) {
      if (min > values[i]) {
        min = values[i];
      }
    }
    return min;
  }

  /**
   * Min value in timeseries.
   *
   * @param series The input timeseries.
   * @return the min value.
   */
  public static double min(double[] series) {
    if (countNaN(series) == series.length) {
      return Double.NaN;
    }
    double min = Double.MAX_VALUE;
    for (int i = 0; i < series.length; i++) {
      if (min > series[i]) {
        min = series[i];
      }
    }
    return min;
  }

  /**
   * Approximate the timeseries using PAA. If the timeseries has some NaN's they are handled as
   * follows: if all piece values are NaN's - the piece is approximated as NaN, if there is a value
   * on the piece, algorithm will handle it as usual.(maybe instead i should if on the approximated
   * segment amount of NaN's greater than amount of actual values)
   *
   * @param ts the timeseries to transform.
   * @param paaSize the size (length) of approximated timeseries.
   * @return PAA-approximated timeseries.
   * @throws TSException if error occurs.
   * @throws CloneNotSupportedException if error occurs.
   */
  public static Timeseries paa(Timeseries ts, int paaSize) throws TSException,
      CloneNotSupportedException {
    // fix the length
    int len = ts.size();
    // check for the trivial case
    if (len == paaSize) {
      return ts.clone();
    }
    else {
      // get values and timestamps
      double[][] vals = ts.valuesAsMatrix();
      long[] tStamps = ts.tstamps();
      // work out PAA by reshaping arrays
      double[][] res;
      if (len % paaSize == 0) {
        res = MatrixFactory.reshape(vals, len / paaSize, paaSize);
      }
      else {
        double[][] tmp = new double[paaSize][len];
        // System.out.println(Matrix.toString(tmp));
        for (int i = 0; i < paaSize; i++) {
          for (int j = 0; j < len; j++) {
            tmp[i][j] = vals[0][j];
          }
        }
        // System.out.println(Matrix.toString(tmp));
        double[][] expandedSS = MatrixFactory.reshape(tmp, 1, len * paaSize);
        // System.out.println(Matrix.toString(expandedSS));
        res = MatrixFactory.reshape(expandedSS, len, paaSize);
        // System.out.println(Matrix.toString(res));
      }
      //
      // now, here is a new trick comes in game - because we have so many
      // "lost" values
      // PAA game rules will change - we will omit NaN values and spit NaN
      // back to PAA series
      //
      //
      // this is the old line of code here:
      // double[] newVals = MatrixFactory.colMeans(res);
      //
      // i will need to test this crap though
      //
      //
      double[] newVals = MatrixFactory.colMeans(res);

      // work out timestamps
      long start = tStamps[0];
      long interval = tStamps[len - 1] - start;
      long increment = interval / (paaSize - 1);
      long[] newTstamps = new long[paaSize];
      for (int i = 0; i < paaSize; i++) {
        newTstamps[i] = start + i * increment;
      }
      return new Timeseries(newVals, newTstamps);
    }
  }

  /**
   * Provides a PAA transform implementation.
   *
   * @param ts The timeseries array.
   * @param paaSize The desired PAA series size.
   * @return Transformed data series.
   * @throws TSException If goes wrong.
   */
  public static double[] paa(double[] ts, int paaSize) throws TSException {
    // fix the length
    int len = ts.length;
    // check for the trivial case
    if (len == paaSize) {
      return Arrays.copyOf(ts, ts.length);
    }
    else {
      // get values and timestamps
      double[][] vals = asMatrix(ts);
      // work out PAA by reshaping arrays
      double[][] res;
      if (len % paaSize == 0) {
        res = MatrixFactory.reshape(vals, len / paaSize, paaSize);
      }
      else {
        double[][] tmp = new double[paaSize][len];
        // System.out.println(Matrix.toString(tmp));
        for (int i = 0; i < paaSize; i++) {
          for (int j = 0; j < len; j++) {
            tmp[i][j] = vals[0][j];
          }
        }
        // System.out.println(Matrix.toString(tmp));
        double[][] expandedSS = MatrixFactory.reshape(tmp, 1, len * paaSize);
        // System.out.println(Matrix.toString(expandedSS));
        res = MatrixFactory.reshape(expandedSS, len, paaSize);
        // System.out.println(Matrix.toString(res));
      }
      //
      // now, here is a new trick comes in game - because we have so many
      // "lost" values
      // PAA game rules will change - we will omit NaN values and spit NaN
      // back to PAA series
      //
      //
      // this is the old line of code here:
      // double[] newVals = MatrixFactory.colMeans(res);
      //
      // i will need to test this crap though
      //
      //
      double[] newVals = MatrixFactory.colMeans(res);

      return newVals;
    }

  }

  /**
   * Convert the timeseries into the string.
   *
   * @param series The timeseries to convert.
   * @param alphabet The alphabet to use.
   * @param alphabetSize The alphabet size in use.
   * @return SAX representation of timeseries.
   * @throws TSException if error occurs.
   */
  public static char[] ts2String(Timeseries series, Alphabet alphabet, int alphabetSize)
      throws TSException {
    double[] cuts = alphabet.getCuts(alphabetSize);
    char[] res = new char[series.size()];
    for (int i = 0; i < series.size(); i++) {
      res[i] = num2char(series.elementAt(i).value(), cuts);
    }
    return res;
  }

  /**
   * Converts the timeseries into Symbolic Representation using given cuts intervals.
   *
   * @param vals The timeseries.
   * @param cuts The cut intervals.
   * @return The timeseries SAX representation.
   */
  public static char[] ts2String(double[] vals, double[] cuts) {
    char[] res = new char[vals.length];
    for (int i = 0; i < vals.length; i++) {
      res[i] = num2char(vals[i], cuts);
    }
    return res;
  }

  /**
   * Convert the timeseries into the string.
   *
   * @param series The timeseries to convert.
   * @param alphabet The alphabet to use.
   * @param alphabetSize The alphabet size in use.
   * @return SAX representation of timeseries.
   * @throws TSException if error occurs.
   */
  public static char[] ts2StringWithNaN(Timeseries series, Alphabet alphabet, int alphabetSize)
      throws TSException {
    double[] cuts = alphabet.getCuts(alphabetSize);
    return ts2StringWithNaNByCuts(series, cuts);
  }

  /**
   * Convert the timeseries into the string.
   *
   * @param series The timeseries to convert.
   * @param cuts The cuts for alphabet.
   * @return SAX representation of timeseries.
   * @throws TSException if error occurs.
   */
  public static char[] ts2StringWithNaNByCuts(Timeseries series, double[] cuts) throws TSException {
    char[] res = new char[series.size()];
    for (int i = 0; i < series.size(); i++) {
      if (Double.isNaN(series.elementAt(i).value())
          || Double.isInfinite(series.elementAt(i).value())) {
        res[i] = '_';
      }
      else {
        res[i] = num2char(series.elementAt(i).value(), cuts);
      }
    }
    return res;
  }

  /**
   * Get mapping of number to char.
   *
   * @param value the value to map.
   * @param cuts the array of intervals.
   * @return character corresponding to numeric value.
   */
  public static char num2char(double value, double[] cuts) {
    int count = 0;
    while ((count < cuts.length) && (cuts[count] <= value)) {
      count++;
    }
    return ALPHABET[count];
  }

  /**
   * Converts index into char.
   *
   * @param idx The index value.
   * @return The char by index.
   */
  public static char num2char(int idx) {
    return ALPHABET[idx];
  }

  /**
   * Convert the timeseries into the index using SAX cuts.
   *
   * @param series The timeseries to convert.
   * @param alphabet The alphabet to use.
   * @param alphabetSize The alphabet size in use.
   * @return SAX representation of timeseries.
   * @throws TSException if error occurs.
   */
  public static int[] ts2Index(Timeseries series, Alphabet alphabet, int alphabetSize)
      throws TSException {
    double[] cuts = alphabet.getCuts(alphabetSize);
    int[] res = new int[series.size()];
    for (int i = 0; i < series.size(); i++) {
      res[i] = num2index(series.elementAt(i).value(), cuts);
    }
    return res;
  }

  /**
   * Get mapping of number to cut index.
   *
   * @param value the value to map.
   * @param cuts the array of intervals.
   * @return character corresponding to numeric value.
   */
  public static int num2index(double value, double[] cuts) {
    int count = 0;
    while ((count < cuts.length) && (cuts[count] <= value)) {
      count++;
    }
    return count;
  }

  /**
   * Converts the vector into one-row matrix.
   *
   * @param vector The vector.
   * @return Vector as a matrix.
   */
  private static double[][] asMatrix(double[] vector) {
    double[][] res = new double[1][vector.length];
    for (int i = 0; i < vector.length; i++) {
      res[0][i] = vector[i];
    }
    return res;
  }

  /**
   * Counts the number of NaNs' in the timeseries.
   *
   * @param series The timeseries.
   * @return The count of NaN values.
   */
  private static int countNaN(Timeseries series) {
    int res = 0;
    for (TPoint tp : series) {
      if (Double.isInfinite(tp.value()) || Double.isNaN(tp.value())) {
        res += 1;
      }
    }
    return res;
  }

  /**
   * Counts the number of NaNs' in the timeseries.
   *
   * @param series The timeseries.
   * @return The count of NaN values.
   */
  public static int countNaN(double[] series) {
    int res = 0;
    for (double d : series) {
      if (Double.isInfinite(d) || Double.isNaN(d)) {
        res += 1;
      }
    }
    return res;
  }

}
