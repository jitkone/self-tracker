/*
 * TestTask.java
 *
 * Created on 4. helmikuuta 2003, 18:58
 */

package checkmate.model;

import java.util.Vector;

import checkmate.recorder.Recorder;

import junit.framework.TestCase;
/**
 *
 * @author  jitkonen
 */
public class TestTask extends TestCase implements TaskListener {
    private String name = "TestTask1";
    private Task task;
    private Vector taskStartedNotifications = new Vector();
    private Vector taskStoppedNotifications = new Vector();
    private Vector taskTimeChangedNotifications = new Vector();
    /** Creates a new instance of TestTask */
    public TestTask(String testName) {
        super(testName);
    }
    protected void setUp() {
    	Recorder.setUnitTestTiming();
        task = new Task(name, null);
    }
    protected void tearDown() {
    }
    
    public void testCreateNewTask() {                
        assertEquals(task.getName(), name);
        assertEquals(task.getTime(), 0);
    }    
    public void testStartAndStopTask() {                
        task.start();         
        assertTrue(task.running());
        Recorder.tickSystemTime(145000);
        task.stop();
        assertTrue(task.getTime() == 145000);
        assertTrue(!task.running());
    }    
    
    public void testAddAndRemoveListeners() {
        task.addTaskListener(this);
        assertTrue(task.getTaskListeners().contains(this));
        task.removeTaskListener(this);
        assertFalse(task.getTaskListeners().contains(this));
    }
    
    public void testTaskListenerNotifications() {
        task.addTaskListener(this);
        resetNotifications();
        task.start();        
        assertEquals(taskStartedNotifications.size(),1);
        task.increment();        
        assertEquals(taskTimeChangedNotifications.size(),1);
        task.stop();        
        assertEquals(taskStoppedNotifications.size(),1);        
        resetNotifications();
        task.removeTaskListener(this);
    }
    private void resetNotifications() {
        taskStartedNotifications.clear();
        taskStoppedNotifications.clear();
        taskTimeChangedNotifications.clear();
    }
        
    public void taskUpdated(int what, Task aTask) {
        if(what == Task.START) {
            taskStartedNotifications.add(new Long(System.currentTimeMillis()));   
        } else if(what == Task.STOP) {
            taskStoppedNotifications.add(new Long(System.currentTimeMillis()));    
        } else if(what == Task.INCREMENT) {
            taskTimeChangedNotifications.add(new Long(System.currentTimeMillis()));
        }
    }
    
    
    
}
