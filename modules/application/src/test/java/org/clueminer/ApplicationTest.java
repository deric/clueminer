package org.clueminer;

import java.util.logging.Level;
import junit.framework.Test;
import junit.framework.TestResult;
import org.netbeans.junit.NbModuleSuite;
import org.netbeans.junit.NbTestCase;

public class ApplicationTest extends NbTestCase {

    public static Test suite() {
		return new Test() {

			@Override
			public int countTestCases() {
				return 0;
//				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}

			@Override
			public void run(TestResult result) {
//				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
		};
//        return NbModuleSuite.createConfiguration(ApplicationTest.class).
//                gui(false).
//                //failOnMessage(Level.WARNING). // works at least in RELEASE71
//                failOnException(Level.WARNING).
//                suite(); // RELEASE71+, else use NbModuleSuite.create(NbModuleSuite.createConfiguration(...))
    }

    public ApplicationTest(String n) {
        super(n);
    }

    public void testApplication() {
        // pass if there are merely no warnings/exceptions
        /* Example of using Jelly Tools with gui(true):
         new ActionNoBlock("Help|About", null).performMenu();
         new NbDialogOperator("About").closeByButton();
         */
    }

}
