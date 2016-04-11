package org.clueminer.sax;

import edu.hawaii.jmotif.datatype.TPoint;
import edu.hawaii.jmotif.datatype.TSUtils;
import edu.hawaii.jmotif.datatype.Timeseries;
import edu.hawaii.jmotif.logic.sax.alphabet.Alphabet;
import edu.hawaii.jmotif.logic.sax.alphabet.NormalAlphabet;
import java.util.Map;
import org.clueminer.attributes.BasicAttrType;
import org.clueminer.dataset.api.Attribute;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.dataset.impl.SampleDataset;
import org.clueminer.exception.OperatorException;

/**
 *
 * @author Tomas Barton
 */
public class SymbolicAggregateApproximation {

    public static final String PARAMETER_PAA_SIZE = "paa_size";
    public static final String PARAMETER_ALPHABET_SIZE = "alphabet_size";
    //  private OutputPort SAXParametersOutputPort;
    private static final Alphabet normalAlphabet = new NormalAlphabet();

    public SymbolicAggregateApproximation() {
        // SAXParametersOutputPort = this.getOutputPorts().createPort("SAX parameters");
    }

    /**
     *
     * @param input
     * @param PAASize      the number of the points used in PAA reduction of the
     *                     time
     *                     series (or the length of the resulting string)
     * @param alphabetSize the alphabet size used
     * @return
     * @throws OperatorException
     */
    public Dataset apply(Dataset input, int PAASize, int alphabetSize) throws OperatorException {
        Dataset output = new SampleDataset(input.size());

        if (PAASize > input.attributeCount()) {
            throw new OperatorException("Parameter PAA_size must not be greater than number of regular attributes in input set.");
        }
        // deliver parameters to output port
        //   double[][] p = {{alphabetSize}, {PAASize}};
        //   ExampleSet params = ExampleSetFactory.createExampleSet(p);
        //   SAXParametersOutputPort.deliver(params);

        Attribute SAXAttributes[] = {input.attributeBuilder().build("SAX_Value", "nominal")};

        //    MemoryExampleTable table = new MemoryExampleTable(SAXAttributes);
        Map<Integer, Attribute> attributes = input.getAttributes();

        for (int i = 0; i < input.size(); i++) {
            int j = 0;
            Timeseries timeSeries = new Timeseries();
            Instance inst = input.instance(i);

            for (Attribute attribute : attributes.values()) {
                double value = inst.value(attribute.getIndex());
                timeSeries.add(new TPoint(value, j));
                j++;
            }

            try {
                // perform the transformation
                String[] SAXValue = {new String(TSUtils.ts2String(TSUtils.paa(timeSeries, PAASize), normalAlphabet, alphabetSize))};

                output.builder().create(SAXValue, SAXAttributes);
                //                System.out.println(SAXValue);
            } catch (Exception e) {
                System.err.println(e);
                throw new OperatorException("Error during transformation.");
            }
        }

        // set special attributes to the resulting example set
        //        List<Attribute> newAttributes = new LinkedList<Attribute>();
        //        Iterator<AttributeRole> itAR = es.getAttributes().specialAttributes();
        //        while (itAR.hasNext()) {
        //            Attribute attribute = itAR.next().getAttribute();
        //            newAttributes.set(attribute);
        //        }
        //        table.addAttributes(newAttributes);
        // return resulting example set
        return output;
    }
    /*
     * @Override public List<ParameterType> getParameterTypes() {
     * List<ParameterType> types = super.getParameterTypes(); types.set(new
     * ParameterTypeInt(PARAMETER_PAA_SIZE, "The number of the points used in
     * PAA reduction of the time series (or the length of the resulting
     * string).", 1, Integer.MAX_VALUE, 10, false)); types.set(new
     * ParameterTypeInt(PARAMETER_ALPHABET_SIZE, "The alphabet size used.", 2,
     * normalAlphabet.getMaxSize(), 5, false));
     *
     * return types; }
     */
}
