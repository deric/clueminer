package org.clueminer.moleculepanel;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
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
        SmilesParser smilesParser = new SmilesParser(DefaultChemObjectBuilder.getInstance());
        IMolecule molecule = smilesParser.parseSmiles(smiles);
        FixBondOrdersTool fbot = new FixBondOrdersTool();
        molecule = fbot.kekuliseAromaticRings(molecule);

    }
}
