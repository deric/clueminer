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

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
final class EncodableInCharsetValidator extends StringValidator {

    private final String charsetName;

    EncodableInCharsetValidator(String charsetName) {
        this.charsetName = charsetName;
        //Be fail-fast with respect to exceptions
        Charset.forName(charsetName);
    }

    EncodableInCharsetValidator() {
        this(Charset.defaultCharset().name());
    }

    @Override
    public void validate(Problems problems, String compName, String model) {
        char[] c = model.toCharArray();
        boolean result = true;
        String curr;
        for (int i = 0; i < c.length; i++) {
            curr = new String(new char[]{c[i]});
            try {
                String nue = new String(curr.getBytes(charsetName));
                result = c[i] == nue.charAt(0);
                if (!result) {
                    problems.add(NbBundle.getMessage(
                            EncodableInCharsetValidator.class,
                            "INVALID_CHARACTER", compName, curr, charsetName)); //NOI18N
                    break;
                }
            } catch (UnsupportedEncodingException ex) {
                //Already tested in constructor
                throw new AssertionError(ex);
            }
        }
    }
}

