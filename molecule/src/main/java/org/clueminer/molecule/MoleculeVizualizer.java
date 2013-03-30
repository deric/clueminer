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
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.IGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;

/**
 *
 * @author Tomas Barton
 */
public class MoleculeVizualizer extends JPanel {

    private static final long serialVersionUID = -1816522111993171756L;
    private IAtomContainer molecule;
    private AtomContainerRenderer renderer;
    // generators make the image elements
    private List<IGenerator<IAtomContainer>> generators = new ArrayList<IGenerator<IAtomContainer>>();

    public MoleculeVizualizer(IChemObject molecule) {
        this.molecule = (IAtomContainer) molecule;
        initComponets();
    }

    private void initComponets() {
        setLayout(new BorderLayout());
        
        generators.add(new BasicSceneGenerator());
        generators.add(new BasicBondGenerator());
        generators.add(new BasicAtomGenerator());
        //generators.add(new RingGenerator());
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
