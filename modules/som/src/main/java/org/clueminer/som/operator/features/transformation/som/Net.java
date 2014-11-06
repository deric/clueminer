package org.clueminer.som.operator.features.transformation.som;

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

import com.rapidminer.datatable.DataTable;
import com.rapidminer.datatable.DataTableRow;
import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Attributes;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.tools.RandomGenerator;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.math.MathFunctions;
import com.rapidminer.tools.math.matrix.CovarianceMatrix;

/**
 *
 * @author Jan Motl
 */
public class Net {

    public int sizeX;							//size of the net horizontally
    public int sizeY;							//size of the net vertically
    public Node[][] net; 						//the net of the nodes
    public TreeMap<String, Integer> allClasses;	//set of all labels
    public boolean labelIsNominal;				//true if the label is nominal, false otherwise
    private boolean useToroidNetwork; 			//defines whether to use a border-less map or with borders
    private RandomGenerator generator;			//source or random numbers
    private double learningRateInitial;			//the initial learning rate. Commonly 0.1
    private int learningFunction;				//which function to use for time decoy

    public double[] minValue; //minimal values in the net, used for initialization and to plot legend
    public double[] maxValue;
    public double explainedVariance; //percentage of explained variance by SOM in data

    private double minPValue = Double.POSITIVE_INFINITY; //minimal value in U-Matrix
    private double maxPValue = Double.NEGATIVE_INFINITY;
    private double meanPValue = 0;

    private double minUStarValue = Double.POSITIVE_INFINITY; //minimal value in U*-Matrix
    private double maxUStarValue = Double.NEGATIVE_INFINITY;

    // constructor - estimate the optimal net size
    public Net(ExampleSet exampleSet, boolean useToroidNetwork, RandomGenerator generator, double learningRateInitial, int learningFunction, int numberOfIterations) {
        this.net = new Node[sizeX][sizeY];
        this.useToroidNetwork = useToroidNetwork;
        this.generator = generator;
        this.learningRateInitial = learningRateInitial;
        this.learningFunction = learningFunction;

        // calculate sizeX and sizeY and store it into global variables
        calculateOptimalNetSize(exampleSet);

        // continue like we have already know the net size...
        initializeNet(exampleSet, numberOfIterations);
    }

    // constructor - the size is predefined
    public Net(ExampleSet exampleSet, int sizeX, int sizeY, boolean useToroidNetwork, RandomGenerator generator, double learningRateInitial, int learningFunction, int numberOfIterations) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.net = new Node[sizeX][sizeY];
        this.useToroidNetwork = useToroidNetwork;
        this.generator = generator;
        this.learningRateInitial = learningRateInitial;
        this.learningFunction = learningFunction;

        initializeNet(exampleSet, numberOfIterations);
    }

    // the first thing to do once Net is called
    private void initializeNet(ExampleSet exampleSet, int numberOfIterations) {
        // Get data matrix from ExampleSet
        double[][] data = getMatrix(exampleSet);

        // Initialize SOM with PCA
        net = new Node[sizeX][sizeY];
        initializeNetworkWithPCA(exampleSet, data);

        // Initialize neighbors references
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (x < (sizeX - 1)) {
                    net[x][y].nRight = net[x + 1][y];
                }

                if ((y % 2 == 0) && (y < (sizeY - 1))) {
                    net[x][y].nBottomRight = net[x][y + 1];
                } else if ((y % 2 == 1) && (x < sizeX - 1) && (y < (sizeY - 1))) {
                    net[x][y].nBottomRight = net[x + 1][y + 1];
                }

                if ((y % 2 == 0) && (x > 0) && (y < (sizeY - 1))) {
                    net[x][y].nBottomLeft = net[x - 1][y + 1];
                } else if ((y % 2 == 1) && (y < (sizeY - 1))) {
                    net[x][y].nBottomLeft = net[x][y + 1];
                }

                if (x > 0) {
                    net[x][y].nLeft = net[x - 1][y];
                }

                if ((y % 2 == 0) && (y > 0)) {
                    net[x][y].nTopRight = net[x][y - 1];
                } else if ((y % 2 == 1) && (x < sizeX - 1) && (y > 0)) {
                    net[x][y].nTopRight = net[x + 1][y - 1];
                }

                if ((y % 2 == 0) && (x > 0) && (y > 0)) {
                    net[x][y].nTopLeft = net[x - 1][y - 1];
                } else if ((y % 2 == 1) && (y > 0)) {
                    net[x][y].nTopLeft = net[x][y - 1];
                }
            }
        }

        // Initialize clusters to unique value
        int cluster = 0;

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                net[x][y].cluster = cluster++;
            }
        }

        // run calculation
        calculate(data, exampleSet, numberOfIterations); //calculate SOM
    }

    // return Node
    public Node at(int x, int y) {
        return net[x][y];
    }

    // perform training of SOM (all steps)
    public void calculate(double[][] data, ExampleSet exampleSet, int numberOfIterations) {
        int iterationCount = 0;

        // Iteratively update the net
        while (iterationCount < numberOfIterations) {
            int dataSelector = generator.nextInt(data.length); //select data randomly
            int[] bestNode = findBMU(data[dataSelector]); //find the closest node to the selected data
            update(data[dataSelector], iterationCount, numberOfIterations, bestNode[0], bestNode[1]); //update net
            iterationCount++;
        }

        // Once done assign the samples to the nods (useful for histogram, tooltip info and P-Matrix)
        for (int i = 0; i < data.length; i++) {
            // calculate position of the sample in the hexmap
            int[] bestNode = findBMU(data[i]);

            // store the numerical values
            net[bestNode[0]][bestNode[1]].samples.add(data[i]);

            // store special (textual) attributes from exampleSet
            Example example = exampleSet.getExample(i);
            String[] texts = new String[exampleSet.getAttributes().specialSize()];		// create temporary storage
            int index = 0;

            Iterator<AttributeRole> it = exampleSet.getAttributes().specialAttributes();
            while (it.hasNext()) {
                AttributeRole role = it.next();
                Attribute specialAttribute = role.getAttribute();
                texts[index] = (example.getValueAsString(specialAttribute));
                index++;
            }
            net[bestNode[0]][bestNode[1]].samplesTexts.add(texts);	// store the textual values
        }

        // normalize the final weight to 0..1
        for (int i = 0; i < net[0][0].weights.length; i++) {
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    net[x][y].normWeights[i] = (net[x][y].weights[i] - minValue[i]) / (maxValue[i] - minValue[i]);
                }
            }
        }

        // calculate U-Matrix
        updateUDistances();

        // get labels
        getLabels(exampleSet);

        // calculate histogram
        histogram(exampleSet);

        // calculate the fraction of unexplained variance
        explainedVariance = getExplainedVarince(data);
    }

	// update all Nodes in SOM (one step)
    // for algorithm description see http://www.ai-junkie.com/ann/som/som1.html
    // but nicer explanation is at http://www.shy.am/wp-content/uploads/2009/01/kohonen-self-organizing-maps-shyam-guthikonda.pdf
    private void update(double data[], int iterationCount, int numberOfIterations, int positionX, int positionY) {

        //recalculate neighborhood size
        int mapRadius = Math.max(sizeX, sizeY) / 2; //sigma at t=0 "radius of the map"
        double timeConstant = numberOfIterations / Math.log(mapRadius); // lambda "speed of decay"
        double sigma = mapRadius * Math.exp(-iterationCount / timeConstant); // "radius of the neighborhood, decreasing with time"

        // recalculate learning rate
        double learningRate = 0.5;

        switch (learningFunction) {
            case 0:
                // Linear (from initial to initial/100)
                learningRate = learningRateInitial - iterationCount / (numberOfIterations - 1.00) * (learningRateInitial - learningRateInitial / 100.0);
                break;
            case 1:
			// Inverse time (see http://www.cis.hut.fi/somtoolbox/documentation/somalg.shtml)
                // alpha(t) = a / (t+b), where a and b are chosen suitably below, they are chosen so that alpha_fin = alpha_ini/100
                double b = (double) numberOfIterations / (100f - 1f);
                double a = b * learningRateInitial;
                learningRate = a / (iterationCount + b);
                break;
            case 2:
                // Power (from initial to initial/100)
                learningRate = learningRateInitial * Math.pow(1f / 100f, (iterationCount / numberOfIterations));
                break;
            case 3:
                // Exponential (note: numberOfIterations in denominator works better than timeConstant)
                learningRate = learningRateInitial * Math.exp(-iterationCount / numberOfIterations);
                break;
        }

        //update each node in the net
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                //double distance=Math.pow(positionX-x, 2)+Math.pow(positionY-y, 2); // euclidean Cartesian distance from BMU
                double distance = Math.pow(hexagonalDistance(positionX, positionY, x, y), 2);
                distance = Math.exp(-0.5 * distance / Math.pow(sigma, 2));  // gaussian distance from BMU (the input distance is squared)
                for (int dim = 0; dim < net[x][y].weights.length; dim++) {
                    net[x][y].weights[dim] = net[x][y].weights[dim] + distance * learningRate * (data[dim] - net[x][y].weights[dim]);
                }

            }
        }

    }

	// Hexagonal distance
    // for description see: http://www-cs-students.stanford.edu/~amitp/Articles/HexLOS.html
    private int hexagonalDistance(int Aax, int Aay, int Bax, int Bay) {
        // Use Torrid network instead of a sheet
        if (useToroidNetwork) {
            Bax = (Bax - Aax) < (-Bax + Aax + sizeX) ? Bax : Bax - sizeX;
            Bay = (Bay - Aay) < (-Bay + Aay + sizeY) ? Bay : Bay - sizeY;
        }

        // convert array coordinates to  hexspace coordinates
        int Ax = Aax - ((Aay >= 0) ? Aay / 2 : (Aay - 1) / 2);		// Aax - Floor(Aay/2);
        int Ay = Aax + ((Aay >= 0) ? (Aay + 1) / 2 : Aay / 2);		// Aax + Ceil(Aay/2);
        int Bx = Bax - ((Bay >= 0) ? Bay / 2 : (Bay - 1) / 2);
        int By = Bax + ((Bay >= 0) ? (Bay + 1) / 2 : Bay / 2);
        // calculate distance using hexcoords as per previous algorithm
        int dx = Bx - Ax;
        int dy = By - Ay;
        int distance = (Math.abs(dx) + Math.abs(dy) + Math.abs(dx - dy)) / 2;

        return distance;
    }

    // find the best Node
    public int[] findBMU(double[] dataVector) {
        double bestDistance = Double.POSITIVE_INFINITY;
        int[] bestNode = new int[2];

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                double currentDistance = net[x][y].getDistance(dataVector);
                if (currentDistance < bestDistance) {
                    bestDistance = currentDistance;
                    bestNode[0] = x;
                    bestNode[1] = y;
                }
            }
        }

        return bestNode;
    }

    // create histogram (use exampleSet to get the index of the class attribute)
    public void histogram(ExampleSet exampleSet) {
        // check presence of label column
        if (exampleSet.getAttributes().getLabel() == null) {
            // if no label column is present count all samples as if they belonged to one class
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    double[] hist = {net[x][y].samples.size()};
                    net[x][y].histogram = hist;
                }
            }
        } else {
            // get index of the class column among special attributes
            int classColumn = 0;
            Iterator<AttributeRole> it = exampleSet.getAttributes().specialAttributes();
            while (it.hasNext()) {
                AttributeRole role = it.next();
                Attribute specialAttribute = role.getAttribute();
                if (exampleSet.getAttributes().getLabel() == specialAttribute) {
                    break;
                }
                classColumn++;
            }

            // set labelIsNominal parameter
            if (exampleSet.getAttributes().getLabel().isNominal()) {
                labelIsNominal = true;
            } else {
                labelIsNominal = false;
            }

            // walk thru all nodes to get some idea about the content
            allClasses = new TreeMap<String, Integer>();

            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    net[x][y].labels.clear();

                    for (String[] sampleTexts : net[x][y].samplesTexts) {	// walk thru all samples in a node
                        // extract className
                        String className = sampleTexts[classColumn];
                        // update local (node) statistics
                        Integer currentValue = net[x][y].labels.get(className);
                        if (currentValue == null) {
                            currentValue = 0;
                        }
                        net[x][y].labels.put(className, currentValue + 1);
                        // update global (net) statistics
                        currentValue = allClasses.get(className);
                        if (currentValue == null) {
                            currentValue = 0;
                        }
                        allClasses.put(className, currentValue + 1);
                    }
                }
            }

            // walk thru all nodes and create a histogram matrix
            for (int y = 0; y < sizeY; y++) {
                for (int x = 0; x < sizeX; x++) {
                    double[] histogram = new double[allClasses.size()];
                    int index = 0;
                    for (Map.Entry<String, Integer> className : allClasses.entrySet()) {
                        if ((net[x][y].labels.get(className.getKey())) == null) {
                            histogram[index] = 0;
                        } else {
                            histogram[index] = net[x][y].labels.get(className.getKey());
                        }
                        index++;
                    }
                    net[x][y].histogram = histogram;
                }
            }
        }

        // Get Maximum value
        double maxHistogramValue = Double.NEGATIVE_INFINITY;

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int i = 0; i < net[x][y].histogram.length; i++) {
                    maxHistogramValue = MathFunctions.robustMax(maxHistogramValue, net[x][y].histogram[i]);
                }
            }
        }

        //  Normalizing histogram circle
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int i = 0; i < net[x][y].histogram.length; i++) {
                    net[x][y].histogram[i] = net[x][y].histogram[i] / maxHistogramValue;
                }
            }
        }

    }

    // OBSOLETE, but time conversion,... is nice create legend
    public String[][] legend(DataTable dataTable, int legendByColumn) {
        String legendName = null;
        String[][] legendList = new String[sizeX][sizeY];
        double[] dataSample = new double[dataTable.getColumnNumber()];
        int[] bestNode = new int[2];

        // Clear labels
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                net[x][y].clearAllLabels();
            }
        }

        // Add labels from the desired column
        Iterator<DataTableRow> i = dataTable.iterator();
        while (i.hasNext()) {
            DataTableRow row = i.next();
            for (int j = 0; j < dataTable.getColumnNumber(); j++) {
                dataSample[j] = row.getValue(j);
            }
            bestNode = findBMU(dataSample); // this is the BMU

            // Prepare labels
            if (legendByColumn >= 0) {
                double nameValue = row.getValue(legendByColumn);
                if (dataTable.isDate(legendByColumn)) {
                    legendName = Tools.formatDate(new Date((long) nameValue));
                } else if (dataTable.isTime(legendByColumn)) {
                    legendName = Tools.formatTime(new Date((long) nameValue));
                } else if (dataTable.isDateTime(legendByColumn)) {
                    legendName = Tools.formatDateTime(new Date((long) nameValue));
                } else if (dataTable.isNominal(legendByColumn)) {
                    legendName = dataTable.mapIndex(legendByColumn, (int) nameValue);
                } else {
                    legendName = Tools.formatIntegerIfPossible(nameValue);
                }
            }

            // Put elements to the map
            net[bestNode[0]][bestNode[1]].addLabel(legendName);
        }

        // Extract the most common label
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                legendList[x][y] = net[x][y].getLabel();
            }
        }

        return legendList;
    }

    // create labels (ExampleSet is used to get the number of attributes )
    public void getLabels(ExampleSet exampleSet) {

        // iterate over all attributes
        for (int labelColumn = 0; labelColumn < exampleSet.getAttributes().allSize(); labelColumn++) {

            // Clear temporary storage
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    net[x][y].clearAllLabels();
                }
            }

            // Collect all possible labels
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    // common attributes (doubles)
                    if (labelColumn < exampleSet.getAttributes().size()) {
                        for (double[] sample : net[x][y].samples) {
                            String labelName = Tools.formatIntegerIfPossible(sample[labelColumn]);
                            net[x][y].addLabel(labelName);
                        }
                    } // special attributes (texts)
                    else {
                        for (String[] sample : net[x][y].samplesTexts) {
                            String labelName = sample[labelColumn - exampleSet.getAttributes().size()];
                            net[x][y].addLabel(labelName);
                        }
                    }

                }
            }

            // Extract the most common label and store it
            for (int x = 0; x < sizeX; x++) {
                for (int y = 0; y < sizeY; y++) {
                    net[x][y].labelList.add(labelColumn, net[x][y].getLabel());
                }
            }

        }

    }

    // get U distances
    public void updateUDistances() {
        // Initialize extremes for normalization
        double maxUValue = Double.NEGATIVE_INFINITY; //maximal value in U-Matrix
        double maxUValueGlobal = Double.NEGATIVE_INFINITY;

        // Calculate raw values
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                // Initialize own value to zero
                net[x][y].uDistance = 0;
                int neighbourCount = 0;

                // Calculate edge values
                if (net[x][y].nRight != null) {
                    net[x][y].uDistanceRight = net[x][y].getDistance(net[x][y].nRight.weights); // on the right
                    net[x][y].uDistance += net[x][y].uDistanceRight;
                    neighbourCount++;
                }
                if (net[x][y].nBottomRight != null) {
                    net[x][y].uDistanceBottomRight = net[x][y].getDistance(net[x][y].nBottomRight.weights); //on the bottom right
                    net[x][y].uDistance += net[x][y].uDistanceBottomRight;
                    neighbourCount++;
                }
                if (net[x][y].nBottomLeft != null) {
                    net[x][y].uDistanceBottomLeft = net[x][y].getDistance(net[x][y].nBottomLeft.weights); //on the bottom left
                    net[x][y].uDistance += net[x][y].uDistanceBottomLeft;
                    neighbourCount++;
                }

                // Calculate own value (add another 3 neighbors)
                if (net[x][y].nLeft != null) {
                    net[x][y].uDistance += net[x][y].nLeft.uDistanceRight;
                    neighbourCount++;
                }
                if (net[x][y].nTopLeft != null) {
                    net[x][y].uDistance += net[x][y].nTopLeft.uDistanceBottomRight;
                    neighbourCount++;
                }
                if (net[x][y].nTopRight != null) {
                    net[x][y].uDistance += net[x][y].nTopRight.uDistanceBottomLeft;
                    neighbourCount++;
                }

                // Normalize own value
                if (neighbourCount > 0) {
                    net[x][y].uDistance = net[x][y].uDistance / neighbourCount;
                }

                // Get max
                if (net[x][y].uDistanceRight > maxUValueGlobal) {
                    maxUValueGlobal = net[x][y].uDistanceRight;
                }
                if (net[x][y].uDistanceBottomRight > maxUValueGlobal) {
                    maxUValueGlobal = net[x][y].uDistanceBottomRight;
                }
                if (net[x][y].uDistanceBottomLeft > maxUValueGlobal) {
                    maxUValueGlobal = net[x][y].uDistanceBottomLeft;
                }
                if (net[x][y].uDistance > maxUValueGlobal) {
                    maxUValueGlobal = net[x][y].uDistance;
                }

                if (net[x][y].uDistance > maxUValue) {
                    maxUValue = net[x][y].uDistance;
                }
            }
        }

        // normalize values between 0-1
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                net[x][y].uDistanceOwn = (net[x][y].uDistance / maxUValueGlobal); //own value globally normalized
                net[x][y].uDistanceRight = (net[x][y].uDistanceRight / maxUValueGlobal); //and neighbors...
                net[x][y].uDistanceBottomRight = (net[x][y].uDistanceBottomRight / maxUValueGlobal); //and neighbors...
                net[x][y].uDistanceBottomLeft = (net[x][y].uDistanceBottomLeft / maxUValueGlobal); //and neighbors...

                net[x][y].uDistance = (net[x][y].uDistance / maxUValue); //own value for stripped U-matrix
            }
        }
    }

    // TO FINISH: get radius for P Matrix
    public double getRadius(LinkedList<int[]> dataPosition) {

        // calculating real paretoradius
        double optimalMedian = 0.2013 * dataPosition.size();
        double estimatedRadius = 0;

        // initialize the storage for all the distances. The storage size is given by: N*(N-1)/2
        int[] distance = new int[dataPosition.size() * (dataPosition.size() - 1) / 2];

        // collect distances between any two points
        int index = 0;

        for (int i = 0; i < dataPosition.size(); i++) {
            for (int j = i + 1; j < dataPosition.size(); j++) {
                distance[index] = hexagonalDistance(dataPosition.get(i)[0], dataPosition.get(i)[1], dataPosition.get(j)[0], dataPosition.get(j)[1]);
                index++;
            }
        }

        // sort the distances
        Arrays.sort(distance);

        // finding percentil, closest to paretoradius
        double percentilSetDifference = Double.POSITIVE_INFINITY;
        for (int percentil = 0; percentil < 100; percentil++) {
            int[] nn = new int[dataPosition.size()];
            double radius = distance[(int) Math.round(((double) (percentil * dataPosition.size() * dataPosition.size())) / 100)];  // error  java.lang.ArrayIndexOutOfBoundsException: 497005
            for (int i = 0; i < dataPosition.size(); i++) {
                for (int j = 0; j < dataPosition.size(); j++) {
                    if (hexagonalDistance(dataPosition.get(i)[0], dataPosition.get(i)[1], dataPosition.get(j)[0], dataPosition.get(j)[1]) <= radius) {
                        nn[i]++;
                    }
                }
            }
            Arrays.sort(nn);
            int currentMedian = nn[dataPosition.size() / 2] - 1; //point himself is no real neighbour, but always nearest point
            if (Math.abs(currentMedian - optimalMedian) <= percentilSetDifference) {
                percentilSetDifference = Math.abs(currentMedian - optimalMedian);
            } else {
                estimatedRadius = radius;
                break;
            }
        }

        return estimatedRadius;
    }

    // get P distances
    public void updatePDistances(DataTable dataTable) {
        // skip all if the result is already calculated
        if (minPValue != Double.POSITIVE_INFINITY) {
            return;
        }

        // get a list of samples' position in the hexagonal map
        LinkedList<int[]> dataPosition = new LinkedList<int[]>();		// storage for the position
        double[] dataSample = new double[dataTable.getColumnNumber()];
        int[] bestNode = new int[2];

        Iterator<DataTableRow> iterator = dataTable.iterator();
        while (iterator.hasNext()) {
            DataTableRow row = iterator.next();
            for (int j = 0; j < dataTable.getColumnNumber(); j++) {
                dataSample[j] = row.getValue(j);
            }
            bestNode = findBMU(dataSample);
            dataPosition.add(bestNode);
        }

        // get radius
        double approximatedParetoRadius = 1;  // well, here we are cheating.... use getRadius(dataPosition); instead. But there is a bug
        System.out.println("The calculated radius is: " + approximatedParetoRadius);

        // count number of samples in the radius (Brutal Force)
        for (int Ay = 0; Ay < sizeY; Ay++) {
            for (int Ax = 0; Ax < sizeX; Ax++) {
                net[Ax][Ay].pDistance = 0;
                for (int By = 0; By < sizeY; By++) {
                    for (int Bx = 0; Bx < sizeX; Bx++) {
                        // change the value
                        if (hexagonalDistance(Ax, Ay, Bx, By) <= approximatedParetoRadius) {
                            net[Ax][Ay].pDistance += net[Bx][By].samples.size();
                        }
                    }
                }
            }
        }

        // get statistics (min, max, mean)
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                minPValue = MathFunctions.robustMin(minPValue, net[x][y].pDistance);
                maxPValue = MathFunctions.robustMax(maxPValue, net[x][y].pDistance);
                meanPValue += net[x][y].pDistance;
            }
        }
        meanPValue = meanPValue / dataPosition.size();

        // normalize the P-Matrix (otherwise it wouldn't create rainbow colors but use class colors - blue, red, green, red, ...)
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                net[x][y].pDistance = (net[x][y].pDistance - minPValue) / (maxPValue - minPValue);
            }
        }
    }

    // get U* distances
    public void updateUStarDistances(DataTable dataTable) {
        // skip all if the result is already calculated
        if (minUStarValue != Double.POSITIVE_INFINITY) {
            return;
        }

        // update input data (P-Matrix) if needed
        if (minPValue == Double.POSITIVE_INFINITY) {
            updatePDistances(dataTable);
        }

		// calculate U*-Matrix
        // for description see http://www.uni-marburg.de/fb12/datenbionik/pdf/pubs/2005/ultsch05ustarf
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                double scaleFactor = (net[x][y].pDistance - meanPValue) / (meanPValue - maxPValue);
                net[x][y].uStarDistance = net[x][y].uDistance * scaleFactor;
            }
        }

        // get statistics (min, max, mean)
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                minUStarValue = MathFunctions.robustMin(minUStarValue, net[x][y].uStarDistance);
                maxUStarValue = MathFunctions.robustMax(maxUStarValue, net[x][y].uStarDistance);
            }
        }

        // normalize the U*-Matrix (otherwise the rainbow colors would overflow)
        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                net[x][y].uStarDistance = (net[x][y].uStarDistance - minUStarValue) / (maxUStarValue - minUStarValue);
            }
        }
    }

    // get amount of explained variance by SOM
    private double getExplainedVarince(double[][] data) {
        // if net size = 1x1, return 0
        if (sizeX == 1 && sizeY == 1) {
            return 0;
        }

        // calculate the mean of the data
        double[] mean = new double[data[0].length];

        for (double sample[] : data) {
            for (int i = 0; i < sample.length; i++) {
                mean[i] += sample[i];
            }
        }

        for (int i = 0; i < data[0].length; i++) {
            mean[i] /= data.length;
        }

        // calculate the variance of the data
        double varianceInData = 0;

        for (double sample[] : data) {
            double distanceFromCentroid = 0;
            for (int i = 0; i < sample.length; i++) {
                distanceFromCentroid += Math.pow(mean[i] - sample[i], 2);
            }
            varianceInData += distanceFromCentroid;
        }

        // calculate variance in SOM
        double unexplainedVariance = 0;

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                for (double[] sample : net[x][y].samples) {
                    double distance = 0;
                    for (int column = 0; column < sample.length; column++) {
                        distance += Math.pow(sample[column] - net[x][y].weights[column], 2);
                    }
                    unexplainedVariance += distance;
                }
            }
        }

        // return proportion of explained variance
        return 1 - unexplainedVariance / varianceInData;
    }

    // initialize the network by PCA
    private void initializeNetworkWithPCA(ExampleSet exampleSet, double[][] data) {
        /*
         // create covariance matrix
         Matrix covarianceMatrix = CovarianceMatrix.getCovarianceMatrix(exampleSet);

         // EigenVector and EigenValues of the covariance matrix
         EigenvalueDecomposition eigenvalueDecomposition = covarianceMatrix.eig();

         // create and deliver results
         double[] eigenvalues = eigenvalueDecomposition.getRealEigenvalues();
         Matrix eigenvectorMatrix = eigenvalueDecomposition.getV();
         double[][] eigenvectors = eigenvectorMatrix.getArray();

         // normalize eigenvectors to unit length and multiply them by corresponding (square-root-of-)eigenvalues
         for (int row = 0; row < 2; row++) {
         double suma=0;
         for (int column = 0; column < data[0].length; column++) {
         suma += eigenvectors[row][column];
         }
         for (int column = 0; column < data[0].length; column++) {
         eigenvectors[row][column] = eigenvectors[row][column] / suma * Math.sqrt(eigenvalues[column]);
         }
         }


         // apply the transformation to the data
         double[][] result = new double[exampleSet.size()][eigenvectors[0].length];
         for (int principal = 0; principal < 2; principal++) {
         for (int row = 0; row < exampleSet.size(); row++){
         result[row][principal]=0;
         for (int column = 0; column < eigenvectors.length; column++) {
         result[row][principal] += eigenvectors[principal][column]*data[row][column];
         }
         }
         }

         // get minimal and maximal values in PCA
         double[] minimalValue={Double.MAX_VALUE, Double.MAX_VALUE};
         double[] maximalValue={Double.MIN_VALUE, Double.MIN_VALUE};
         for (int principal = 0; principal < 2; principal++) {
         for (int row = 0; row < exampleSet.size(); row++) {
         if (minimalValue[principal] > result[row][principal]) minimalValue[principal] = result[row][principal];
         if (maximalValue[principal] < result[row][principal]) maximalValue[principal] = result[row][principal];
         }
         }

         // normalize PCA values to (0..netSize-1)
         int[] netSize = {sizeX-1, sizeY-1};
         for (int principal = 0; principal < 2; principal++) {
         for (int row = 0; row < exampleSet.size(); row++) {
         result[row][principal] = Math.round(netSize[principal] * (result[row][principal] - minimalValue[principal]) / (maximalValue[principal] - minimalValue[principal]));
         }
         }
         */

        // get maximal and minimal values in the data (to randomly initialize the network)
        Attributes attributes = exampleSet.getAttributes();
        minValue = new double[attributes.size()]; //minimal value in the network
        maxValue = new double[attributes.size()];
        int column = 0;

        for (Attribute attribute : attributes) {
            minValue[column] = Double.POSITIVE_INFINITY;
            maxValue[column] = Double.NEGATIVE_INFINITY;

            for (Example example : exampleSet) {
                minValue[column] = MathFunctions.robustMin(minValue[column], example.getValue(attribute));
                maxValue[column] = MathFunctions.robustMax(maxValue[column], example.getValue(attribute));
            }

            column++;
        }

        // randomly initialize node
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                double[] weights = new double[attributes.size()];
                for (int i = 0; i < attributes.size(); i++) {
                    weights[i] = generator.nextDoubleInRange(minValue[i], maxValue[i]);
                }
                net[x][y] = new Node(weights);
            }
        }

        /*
         // initialize weights in x,y coordinates with PCA (just use the last sample)
         for (int row = 0; row < exampleSet.size(); row++) {
         int x = (int)result[row][0];
         int y = (int)result[row][1];
         System.out.println(x);
         System.out.println(y);
         System.out.println(row + " from: " + data.length);
         net[x][y] = new Node(data[row]);
         }
         */
    }

    // calculate the optimal net size with PCA
    private void calculateOptimalNetSize(ExampleSet exampleSet) {
        // declare variables
        double munits = 5 * Math.sqrt(exampleSet.size());	// optimal number of units in the net
        double ratio = 1;									// the default aspect ratio of the net
        boolean useHexagonalMap = true;						// so far we are always using hexagonal map, but in the feature...

        // create covariance matrix
        Matrix covarianceMatrix = CovarianceMatrix.getCovarianceMatrix(exampleSet);

        // EigenVector and EigenValues of the covariance matrix
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
            sizeY = (int) (Math.min(munits, Math.sqrt(munits / ratio * Math.sqrt(0.75))) + 0.5);
        } else {
            sizeY = (int) (Math.min(munits, Math.sqrt(munits / ratio)) + 0.5);
        }
        sizeX = (int) ((munits / sizeY) + 0.5);

        // if actual dimension of the data is 1, make the map 1-D
        if (Math.min(sizeX, sizeY) == 1) {
            sizeY = Math.max(sizeX, sizeY);
            sizeX = 1;
        }

		// a special case: if the map is toroid with hexa lattice,
        // size along first axis must be even
        if (useHexagonalMap && useToroidNetwork && sizeX % 2 == 1) {
            sizeX = sizeX + 1;
        }
    }

    // convert ExampleSet to matrix
    private double[][] getMatrix(ExampleSet exampleSet) {
        // create data matrix
        Attributes attributes = exampleSet.getAttributes();
        double[][] inputMatrix = new double[exampleSet.size()][attributes.size()];

        int column = 0;
        for (Attribute attribute : attributes) {
            int row = 0;
            for (Example example : exampleSet) {
                inputMatrix[row][column] = example.getValue(attribute);
                row++;
            }
            column++;
        }

        return inputMatrix;
    }

    // get clusters
    public void updateClusters() {
        double[][][][] distance = new double[sizeX][sizeY][sizeX][sizeY];

        //calculate distances
        for (int ax = 0; ax < sizeX; ax++) {
            for (int ay = 0; ay < sizeY; ay++) {
                for (int bx = ax + 1; bx < sizeX; bx++) {
                    for (int by = ay + 1; by < sizeY; by++) {
                        distance[ax][ay][bx][by] = net[ax][ay].getDistance(net[bx][by].weights);
                    }
                }
            }
        }

		// measure clustering efficiency
        // unite clusters until the optimal solution
        TreeSet<Integer> clusters = new TreeSet<Integer>();
        int clusterCounter = 0;
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                clusters.add(clusterCounter++);
            }
        }

        int clusterCount = (sizeX - 1) * sizeY + (2 * sizeX - 1) * (sizeY - 1);

        while (clusterCount > 3) {
            // get minimal distance
            double minDistance = Double.POSITIVE_INFINITY;
            int minDistanceAX = 0;
            int minDistanceAY = 0;
            int minDistanceBX = 0;
            int minDistanceBY = 0;

            for (int ax = 0; ax < sizeX; ax++) {
                for (int ay = 0; ay < sizeY; ay++) {
                    for (int bx = ax + 1; bx < sizeX; bx++) {
                        for (int by = ay + 1; by < sizeY; by++) {
                            if (distance[ax][ay][bx][by] < minDistance) {
                                minDistance = distance[ax][ay][bx][by];
                                minDistanceAX = ax;
                                minDistanceAY = ay;
                                minDistanceBX = bx;
                                minDistanceBY = by;
                                distance[ax][ay][bx][by] = Double.POSITIVE_INFINITY;
                            }
                        }
                    }
                }
            }

            //unite clusters
            System.out.println(" " + minDistanceAX + " " + minDistanceAY + " " + clusterSim(clusters));

            net[minDistanceBX][minDistanceBY].cluster = net[minDistanceAX][minDistanceAY].cluster;
            clusters.remove(net[minDistanceBX][minDistanceBY].cluster);
            clusterCount--;
        }

    }

    private double withinClusterAvgSim(int cluster) {
        double distance = 0;
        int countOfLinks = 0;

        for (int ax = 0; ax < sizeX; ax++) {
            for (int ay = 0; ay < sizeY; ay++) {
                for (int bx = ax + 1; bx < sizeX; bx++) {
                    for (int by = ay + 1; by < sizeY; by++) {
                        if (net[ax][ay].cluster == cluster && net[bx][by].cluster == cluster) {
                            distance += net[ax][ay].getDistance(net[bx][by].weights); // increase distance
                            countOfLinks++; // increment number of links
                        }

                    }
                }
            }
        }

        return distance / countOfLinks;
    }

    private double withenClusterAvgSim(int clusterA, int clusterB) {
        double distance = 0;
        int countOfLinks = 0;

        for (int ax = 0; ax < sizeX; ax++) {
            for (int ay = 0; ay < sizeY; ay++) {
                for (int bx = ax + 1; bx < sizeX; bx++) {
                    for (int by = ay + 1; by < sizeY; by++) {
                        if (net[ax][ay].cluster == clusterA && net[bx][by].cluster == clusterB) {
                            distance += net[ax][ay].getDistance(net[bx][by].weights); // increase distance
                            countOfLinks++; // increment number of links
                        }

                    }
                }
            }
        }

        return distance / countOfLinks;
    }

    private double clusterSim(TreeSet<Integer> clusters) {
        double maxR = Double.NEGATIVE_INFINITY;

        for (int clusterA : clusters) {
            for (int clusterB : clusters) {
                double R = ((withinClusterAvgSim(clusterA) + withinClusterAvgSim(clusterB)) / withenClusterAvgSim(clusterA, clusterB));
                if (R > maxR) {
                    maxR = R;
                }
            }
        }

        return maxR / clusters.size();
    }
}
