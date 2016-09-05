package checkmate.model;

import checkmate.recorder.Recorder;
import junit.framework.TestCase;
/**
 * 
 */
public class CategoryTest extends TestCase {
	private Category anonymousCategory;
	private Category namedEmptyCategory;
	private Category namedOneItemCategory;
	private Category namedMultipleItemCategory;
	
	/*
	 * @see TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
		Recorder.setUnitTestTiming();
		anonymousCategory = Category.get(null);
		namedEmptyCategory = Category.get("namedEmpty");
		
		namedOneItemCategory = Category.get("namedOneItem");
		TaskData taskData = new TaskData("Teaching", 44000000, 560000, "123");
		Task task = new Task(taskData);
		namedOneItemCategory.addTask(task);
		
		namedMultipleItemCategory = Category.get("namedMultipleItem");
		taskData = new TaskData("Teaching", 44000000, 560000, "123");
		task = new Task(taskData);
		task.start();
		Recorder.tickSystemTime(123000);
		task.stop();
		namedMultipleItemCategory.addTask(task);
		taskData = new TaskData("Research", 55000000, -560000, "000");
		task = new Task(taskData);
		namedMultipleItemCategory.addTask(task);
		taskData = new TaskData("Studying", 66000000, 1230000, "5555");
		task = new Task(taskData);
		namedMultipleItemCategory.addTask(task);
		taskData = new TaskData("Sleeping", 100000000, 1000000, "999999");
		task = new Task(taskData);
		namedMultipleItemCategory.addTask(task);
		
	}

	/*
	 * @see TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
		Category.removeCategories();
	}

	/**
	 * Constructor for CategoryTest.
	 * @param arg0
	 */
	public CategoryTest(String arg0) {
		super(arg0);
	}

	public void testGetName() {
		assertEquals("Misc", this.anonymousCategory.getName());
		assertEquals("namedEmpty", this.namedEmptyCategory.getName());
		assertEquals("namedMultipleItem", this.namedMultipleItemCategory.getName());
	}

	public void testGetTime() {
		assertEquals(44000000,Category.get("namedOneItem").getTime());
		assertEquals(44000000,namedOneItemCategory.getTime());
		assertEquals(44000000+123000+55000000+66000000+100000000,
				    namedMultipleItemCategory.getTime());
	}
	public void testGetSessionTime() {
		assertEquals(0,namedOneItemCategory.getSessionTime());
		assertEquals(0, namedMultipleItemCategory.getSessionTime());
	}
	public void testGetTimeToday() {
		assertEquals(123000, namedMultipleItemCategory.getTimeToday());
	}

	public void testGetMonthTime() {
		assertEquals(123000, namedMultipleItemCategory.getMonthTime());
	}

	public void testToString() {
		assertEquals("namedMultipleItem", namedMultipleItemCategory.toString());
	}
	
	public void testEqualsObject() {
		assertTrue(namedMultipleItemCategory.equals(namedMultipleItemCategory));
		assertFalse(namedMultipleItemCategory.equals(namedOneItemCategory));
		
	}

	public void testGet() {
		assertEquals("namedMultipleItem", Category.get("namedMultipleItem").getName());
	}

}
