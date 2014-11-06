package org.clueminer.som.operator.features.transformation.som;

import java.util.Arrays;
import java.util.List;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.learner.CapabilityCheck;
import com.rapidminer.operator.learner.CapabilityProvider;
import com.rapidminer.operator.Model;
import com.rapidminer.operator.Operator;
import com.rapidminer.operator.OperatorCapability;
import com.rapidminer.operator.OperatorDescription;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.operator.ports.InputPort;
import com.rapidminer.operator.ports.OutputPort;
import com.rapidminer.operator.ports.metadata.AttributeMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetMetaData;
import com.rapidminer.operator.ports.metadata.ExampleSetPassThroughRule;
import com.rapidminer.operator.ports.metadata.GenerateModelTransformationRule;
import com.rapidminer.operator.ports.metadata.LearnerPrecondition;
import com.rapidminer.operator.ports.metadata.SetRelation;
import com.rapidminer.operator.UserError;
import com.rapidminer.parameter.conditions.EqualTypeCondition;
import com.rapidminer.parameter.ParameterType;
import com.rapidminer.parameter.ParameterTypeBoolean;
import com.rapidminer.parameter.ParameterTypeCategory;
import com.rapidminer.parameter.ParameterTypeDouble;
import com.rapidminer.parameter.ParameterTypeInt;
import com.rapidminer.parameter.UndefinedParameterError;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.ParameterService;
import com.rapidminer.tools.RandomGenerator;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.math.container.Range;
import com.rapidminer.tools.math.matrix.CovarianceMatrix;

/**
 * This class defines input & output ports and UI for model settings
 *
 * @author Jan Motl
 *
 */
public class SOM extends Operator implements CapabilityProvider {

    public static final String PARAMETER_TRAINING_ROUNDS = "training_rounds";
    public static final String PARAMETER_AUTOMATIC_NET_SIZE = "net_size";
    public static final String PARAMETER_NET_SIZE_X = "net_size_x";
    public static final String PARAMETER_NET_SIZE_Y = "net_size_y";
    public static final String PARAMETER_INITIAL_LEARNING_RATE = "initial_learning_rate";
    public static final String PARAMETER_LEARNING_RATE_FUNCTION = "learning_rate_function";
    public static final String PARAMETER_TOROID_NETWORK = "use_toroid_network";

    public static final String[] NET_SIZE = new String[]{
        "automatic",
        "own",};

    public static final String[] LEARNING_RATE_FUNCTION = new String[]{
        "linear",
        "inverse-of-time",
        "power series",
        "exponential",};

    private InputPort exampleSetInput = getInputPorts().createPort("example set");
    private OutputPort exampleSetOutput = getOutputPorts().createPort("example set");
    private OutputPort originalOutput = getOutputPorts().createPort("original");
    private OutputPort modelOutput = getOutputPorts().createPort("preprocessing model");

    // define input and output ports
    public SOM(OperatorDescription description) {
        super(description);
        exampleSetInput.addPrecondition(new LearnerPrecondition(this, exampleSetInput)); // suggests solutions for attribute conversions...
        getTransformer().addRule(new GenerateModelTransformationRule(exampleSetInput, modelOutput, SOMModel.class));
        getTransformer().addRule(new ExampleSetPassThroughRule(exampleSetInput, exampleSetOutput, SetRelation.EQUAL) {
            @Override
            public ExampleSetMetaData modifyExampleSet(ExampleSetMetaData metaData) throws UndefinedParameterError {
                metaData.clearRegular();
                int netSizeX = getParameterAsInt(PARAMETER_NET_SIZE_X);
                int netSizeY = getParameterAsInt(PARAMETER_NET_SIZE_Y);
                for (int i = 0; i < 2; i++) {
                    AttributeMetaData newAMD = new AttributeMetaData("SOM_" + i, Ontology.REAL);
                    newAMD.setValueRange(new Range(0, netSizeX - 1), SetRelation.EQUAL);
                    newAMD.setValueRange(new Range(0, netSizeY - 1), SetRelation.EQUAL);
                    metaData.addAttribute(newAMD);
                }
                return super.modifyExampleSet(metaData);
            }
        });
        getTransformer().addPassThroughRule(exampleSetInput, originalOutput);
    }

    /**
     * Helper method for anonymous operators.
     *
     * @param exampleSet
     * @return the processed data
     * @throws OperatorException
     */
    public Model doWork(ExampleSet exampleSet) throws OperatorException {
        exampleSetInput.receive(exampleSet);
        doWork();
        return modelOutput.getData();
    }

    @Override
    public void doWork() throws OperatorException {
        ExampleSet exampleSet = exampleSetInput.getData();

        // some checks of input data
        if (exampleSet.getAttributes().size() == 0) {
            throw new UserError(this, 106);
        }
        if (exampleSet.size() == 0) {
            throw new UserError(this, 117);
        }

        // check capabilities and produce errors if they are not fulfilled
        CapabilityCheck check = new CapabilityCheck(this, Tools.booleanValue(ParameterService.getParameterValue(PROPERTY_RAPIDMINER_GENERAL_CAPABILITIES_WARN), true));
        check.checkLearnerCapabilities(this, exampleSet);

        // get and check parameter values
        int numberOfIterations = getParameterAsInt(PARAMETER_TRAINING_ROUNDS);
        int learningFunction = getParameterAsInt(PARAMETER_LEARNING_RATE_FUNCTION);
        double learningRateInitial = getParameterAsDouble(PARAMETER_INITIAL_LEARNING_RATE);
        boolean useToroidNetwork = getParameterAsBoolean(PARAMETER_TOROID_NETWORK);
        RandomGenerator generator = RandomGenerator.getRandomGenerator(this);

        // calculate the network
        Net net;

        if (getParameterAsInt(PARAMETER_AUTOMATIC_NET_SIZE) == 0) { // use heuristic to estimate the optimal net size
            net = new Net(exampleSet, useToroidNetwork, generator, learningRateInitial, learningFunction, numberOfIterations);
        } else {
            int sizeX = getParameterAsInt(PARAMETER_NET_SIZE_X); // use user defined net size
            int sizeY = getParameterAsInt(PARAMETER_NET_SIZE_Y);

            net = new Net(exampleSet, sizeX, sizeY, useToroidNetwork, generator, learningRateInitial, learningFunction, numberOfIterations);
        }

        SOMModel model = new SOMModel(exampleSet, net);

        // output
        if (exampleSetOutput.isConnected()) {
            exampleSetOutput.deliver(model.apply((ExampleSet) exampleSet.clone()));
        }
        originalOutput.deliver(exampleSet);
        modelOutput.deliver(model);
    }

    // description of the block parameters
    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type = new ParameterTypeInt(PARAMETER_TRAINING_ROUNDS, "Defines the number of trainnig rounds", 1, Integer.MAX_VALUE, 1000);
        type.setExpert(false);
        //type.setOptional(false);
        types.add(type);

        type = new ParameterTypeCategory(PARAMETER_AUTOMATIC_NET_SIZE, "The default number of neurons is <i>5 * sqrt(n)</i> where <i>n</i> is the number of training samples. You can use \"explained variance\" of the data by SOM as a hint whether the net is big enough. However, note that if you use more neurons than is the number of samples then it results to overfitting.", NET_SIZE, 0);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_NET_SIZE_X, "Defines the size of the SOM net in the horizontal direction.", 1, Integer.MAX_VALUE, 20);
        type.setExpert(false);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_AUTOMATIC_NET_SIZE, NET_SIZE, true, 1));
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_NET_SIZE_Y, "Defines the size of the SOM net in the vertical direction.", 1, Integer.MAX_VALUE, 10);
        type.setExpert(false);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_AUTOMATIC_NET_SIZE, NET_SIZE, true, 1));
        types.add(type);

        type = new ParameterTypeDouble(PARAMETER_INITIAL_LEARNING_RATE, "Defines the initial learning rate. Commonly the value is between 0 and 1.", 0, Double.POSITIVE_INFINITY, 0.9);
        type.setExpert(true);
        types.add(type);

        type = new ParameterTypeCategory(PARAMETER_LEARNING_RATE_FUNCTION, "The learning rate <i>a(t)</i> is a decreasing function of time between [0,1]. Two commonly used forms are a linear function and a function inversely proportional to time: <i>a(t) = A / (t+B)</i>, where A and B are some suitably selected constants. Hence it decays at the beginning much faster than linear function. Use of the inverse-of-time function ensures that all input samples have approximately equal influence on the training result. Power series is somewhere between linear function and inverse-of-time.", LEARNING_RATE_FUNCTION, 1);
        type.setExpert(true);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_TOROID_NETWORK, "Use a borderless map", Boolean.FALSE);
        type.setExpert(true);
        types.add(type);
        types.addAll(RandomGenerator.getRandomGeneratorParameters(this));

        return types;
    }

    // what does SOM accept, everything else is unsupported
    @Override
    public boolean supportsCapability(OperatorCapability capability) {
        if (capability.equals(OperatorCapability.NUMERICAL_ATTRIBUTES)) {
            return true;
        }
        if (capability.equals(OperatorCapability.BINOMINAL_LABEL)) {
            return true;
        }
        if (capability.equals(OperatorCapability.POLYNOMINAL_LABEL)) {
            return true;
        }
        if (capability.equals(OperatorCapability.NUMERICAL_LABEL)) {
            return true;
        }
        if (capability.equals(OperatorCapability.NO_LABEL)) {
            return true;
        }
        return false;
    }

    // calculate the optimal net size with PCA
    private int[] calculateOptimalNetSize(ExampleSet exampleSet) {
        // declare variables
        int[] netSize = new int[2]; 						// the calculated net size
        double munits = 5 * Math.sqrt(exampleSet.size());	// optimal number of units in the net
        double ratio = 1;									// the default aspect ratio of the net
        boolean useHexagonalMap = true;						// so far we are always using hexagonal map, but in the feature...

        // create covariance matrix
        log("Creating the covariance matrix...");
        Matrix covarianceMatrix = CovarianceMatrix.getCovarianceMatrix(exampleSet);

        // EigenVector and EigenValues of the covariance matrix
        log("Performing the eigenvalue decomposition...");
        EigenvalueDecomposition eigenvalueDecomposition = covarianceMatrix.eig();

        // create and deliver results
        double[] eigenvalues = eigenvalueDecomposition.getRealEigenvalues();
		//Matrix eigenvectorMatrix = eigenvalueDecomposition.getV();
        //double[][] eigenvectors = eigenvectorMatrix.getArray();

        // take two the greatest eigenvalues
        Arrays.sort(eigenvalues); // sort from min to max
        int eigLen = eigenvalues.length - 1; // hence we index from the end

        if (eigenvalues[eigLen] != 0 && eigenvalues[eigLen - 1] * munits >= eigenvalues[eigLen]) {  // if we can...
            ratio = Math.sqrt(eigenvalues[eigLen] / eigenvalues[eigLen - 1]); // ... set ratio between map sidelengths
        }

		// in hexagonal lattice, the sidelengths are not directly
        // proportional to the number of units since the units on the
        // y-axis are squeezed together by a factor of sqrt(0.75).
        // The result is then rounded: (int) (value + 0.5)
        if (useHexagonalMap) {
            netSize[1] = (int) (Math.min(munits, Math.sqrt(munits / ratio * Math.sqrt(0.75))) + 0.5);
        } else {
            netSize[1] = (int) (Math.min(munits, Math.sqrt(munits / ratio)) + 0.5);
        }
        netSize[0] = (int) ((munits / netSize[1]) + 0.5);

        // if actual dimension of the data is 1, make the map 1-D
        if (Math.min(netSize[0], netSize[1]) == 1) {
            netSize[1] = Math.max(netSize[0], netSize[1]);
            netSize[0] = 1;
        }

		// a special case: if the map is toroid with hexa lattice,
        // size along first axis must be even
        if (useHexagonalMap && getParameterAsBoolean(PARAMETER_TOROID_NETWORK) && netSize[0] % 2 == 1) {
            netSize[0] = netSize[0] + 1;
        }

        return netSize;
    }
}
