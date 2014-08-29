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
package org.netbeans.validation.api.builtin.stringvalidation;

import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
final class IpAddressValidator extends StringValidator {

    @Override
    public void validate(Problems problems, String compName, String s) {
        if (s.startsWith(".") || s.endsWith(".")) { //NOI18N
            problems.add(NbBundle.getMessage(IpAddressValidator.class,
                    "HOST_STARTS_OR_ENDS_WITH_PERIOD", s)); //NOI18N
            return;
        }
        if (s.indexOf(" ") >= 0 || s.indexOf ("\t") >= 0) {
            problems.add(NbBundle.getMessage(IpAddressValidator.class,
                    "IP_ADDRESS_CONTAINS_WHITESPACE", compName, s)); //NOI18N
            return;
        }
        String[] parts = s.split("\\.");
        if (parts.length > 4) {
            problems.add(NbBundle.getMessage(IpAddressValidator.class,
                    "TOO_MANY_LABELS", s)); //NOI18N
            return;
        }
        if( parts.length < 3) {
            problems.add(NbBundle.getMessage(IpAddressValidator.class,
                            "ADDR_PART_BAD", s)); //NOI18N
                    return;
        }
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (i == parts.length - 1 && part.indexOf(":") > 0) { //NOI18N
                if (part.endsWith(":")) {
                    problems.add(NbBundle.getMessage(IpAddressValidator.class,
                            "TOO_MANY_COLONS", compName, s)); //NOI18N
                    return;
                }
                String[] pts = part.split(":"); //NOI18N
                try {
                    int addr = Integer.parseInt(pts[0]);
                    if (addr < 0) {
                        problems.add(NbBundle.getMessage(IpAddressValidator.class,
                                "ADDR_PART_NEGATIVE", pts[1])); //NOI18N
                        return;
                    }
                    if (addr > 256) {
                        problems.add(NbBundle.getMessage(IpAddressValidator.class,
                                "ADDR_PART_HIGH", pts[1])); //NOI18N
                        return;
                    }
                } catch (NumberFormatException e) {
                    problems.add(NbBundle.getMessage(IpAddressValidator.class,
                            "ADDR_PART_BAD", pts.length >= 2 ? pts[1] : "''")); //NOI18N
                    return;
                }
                if (pts.length == 2 && pts[1].length() == 0) {
                    problems.add(NbBundle.getMessage(IpAddressValidator.class,
                            "INVALID_PORT", compName, "")); //NOI18N
                    return;
                }
                if (pts.length == 1 ) {
                    problems.add(NbBundle.getMessage(IpAddressValidator.class,
                            "INVALID_PORT", compName, "")); //NOI18N
                    return;
                }
                if (pts.length > 1) {
                    try {
                        int port = Integer.parseInt(pts[1]);
                        if (port < 0) {
                            problems.add(NbBundle.getMessage(IpAddressValidator.class,
                                    "NEGATIVE_PORT", pts[1])); //NOI18N
                            return;
                        } else if (port >= 65536) {
                            problems.add(NbBundle.getMessage(IpAddressValidator.class,
                                    "PORT_TOO_HIGH", pts[1])); //NOI18N
                            return;
                        }
                    } catch (NumberFormatException e) {
                        problems.add(NbBundle.getMessage(IpAddressValidator.class,
                                "INVALID_PORT", compName, pts[1])); //NOI18N
                        return;
                    }
                }
            } else {
                try {
                    int addr = Integer.parseInt(part);
                    if (addr < 0) {
                        problems.add(NbBundle.getMessage(IpAddressValidator.class,
                                "ADDR_PART_NEGATIVE", part)); //NOI18N
                        return;
                    }
                    if (addr > 256) {
                        problems.add(NbBundle.getMessage(IpAddressValidator.class,
                                "ADDR_PART_HIGH", part)); //NOI18N
                        return;
                    }
                } catch (NumberFormatException e) {
                    problems.add(NbBundle.getMessage(IpAddressValidator.class,
                            "ADDR_PART_BAD", part)); //NOI18N
                    return;
                }
            }
        } //for
    }
}
