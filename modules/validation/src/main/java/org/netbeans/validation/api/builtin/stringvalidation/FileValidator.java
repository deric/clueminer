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

import java.io.File;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Severity;
import org.netbeans.validation.api.Validator;
import org.openide.util.NbBundle;

/**
 *
 * @author Tim Boudreau
 */
final class FileValidator extends StringValidator {
    private final Type type;
    FileValidator(Type type) {
        this.type = type;
    }

    @Override
    public void validate(Problems problems, String compName, String model) {
        File file = new File (model);
        String key;
        boolean ok;
        switch (type) {
            case MUST_EXIST :
                key = "FILE_DOES_NOT_EXIST"; //NOI18N
                ok = file.exists();
                break;
            case MUST_BE_DIRECTORY :
                key = "FILE_IS_NOT_A_DIRECTORY"; //NOI18N
                ok = file.isDirectory();
                break;
            case MUST_BE_FILE :
                key = "FILE_IS_NOT_A_FILE"; //NOI18N
                ok = file.isFile();
                break;
            case MUST_NOT_EXIST :
                key = "FILE_EXISTS"; //NOI18N
                ok = !file.exists();
                break;
            default :
                throw new AssertionError();
        }
        if (!ok) {
            String problem = NbBundle.getMessage(FileValidator.class, key,
                    file.getName());
            problems.add(problem, Severity.FATAL);
        }
    }
    
    enum Type {
        MUST_EXIST,
        MUST_NOT_EXIST,
        MUST_BE_DIRECTORY,
        MUST_BE_FILE,
    }

}
