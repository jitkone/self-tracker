package checkmate.model;

import junit.framework.TestCase;

/**
 * 
 */
public class EffortCollectionTest extends TestCase {

	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Constructor for EffortCollectionTest.
	 * @param arg0
	 */
	public EffortCollectionTest(String arg0) {
		super(arg0);
	}

	public void testMsTimeToString() {
		assertEquals(EffortCollection.msTimeToString(1000, true),"00:00:01");
		assertEquals(EffortCollection.msTimeToString(1000, false),"00:00");
		
		assertEquals(EffortCollection.msTimeToString(5555, true),"00:00:05");
		assertEquals(EffortCollection.msTimeToString(123617000, true),"34:20:17");
	}

	public void testIntToTwoDigitString() {
		assertEquals("",EffortCollection.intToTwoDigitString(15),"15");
		assertEquals("",EffortCollection.intToTwoDigitString(0),"00");
		assertEquals("",EffortCollection.intToTwoDigitString(1),"01");
		assertEquals("",EffortCollection.intToTwoDigitString(9),"09");
		assertEquals("",EffortCollection.intToTwoDigitString(99),"99");
		assertEquals("","456",EffortCollection.intToTwoDigitString(456));
	}

}
