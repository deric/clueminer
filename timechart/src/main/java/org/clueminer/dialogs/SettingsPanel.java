package org.clueminer.dialogs;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.ChartFrame;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;

/**
 *
 * @author Tomas Barton
 */
public class SettingsPanel extends JPanel {

    private static SettingsPanel instance;
    private static final long serialVersionUID = -6145757224340533999L;
    private PropertySheet propertySheet;
    private String title = "";

    public static SettingsPanel getDefault() {
        if (instance == null) {
            instance = new SettingsPanel();
        }
        return instance;
    }

    private SettingsPanel() {
        super(new BorderLayout());
        setOpaque(false);
        initComponents();
    }

    private void initComponents() {
        propertySheet = new PropertySheet();
        add(propertySheet, BorderLayout.CENTER);
    }

    public void forChart(ChartFrame chartFrame) {
        title = "Chart Settings";
        propertySheet.setNodes(new Node[]{
            chartFrame.getNode()
        });
    }

    public void forOverlay(Overlay overlay) {
        title = overlay.getName() + " Settings";
        propertySheet.setNodes(new Node[]{
            overlay.getNode()
        });
    }

    public void reset() {
        propertySheet.setNodes(new Node[0]);
        title = "";
    }

    public void openSettingsWindow(Object object) {
        if (object instanceof ChartFrame) {
            forChart((ChartFrame) object);
        } else if (object instanceof Overlay) {
            forOverlay((Overlay) object);
        } else {
            return;
        }

        DialogDescriptor descriptor = new DialogDescriptor(
                this, title, true, null);
        descriptor.setMessageType(DialogDescriptor.PLAIN_MESSAGE);
        descriptor.setOptions(new Object[]{DialogDescriptor.OK_OPTION});
        Object ret = DialogDisplayer.getDefault().notify(descriptor);
        if (ret != null) {
            if (object instanceof ChartFrame) {
                ((ChartFrame) object).repaint();
            } else if (object instanceof Overlay) {
                ((Overlay) object).calculate();
            }
        }
    }
}
