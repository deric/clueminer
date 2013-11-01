
package org.clueminer.moleculepanel;

import javax.vecmath.Point2d;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.FixBondOrdersTool;
import org.openscience.cdk.smiles.SmilesParser;

/**
 *
 * @author deric
 */
public class MoleculesGroupTest {

    private static MoleculesGroup subject = new MoleculesGroup();

    public MoleculesGroupTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of plateUpdate method, of class MoleculesGroup.
     */
    @Test
    public void testPlateUpdate() {
    }

    /**
     * Test of clusterSelected method, of class MoleculesGroup.
     */
    @Test
    public void testSmiliesPainting() throws InvalidSmilesException, CDKException {

        String smiles = "c2ccc3n([H])c1ccccc1c3(c2)";
        SmilesParser smilesParser = new SmilesParser( DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = smilesParser.parseSmiles(smiles);
        FixBondOrdersTool fbot = new FixBondOrdersTool();
        molecule = fbot.kekuliseAromaticRings(molecule);

    }


    /**
     * Test of parseSmiles method, of class MoleculesGroup.
     */
    @Test
    public void testParseSmiles() throws CDKException {
        IMolecule molecule = subject.parseSmiles("COc1ccc(cc1OC)C(=O)NCc2ccc(OCCN(C)C)cc2.Cl");
        FixBondOrdersTool fbot = new FixBondOrdersTool();
        molecule = fbot.kekuliseAromaticRings(molecule);
        assertNotNull(molecule);
        assertEquals(27, molecule.getAtomCount());

        double xmin = Double.POSITIVE_INFINITY;
        double xmax = Double.NEGATIVE_INFINITY;
        double ymin = Double.POSITIVE_INFINITY;
        double ymax = Double.NEGATIVE_INFINITY;

        int i = 0;
        for (IAtom atom : molecule.atoms()) {
            System.out.println("class: " + atom.getClass().getName());
            Point2d point = atom.getPoint2d();
            System.out.println("atom " + (i++));
            System.out.println(atom.toString());
            System.out.println("point: " + point);
            /* if (point == null) {                throw new IllegalArgumentException(
                        "Cannot calculate bounds when 2D coordinates are missing.");

             }*/
            /*xmin = Math.min(xmin, point.x);
             xmax = Math.max(xmax, point.x);
            ymin = Math.min(ymin, point.y);

             ymax = Math.max(ymax, point.y);*/
        }
        double width = xmax - xmin;
        double height = ymax - ymin;
        System.out.println("width = " + width);
        System.out.println("height = " + height);
    }
}