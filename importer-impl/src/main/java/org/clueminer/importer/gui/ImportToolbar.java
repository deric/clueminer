package org.clueminer.importer.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedHashMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.clueminer.importer.FileImporterFactory;
import org.clueminer.spi.FileImporter;

/**
 *
 * @author Tomas Barton
 */
public class ImportToolbar extends JPanel implements ActionListener {

    private static final long serialVersionUID = 239526173161101672L;

    private JToolBar toolbar;
    private JComboBox comboSpline;
    private JButton btnClear;

    protected LinkedHashMap<String, FileImporter> providers;

    public ImportToolbar() {
        init();
    }

    private void init() {
        toolbar = new JToolBar(SwingConstants.HORIZONTAL);
        JLabel label = new JLabel("Importer: ");
        toolbar.add(label);
        comboSpline = new JComboBox(getProviders());
        comboSpline.addActionListener(this);
        toolbar.add(comboSpline);
        btnClear = new JButton("Clear");
        toolbar.add(btnClear);
        btnClear.addActionListener(this);
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(toolbar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source.equals(btnClear)) {

        } else if (source.equals(comboSpline)) {
            String item = (String) comboSpline.getSelectedItem();
            //control.setSpline(providers.get(item));
            System.out.println("selected:" + item);
        }
    }

    public String[] getProviders() {
        Collection<? extends FileImporter> list = FileImporterFactory.getInstance().getAll();
        String[] res = new String[list.size()];
        providers = new LinkedHashMap<String, FileImporter>();
        int i = 0;
        for (FileImporter importer : list) {
            providers.put(importer.getName(), importer);
            res[i++] = importer.getName();
        }
        return res;
    }

}
