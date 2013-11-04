package org.clueminer.moleculepanel;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;
import org.openide.util.Exceptions;
import org.openscience.cdk.DefaultChemObjectBuilder;
import org.openscience.cdk.Molecule;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.interfaces.IChemObject;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.font.AWTFontManager;
import org.openscience.cdk.renderer.generators.BasicAtomGenerator;
import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
import org.openscience.cdk.smiles.DeduceBondSystemTool;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.templates.MoleculeFactory;
import org.openscience.jchempaint.JChemPaintViewerPanel;

/**
 *
 * @author Tomas Barton
 */
public class MoleculesGroup extends JPanel implements Serializable {

    private static final long serialVersionUID = -7149849370175201281L;

    public MoleculesGroup() {
        initComponents();
        //setBackground(Color.red);
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
    }

    public void plateUpdate(HtsPlate<HtsInstance> d) {
        if (d != null) {
            int j = 0;
            JPanel panel;
            System.out.println("hts plate: " + d.getName() + ", " + d.getId());
            for (HtsInstance inst : d) {
                System.out.println("instance: " + inst.getName());
                if (inst.hasFormula()) {
                    System.out.println("formula: " + inst.getSmiles());
                    String smiles = inst.getSmiles();

                    //panel = parser1(smiles);
                    panel = parser2(smiles);
                    if (panel != null) {
                        GridBagConstraints c = new GridBagConstraints();

                        c.anchor = GridBagConstraints.NORTH;
                        c.weightx = 0;
                        c.fill = GridBagConstraints.BOTH;
                        c.weighty = 1.0;
                        c.insets = new java.awt.Insets(0, 0, 0, 0);
                        c.gridx = 0;

                        c.gridy = j++;
                        add(panel, c);
                    } else {
                        addLabel("Missing molecule data", j++);

                    }
                }
            }
        }
    }

    private JPanel parser1(String smiles) {
        try {
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            IMolecule molecule = sp.parseSmiles(smiles);

            if (molecule != null) {
                StructureDiagramGenerator sdg = new StructureDiagramGenerator();
                sdg.setMolecule(molecule);
                sdg.generateCoordinates(new Vector2d(0, 1));
                molecule = sdg.getMolecule();
                molecule = new DeduceBondSystemTool().fixAromaticBondOrders(molecule);
                //for some reason, smilesparser sets valencies, which we don't want in jcp
                for (int i = 0; i < molecule.getAtomCount(); i++) {
                    molecule.getAtom(i).setValency(null);
                }

                IChemModel chemModel = DefaultChemObjectBuilder.getInstance()
                        .newInstance(IChemModel.class);
                chemModel.setMoleculeSet(DefaultChemObjectBuilder.getInstance()
                        .newInstance(IMoleculeSet.class));
                chemModel.getMoleculeSet().addAtomContainer(molecule);

                return new JChemPaintViewerPanel(chemModel, 500, 500, true, true, null);

            }
        } catch (InvalidSmilesException ise) {
            Exceptions.printStackTrace(ise);
        } catch (CDKException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    private JPanel parser2(String smiles) {
        try {
            // TODO add your handling code here:
            JPanel panel = new JPanel();
            Image image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
            IMolecule triazole = MoleculeFactory.makeCyclobutane();

            Molecule molecule = (Molecule) new SmilesParser(DefaultChemObjectBuilder.getInstance()).parseSmiles(smiles);

            StructureDiagramGenerator sdg = new StructureDiagramGenerator();
            sdg.setMolecule(molecule);
            try {
                sdg.generateCoordinates();
            } catch (Exception ex) {
                //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            triazole = sdg.getMolecule();
            // generators make the image elements
            List generators = new ArrayList();
            generators.add(new BasicSceneGenerator());
            generators.add(new BasicBondGenerator());
            generators.add(new BasicAtomGenerator());
            // the renderer needs to have a toolkit-specific font manager
            AtomContainerRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager());
            // the call to 'setup' only needs to be done on the first paint
            //renderer.setup(triazole, drawArea);
            // paint the background
            Graphics2D g2 = (Graphics2D) image.getGraphics();
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, WIDTH, HEIGHT);
            Rectangle2D drawArea = new Rectangle2D.Double(0, 0, 200, 200);
            //renderer.paint(molecule, new AWTDrawVisitor((Graphics2D) g2), drawArea, true);
            // the paint method also needs a toolkit-specific renderer
            //renderer.paint(triazole, new AWTDrawVisitor(g2));
            JLabel jLabel = new JLabel();
            jLabel.setIcon(new ImageIcon(image));
            // panel.add(drawArea);
            return panel;

        } catch (InvalidSmilesException ex) {
            Logger.getLogger(MoleculesGroup.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException e) {
            Exceptions.printStackTrace(e);
        }
        return null;
    }

    //public void clusterSelected(DendrogramTree source, TreeCluster cluster, DendrogramMapping data) {
    public void clusterSelected(HtsPlate plate) {
        if (plate != null) {

            removeAll();
            int j = 0;
            HtsInstance inst;
            IChemObject molecule;
            JPanel panel;
            //  for (int i = cluster.firstElem; i <= cluster.lastElem; i++) {
            //  System.out.println("getting index: " + data.getRowIndex(i));
            //  inst = plate.instance(data.getRowIndex(i));

            GridBagConstraints c = new GridBagConstraints();

            c.anchor = GridBagConstraints.NORTH;
            c.weightx = 0;
            c.fill = GridBagConstraints.BOTH;
            c.weighty = 1.0;
            c.insets = new java.awt.Insets(0, 0, 0, 0);
            c.gridx = 0;

            /*  molecule = inst.getMolecule();                    addLabel(inst.getName(), j++);
             if (molecule != null) {
             panel = new MoleculeVizualizer(inst.getMolecule());
             c.gridy = j++;
             add(panel, c);
             }else{
             addLabel("Missing molecule data", j++);

             }*/
            //     }


            revalidate();
        }
    }

    private void addLabel(String label, int row) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 0;
        c.weighty = 0.0; //no fill while resize
        c.insets = new java.awt.Insets(0, 0, 0, 0);
        c.gridx = 0;
        c.gridy = row;
        c.fill = GridBagConstraints.NONE;
        add(new JLabel(label), c);
    }

    public IMolecule parseSmiles(String smiles) {
        IMolecule molecule = null;
        try {
            SmilesParser sp = new SmilesParser(DefaultChemObjectBuilder.getInstance());
            molecule = sp.parseSmiles(smiles);


        } catch (InvalidSmilesException ise) {
            Exceptions.printStackTrace(ise);
        }
        return molecule;
    }
}
