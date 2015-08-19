/*
 * Copyright (C) 2011-2015 clueminer.org
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
package org.clueminer.clustering.gui.dlg;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.clustering.algorithm.DBSCAN;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class DBSCANDialogTest {

    private final DBSCANDialog subject;

    public DBSCANDialogTest() {
        subject = new DBSCANDialog();
    }

    @Test
    public void testIsUIfor() {
        assertEquals(true, subject.isUIfor(new DBSCAN()));
    }

    public String getTitle() {
        return "DBSCAN test";
    }

    protected JFrame showInFrame() {
        JFrame frame = new JFrame(getTitle());
        frame.getContentPane().add(subject, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(800, 600));
        //frame.setSize(getPreferredSize());
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new DBSCANDialogTest().showInFrame();
            }
        });
    }

}
