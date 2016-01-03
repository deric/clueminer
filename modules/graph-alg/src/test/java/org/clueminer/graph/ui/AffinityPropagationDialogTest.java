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
package org.clueminer.graph.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.clueminer.ap.AffinityPropagation;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author deric
 */
public class AffinityPropagationDialogTest {

    private final AffinityPropagationDialog subject;

    public AffinityPropagationDialogTest() {
        subject = new AffinityPropagationDialog();
    }

    @Test
    public void testIsUIfor() {
        assertEquals(true, subject.isUIfor(new AffinityPropagation(), null));
    }

    public String getTitle() {
        return "Affinity propagation test";
    }

    protected JFrame showInFrame() {
        JFrame frame = new JFrame(getTitle());
        frame.getContentPane().add(new AffinityPropagationDialog(), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(500, 200));
        frame.setVisible(true);
        return frame;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new AffinityPropagationDialogTest().showInFrame();
            }
        });
    }

}
