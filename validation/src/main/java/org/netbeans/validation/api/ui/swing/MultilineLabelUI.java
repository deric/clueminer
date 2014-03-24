/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.validation.api.ui.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Component.BaselineResizeBehavior;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import javax.swing.plaf.LabelUI;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Severity;

/**
 * Label UI which renders multiline text
 *
 * @author Tim Boudreau
 */
final class MultilineLabelUI extends LabelUI {

    private static Graphics2D createOffscreenGraphics(JComponent c) {
        if (c == null || c.getGraphicsConfiguration() == null) {
            return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleImage(1, 1).createGraphics();
        }
        return c.getGraphicsConfiguration().createCompatibleImage(1, 1).createGraphics();
    }

    @Override
    public int getBaseline(JComponent c, int width, int height) {
        return c.getInsets().top + createOffscreenGraphics(null).getFontMetrics(c.getFont()).getMaxAscent();
    }

    @Override
    public BaselineResizeBehavior getBaselineResizeBehavior(JComponent c) {
        return BaselineResizeBehavior.CONSTANT_DESCENT;
    }

    @Override
    public void installUI(JComponent c) {
        super.installUI(c);
        ((JLabel) c).setIconTextGap(5);
        ((JLabel) c).setOpaque(false);
    }

    @Override
    public void update(Graphics g, JComponent c) {
        if (c.isOpaque()) {
            g.setColor (c.getBackground());
            g.fillRect(0, 0, c.getWidth(), c.getHeight());
        }
        paint (g, c);
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        GraphicsConfiguration gc = c.getGraphicsConfiguration();
        if (gc == null) {
            return super.getMaximumSize(c);
        }
        int maxw = gc.getDevice().getDisplayMode().getWidth();
        String s = ((JLabel) c).getText();
        Insets ins = c.getInsets();
        Graphics2D g = createOffscreenGraphics(c);
        int txtWidth = ins.left + g.getFontMetrics(c.getFont()).stringWidth(s);
        int parentWidth = (c.getParent() == null ? maxw - (maxw / 4) : c.getParent().getWidth() - (c.getParent().getWidth() / 4)) - ins.right;
        Dimension result;
        if (txtWidth > parentWidth) {
            int ht = renderPlainString(c, s, g, ins.left, ins.top, parentWidth, c.getFont(), Color.BLACK, false).requiredHeight;
            result = new Dimension (parentWidth, ht);
        } else {
            result = new Dimension (txtWidth, g.getFontMetrics(c.getFont()).getHeight());
        }
        Icon icon = ((JLabel) c).getIcon();
        if (icon != null) {
            result.width += icon.getIconWidth();
            result.height += icon.getIconHeight();
        }
        result.width += ins.left + ins.right;
        result.height += ins.top + ins.bottom;
        return result;
    }

    @Override
    public Dimension getMinimumSize(JComponent c) {
        JLabel lbl = (JLabel) c;
        char[] dummy = new char[80];
        Arrays.fill(dummy, 'X');
        Graphics2D g = createOffscreenGraphics(c);
        int testWidth = g.getFontMetrics(c.getFont()).stringWidth(new String(dummy));
        GraphicsConfiguration gc = c.getGraphicsConfiguration();
        if (gc == null) {
            return super.getMinimumSize(c);
        }
        int maxw = gc.getDevice().getDisplayMode().getWidth();
        Insets ins = c.getInsets();
        int parentWidth = c.getWidth();
        if (parentWidth == 0 && c.getParent() != null) {
            Component parent = c.getParent();
            while (parentWidth == 0 && parent != null) {
                parentWidth = parent.getWidth();
                if (parent instanceof JScrollPane) {
                    JScrollPane pane = (JScrollPane) parent;
                    JViewport port = pane.getViewport();
                    parentWidth = port.getWidth();
                    Insets paneInsets = pane.getInsets();
                    Insets portInsets = port.getInsets();
                    parentWidth -= paneInsets.left + paneInsets.right + portInsets.left + portInsets.right;
                }
                parent = parent.getParent();
            }
        }
        if (parentWidth == 0) {
            parentWidth = maxw + (maxw / 4);
        }
        parentWidth -= ins.left + ins.right;
        boolean needWrap = parentWidth < testWidth;
        int ht = Math.max(16, g.getFontMetrics(c.getFont()).getHeight());
        if (!needWrap) {
            //Always provide for 2 lines
            ht *= 2;
        }
        int w = g.getFontMetrics(c.getFont()).stringWidth(lbl.getText());
        Dimension result = new Dimension (ins.left + ins.right + w, ins.bottom + ins.top + ht);
        return result;
    }

    private static final int MIN_CHARS = 100;
    @Override
    public Dimension getPreferredSize(JComponent c) {
        JLabel lbl = (JLabel) c;
        String s = lbl.getText();
        boolean popup = c instanceof MultilineLabel && ((MultilineLabel) c).isPopup();
        if (!popup) {
            if (s == null || s.length() < MIN_CHARS) {
                char[] dummy = new char[MIN_CHARS];
                Arrays.fill(dummy, 'X');
                s = new String(dummy);
            }
        }
        int maxw = c.getGraphicsConfiguration() == null ? Toolkit.getDefaultToolkit().getScreenSize().width :
            c.getGraphicsConfiguration().getDevice().getDisplayMode().getWidth();
        Insets ins = c.getInsets();
        int parentWidth = c.getWidth();
        if (parentWidth == 0 && c.getParent() != null) {
            Component parent = c.getParent();
            while (parentWidth == 0 && parent != null) {
                parentWidth = parent.getWidth();
                parent = parent.getParent();
            }
        }
        if (parentWidth <= 0) {
            parentWidth = maxw + (maxw / 4);
        }
        parentWidth -= ins.left + ins.right;
        return getPreferredSize(lbl, parentWidth).dim;
    }

    static Metrics getPreferredSize (JLabel lbl, int parentWidth) {
        Icon icon = lbl.getIcon();
        if (icon != null) {
            parentWidth -= icon.getIconWidth();
        }
        Insets ins = lbl.getInsets();
        int iconW = icon == null ? 0 : icon.getIconWidth() + lbl.getIconTextGap();
        Graphics2D g = createOffscreenGraphics(lbl);
        Metrics metrics = renderPlainString(lbl, lbl.getText(),
                g, ins.left, ins.top, parentWidth, lbl.getFont(), Color.BLACK, false, iconW);
        int ht = metrics.requiredHeight;
        Dimension result = new Dimension();
        if (icon != null) {
            result.height = Math.max (metrics.lineheight * metrics.linecount, icon.getIconHeight());
        } else {
            result.height = metrics.lineheight * metrics.linecount;
        }
        result.width = metrics.widestLine;
        result.width += ins.left + ins.right;
        result.height += ins.top + ins.bottom;
        metrics.dim = result;
        return metrics;
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Insets ins = c.getInsets();
        Graphics2D gg = (Graphics2D) g;
        JLabel lbl = (JLabel) c;
        Icon icon = lbl.getIcon();
        int gap = lbl.getIconTextGap();
        int startX = ins.left + gap;
        int startY = ins.top;
        if (icon != null) {
            icon.paintIcon(c, g, ins.top, ins.left);
            startX = icon.getIconWidth() + gap;
            boolean popup = c instanceof MultilineLabel && ((MultilineLabel) c).isPopup();
            int yHeight = popup ? g.getFontMetrics(c.getFont()).getHeight() : g.getFontMetrics(c.getFont()).getMaxAscent();
            startY += (icon.getIconHeight() / 2) - (yHeight / 2);
        }
        String txt = ((JLabel) c).getText();
        if (txt != null) {
            renderPlainString(c, txt, gg, ins.left, startY, c.getWidth() - (ins.left + ins.right), c.getFont(), c.getForeground(), true, startX);
        }
    }
    private static Metrics renderPlainString (JComponent c, String s, Graphics2D g, int x, int y, int w, Font f, Color foreground, boolean paint) {
        return renderPlainString(c, s, g, x, y, w, f, foreground, paint, 0);
    }

    private static Metrics renderPlainString (JComponent comp, String s, Graphics2D g, int x, int y, int w, Font f, Color foreground, boolean paint, int startX) {
        if (g == null) {
            g = createOffscreenGraphics(comp);
        }
        if (f == null) {
            f = UIManager.getFont("controlFont");
            if (f == null) {
                int fs = 11;
                Object cfs = UIManager.get("customFontSize"); //NOI18N
                if (cfs instanceof Integer) {
                    fs = ((Integer) cfs).intValue();
                }
                f = new Font("Dialog", Font.PLAIN, fs); //NOI18N
            }
        }
        if (paint) {
            g.setRenderingHints(getHints());
            g.setFont(f);
            g.setColor(foreground);
        }
        FontMetrics fm = g.getFontMetrics(f);
        int baseline = y + fm.getMaxAscent();
        int ht = fm.getHeight();
        Metrics result = new Metrics(ht);
        int dx = x + startX;
        result.lineLength(dx);
        String[] words = s.split(" ");
        int spaceWidth = fm.stringWidth(" ");
wordloop: for (int i=0; i < words.length; i++) {
            String word = words[i];
            int wordWidth = fm.stringWidth(word);
            boolean wrap = dx + wordWidth > w && i > 0;
            boolean brutalWrap = wordWidth > w;
            if (brutalWrap) {
                char[] chars = word.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    char c = chars[j];
                    int cwidth = fm.charWidth(c);
                    if (dx + cwidth > w) {
                        result.lineLength(dx);
                        dx = x;
                        baseline += ht;
                        result.wrap();
                    }
                    if (paint) {
                        g.drawChars(chars, j, 1, dx, baseline);
                    }
                    dx += cwidth;
                    result.lineLength(dx);
                    if (j == chars.length - 1) {
                        dx += spaceWidth;
                        result.lineLength(dx);
                        continue wordloop;
                    }
                }
            }
            if (wrap) {
                result.lineLength(dx);
                result.wrap();
                dx = x;
                baseline += ht;
            }
            if (paint) {
                g.drawString(word, dx, baseline);
            }
            dx += spaceWidth + wordWidth;
            result.lineLength(dx);
        }
//        result.finished(baseline + fm.getMaxDescent() + y);
        result.finished(baseline + y);
        return result;
    }

    private static final boolean antialias = Boolean.getBoolean("nb.cellrenderer.antialiasing") // NOI18N
         ||Boolean.getBoolean("swing.aatext") // NOI18N
         ||(isGTK() && gtkShouldAntialias()) // NOI18N
         || isAqua();

    static Map<Object,Object> hintsMap;
    @SuppressWarnings("unchecked")
    static final Map<?,?> getHints() {
        //XXX We REALLY need to put this in a graphics utils lib
        if (hintsMap == null) {
            //Thanks to Phil Race for making this possible
            hintsMap = (Map<Object,Object>)(Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints")); //NOI18N
            if (hintsMap == null) {
                hintsMap = new HashMap<Object,Object>();
                if (antialias) {
                    hintsMap.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                }
            }
        }
        Map<?,?> ret = hintsMap;
        assert ret != null; // does this method need to be synchronized?
        return ret;
    }

    static boolean isAqua () {
        return "Aqua".equals(UIManager.getLookAndFeel().getID());
    }

    static boolean isGTK () {
        return "GTK".equals(UIManager.getLookAndFeel().getID());
    }

    static boolean isNimbus () {
        return "Nimbus".equals(UIManager.getLookAndFeel().getID());
    }

    private static Boolean gtkAA;
    static final boolean gtkShouldAntialias() {
        if (gtkAA == null) {
            Object o = Toolkit.getDefaultToolkit().getDesktopProperty("gnome.Xft/Antialias"); //NOI18N
            gtkAA = new Integer(1).equals(o) ? Boolean.TRUE : Boolean.FALSE;
        }

        return gtkAA.booleanValue();
    }

    static Popup showPopup(Problem problem, Component parent, int x, int y) {
        Severity severity = problem.severity();
        Color bg = UIManager.getColor("white") == null ? Color.WHITE : UIManager.getColor("white");
        Border b = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(severity.color(), 1),
                BorderFactory.createEmptyBorder(3, 3, 3, 3));
        final MultilineLabel lbl = new MultilineLabel(true);
        lbl.createUI().showProblem(problem);
        lbl.setBorder(b);
        lbl.setForeground(severity.color());
        lbl.setBackground(bg);
        Point screenLoc = parent.getLocationOnScreen();
        int screenWidth = parent.getGraphicsConfiguration().getDevice().getDisplayMode().getWidth();
        int screenX = screenLoc.x + x;
        int screenY = screenLoc.y + y;
        int availWidth = screenWidth - screenX;
        Popup popup = PopupFactory.getSharedInstance().getPopup(parent, lbl, screenX, screenY);
        Insets ins = b.getBorderInsets(lbl);
        Metrics mm = getPreferredSize(lbl, availWidth);
        System.err.println("METRICS:\n" + mm);
        Dimension d = mm.dim;
        d.width = mm.widestLine + ins.left + ins.right + severity.icon().getIconWidth() + lbl.getIconTextGap();
        lbl.setPreferredSize(d);
        return new WrapPopup(parent, popup);
    }

    private static final class WrapPopup extends Popup implements HierarchyListener, HierarchyBoundsListener {
        private final Popup realPopup;
        private final Component target;
        WrapPopup(Component target, Popup realPopup) {
            this.realPopup = realPopup;
            this.target = target;
        }

        @Override
        public void hide() {
            realPopup.hide();
            detach();
        }

        @Override
        public void show() {
            attach();
            realPopup.show();
        }

        private void attach() {
            target.addHierarchyListener(this);
            target.addHierarchyBoundsListener(this);
        }

        private void detach() {
            target.removeHierarchyListener(this);
            target.removeHierarchyBoundsListener(this);
        }

        @Override
        public void ancestorMoved(HierarchyEvent e) {
            hide();
        }

        @Override
        public void ancestorResized(HierarchyEvent e) {
            hide();
        }

        @Override
        public void hierarchyChanged(HierarchyEvent e) {
            hide();
        }
    }

    public static void main(String[] args) throws Exception {
        JFrame jf = new JFrame();
        final JLabel lbl = new JLabel("Hello");
        jf.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jf.setContentPane(lbl);
        final String err = "This is a big honkin long error message, in fact its so long it will probably have to be wrapped to multiple lines whether " +
                "we want it to or not - otherwise there will be much trouble.";
        lbl.addMouseListener(new MouseAdapter() {
            Popup popup;

            @Override
            public void mouseEntered(MouseEvent e) {
                if (popup != null) {
                    popup.hide();
                }
                popup = MultilineLabelUI.showPopup(new Problem(err, Severity.FATAL), lbl, e.getX(), e.getY());
                popup.show();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (popup != null) popup.hide();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseEntered(e);
            }
        });
        jf.pack();
        jf.setVisible(true);
    }

    private static final class Metrics {
        int linecount = 1;
        int widestLine;
        int requiredHeight;
        final int lineheight;
        Dimension dim;
        Metrics(int lineheight) {
            this.lineheight = lineheight;
        }

        void wrap() {
            linecount++;
        }

        void finished(int bottom) {
            this.requiredHeight = bottom;
        }
        
        void lineLength(int pixels) {
            widestLine = Math.max(pixels, widestLine);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append ("Line Count: " + linecount);
            sb.append("\nWidest Line: " + widestLine);
            sb.append("\nRequired Height " + requiredHeight);
            sb.append("\nLine Height: " + lineheight);
            return sb.toString();
        }
    }
}
