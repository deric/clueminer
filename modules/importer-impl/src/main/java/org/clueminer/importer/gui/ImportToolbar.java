/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.importer.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
            //System.out.println("selected importer:" + item);
        }
    }

    public String[] getProviders() {
        return FileImporterFactory.getInstance().getProvidersArray();
    }

}
