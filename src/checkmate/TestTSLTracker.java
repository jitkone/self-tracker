/*
 * TestCheckmate.java
 *
 * Created on 4. helmikuuta 2003, 19:19
 */

package checkmate;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 *
 * @author  jitkonen
 */
public class TestTSLTracker extends TestCase {
    
    /** Creates a new instance of TestCheckmate */
    public TestTSLTracker() {
    }
    public static Test suite() {

        //
        // Reflection is used here to add all
        // the testXXX() methods to the suite.
        //
        TestSuite suite = new TestSuite();
        suite.addTestSuite(checkmate.model.TestTask.class);
        suite.addTestSuite(checkmate.recorder.TestRecorder.class);
        return suite;
    }
}
