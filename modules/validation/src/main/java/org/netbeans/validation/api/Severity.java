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
package org.netbeans.validation.api;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import org.openide.util.NbBundle;

/**
 * enum indicating {@code Severity}, used for classifying {@link Problem} instances.
 *
 * @author Tim Boudreau
 */
public enum Severity {

    /**
     * An information message for the user, which should not block
     * them from proceeding but may provide advice
     */
    INFO,
    /**
     * A warning to the user that they should change a value, but 
     * which does not block them from proceeding
     */
    WARNING,
    /**
     * A fatal problem with user input which must be corrected
     */
    FATAL;
    private BufferedImage image;

    /**
     * Get a warning icon as an image
     * @return An image
     */
    public synchronized BufferedImage image() {
        if (image == null) {
            String name;
            switch (this) {
                case INFO:
                    name = "info.png"; //NOI18N
                    break;
                case WARNING:
                    name = "warning.png"; //NOI18N
                    break;
                case FATAL:
                    name = "error.png"; //NOI18N
                    break;
                default:
                    throw new AssertionError();
            }
            try {
                image = ImageIO.read(Severity.class.getResourceAsStream(name));
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        return image;
    }

    /**
     * Get an icon version of the warning image
     * @return An icon
     */
    public Icon icon() {
        return new ImageIcon(image());
    }

    /**
     * Get a suitable color for displaying problem text
     * @return A color
     */
    public Color color() {
        switch (this) {
            case FATAL: {
                Color c = UIManager.getColor("nb.errorForeground"); //NOI18N
                if (c == null) {
                    c = Color.RED.darker();
                }
                return c;
            }
            case WARNING:
                return Color.BLUE.darker();
            case INFO:
                return UIManager.getColor("textText");
            default:
                throw new AssertionError();
        }
    }

    BufferedImage badge;
    public BufferedImage badge() {
        if (badge == null) {
            String name;
            switch (this) {
                case INFO:
                    name = "info-badge.png"; //NOI18N
                    break;
                case WARNING:
                    name = "warning-badge.png"; //NOI18N
                    break;
                case FATAL:
                    name = "error-badge.png"; //NOI18N
                    break;
                default:
                    throw new AssertionError();
            }
            try {
                badge = ImageIO.read(Severity.class.getResourceAsStream(name));
            } catch (IOException ex) {
                throw new IllegalArgumentException(ex);
            }
        }
        return badge;
    }

    /**
     * Annotate an error description with the severity.  Used for providing
     * accessible descriptions for error components.
     *
     * @param toDescribe
     * @return
     */
    public String describeError (String toDescribe) {
        return NbBundle.getMessage(Severity.class, name() + ".annotation",
                toDescribe);
    }

    /**
     * Returns a localized name for this enum constant
     * @return A localized name
     */
    @Override
    public String toString() {
        return NbBundle.getMessage(Severity.class, name());
    }
}
