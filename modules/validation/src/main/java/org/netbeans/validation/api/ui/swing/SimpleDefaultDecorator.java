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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JToolTip;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.RGBImageFilter;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.UIManager;
import org.netbeans.validation.api.Problem;
import org.netbeans.validation.api.Severity;
import org.netbeans.validation.api.ui.ValidationUI;

/**
 * Default decorator class provided by simplevalidation.
 *
 * <p> By default, one instance of this class is used to decorate all
 * components validated by simplevalidation in the application. This
 * instance can be replaced with a custom one, see {@link
 * SwingComponentDecorationFactory}. Either a completely different {@code
 * SwingComponentDecorationFactory} class <b>or an instance of a class deriving
 * from {@code SimpleDefaultDecorator}</b>, overriding one or more of
 * the protected methods.
 *
 * <p> {@code SimpleDefaultDecorator} adds an problem icon to the
 * decorated component when there is a problem in it. The icon is
 * added using the {@code JLayeredPane} mechanism in Swing, so that
 * ends up "over" the decorated component (it partly covers the
 * decorated component). {@code JLayeredPane} gives us the freedom to
 * place the icon outside of the bounding rectangle of the decorated
 * component. The exakt location of the icon can be customized by
 * deriving from this class and overriding the {@code
 * getDecorationLocation} method.
 *
 * <p> {@code SimpleDefaultDecorator} also provides with some toolTip
 * management: The toolTip owned by the decoration icon is updated
 * live if it happens to be visible when the problem changes (and the
 * tooltip disappears if the problem disappears).  The content of the
 * tooltip is a JLabel, by default showing the {@code icon()} of the
 * {@link Severity} of the current problem together with the problem
 * message, in suitable colors. This can be overridden by deriving
 * classes.
 *
 * <p> A customization example would be the following, which makes the
 * icons a little bit bigger than the default, and the colors a little
 * bit stronger.
 *
 * <blockquote><pre>{@code
 *  SwingComponentDecorationFactory.set(new SimpleDefaultDecorator(){
 *   protected Color getComponentOverlayColor(Severity s, JComponent decoratedComponent){
 *       if(s == Severity.INFO) {
 *           return null; // Skip the gray background for INFO 
 *       }
 *       int alpha = 26; // Corresponds to 0.10f -- rather strong actually
 *       return new Color(s.color().getRed(), s.color().getGreen(), s.color().getBlue(), alpha);
 *   }
 *   
 *   protected Point getDecorationLocation(Severity s, JComponent decoratedComponent, Dimension decorationIconSize) {
 *       return new Point( decoratedComponent.getWidth() - (int)(0.6*decorationIconSize.width), -2);
 *   }
 *
 *   protected Image getDecorationImage(Severity severity, JComponent decoratedComponent) {
 *       return severity.image(); // bigger than the small severity.badge() -- a bit too big actually, will need to be scaled down 
 *   }
 *
 *   protected Double getDecorationImageScaling(Severity s, JComponent decoratedComponent) {
 *        if (s == Severity.FATAL) {
 *            // Scale down the FATAL image a bit more
 *            return 0.75;
 *        }
 *        return 0.85;
 *    }
 *
 *    protected Integer getDecorationOverlapTransparency(Severity s, JComponent decoratedComponent) {
 *        if (s == Severity.WARNING) {
 *            // Not much transparency for warning icon, which has a light color.
 *            return 0xAA;
 *        }
 *        return 0x77;
 *    }
 *});
 * }</pre></blockquote>
 * 
 * @author Tim Boudreau
 * @author Hugo Heden
 */
final class SimpleDefaultDecorator extends SwingComponentDecorationFactory {

    private Icon fatalIconTransp = null;
    private Icon warningIconTransp = null;
    private Icon infoIconTransp = null;
    
    @Override
    public ValidationUI decorationFor(JComponent c) {
        return new ToolTippedIconLabel(c, this);
    }

    /**
     * A problem has occured in a component, and this method the a way
     * to specify what the <b>tooltip owned by the decoration</b> icon
     * should contain from now on (until another problem occurs in
     * this component)
     *
     * <p> If no toolTip is desired, this method should just return
     * false.
     *
     * <p> Otherwise, the toolTip will contain the JLabel passed to
     * this method. An example implementation would be the following.
     * 
     * <blockquote><pre>{@code
     * ttLabel.setText(problem.getMessage());
     * return true;
     * }</pre></blockquote>
     *
     * or perhaps (adjusting the ttLabel configured by the super
     * class):
     *
     * <blockquote><pre>{@code
     * super.configureToolTipLabel(problem, decoratedComponent, ttLabel);
     * ttLabel.setIcon(null);
     * return true;
     * }</pre></blockquote>
     * 
     * 
     * @param problem The problem that has occured.
     * @param decoratedComponent The component in which the problem has occured.
     * @param ttLabel label that will be contained within the tooltip
     * @return false if <b>no toolTip is to be shown at all</b>, true otherwise
     */
    protected boolean configureToolTipLabel(Problem problem, JComponent decoratedComponent, JLabel ttLabel){
        // No good: color transparent, stuff "under" toolTip may leak through (the color chooser in ValidationDemo does so)
        // ttLabel.setBackground(this.getComponentOverlayColor(problem.severity(), decoratedComponent));
        //TDB:  Try for JDK default theme if available
        Color bg = UIManager.getColor("white");
        //But may not be available on, e.g., GTK look and feel
        ttLabel.setBackground(bg == null ? Color.WHITE : bg);
        // The following works well, but creates garbage -- a new Border for every new Problem that occurs:
        // ttLabel.setBorder(new ColorizingBorder(decoratedComponent, this));
        ttLabel.setOpaque(true);
        ttLabel.setIcon(problem.severity().icon());
        ttLabel.setForeground(problem.severity().color());
        Border b = BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(problem.severity().color(), 1),
                BorderFactory.createMatteBorder(3, 3, 3, 3, bg));
        ttLabel.setBorder(b);
        ttLabel.setText(problem.getMessage());
        return true;
    }

    /**
     * A {@code Color} with which to paint over the decorated
     * component -- a color overlay.  The whole bounding rectangle of
     * the component will be filled.  It is recommended to make this
     * color transparent to a high degree, or else the content of the
     * decorated component will be hidden by the painted color.  If
     * the returned Color is null, the component will not get any
     * color overlay.
     *
     * 
     * @param s The {@code Severity} of the problem the component is to be decorated for.
     * @param decoratedComponent The component to be decorated
     * @return A {@code Color} to be painted over the bounding rectangle over the component,
     * or null if nothing is to be painted.
     */
    protected Color getComponentOverlayColor(Severity s, JComponent decoratedComponent) {
        int alpha = 13; // Corresponds to alpha of 0.05f [(int)(0.05*255+0.5) == 13]
        return new Color(s.color().getRed(), s.color().getGreen(), s.color().getBlue(), alpha);
    }

    /**
     * Specifies where the decorative icon is to be drawn, expressed
     * in the coordinate system of the decoratedComponent. The
     * decoration does not need to be drawn inside of the component
     * bounds -- for example negative numbers are ok.
     *
     * <p> For example, to have the top left corner of the icon
     * located at the top left corner of the decorated component:
     * {@code return new Point(0, 0); }
     *
     * <p> Or, to have the bottom right corner of the icon located at
     * the top right corner of the decoratedComponent: {@code return
     * new
     * Point(decoratedComponent.getWidth()-decorationIconSize.width,
     * -decorationIconSize.height); }
     *
     *
     * @param s The Severity for which the returned decoration location is intended
     * @param decorationIconSize Size of the decoration icon
     * @param decoratedComponent Component to be decorated
     * @return Location of the upper left corner of the icon, expressed in the decoratedComponent
     * coordinate system.
     */
    protected Point getDecorationLocation(Severity s, JComponent decoratedComponent, Dimension decorationIconSize) {
        return new Point(decoratedComponent.getWidth() - decorationIconSize.width + 2, -2);
    }

    /**
     * Specifies an {@code Image} to be used for the decorative icon. Would 
     * typically return {@code severity.image()} or {@code severity.badge()},
     * but any custom image would of course be possible to use. The methods
     * {@code getDecorationImageScaling}  and
     * {@code getDecorationOverlapTransparency} are used to process this image
     * somewhat before rendering,
     * @param severity The {@code Severity} for which this image is to be used.
     * @param decoratedComponent {@code Component} to be decorated
     * @return An {@code Image} with which to decorate the component, or null
     * if no Icon is to be shown for this severity and/or decoratedComponent
     *
     */
    protected Image getDecorationImage(Severity severity, JComponent decoratedComponent) {
        return severity.badge();
    }

    /**
     * Specifies a scaling to apply to the icon returned by {@code
     * getDecorationImage} before rendering it.
     *
     * @param s The severity for which this scaling will apply
     * @param decoratedComponent The component being decorated
     * @return A Double representing a positive number, or null if no scaling should be applied.
     */
    protected Double getDecorationImageScaling(Severity s, JComponent decoratedComponent) {
        return null;
    }

    /**
     * Specifies a transparency to apply to the icon returned by
     * {@code getDecorationImage} before rendering it. <b>This
     * transparency will only be applied to the part of the icon that
     * overlaps/covers the decorated component</b>.
     *
     * <p> The alpha value defines the transparency of a color and can
     * be represented 0 - 255. An alpha value of 255 means that the
     * color is completely opaque and an alpha value of 0 means that
     * the color is completely transparent.
     *
     * @param s The severity for which this transparency will apply
     * @param decoratedComponent The component being decorated
     * @return An Integer representing a positive number between 0 and 255, or null if no transparency should be applied.
     */
    protected Integer getDecorationOverlapTransparency(Severity s, JComponent decoratedComponent) {
        return null;
    }

    /**
     * Creates/gets a version of the Icon returned by {@code
     * getDecorationImage} (for this severity and decoratedComponent)
     * that has been scaled and made transparent according to what is
     * returned by {@code getDecorationImageScaling} and {@code
     * getDecorationOverlapTransparency}
     *
     * <p> Method is intended to be called internally from the {@code
     * SimpleDefaultDecorator} infrastructure.
     *
     * @param severity The {@code Severity} for which this icon is to be applied
     * @param decoratedComponent
     * @return
     */
    Icon getDecorationIcon(Severity severity, final JComponent decoratedComponent) {
        // "singletons", only created once.
        if (severity.equals(Severity.FATAL) && fatalIconTransp != null) {
            return fatalIconTransp;
        }
        if (severity.equals(Severity.WARNING) && warningIconTransp != null) {
            return warningIconTransp;
        }
        if (severity.equals(Severity.INFO) && infoIconTransp != null) {
            return infoIconTransp;
        }
        Image image = getDecorationImage(severity, decoratedComponent);
        if (image == null) {
            return null;
        }
        ImageIcon icon = new ImageIcon(image);
        Double scaling = getDecorationImageScaling(severity, decoratedComponent);
        if (scaling != null) {
            int scaledWidth = (int) (scaling * icon.getIconWidth());
            int scaledHeight = (int) (scaling * icon.getIconHeight());
            BufferedImage scaledImg =
                    new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = scaledImg.createGraphics();
            // TODO: Better rescaling if downscaling? See code example on
            // http://today.java.net/pub/a/today/2007/04/03/perils-of-image-getscaledinstance.html
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC); // Slowest but with highest quality
            g2.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
            g2.dispose();
            image = scaledImg;
            icon = new ImageIcon(image);
        }

        final Integer alpha = getDecorationOverlapTransparency(severity, decoratedComponent);
        if (alpha != null) {
            final Point translate =
                    this.getDecorationLocation(null, decoratedComponent,
                    new Dimension(icon.getIconWidth(), icon.getIconWidth()));
            final int dcW = decoratedComponent.getWidth();
            final int dcH = decoratedComponent.getHeight();
            ImageFilter filter = new RGBImageFilter() {

                @Override
                public int filterRGB(int x, int y, int rgb) {
                    // If this x,y-coordinate covers the decorated component, make some transparency.
                    // The trailing FFFFFF means to keep all *colors* as is.
                    x += translate.x;
                    y += translate.y;
                    if (x > 0 && x < dcW && y > 0 && y < dcH) {
                        return (alpha << 24 | 0xFFFFFF) & rgb;
                    }
                    return rgb;

                }
            };
            image = Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image.getSource(), filter));
            icon = new ImageIcon(image);
        }
        if (severity.equals(Severity.FATAL)) {
            return fatalIconTransp = icon;
        }
        if (severity.equals(Severity.WARNING)) {
            return warningIconTransp = icon;
        }
        assert severity.equals(Severity.INFO);
        return infoIconTransp = icon;
    }

/**
 * A border to apply to the component which shows an error.  A useful
 * way to create custom borders is to wrap the original border and
 * paint it, then paint over it.  If the insets of the border returned
 * by this method are different than the insets of the original
 * border, then the UI layout will "jump".  <p> Severity.color() and
 * Severity.image() are handy here.
 */
static final class ColorizingBorder implements Border, ValidationUI {

    private SimpleDefaultDecorator decorator;
    private final JComponent decoratedComponent;
    private final Border real;
    private Severity severity = null;

    public ColorizingBorder(JComponent c, SimpleDefaultDecorator decorator) {
        this.decorator = decorator;
        this.decoratedComponent = c;
        this.real = (c.getBorder() != null ? c.getBorder() : BorderFactory.createEmptyBorder());
        c.setBorder(this);
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {

        real.paintBorder(c, g, x, y, width, height);
        if (severity == null) {
            return;
        }
        Color niceTransparentColorForRectangle =
                decorator.getComponentOverlayColor(severity, decoratedComponent);
        if (niceTransparentColorForRectangle == null) {
            return;
        }
        g.setColor(niceTransparentColorForRectangle);
        g.fillRect(x, y, width, height);
    // Graphics2D gg = (Graphics2D) g;
//         Composite composite = gg.getComposite();
//        AlphaComposite alpha =
//                AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
//                decorator.decorationBackgroundAlpha(severity, decoratedComponent));
//        try {
//            gg.setComposite(alpha);
//            gg.fillRect(x, y, width, height);
//        } finally {
//            gg.setComposite(composite);
//        }
//            Insets ins = getBorderInsets(c);
//            BufferedImage badge = severity.image();
//            int by = (c.getHeight() / 2) - (badge.getHeight() / 2);
//            int w = Math.max (2, ins.left);
//            int bx = x + width - (badge.getHeight() + (w * 2));
//            gg.drawRenderedImage(badge, AffineTransform.getTranslateInstance(bx, by));
    }

        @Override
    public Insets getBorderInsets(Component c) {
        return real.getBorderInsets(c);
    }

        @Override
    public boolean isBorderOpaque() {
        return false;
    }

        @Override
    public void showProblem(Problem problem) {
        if ( problem == null) {
            severity = null;
        }  else {
            severity = problem.severity();
        //c.repaint();
        }
    }

    public void clearProblem() {
        severity = null;
    }
}

/**
 * A JLabel with a decorative icon (that will update when a new {@code
 * Problem} appears) and that displays an informative tooltip.
 * @author heden
 */
static final class ToolTippedIconLabel extends JLabel implements ValidationUI {

    final private JToolTip tt = new JToolTip();
    final private JLabel ttLabel = new JLabel();
    final private JComponent decoratedComponent;
    final private ValidationUI colorizingBorder;
    private Problem currentProblem = null;
    private MouseEvent lastMouseEvent = null;
    private boolean hasAddedToPane = false;
    private SimpleDefaultDecorator decorator;

    ToolTippedIconLabel(final JComponent component, SimpleDefaultDecorator decorator) {
        this.decorator = decorator;
        this.decoratedComponent = component;
        this.colorizingBorder = new ColorizingBorder(component, decorator);
        this.setOpaque(false);

        // Using HierarchyListener instead of ComponentListener.componentShown()
        // and ComponentListener.componentHidden() to handle JTabbedPane, see
        // issue 32
        decoratedComponent.addHierarchyListener(new HierarchyListener() {
                @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0){
                    tryDecorationIcon();
                    ToolTippedIconLabel.this.setVisible(
                            decoratedComponent.isShowing() &&
                            currentProblem != null
                            );
                }
            }
        });

        decoratedComponent.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent evt) {
                tryDecorationIcon();
            }

            @Override
            public void componentResized(ComponentEvent evt) {
                tryDecorationIcon();
            }
        });
        
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                lastMouseEvent = e;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lastMouseEvent = null;
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {
                lastMouseEvent = e;
            }
        });

        //////////////////////////////////////////
        // Fiddle with tooltip
        tt.setLayout(new BorderLayout());
        tt.add(ttLabel);
        tt.setBorder(null);
    }

    @Override
    public JToolTip createToolTip() {
        assert SwingUtilities.isEventDispatchThread() : "Not on EventDispatchThread";
        assert tt != null;
        return tt;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible && currentProblem != null) {
            colorizingBorder.showProblem(currentProblem);
        } else {
            colorizingBorder.clearProblem();
        }
    }

    @Override
    public void showProblem(final Problem problem) {
        // JComponent has been newly validated.
        assert SwingUtilities.isEventDispatchThread() : "Not on EventDispatchThread";
        if (problem != null && problem.equals(currentProblem)) {
            return;
        }
        colorizingBorder.showProblem(problem);
        currentProblem = problem;
        if (currentProblem == null) {
            if (this.isVisible()) {
                this.setVisible(false);
            }
            if (tt.isShowing()) {
                // Bring down tooltip if it isShowing
                final MouseEvent theEvent = lastMouseEvent; // lastEvent will be set to null in mouseExited, so hold a local reference here.
                this.dispatchEvent(new MouseEvent(this, MouseEvent.MOUSE_EXITED, System.currentTimeMillis(), 0, 0, 0, 0, false));
                lastMouseEvent = theEvent;
            }
            return;
        }
        ////////////////////////////////////////////////////
        // Fiddle with tooltip. We do not do this within tryDecorationIcon(),
        // because it is only necessary to do this in updateProblem(). Also, we do
        // this *before* calling tryDecorationIcon(), because that method can
        // return false in cases when we still want to prepare the tooltip.
        problemUpdateTooltip(problem);
        if (!this.tryDecorationIcon()) {
            return;
        }
        this.setVisible(true);
    }

    private void problemUpdateTooltip(Problem problem) {
        if (decorator.getDecorationImage(problem.severity(), decoratedComponent) == null
                || !decorator.configureToolTipLabel(problem, decoratedComponent, ttLabel)) {
            // No tooltip if no decoration, and also no toolTip if configureToolTipLabel
            // returned false
            tt.setVisible(false);
            if(this.getToolTipText() != null) {
                this.setToolTipText(null); // "Unregister" from ToolTipManager..
            }
            return;
        }
        tt.setVisible(true);
        if(this.getToolTipText() == null) {
            this.setToolTipText(""); // "Register" with ToolTipManager..
        }
        tt.setPreferredSize(ttLabel.getPreferredSize());
        if (lastMouseEvent != null) {
            // * If the tooltip is showing: "adapt" the *size* of the tooltip by bringing
            // it down and up again -- by emulating some mouse movements.
            // * If the tooltip is not showing but the mouse pointer seems to be
            // above icon, fire up tooltip.
            final MouseEvent theEvent = lastMouseEvent; // lastEvent will be set to null in mouseExited, so hold a local reference here.
            this.dispatchEvent(new MouseEvent(this, MouseEvent.MOUSE_EXITED, System.currentTimeMillis() - 100, theEvent.getModifiers(), theEvent.getX(), theEvent.getY(), 0, false));
            assert lastMouseEvent == null;
            this.dispatchEvent(new MouseEvent(this, MouseEvent.MOUSE_ENTERED, System.currentTimeMillis() - 50, theEvent.getModifiers(), theEvent.getX(), theEvent.getY(), 0, false));
            this.dispatchEvent(new MouseEvent(this, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), theEvent.getModifiers(), theEvent.getX(), theEvent.getY(), 0, false));
            assert lastMouseEvent != null;
        }
    ////////////////////////////////////////////////////
    }

    /**
     * Set/Update the location of the icon so that it follows the
     * component at resize etc.
     *
     * @return true if there is an icon and the state of the UI is
     * such that it is ready to be set visible.
     *
     * @return false otherwise: If there is currently no problem,
     * or if the UI has not finished laying out
     * (so that the location of the decoration icon can not yet be
     * calculated), or if the decorator has been configured in a way
     * that there should be no Icon visible at all for the current
     * severity (only the ColorizingBorder is to be used)
     */
    private boolean tryDecorationIcon() {
        assert SwingUtilities.isEventDispatchThread() : "Not on EventDispatchThread";
        if (!decoratedComponent.isShowing()) {
            // This is before UI has finished being laid out. Can't do
            // anything now, wait until component listener gets notified.
            // (problemUpdate() has been called during initialization --
            // the UI has errors from the start.)
            return false;
        }
        assert JLayeredPane.getLayeredPaneAbove(decoratedComponent) != null :
                "JLayeredPane.getLayeredPaneAbove(decoratedComponent) unexpectedly returned null";
        // Actually this is not so unexpected if we're not in a JFrame, JDialog, JApplet or JInternalFrame.
        // See http://java.sun.com/docs/books/tutorial/uiswing/components/layeredpane.html .
        // Should we account for this possibility somehow?
        if (!hasAddedToPane) {
            JLayeredPane.getLayeredPaneAbove(decoratedComponent).add(ToolTippedIconLabel.this,
                    new Integer(JLayeredPane.getLayer(decoratedComponent) + 10));
            hasAddedToPane = true;
        }
        if (currentProblem == null) {
            // No point in calculating location now, we don't know our size yet.
            // (We have not encountered a problem yet so there's no icon. We're
            // here because the ComponentListener has been notified about a resize or a move.)
            return false;
        }
        Icon icon = decorator.getDecorationIcon(currentProblem.severity(), decoratedComponent);
        if (icon == null) {
            return false;
        }
        this.setIcon(icon);
        this.setSize(new Dimension(getIcon().getIconWidth(), getIcon().getIconHeight()));

        Point p = decoratedComponent.getLocationOnScreen();
        SwingUtilities.convertPointFromScreen(p, this.getParent());

        Point transl =
                decorator.getDecorationLocation(null, decoratedComponent,
                new Dimension(this.getIcon().getIconWidth(), this.getIcon().getIconHeight()));
        p.translate(transl.x, transl.y);
        this.setLocation(p);
        return true;
    }

        public void clearProblem() {
            showProblem(null);
        }
    }
}

