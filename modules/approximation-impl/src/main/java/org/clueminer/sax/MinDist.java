package org.clueminer.sax;

import edu.hawaii.jmotif.datatype.TSException;
import edu.hawaii.jmotif.logic.sax.SAXFactory;
import edu.hawaii.jmotif.logic.sax.alphabet.Alphabet;
import edu.hawaii.jmotif.logic.sax.alphabet.NormalAlphabet;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.distance.api.Distance;
import org.clueminer.math.Vector;

/**
 * Implements SAX MINDIST nominal metric. Uses jMotif library
 * (http://code.google.com/p/jmotif/wiki/SAX). Must be connected with SAX
 * operator.
 *
 * @author Marek Dvoroznak
 * @author Tomas Barton
 */
public class MinDist implements Distance {

    private static final long serialVersionUID = 2961510614729965506L;
    private static final Alphabet normalAlphabet = new NormalAlphabet();
    //private static final String SAX_PARAMETERS_PORT_NAME = "SAX parameters";
    private int alphabetSize = 5;
    // private InputPort SAXParametersPort;
    private Attribute inputAttribute;

    @Override
    public String getName() {
        return "SAX MinDist";
    }

    public double calculateDistance(double[] value1, double[] value2) {
        // value1 and value2 are mappings values of the attributes
        // in our case, there should be only one nominal attribute (inputAttribute)

        String s1 = inputAttribute.getMapping().mapIndex((int) value1[0]);
        String s2 = inputAttribute.getMapping().mapIndex((int) value2[0]);

        double distance;
        try {
            distance = SAXFactory.saxMinDist(s1.toCharArray(), s2.toCharArray(), normalAlphabet.getDistanceMatrix(alphabetSize));
        } catch (TSException e) {
            System.err.println(e);
            distance = Double.NaN;
        }

//        System.out.println(value1[0]+ " " + value2[0]+ " " +s1+" "+s2 + " "+distance);
        return distance;
    }

    public double calculateSimilarity(double[] value1, double[] value2) {
        return -calculateDistance(value1, value2);
    }

    public void init(Dataset exampleSet) {
        //inputAttribute = exampleSet.getAttributes()[0];
        // Dataset es = //SAXParametersPort.getData();
        //  Attribute[] attributes = es.getAttributes().
        //   alphabetSize = (int)es.getExample(0).getNumericalValue(attribute[0]);
//        System.out.println("alphabetSize: "+alphabetSize);
    }

    @Override
    public boolean compare(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getMinValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public double getMaxValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean isSymmetric() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double measure(Vector<Double> x, Vector<Double> y, double[] weights) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSubadditive() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isIndiscernible() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double measure(double[] x, double[] y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
