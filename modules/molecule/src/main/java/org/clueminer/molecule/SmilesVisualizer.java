package org.clueminer.molecule;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.openide.util.Exceptions;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.AtomNumberGenerator;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.ExtendedAtomGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.generators.RingGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.smiles.DeduceBondSystemTool;
import org.openscience.cdk.smiles.SmilesParser;

/**
 *
 * @author Tomas Barton
 */
public class SmilesVisualizer extends JPanel {

    private static final long serialVersionUID = -1816522111993171756L;
    private IMolecule molecule;
    private AtomContainerRenderer renderer;
    // generators make the image elements
    private List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();

    public SmilesVisualizer(String smiles) {
        try {

            molecule = (Molecule) new SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles(smiles);

            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setMolecule(molecule);
            sdg.generateCoordinates();
            molecule = sdg.getMolecule();
            DeduceBondSystemTool dbt = new DeduceBondSystemTool();
            IMolecule mol = dbt.fixAromaticBondOrders(molecule);
            if (mol != null) {
                molecule = mol;
            }
        } catch (Exception ex) {
            Logger.getLogger(SmilesVisualizer.class.getName()).log(Level.SEVERE, null, ex);
            Exceptions.printStackTrace(ex);
        }

        initComponets();
    }

    private void initComponets() {
        setLayout(new BorderLayout());

        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        generators.add(new RingGenerator());
        generators.add(new ExtendedAtomGenerator());
        generators.add(new AtomNumberGenerator());
        // the renderer needs to have a toolkit-specific font manager
        renderer = new AtomContainerRenderer(generators, new AWTFontManager());
    }

    @Override
    protected void paintComponent(final Graphics g) {

        super.paintComponent(g);
        try {
            Dimension d = getSize();

            // layout is handled by the paint method
            Rectangle2D drawArea = new Rectangle2D.Double(0, 0, d.getWidth(), d.getHeight());
            renderer.paint(molecule, new AWTDrawVisitor((Graphics2D) g), drawArea, true);
        } catch (Exception e) {
            System.out.println("Unable to paint molecule \"" + molecule.toString() + "\": " + e.getMessage());
            Logger.getLogger(MoleculeVizualizer.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
