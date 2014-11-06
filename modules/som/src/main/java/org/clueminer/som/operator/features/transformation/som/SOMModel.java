package org.clueminer.som.operator.features.transformation.som;

import java.util.HashMap;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.AttributeRole;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DataRow;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.AbstractModel;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.Ontology;

public class SOMModel extends AbstractModel {

    private static final long serialVersionUID = 8270281740686080765L;

    public Net network; // SOM network (includes position of data in SOM map)

    private String[] attributeNames; // attribute names

    private String[] specialAttributeNames; // attribute names

    private ExampleSet exampleSet; // input data

    protected SOMModel(ExampleSet exampleSet, Net net) {
        super(exampleSet);
        network = net;

        // get attribute names for renderer
        this.attributeNames = new String[exampleSet.getAttributes().size()];
        int counter = 0;
        for (Attribute attribute : exampleSet.getAttributes()) {
            attributeNames[counter] = attribute.getName();
            counter++;
        }

        // get special attribute names for renderer
        this.specialAttributeNames = new String[exampleSet.getAttributes().specialSize()];
        counter = 0;
        Iterator<AttributeRole> s = exampleSet.getAttributes().specialAttributes();
        while (s.hasNext()) {
            AttributeRole role = s.next();
            Attribute specialAttribute = role.getAttribute();
            specialAttributeNames[counter] = specialAttribute.getName();
            counter++;
        }

        // store exampleSet for renderer
        this.exampleSet = exampleSet;
    }

    @Override
    public String getName() {
        return "SOM Dimensionality Reduction Model";
    }

    @Override
    public String toString() {
        return "Transforms the input data into a new data set with 2 dimensions.";
    }

    @Override
    public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
        // store exampleSet
        this.exampleSet = exampleSet;

        // creating new ExampleSet for output
        List<Attribute> attributes = new LinkedList<Attribute>();
        for (int i = 0; i < 2; i++) {
            attributes.add(AttributeFactory.createAttribute("SOM_" + i, Ontology.NUMERICAL));
        }

        // copy special attributes
        Iterator<AttributeRole> s = exampleSet.getAttributes().specialAttributes();
        Map<Attribute, String> newSpecialAttributes = new HashMap<Attribute, String>();
        while (s.hasNext()) {
            AttributeRole role = s.next();
            Attribute specialAttribute = role.getAttribute();
            Attribute newAttribute = (Attribute) specialAttribute.clone();
            attributes.add(newAttribute);
            newSpecialAttributes.put(newAttribute, role.getSpecialName());
        }

        MemoryExampleTable newDataTable = new MemoryExampleTable(attributes);

        // applying Example on net
        for (Example currentExample : exampleSet) {
            int[] coords = network.findBMU(example2vector(currentExample)); // get coordinates

            double[] exampleData = new double[attributes.size()]; // put SOM coordinates into the exampleSet
            for (int i = 0; i < 2; i++) {
                exampleData[i] = coords[i];
            }
            s = exampleSet.getAttributes().specialAttributes(); // copy special attributes into the exampleSet
            int i = 2; // put it after SOM_1 and SOM_2
            while (s.hasNext()) {
                exampleData[i++] = currentExample.getValue(s.next().getAttribute());
            }
            DataRow newRow = new DoubleArrayDataRow(exampleData);
            newDataTable.addDataRow(newRow);
        }

        return newDataTable.createExampleSet(newSpecialAttributes);
    }

    // Convert Example to vector of Doubles
    private static double[] example2vector(Example example) {
        double[] vector = new double[example.getAttributes().size()];

        int pointer = 0;
        for (Attribute attribute : example.getAttributes()) {
            vector[pointer] = example.getValue(attribute);
            pointer++;
        }

        return vector;
    }

    public ExampleSet getData() {
        return exampleSet;
    }

    public String[] getAttributeNames() {
        return attributeNames;
    }

    public String[] getSpecialAttributeNames() {
        return specialAttributeNames;
    }

}
