package edu.hawaii.jmotif.logic.sax;

import edu.hawaii.jmotif.datatype.TSException;
import edu.hawaii.jmotif.datatype.TSUtils;
import edu.hawaii.jmotif.datatype.Timeseries;
import edu.hawaii.jmotif.logic.distance.EuclideanDistance;
import edu.hawaii.jmotif.logic.sax.alphabet.Alphabet;
import edu.hawaii.jmotif.logic.sax.alphabet.NormalAlphabet;
import edu.hawaii.jmotif.logic.sax.datastructures.SAXFrequencyData;
import edu.hawaii.jmotif.logic.sax.trie.*;
import java.util.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.openide.util.Exceptions;

/**
 * Implements SAX algorithms.
 *
 * @author Pavel Senin
 * @author Tomas Barton
 *
 */
public final class SAXFactory {

    private static Alphabet normalAlphabet = new NormalAlphabet();

    /**
     * Constructor.
     */
    private SAXFactory() {
        super();
    }

    /**
     * Convert the timeseries into SAX string representation, normalizes each of
     * the pieces before SAX conversion.
     *
     * @param ts The timeseries given.
     * @param windowSize The sliding window size used.
     * @param paaSize The number of the points used in the PAA reduction of the
     * time series.
     * @param cuts The alphabet cuts to use.
     * @return The SAX representation of the timeseries.
     * @throws TSException If error occurs.
     */
    public static SAXFrequencyData ts2saxZnormByCutsNoSkip(Timeseries ts, int windowSize,
            int paaSize, double[] cuts) throws TSException {

        // Initialize symbolic result data
        SAXFrequencyData res = new SAXFrequencyData();

        // scan across the time series extract sub sequences, and converting
        // them to strings
        for (int i = 0; i < ts.size() - (windowSize - 1); i++) {

            // fix the current subsection
            Timeseries subSection = ts.subsection(i, i + windowSize - 1);

            // Z normalize it
            subSection = TSUtils.normalize(subSection);

            // perform PAA conversion if needed
            Timeseries paa;
            try {
                paa = TSUtils.paa(subSection, paaSize);
            } catch (CloneNotSupportedException e) {
                Exceptions.printStackTrace(e);
                throw new TSException("Unable to clone: ");
            }

            // Convert the PAA to a string.
            char[] currentString = TSUtils.ts2StringWithNaNByCuts(paa, cuts);

            res.put(new String(currentString), i);
        }
        return res;
    }

    /**
     * Convert the timeseries into SAX string representation, normalizes each of
     * the pieces before SAX conversion.
     *
     * @param ts The timeseries given.
     * @param windowSize The sliding window size used.
     * @param paaSize The number of the points used in the PAA reduction of the
     * time series.
     * @param cuts The alphabet cuts to use.
     * @return The SAX representation of the timeseries.
     * @throws TSException If error occurs.
     */
    public static SAXFrequencyData ts2saxZnormByCuts(Timeseries ts, int windowSize, int paaSize,
            double[] cuts) throws TSException {

        // Initialize symbolic result data
        SAXFrequencyData res = new SAXFrequencyData();
        String previousString = "";

        // scan across the time series extract sub sequences, and converting
        // them to strings
        for (int i = 0; i < ts.size() - (windowSize - 1); i++) {

            // fix the current subsection
            Timeseries subSection = ts.subsection(i, i + windowSize - 1);

            // Z normalize it
            subSection = TSUtils.normalize(subSection);

            // perform PAA conversion if needed
            Timeseries paa;
            try {
                paa = TSUtils.paa(subSection, paaSize);
            } catch (CloneNotSupportedException e) {
                Exceptions.printStackTrace(e);
                throw new TSException("Unable to clone: ");
            }

            // Convert the PAA to a string.
            char[] currentString = TSUtils.ts2StringWithNaNByCuts(paa, cuts);

            // check if previous one was the same, if so, ignore that (don't
            // know why though, but guess
            // cause we didn't advance much on the timeseries itself)
            if (!previousString.isEmpty() && previousString.equalsIgnoreCase(new String(currentString))) {
                continue;
            }
            previousString = new String(currentString);
            res.put(new String(currentString), i);
        }
        return res;
    }

    /**
     * Convert the timeseries into SAX string representation, normalizes each of
     * the pieces before SAX conversion.
     *
     * @param s The timeseries given.
     * @param windowSize The sliding window size used.
     * @param paaSize The number of the points used in the PAA reduction of the
     * time series.
     * @param cuts The alphabet cuts to use.
     * @return The SAX representation of the timeseries.
     * @throws TSException If error occurs.
     */
    public static SAXFrequencyData ts2saxZnormByCuts(double[] s, int windowSize, int paaSize,
            double[] cuts) throws TSException {
        long[] ticks = new long[s.length];
        for (int i = 0; i < s.length; i++) {
            ticks[i] = i;
        }
        Timeseries ts = new Timeseries(s, ticks);
        return ts2saxZnormByCuts(ts, windowSize, paaSize, cuts);
    }

    /**
     * Convert the timeseries into SAX string representation, normalizes each of
     * the pieces before SAX conversion.
     *
     * @param s The timeseries given.
     * @param windowSize The sliding window size used.
     * @param paaSize The number of the points used in the PAA reduction of the
     * time series.
     * @param cuts The alphabet cuts to use.
     * @return The SAX representation of the timeseries.
     * @throws TSException If error occurs.
     */
    public static SAXFrequencyData ts2saxZnormByCutsNoSkip(double[] s, int windowSize, int paaSize,
            double[] cuts) throws TSException {
        long[] ticks = new long[s.length];
        for (int i = 0; i < s.length; i++) {
            ticks[i] = i;
        }
        Timeseries ts = new Timeseries(s, ticks);
        return ts2saxZnormByCutsNoSkip(ts, windowSize, paaSize, cuts);
    }

    /**
     * Convert the timeseries into SAX string representation. It doesn't
     * normalize anything.
     *
     * @param ts The timeseries given.
     * @param windowSize The sliding window size used.
     * @param paaSize The number of the points used in the PAA reduction of the
     * time series.
     * @param cuts The alphabet cuts to use.
     * @return The SAX representation of the timeseries.
     * @throws TSException If error occurs.
     */
    public static SAXFrequencyData ts2saxNoZnormByCuts(Timeseries ts, int windowSize, int paaSize,
            double[] cuts) throws TSException {

        // Initialize symbolic result data
        SAXFrequencyData res = new SAXFrequencyData();
        String previousString = "";

        // scan across the time series extract sub sequences, and converting
        // them to strings
        for (int i = 0; i < ts.size() - (windowSize - 1); i++) {

            // fix the current subsection
            Timeseries subSection = ts.subsection(i, i + windowSize - 1);

            // Z normalize it
            // subSection = TSUtils.normalize(subSection);
            // perform PAA conversion if needed
            Timeseries paa;
            try {
                paa = TSUtils.paa(subSection, paaSize);
            } catch (CloneNotSupportedException e) {
                Exceptions.printStackTrace(e);
                throw new TSException("Unable to clone: " + e.getMessage());
            }

            // Convert the PAA to a string.
            char[] currentString = TSUtils.ts2StringWithNaNByCuts(paa, cuts);

            // check if previous one was the same, if so, ignore that (don't
            // know why though, but guess
            // cause we didn't advance much on the timeseries itself)
            if (!previousString.isEmpty() && previousString.equalsIgnoreCase(new String(currentString))) {
                previousString = new String(currentString);
                continue;
            }
            previousString = new String(currentString);
            res.put(new String(currentString), i);
        }
        return res;
    }

    /**
     * Convert the timeseries into SAX string representation.
     *
     * @param ts The timeseries given.
     * @param windowSize The sliding window size used.
     * @param paaSize The number of the points used in the PAA reduction of the
     * time series.
     * @param alphabet The alphabet to use.
     * @param alphabetSize The alphabet size used.
     * @return The SAX representation of the timeseries.
     * @throws TSException If error occurs.
     */
    public static SAXFrequencyData ts2saxZNorm(Timeseries ts, int windowSize, int paaSize,
            Alphabet alphabet, int alphabetSize) throws TSException {

        if (alphabetSize > alphabet.getMaxSize()) {
            throw new TSException("Unable to set the alphabet size greater than " + alphabet.getMaxSize());
        }

        return ts2saxZnormByCuts(ts, windowSize, paaSize, alphabet.getCuts(alphabetSize));

    }

    /**
     * Convert the timeseries into SAX string representation.
     *
     * @param ts The timeseries given.
     * @param windowSize The sliding window size used.
     * @param paaSize The number of the points used in the PAA reduction of the
     * time series.
     * @param alphabet The alphabet to use.
     * @param alphabetSize The alphabet size used.
     * @return The SAX representation of the timeseries.
     * @throws TSException If error occurs.
     */
    public static SAXFrequencyData ts2saxNoZnorm(Timeseries ts, int windowSize, int paaSize,
            Alphabet alphabet, int alphabetSize) throws TSException {

        if (alphabetSize > alphabet.getMaxSize()) {
            throw new TSException("Unable to set the alphabet size greater than " + alphabet.getMaxSize());
        }

        return ts2saxNoZnormByCuts(ts, windowSize, paaSize, alphabet.getCuts(alphabetSize));

    }

    /**
     * Convert the timeseries into SAX string representation.
     *
     * @param ts The timeseries given.
     * @param paaSize The number of the points used in the PAA reduction of the
     * time series.
     * @param alphabet The alphabet to use.
     * @param alphabetSize The alphabet size used.
     * @return The SAX representation of the timeseries.
     * @throws TSException If error occurs.
     */
    public static String ts2string(Timeseries ts, int paaSize, Alphabet alphabet, int alphabetSize)
            throws TSException {

        if (alphabetSize > alphabet.getMaxSize()) {
            throw new TSException("Unable to set the alphabet size greater than " + alphabet.getMaxSize());
        }

        int tsLength = ts.size();
        if (tsLength == paaSize) {
            return new String(TSUtils.ts2String(TSUtils.normalize(ts), alphabet, alphabetSize));
        } else {
            // perform PAA conversion
            Timeseries PAA;
            try {
                PAA = TSUtils.paa(TSUtils.normalize(ts), paaSize);
            } catch (CloneNotSupportedException e) {
                Exceptions.printStackTrace(e);
                throw new TSException("Unable to clone: " + e.getMessage());
            }
            return new String(TSUtils.ts2String(PAA, alphabet, alphabetSize));
        }
    }

    /**
     * Compute the distance between the two strings, this function use the
     * numbers associated with ASCII codes, i.e. distance between a and b would
     * be 1.
     *
     * @param a The first string.
     * @param b The second string.
     * @return The pairwise distance.
     * @throws TSException if length are differ.
     */
    public static int strDistance(char[] a, char[] b) throws TSException {
        if (a.length == b.length) {
            int distance = 0;
            for (int i = 0; i < a.length; i++) {
                int tDist = Math.abs(Character.getNumericValue(a[i]) - Character.getNumericValue(b[i]));
                if (tDist > 1) {
                    distance += tDist;
                }
            }
            return distance;
        } else {
            throw new TSException("Unable to compute SAX distance, string lengths are not equal");
        }
    }

    /**
     * Compute the distance between the two chars based on the ASCII symbol
     * codes.
     *
     * @param a The first char.
     * @param b The second char.
     * @return The distance.
     */
    public static int strDistance(char a, char b) {
        return Math.abs(Character.getNumericValue(a) - Character.getNumericValue(b));
    }

    /**
     * This function implements SAX MINDIST function which uses alphabet based
     * distance matrix.
     *
     * @param a The SAX string.
     * @param b The SAX string.
     * @param distanceMatrix The distance matrix to use.
     * @return distance between strings.
     * @throws TSException If error occurs.
     */
    public static double saxMinDist(char[] a, char[] b, double[][] distanceMatrix) throws TSException {
        if (a.length == b.length) {
            double dist = 0.0D;
            for (int i = 0; i < a.length; i++) {
                if (Character.isLetter(a[i]) && Character.isLetter(b[i])) {
                    int numA = Character.getNumericValue(a[i]) - 10;
                    int numB = Character.getNumericValue(b[i]) - 10;
                    if (numA > 19 || numA < 0 || numB > 19 || numB < 0) {
                        throw new TSException("The character index greater than 19 or less than 0!");
                    }
                    double localDist = distanceMatrix[numA][numB];
                    dist += localDist;
                } else {
                    throw new TSException("Non-literal character found!");
                }
            }
            return dist;
        } else {
            throw new TSException("Data arrays lengths are not equal!");
        }
    }

    /**
     * Build the SAX trie out of the series.
     *
     * @param tsData The timeseries.
     * @param windowSize PAA window size to use.
     * @param alphabetSize The SAX alphabet size.
     * @param dataAttributeName The WEKA attribute - essentially points on the
     * instance attribute which bears the data value in this case.
     * @return Discords found within the series.
     * @throws TrieException if error occurs.
     * @throws TSException if error occurs.
     */
    public static DiscordRecords instances2Discords(Dataset<Instance> tsData, String dataAttributeName,
            int windowSize, int alphabetSize) throws TrieException, TSException {

        boolean debug = true;

        // get the timestamps and data attributes
        //
        Attribute dataAttribute = null;
        dataAttribute = tsData.attributeBuilder().create(dataAttributeName, "double");

        // now init the SAX structures
        //
        DiscordRecords res = new DiscordRecords(200);
        /**
         * @TODO maybe replace numInstances by numAttributes
         */
        SAXTrie data = new SAXTrie(tsData.size() - windowSize, alphabetSize);
        if (debug) {
            System.out.println("Data size: " + tsData.size() + ", window size: " + windowSize
                    + ", SAX Trie size: " + (tsData.size() - windowSize));
        }
        Alphabet normalA = new NormalAlphabet();

        // [1.0] PREPROCESSING: in the sliding window loop build SAX string entries
        //
        int currPosition = 0;
        while ((currPosition + windowSize) < tsData.size()) {
            // get the window SAX representation
            double[] series = getSubSeries(tsData, dataAttribute, currPosition, currPosition + windowSize);
            char[] saxVals = getSaxVals(series, windowSize, normalA.getCuts(alphabetSize));
            // add result to the structure
            data.put(String.valueOf(saxVals), currPosition);
            // increment the position
            currPosition++;
        }
        if (debug) {
            ArrayList<SAXTrieHitEntry> f = data.getFrequencies();
            System.out.println("Words in the trie: " + f.size());
        }
        // [1.1] PREPROCESSING: sort the entries array by the frequency occurrence to feed into the
        // outer loop
        //
        ArrayList<SAXTrieHitEntry> frequencies = data.getFrequencies();
        Collections.sort(frequencies);

        // *****************************************************************************
        // WORTH noting that here we just build a structure. No search was conducted yet.
        //
        // THIS IS AN OUTER LOOP IN THE ARTICLE
        //
        double bestSoFarDistance = 0.0D;
        int bestSoFarPosition = -1;

        for (SAXTrieHitEntry e : frequencies) {

            currPosition = e.getPosition();
            // if ((currPosition > (10869 - 600) && currPosition < (10869 + 600))){
            // || (currPosition > (10855 - 600) && currPosition < (10855 + 600))) {
            // continue;
            // }
            System.out.println("POSITION: " + currPosition + ", '" + Arrays.toString(e.getStr())
                    + "' , freq: " + e.getFrequency());
            double[] vals = getSubSeries(tsData, dataAttribute, currPosition, currPosition + windowSize);
            double nearestNeighborDist = Double.MAX_VALUE;
            boolean breakLoop = false;

            // get the list of the same SAX occurrences & create the visited locations register
            //
            List<Integer> occurrences = data.getOccurences(e.getStr());
            VisitRegistry registry = new VisitRegistry(tsData.attributeCount() - windowSize);
            int visitingCount = 0;
            // mark all trivial matches as visited
            for (int i = currPosition - windowSize; i < currPosition + windowSize; i++) {
                if (i > 0 && i < (tsData.attributeCount() - windowSize)) {
                    registry.markVisited(i);
                }
            }

            // first we are going over the occurrences of the same SAX approximation
            //
            for (Integer i : occurrences) {
                if (registry.isNnotVisited(i)) {
                    // System.out.println("  - visiting : " + i);
                    registry.markVisited(i);
                    visitingCount++;
                    // get the piece of the timeseries
                    double[] oVals = getSubSeries(tsData, dataAttribute, i, i + windowSize);
                    double dist = EuclideanDistance.distance(vals, oVals);
                    // check if it is less then best so far
                    if (dist < bestSoFarDistance || 0.0D == dist) {
                        // System.out.println("    * breaking the 'neighbors' loop, d=" + dist +
                        // ", best so far: "
                        // + bestSoFarDistance);
                        breakLoop = true; // here TRUE means that this location is not discord - next outer loop
                        nearestNeighborDist = dist;
                        break;
                    }
                    if (dist < nearestNeighborDist) {
                        // System.out.println("    - best so far = " + dist);
                        nearestNeighborDist = dist;
                    }
                }
            }

            // check if we must break the loop
            //
            if (!breakLoop) {
                // System.out.println("  ** starting random visiting loop, " +
                // registry.getUnvisited().size()
                // + " locations to go ...");
                // if we must continue - we need to iterate over remained locations in random order
                //
                int i = -1;
                while ((i = registry.getNextRandomUnvisitedPosition()) != -1) {
                    // System.out.println("  - visiting : " + i);
                    registry.markVisited(i);
                    visitingCount++;
                    // get the piece of the timeseries
                    double[] oVals = getSubSeries(tsData, dataAttribute, i, i + windowSize);
                    double dist = EuclideanDistance.distance(vals, oVals);
                    // check if it is less then best so far
                    if (dist < bestSoFarDistance || 0.0 == dist) {
                        // System.out.println("    * breaking the 'random' loop, d=" + dist + ", best so far: "
                        // + bestSoFarDistance);
                        // this location is not discord - next outer loop
                        nearestNeighborDist = dist;
                        breakLoop = true;
                        break;
                    }
                    if (dist < nearestNeighborDist) {
                        // System.out.println("    - best so far = " + dist);
                        nearestNeighborDist = dist;
                    }
                }
            }

            if (breakLoop) {
                System.out.println("Search early abandoned after " + visitingCount + " iterations");
                if (nearestNeighborDist > res.getMinDistance()) {
                    res.add(new DiscordRecord(currPosition, nearestNeighborDist));
                }
                assert true;
            } else {
                // check if got any close
                if (nearestNeighborDist > bestSoFarDistance) {
                    System.out.println("It is best so far - goes to the results, at " + currPosition
                            + ", dist " + nearestNeighborDist + ", locations Visited: " + visitingCount);
                    bestSoFarDistance = nearestNeighborDist;
                    bestSoFarPosition = currPosition;
                    res.add(new DiscordRecord(bestSoFarPosition, bestSoFarDistance));
                } else {
                    System.out.println("Oops pos:" + currPosition + ", dist " + nearestNeighborDist
                            + ", locations Visited: " + visitingCount);
                }
            }

        }
        return res;
    }

    /**
     * Build the SAX trie out of the series.
     *
     * @param tsData The timeseries.
     * @param windowSize PAA window size to use.
     * @param alphabetSize The SAX alphabet size.
     * @return Discords found within the series.
     * @throws TrieException if error occurs.
     * @throws TSException if error occurs.
     */
    public static DiscordRecords instances2Discords(double[] tsData, int windowSize, int alphabetSize)
            throws TrieException, TSException {

        boolean debug = true;

        // now init the SAX structures
        //
        DiscordRecords res = new DiscordRecords(200);
        SAXTrie data = new SAXTrie(tsData.length - windowSize, alphabetSize);
        if (debug) {

            StringBuilder sb = new StringBuilder();
            sb.append("Data size: ").append(tsData.length);

            double max = TSUtils.max(tsData);
            sb.append("; max: ").append(max);

            double min = TSUtils.min(tsData);
            sb.append("; min: ").append(min);

            double mean = TSUtils.mean(tsData);
            sb.append("; mean: ").append(mean);

            int nans = TSUtils.countNaN(tsData);
            sb.append("; NaNs: ").append(nans);

            System.out.println(sb.toString());

            System.out.println("window size: " + windowSize + ",alphabet size: " + alphabetSize
                    + ", SAX Trie size: " + (tsData.length - windowSize));
        }

        Alphabet normalA = new NormalAlphabet();

        // [1.0] PREPROCESSING: in the sliding window loop build SAX string entries
        //
        int currPosition = 0;
        while ((currPosition + windowSize) < tsData.length) {
            // get the window SAX representation
            double[] series = getSubSeries(tsData, currPosition, currPosition + windowSize);
            char[] saxVals = getSaxVals(series, windowSize, normalA.getCuts(alphabetSize));
            // add result to the structure
            data.put(String.valueOf(saxVals), currPosition);
            // increment the position
            currPosition++;
        }
        // [1.1] PREPROCESSING: sort the entries array by the frequency occurrence to feed into the
        // outer loop
        //
        ArrayList<SAXTrieHitEntry> frequencies = data.getFrequencies();
        Collections.sort(frequencies);

        if (debug) {
            System.out.println("Words in the trie: " + frequencies.size());
            //
            // get printed 10 non-trivial patterns with offsets
            // non-trivial here means the one which are not the same letters
            int counter = 0;
            Set<SAXTrieHitEntry> seen = new TreeSet<SAXTrieHitEntry>();
            for (int i = frequencies.size() - 1; i >= 0; i--) {
                SAXTrieHitEntry entry = frequencies.get(i);
                if (entry.isTrivial(2) || seen.contains(entry)) {
                    continue;
                } else {
                    counter += 1;
                    seen.add(entry);
                    System.out.println(" freq entry: \'" + Arrays.toString(entry.getStr()) + "', offset: "
                            + entry.getPosition() + ", freq: " + entry.getFrequency());
                    if (counter > 10) {
                        break;
                    }
                }
            }
        }

        // *****************************************************************************
        // WORTH noting that here we just build a structure. No search was conducted yet.
        //
        // THIS IS AN OUTER LOOP IN THE ARTICLE
        //
        double bestSoFarDistance = 0.0D;
        int bestSoFarPosition = -1;

        for (SAXTrieHitEntry e : frequencies) {

            currPosition = e.getPosition();
            //
            // System.out.println("POSITION: " + currPosition + ", '" + Arrays.toString(e.getStr())
            // + "' , freq: " + e.getFrequency());
            //
            double[] vals = getSubSeries(tsData, currPosition, currPosition + windowSize);
            double nearestNeighborDist = Double.MAX_VALUE;
            boolean breakLoop = false;

            // get the list of the same SAX occurrences & create the visited locations register
            //
            List<Integer> occurrences = data.getOccurences(e.getStr());
            VisitRegistry registry = new VisitRegistry(tsData.length - windowSize);
            int visitingCount = 0;
            // mark all trivial matches as visited
            for (int i = currPosition - windowSize; i < currPosition + windowSize; i++) {
                if (i > 0 && i < (tsData.length - windowSize)) {
                    registry.markVisited(i);
                }
            }

            // first we are going over the occurrences of the same SAX approximation
            //
            for (Integer i : occurrences) {
                if (registry.isNnotVisited(i)) {
                    // System.out.println("  - visiting : " + i);
                    registry.markVisited(i);
                    visitingCount++;
                    // get the piece of the timeseries
                    double[] oVals = getSubSeries(tsData, i, i + windowSize);
                    double dist = EuclideanDistance.distance(vals, oVals);
                    // check if it is less then best so far
                    if (dist < bestSoFarDistance || 0.0D == dist) {
                        // System.out.println("    * breaking the 'neighbors' loop, d=" + dist +
                        // ", best so far: "
                        // + bestSoFarDistance);
                        breakLoop = true; // here TRUE means that this location is not discord - next outer loop
                        nearestNeighborDist = dist;
                        break;
                    }
                    if (dist < nearestNeighborDist) {
                        // System.out.println("    - best so far = " + dist);
                        nearestNeighborDist = dist;
                    }
                }
            }

            // check if we must break the loop
            //
            if (!breakLoop) {
                // System.out.println("  ** starting random visiting loop, " +
                // registry.getUnvisited().size()
                // + " locations to go ...");
                // if we must continue - we need to iterate over remained locations in random order
                //
                int i = -1;
                while ((i = registry.getNextRandomUnvisitedPosition()) != -1) {
                    // System.out.println("  - visiting : " + i);
                    registry.markVisited(i);
                    visitingCount++;
                    // get the piece of the timeseries
                    double[] oVals = getSubSeries(tsData, i, i + windowSize);
                    double dist = EuclideanDistance.distance(vals, oVals);
                    // check if it is less then best so far
                    if (dist < bestSoFarDistance || 0.0 == dist) {
                        // System.out.println("    * breaking the 'random' loop, d=" + dist + ", best so far: "
                        // + bestSoFarDistance);
                        // this location is not discord - next outer loop
                        nearestNeighborDist = dist;
                        breakLoop = true;
                        break;
                    }
                    if (dist < nearestNeighborDist) {
                        // System.out.println("    - best so far = " + dist);
                        nearestNeighborDist = dist;
                    }
                }
            }

            if (breakLoop) {
                // System.out.println("Search early abandoned after " + visitingCount + " iterations");
                if (nearestNeighborDist > res.getMinDistance()) {
                    res.add(new DiscordRecord(currPosition, nearestNeighborDist));
                }
                assert true;
            } else {
                // check if got any close
                if (nearestNeighborDist > bestSoFarDistance) {
                    // System.out.println("It is best so far - goes to the results, at " + currPosition
                    // + ", dist " + nearestNeighborDist + ", locations Visited: " + visitingCount);
                    bestSoFarDistance = nearestNeighborDist;
                    bestSoFarPosition = currPosition;
                    res.add(new DiscordRecord(bestSoFarPosition, bestSoFarDistance));
                } else {
                    assert true;
                    // System.out.println("Oops pos:" + currPosition + ", dist " + nearestNeighborDist
                    // + ", locations Visited: " + visitingCount);
                }
            }

        }
        return res;
    }

    /**
     * Builds two collections - collection of "discords" - the surprise or
     * unique patterns and the collection of the motifs - most frequent
     * patterns. This method leveraging the Trie structure - so the sliding
     * window size will be translated into the alphabet size by using PAA.
     *
     * @param series The data series.
     * @param windowSize The sliding window size.
     * @param alphabetSize The alphabet size.
     * @param discordCollectionSize The size of the discord collection - how
     * many top discords we want to keep.
     * @param motifsCollectionSize The size of the motif collection - how many
     * top motifs we want to keep.
     * @return All what was promised if finishes.
     *
     * @throws TrieException if error occurs.
     * @throws TSException if error occurs.
     */
    public static DiscordsAndMotifs seriesToDiscordsAndMotifs(double[] series, int windowSize,
            int alphabetSize, int discordCollectionSize, int motifsCollectionSize) throws TrieException,
            TSException {

        boolean debug = true;

        // now init the SAX structures
        //
        DiscordsAndMotifs res = new DiscordsAndMotifs(discordCollectionSize, motifsCollectionSize);
        SAXTrie trie = new SAXTrie(series.length - windowSize, alphabetSize);
        if (debug) {

            StringBuilder sb = new StringBuilder();
            sb.append("Data size: ").append(series.length);

            double max = TSUtils.max(series);
            sb.append("; max: ").append(max);

            double min = TSUtils.min(series);
            sb.append("; min: ").append(min);

            double mean = TSUtils.mean(series);
            sb.append("; mean: ").append(mean);

            int nans = TSUtils.countNaN(series);
            sb.append("; NaNs: ").append(nans);

            System.out.println(sb.toString());

            System.out.println("window size: " + windowSize + ", alphabet size: " + alphabetSize
                    + ", SAX Trie size: " + (series.length - windowSize));
        }

        Alphabet normalA = new NormalAlphabet();

        // [1.0] PREPROCESSING: in the sliding window loop build SAX string entries
        //
        int currPosition = 0;
        while ((currPosition + windowSize) < series.length) {
            // get the window SAX representation
            double[] subSeries = getSubSeries(series, currPosition, currPosition + windowSize);
            char[] saxVals = getSaxVals(subSeries, windowSize, normalA.getCuts(alphabetSize));
            // add result to the structure
            trie.put(String.valueOf(saxVals), currPosition);
            // increment the position
            currPosition++;
        }

        // [1.1] PREPROCESSING: sort the entries array by the frequency occurrence to feed into the
        // outer loop
        //
        ArrayList<SAXTrieHitEntry> frequencies = trie.getFrequencies();
        Collections.sort(frequencies);

        // it got sorted - from one end we have unique words - those discords
        // from the other end - we have motifs - the most frequent entries
        //
        // what I'll do here - is to populate non-trivial frequent entries into the resulting container
        if (debug) {
            System.out.println("Words in the trie: " + frequencies.size());
        }

        // picking those non-trivial patterns this method job
        // non-trivial here means the one which are not the same letters
        //
        int counter = 0;
        Set<SAXTrieHitEntry> seen = new TreeSet<SAXTrieHitEntry>();
        // iterating backward - collection is sorted
        for (int i = frequencies.size() - 1; i >= 0; i--) {
            SAXTrieHitEntry entry = frequencies.get(i);
            if (entry.isTrivial(2) || seen.contains(entry) || (2 > entry.getFrequency())) {
                if ((2 > entry.getFrequency())) {
                    break;
                }
                continue;
            } else {
                counter += 1;
                res.addMotif(new MotifRecord(entry.getStr(), trie.getOccurences(entry.getStr())));
                seen.add(entry);
                if (counter > motifsCollectionSize) {
                    break;
                }
            }
        }

        // *****************************************************************************
        // WORTH noting that here we just build a structure. No search was conducted yet.
        //
        // THIS IS AN OUTER LOOP IN THE ARTICLE
        //
        double bestSoFarDistance = 0.0D;
        int bestSoFarPosition = -1;

        for (SAXTrieHitEntry e : frequencies) {

            currPosition = e.getPosition();
            String payload = String.valueOf(e.getStr());
            //
            // System.out.println("POSITION: " + currPosition + ", '" + Arrays.toString(e.getStr())
            // + "' , freq: " + e.getFrequency());
            //
            double[] vals = getSubSeries(series, currPosition, currPosition + windowSize);
            double nearestNeighborDist = Double.MAX_VALUE;
            boolean breakLoop = false;

            // get the list of the same SAX occurrences & create the visited locations register
            //
            List<Integer> occurrences = trie.getOccurences(e.getStr());
            VisitRegistry registry = new VisitRegistry(series.length - windowSize);
            int visitingCount = 0;
            // mark all trivial matches as visited
            for (int i = currPosition - windowSize; i < currPosition + windowSize; i++) {
                if (i > 0 && i < (series.length - windowSize)) {
                    registry.markVisited(i);
                }
            }

            // first we are going over the occurrences of the same SAX approximation
            //
            for (Integer i : occurrences) {
                if (registry.isNnotVisited(i)) {
                    // System.out.println("  - visiting : " + i);
                    registry.markVisited(i);
                    visitingCount++;
                    // get the piece of the timeseries
                    double[] oVals = getSubSeries(series, i, i + windowSize);
                    double dist = EuclideanDistance.distance(vals, oVals);
                    // check if it is less then best so far
                    if (dist < bestSoFarDistance || 0.0D == dist) {
                        // System.out.println("    * breaking the 'neighbors' loop, d=" + dist +
                        // ", best so far: "
                        // + bestSoFarDistance);
                        breakLoop = true; // here TRUE means that this location is not discord - next outer loop
                        nearestNeighborDist = dist;
                        break;
                    }
                    if (dist < nearestNeighborDist) {
                        // System.out.println("    - best so far = " + dist);
                        nearestNeighborDist = dist;
                    }
                }
            }

            // check if we must break the loop
            //
            if (!breakLoop) {
                // System.out.println("  ** starting random visiting loop, " +
                // registry.getUnvisited().size()
                // + " locations to go ...");
                // if we must continue - we need to iterate over remained locations in random order
                //
                int i = -1;
                while ((i = registry.getNextRandomUnvisitedPosition()) != -1) {
                    // System.out.println("  - visiting : " + i);
                    registry.markVisited(i);
                    visitingCount++;
                    // get the piece of the timeseries
                    double[] oVals = getSubSeries(series, i, i + windowSize);
                    double dist = EuclideanDistance.distance(vals, oVals);
                    // check if it is less then best so far
                    if (dist < bestSoFarDistance || 0.0 == dist) {
                        // System.out.println("    * breaking the 'random' loop, d=" + dist + ", best so far: "
                        // + bestSoFarDistance);
                        // this location is not discord - next outer loop
                        nearestNeighborDist = dist;
                        breakLoop = true;
                        break;
                    }
                    if (dist < nearestNeighborDist) {
                        // System.out.println("    - best so far = " + dist);
                        nearestNeighborDist = dist;
                    }
                }
            }

            if (breakLoop) {
                // System.out.println("Search early abandoned after " + visitingCount + " iterations");
                if (nearestNeighborDist > res.getMinDistance()) {
                    res.addDiscord(new DiscordRecord(currPosition, nearestNeighborDist, payload));
                }
                assert true;
            } else {
                // check if got any close
                if (nearestNeighborDist > bestSoFarDistance) {
                    bestSoFarDistance = nearestNeighborDist;
                    bestSoFarPosition = currPosition;
                    res.addDiscord(new DiscordRecord(bestSoFarPosition, bestSoFarDistance, payload));
                } else {
                    assert true;
                }
            }

        }
        trie = null;
        return res;
    }

    /**
     * Convert real-valued series into symbolic representation.
     *
     * @param vals Real valued timeseries.
     * @param windowSize The PAA window size.
     * @param cuts The cut values array used for SAX transform.
     * @return The symbolic representation of the given real time-series.
     * @throws TSException If error occurs.
     */
    private static char[] getSaxVals(double[] vals, int windowSize, double[] cuts) throws TSException {
        char[] saxVals;
        if (windowSize == cuts.length + 1) {
            saxVals = TSUtils.ts2String(TSUtils.normalize(vals), cuts);
        } else {
            saxVals = TSUtils.ts2String(TSUtils.normalize(TSUtils.paa(vals, cuts.length + 1)), cuts);
        }
        return saxVals;
    }

    /**
     * Extracts sub-series from the WEKA-style series.
     *
     * @param data The series.
     * @param attribute The data-bearing attribute.
     * @param start The start timestamp.
     * @param end The end timestamp
     * @return sub-series from start to end.
     */
    private static double[] getSubSeries(Dataset<Instance> data, Attribute attribute, int start, int end) {
        List<Instance> tmpList = new ArrayList<Instance>(); //data.subList(start, end);
        for (int i = start; i <= end; i++) {
            tmpList.add(data.instance(i));
        }
        double[] vals = new double[end - start];
        for (int i = 0; i < end - start; i++) {
            vals[i] = tmpList.get(i).value(attribute.getIndex());
        }
        return vals;
    }

    /**
     * Extracts sub-series from series.
     *
     * @param data The series.
     * @param start The start position.
     * @param end The end position
     * @return sub-series from start to end.
     */
    private static double[] getSubSeries(double[] data, int start, int end) {
        double[] vals = new double[end - start];
        for (int i = 0; i < end - start; i++) {
            vals[i] = data[start + i];
        }
        return vals;
    }

    /**
     * Brute force calculation of the distances.
     *
     * @param tsData timeseries.
     * @param dataAttributeName The pointer onto data-bearing attribute.
     * @param controls The control values.
     * @param window Window size.
     * @throws TSException if error occurs.
     */
    public static void maxDistances(Dataset<Instance> tsData, String dataAttributeName, int[] controls,
            int window) throws TSException {
        // get the timestamps and data attributes
        //
        Attribute dataAttribute = null;
        dataAttribute = tsData.attributeBuilder().build(dataAttributeName, "double");

        double[] distances = new double[controls.length];
        int[] maxPos = new int[controls.length];

        for (int i = 0; i < controls.length; i++) {
            distances[i] = Double.MAX_VALUE;
            maxPos[i] = -1;
        }

        // [1.0] PREPROCESSING: in the sliding window loop build SAX string entries
        //
        int currPosition = 0;
        while ((currPosition + window) < tsData.attributeCount()) {

            double[] vals = getSubSeries(tsData, dataAttribute, currPosition, currPosition + window);

            for (int i = 0; i < controls.length; i++) {
                if (Math.abs(controls[i] - currPosition) < window) {
                    continue;
                } else {
                    double[] oVals = getSubSeries(tsData, dataAttribute, controls[i], controls[i] + window);
                    double dist = EuclideanDistance.distance(vals, oVals);
                    if (distances[i] > dist) {
                        distances[i] = dist;
                        maxPos[i] = currPosition;
                    }
                }
            }
            currPosition++;
        }

        // for (int i = 0; i < controls.length; i++) {
        // System.out.println(controls[i] + " - " + distances[i] + ", at " + maxPos[i]);
        // }
        // for (int i = 0; i < controls.length; i++) {
        // double[] is = getSubSeries(tsData, dataAttribute, controls[i], controls[i] + window);
        // double[] os = getSubSeries(tsData, dataAttribute, maxPos[i], maxPos[i] + window);
        // System.out.println(Arrays.toString(is) + "\n" + Arrays.toString(os));
        // }
    }

    /**
     *
     * "We are given n, the length of the discords in advance, and we must
     * choose two parameters, the cardinality of the SAX alphabet size a, and
     * the SAX word size w. We defer a discussion of how to set these parameters
     * until"
     *
     *
     * @param tsData timeseries.
     * @param windowLength window length.
     * @param paaSize The PAA window size.
     * @param alphabetSize The SAX alphabet size.
     * @param timeAttributeName Time-stamp attribute.
     * @param dataAttributeName Value attribute.
     * @return top discords for the time-series given
     * @throws TSException if error occurs.
     */
    public static DiscordRecords getBruteForceDiscords(Dataset<Instance> tsData, int windowLength,
            int paaSize, int alphabetSize, String timeAttributeName, String dataAttributeName)
            throws TSException {

        double[] cuts = normalAlphabet.getCuts(alphabetSize);

        // get the timestamps and data attributes
        //
        Attribute dataAttribute = tsData.getAttribute(dataAttributeName);
        double[] theRawData = TSUtils.normalize(tsData.instance(dataAttribute.getIndex()).arrayCopy());

        // Init variables
        //
        DiscordRecords discords = new DiscordRecords(10);
        DiscordRecord discord = new DiscordRecord();

        XMLGregorianCalendar cTstamp = makeTimestamp(System.currentTimeMillis());

        // run the search loop
        //
        for (int i = 0; i < tsData.attributeCount() - windowLength; i++) {

            if (i % 100 == 0) {
                XMLGregorianCalendar nTstamp = makeTimestamp(System.currentTimeMillis());
                System.out.println("i: " + i + ", at: " + nTstamp + ", diff: "
                        + diff(cTstamp, nTstamp) + ", discord at: " + discord.getPosition()
                        + ", distance: " + discord.getDistance());
                cTstamp = nTstamp;
            }

            // fix the i-s string
            //
            // char[] ssA = TSUtils.ts2String(TSUtils.paa(TSUtils.normalize(Arrays
            // .copyOfRange(theRawData, i, i + windowLength)), paaSize), cuts);
            //
            char[] ssA = TSUtils.ts2String(
                    TSUtils.paa(Arrays.copyOfRange(theRawData, i, i + windowLength), paaSize), cuts);

            Integer nearestNeighborDist = Integer.MAX_VALUE;

            // the inner loop
            //
            for (int j = 0; j < tsData.attributeCount() - windowLength; j++) {

                // check for the trivial match
                //
                if (Math.abs(i - j) >= windowLength) {

                    // get the SAX approximations of both series here
                    //
                    // char[] ssB = TSUtils.ts2String(TSUtils.paa(TSUtils.normalize(Arrays
                    // .copyOfRange(theRawData, j, j + windowLength)), paaSize), cuts);
                    char[] ssB = TSUtils.ts2String(
                            TSUtils.paa(Arrays.copyOfRange(theRawData, j, j + windowLength), paaSize), cuts);

                    // get the distance here and early terminate if it's less than the
                    // largest
                    //
                    Integer tmpDist = strDistance(ssA, ssB);
                    // System.out.println(String.valueOf(ssA) + " VS " +
                    // String.valueOf(ssB)
                    // + " : " + tmpDist);
                    if (tmpDist == 0 || tmpDist < discords.getMinDistance()) {
                        break;
                    }
                    if (tmpDist < nearestNeighborDist) {
                        nearestNeighborDist = tmpDist;
                    }
                }

            }
            if ((nearestNeighborDist != Integer.MAX_VALUE)
                    && (nearestNeighborDist > discords.getMinDistance())) {
                discord.setDistance(nearestNeighborDist);
                discord.setIndex(i);
                discords.add(discord);
                discord = new DiscordRecord();
            }
        } // i loop - outer
        return discords;
    }
    private static final String factoryErrorMsg = "Bad DataTypeFactory";

    private static XMLGregorianCalendar makeTimestamp(long timeInMillis) {
        DatatypeFactory factory = null;
        try {
            factory = DatatypeFactory.newInstance();
            GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTimeInMillis(timeInMillis);
            return factory.newXMLGregorianCalendar(calendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(factoryErrorMsg, e);
        }
    }

    /**
     * Returns differences between time1 and time2 in milliseconds.
     *
     * @param time1 Start.
     * @param time2 End.
     * @return Difference between two times in milliseconds.
     */
    public static long diff(XMLGregorianCalendar time1, XMLGregorianCalendar time2) {
        long millis1 = time1.toGregorianCalendar().getTimeInMillis();
        long millis2 = time2.toGregorianCalendar().getTimeInMillis();
        return millis2 - millis1;
    }

    /**
     *
     * "We are given n, the length of the discords in advance, and we must
     * choose two parameters, the cardinality of the SAX alphabet size a, and
     * the SAX word size w. We defer a discussion of how to set these parameters
     * until"
     *
     *
     * @param tsData timeseries.
     * @param windowLength window length.
     * @param paaSize The PAA window size.
     * @param alphabetSize The SAX alphabet size.
     * @return top discords for the time-series given
     * @throws TSException if error occurs.
     */
    public static DiscordRecords getBruteForceDiscords(double[] tsData, int windowLength,
            int paaSize, int alphabetSize) throws TSException {

        double[] cuts = normalAlphabet.getCuts(alphabetSize);

        double[] theRawData = TSUtils.normalize(tsData);

        // Init variables
        //
        DiscordRecords discords = new DiscordRecords(10);
        DiscordRecord discord = new DiscordRecord();

        XMLGregorianCalendar cTstamp = makeTimestamp(System.currentTimeMillis());

        // run the search loop
        //
        for (int i = 0; i < tsData.length - windowLength; i++) {

            if (i % 100 == 0) {
                XMLGregorianCalendar nTstamp = makeTimestamp(System.currentTimeMillis());
                System.out.println("i: " + i + ", at: " + nTstamp + ", diff: "
                        + diff(cTstamp, nTstamp) + ", discord at: " + discord.getPosition()
                        + ", distance: " + discord.getDistance());
                cTstamp = nTstamp;
            }

            // fix the i-s string
            //
            // char[] ssA = TSUtils.ts2String(TSUtils.paa(TSUtils.normalize(Arrays
            // .copyOfRange(theRawData, i, i + windowLength)), paaSize), cuts);
            //
            char[] ssA = TSUtils.ts2String(
                    TSUtils.paa(Arrays.copyOfRange(theRawData, i, i + windowLength), paaSize), cuts);

            Integer nearestNeighborDist = Integer.MAX_VALUE;

            // the inner loop
            //
            for (int j = 0; j < tsData.length - windowLength; j++) {

                // check for the trivial match
                //
                if (Math.abs(i - j) >= windowLength) {

                    // get the SAX approximations of both series here
                    //
                    // char[] ssB = TSUtils.ts2String(TSUtils.paa(TSUtils.normalize(Arrays
                    // .copyOfRange(theRawData, j, j + windowLength)), paaSize), cuts);
                    char[] ssB = TSUtils.ts2String(
                            TSUtils.paa(Arrays.copyOfRange(theRawData, j, j + windowLength), paaSize), cuts);

                    // get the distance here and early terminate if it's less than the
                    // largest
                    //
                    Integer tmpDist = strDistance(ssA, ssB);
                    // System.out.println(String.valueOf(ssA) + " VS " +
                    // String.valueOf(ssB)
                    // + " : " + tmpDist);
                    if (tmpDist == 0 || tmpDist < discords.getMinDistance()) {
                        break;
                    }
                    if (tmpDist < nearestNeighborDist) {
                        nearestNeighborDist = tmpDist;
                    }
                }

            }
            if ((nearestNeighborDist != Integer.MAX_VALUE)
                    && (nearestNeighborDist > discords.getMinDistance())) {
                discord.setDistance(nearestNeighborDist);
                discord.setIndex(i);
                discords.add(discord);
                discord = new DiscordRecord();
            }
        } // i loop - outer
        return discords;
    }
}
