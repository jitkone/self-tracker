/*
 * Recorder.java
 *
 * Created on 4. helmikuuta 2003, 19:42
 */

package checkmate.recorder;

import java.util.Collection;
import java.util.List;

import checkmate.model.Task;
import checkmate.model.TaskData;


import junit.framework.TestCase;
/**
 *
 * @author  jitkonen
 */
public class TestRecorder extends TestCase implements RecorderListener {
    private Recorder recorder;
    private Task task1 = null;
    private Task task2 = null;
    private Task task3 = null;
    private Task active = null;
    /** Creates a new instance of Recorder */
    public TestRecorder(String name) {
        super(name);
    }
    protected void setUp() {              
        recorder = new Recorder(null, 0);
        Recorder.debug_timing = Recorder.UNIT_TESTING;
        task1 = new Task("Task1", null);
        task2 = new Task("Task2", null);
        task3 = new Task("Task3", null);
    }
    protected void tearDown() {
        recorder.shutDown();
        recorder = null;
    }
   
     public void testAddAndRemoveTask() {     
        recorder.removeTask(task1); 
        recorder.addTask(task1); 
        recorder.removeTask(task1); 
        Collection tasks = recorder.getTasks();
        assertTrue(tasks.isEmpty());
        recorder.addTask(task1); 
        recorder.addTask(task2);
        recorder.addTask(task3); 
        recorder.removeTask(task2);
        tasks = recorder.getTasks();
        assertEquals(tasks.size(), 2);
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task3));
        recorder.removeTasks();
        assertTrue(recorder.getTasks().isEmpty());
        recorder.shutDown();
    }
    public void testStartAndStopRecording() {     
        recorder.startTask();
        assertFalse(recorder.startTask());
        recorder.addTask(task1);
        assertFalse(recorder.startTask());
        recorder.setActiveTask(task1);
        assertTrue(recorder.startTask());
        assertEquals(recorder.getActiveTask(), task1);
        assertTrue(task1.running());        
        recorder.stopTask();        
        assertEquals(recorder.getActiveTask(), task1);
        assertTrue(!task1.running());
        assertTrue(recorder.getTasks().contains(task1));
        recorder.stopTask();
        recorder.shutDown();
    }    
    
    public void testChangingTaskOnFly() {     
        recorder.addTask(task1); 
        recorder.addTask(task2);
        recorder.addTask(task3); 
        recorder.setActiveTask(task1);
        assertTrue(recorder.startTask());        
        assertTrue(task1.running());        
        recorder.setActiveTask(task2);        
        assertEquals(recorder.getActiveTask(), task2);
        assertTrue(!task1.running());
        assertTrue(task2.running());
        recorder.stopTask();
        recorder.shutDown();
    }    
         
     public void testSetActiveTask() {     
        recorder.addTask(task1); 
        recorder.addTask(task2);
        recorder.addTask(task3); 
        assertNull(recorder.getActiveTask());
        recorder.setActiveTask(task2);
        assertEquals(recorder.getActiveTask(),task2);
        recorder.setActiveTask(null);
        assertNull(recorder.getActiveTask());
        recorder.shutDown();
    }
     
    public void testAddAndRemoveListener() {
        recorder.addRecorderListener(this);
        assertTrue(recorder.getRecorderListeners().contains(this));
        recorder.removeRecorderListener(this);
        assertFalse(recorder.getRecorderListeners().contains(this));
    }
    public void testNotifyListeners() {
        recorder.addTask(task1);
        recorder.addTask(task2);
        recorder.addTask(task3);
        recorder.addRecorderListener(this);
        recorder.setActiveTask(task1);
        assertEquals(active, task1);
        recorder.setActiveTask(task3);
        assertEquals(active, task3);
        recorder.setActiveTask(null);
        assertNull(active);
        recorder.removeRecorderListener(this);
        recorder.shutDown();
    }
    public void testSavingAndLoadingTasks() {
        TaskData tda = new TaskData("Task A", 1000, 0, "0");
        TaskData tdb = new TaskData("Task B", 2000, 200, "1");
        TaskData tdc = new TaskData("Task C", 3000, 300, "2");
        Task task_a = new Task(tda);
        Task task_b = new Task(tdb);
        Task task_c = new Task(tdc);
        recorder.addTask(task_a);
        recorder.addTask(task_b);
        recorder.addTask(task_c);
        try {
            recorder.saveTasks();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }
        recorder.shutDown();
         try {
            recorder.loadTasks();
        } catch (Exception ex) {
            fail(ex.getMessage());
        }        
        List tasks = recorder.getTasks();
        Task t = (Task) tasks.get(0);
        assertEquals(t.getName(), "Task A");
        assertEquals(t.getTime(), 1000);
        assertEquals(t.getSessionTime(), 0);
        t = (Task) tasks.get(1);
        assertEquals(t.getName(), "Task B");
        assertEquals(t.getTime(), 2000);
        assertEquals(t.getSessionTime(), 200);
        t = (Task) tasks.get(2);
        assertEquals(t.getName(), "Task C");
        assertEquals(t.getTime(), 3000);
        assertEquals(t.getSessionTime(), 300);
        recorder.shutDown();
    }
    public void testIdle() {
        Task.setIdleTask(task1);
        recorder.addTask(task1);
        recorder.addTask(task2);        
        assertFalse(task1.running());
        assertFalse(task2.running());
        recorder.setActiveTask(task2);
        recorder.startTask();
        assertTrue(task2.running());
        Recorder.tickSystemTime(120000);
        recorder.setIdleTaskActive();
        assertTrue(task1.running());
        Recorder.tickSystemTime(20000);
        assertFalse(task2.running());
        recorder.setNonIdleTaskActive();
        assertTrue(task2.running());    
        Recorder.tickSystemTime(25000);
        assertFalse(task1.running());
        recorder.stopTask();
        assertEquals(25000, task2.getSessionTime());
        recorder.shutDown();        
    }
   
    /* RecorderListener interface test implementation */
    public void activeTaskChanged(Task oldTask, Task activeTask) {
        active = activeTask;
    }
    
    public void taskAdded(Task newTask) {
    }
    
    public void taskDeleted(Task newTask) {
    }
    
}

    
    
    

