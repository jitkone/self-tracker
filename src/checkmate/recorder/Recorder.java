/*
 * Recorder.java
 *
 * Created on 4. helmikuuta 2003, 19:50
 */

package checkmate.recorder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.JOptionPane;

import checkmate.model.Entry;
import checkmate.model.Task;
import checkmate.model.TaskData;



/**
 *
 * @author  jitkonen
 */
public class Recorder {
	public static char UNIT_TESTING = 'U';
    public static char ACCELERATED_TIME = 'A';
    public static char NORMAL_TIME = '-';
    
    /* UNIT_TESTING, ACCELERATED_TIME, or NORMAL_TIME */
	public static char debug_timing = NORMAL_TIME; //ACCELERATED_TIME; //
	private static int timeAcceleration = 47; // times normal time
	private static final int AUTO_SAVE_INTERVAL = 15; //minutes
    private static long startTime = 0;
    private static long systemTime = 0;
    private static long entryExpiresAfterDays = 10*365; // ten years; after that time all data entries are removed
    private List tasks = new LinkedList();
    private static Task activeTask = null;
    private Collection listeners = new Vector();
    
    private String dataFilePath = "./logs/";
    private String taskFileName = "cm_taskdata.cmt";
    private String logFileName = "cm_log.txt";
    private String entryFileName = "cm_entrydata.cmt";
    
    private BufferedWriter logFile = null;
    private Task nonIdleTask = null;
    private Entry runningEntry = null;
    private List entries = new ArrayList();
    private AutoSaver autosaver = new AutoSaver();
    /** Creates a new instance of Recorder */    
    public Recorder(String path, int debug_time_factor) {                                
        Thread ast = new Thread(autosaver,"AutoSaver");
        ast.start();
        if(path != null && path.length() > 0) {
        	dataFilePath = path;
        }
        if(debug_time_factor > 0) {
        	debug_timing = ACCELERATED_TIME;
        	timeAcceleration = debug_time_factor;
        }
    }
    
    public void addTask(Task task) {
        if(task == null) return;
        tasks.add(task);
        notifyListenersAddTask(task);
    }
    public void updateTask(Task task) {
        if(task == null) return;
        notifyListeners(task);
    }
    
    public void deleteActiveTask() {
        if(activeTask == null) return;
        Task task = activeTask;
        removeTask(task);
        notifyListenersDeleteTask(task);
    }
    
    public void removeTask(Task task) {        
        if(activeTask != null && activeTask.equals(task)) {
            activeTask.stop();
            activeTask = null;
        }
        task.kill();
        //tasks.remove(task);
    }
    public void removeTasks() {
        if(activeTask != null) {
            activeTask.stop();
            activeTask = null;
        }
        tasks.clear();
    }
    public List getTasks() {
    	Collections.sort(tasks);
        return new LinkedList(tasks);
    }
    public List getEntries() {
    	return entries;
    }
    public Task findTask(String name) {
        Iterator it = tasks.iterator();
        while(it.hasNext()) {
            Task t = (Task) it.next();
            if(t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }
    public void setActiveTask(Task activate) {       
        boolean running = false;
        Task oldTask = activeTask;
        if(activeTask != null && activeTask.equals(activate)) {
            return;
        }        
        if(activate != null) {
            if(activeTask != null) {
                running = activeTask.running();
                stopTask();
            }
            activeTask = activate;            
        } else {
            stopTask();
            activeTask = null;            
        }
        if(activeTask != null && !activeTask.equals(Task.getIdleTask())) {
            nonIdleTask = null;
        }
        notifyListeners(oldTask);
        if(running) {
            startTask();
        }        
    }
    public void setIdleTaskActive() {
        nonIdleTask = activeTask;
        setActiveTask(Task.getIdleTask());
    }
    public void setNonIdleTaskActive() {
        if(nonIdleTask != null) {
            setActiveTask(nonIdleTask);
        }
        nonIdleTask = null;
    }
    public boolean startTask() {       
        if(activeTask != null) {
            activeTask.start();
            runningEntry = new Entry(activeTask);
            return true;
        }        
        return false;
    }
    
    public void stopTask() {       
        if(activeTask != null) {
            if(activeTask.running()) {
                if(activeTask.getSessionTime() > 30000) {
                    activeTask.stop();
                    runningEntry.create(activeTask);
                    entries.add(runningEntry);                    
                } else {
                    activeTask.stopAndRollback();
                }                
            }
        }
        try {
            saveTasks();
        } catch (java.io.IOException ioex) {
            JOptionPane.showMessageDialog(null, ioex.getMessage(), "Error: Saving task data failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void addManualEntry(int minutes, Task t) {
        Entry e = new Entry(t);
        long millis = (long) minutes*60*1000;
        t.addMinutes(minutes);
        e.manualCreate(millis, t.getTime());        
        entries.add(e);
        try {
            saveTasks();
        } catch (java.io.IOException ioex) {
            JOptionPane.showMessageDialog(null, ioex.getMessage(), "Error: Saving task data failed", JOptionPane.ERROR_MESSAGE);
        }
    }
	
    private void writeLogFile() {
		Iterator i = entries.iterator();
		try {
			File dataFolder = new File(dataFilePath);
	    	if(!dataFolder.exists()) {
	    		dataFolder.mkdir();
	    	} 
			logFile = new BufferedWriter(new FileWriter(new File(dataFolder, logFileName), false));
			while(i.hasNext()) {
				Entry e = (Entry) i.next();
			    logFile.write(e.getEntryDataString());
			    logFile.newLine();
			}
			logFile.flush();
		    logFile.close();        
		} catch(Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "Error: Writing task log failed", JOptionPane.ERROR_MESSAGE);
		}
	}

	public Task getActiveTask() {
        return activeTask;
    }
    
    public void removeEntry(Entry e) {
    	if(entries.remove(e)) {
    		Iterator i = tasks.iterator();
    		while(i.hasNext()) {
    			Task t = (Task) i.next();
    			if(t.getName().equals(e.getTaskName())) {
    				t.removeEntry(e.getDate(), e.getRecordedTime());
    			}
    		}
    	}
    }
    public void shutDown() {
        autosaver.stop();
        removeTasks();
        listeners.clear();       
    }
    public synchronized void saveTasks() throws IOException {
        ArrayList data = new ArrayList(tasks.size());
        Iterator it = tasks.iterator();
        Date today = new Date();
        while(it.hasNext()) {
            Task t = (Task) it.next();            
            if(!t.isRemovable(today)) {
            	data.add(t.getData());
            } else {
            	System.out.println("Removed task: "+t.getName());
            }
        }    
        File dataFolder = new File(dataFilePath);
    	if(!dataFolder.exists()) {
    		dataFolder.mkdir();
    	}   
        ObjectOutputStream ostr = new ObjectOutputStream(new FileOutputStream(new File(dataFolder, taskFileName)));   
        ostr.writeObject(data);
        ostr.flush();
        ostr.close();
        ostr = new ObjectOutputStream(new FileOutputStream(new File(dataFolder, entryFileName)));
        ostr.writeObject(entries);
        ostr.flush();
        ostr.close();
        //saveBackupTasks();
        writeLogFile();
    }
    public synchronized void saveBackupTasks() throws IOException {
    	SimpleDateFormat ddf = (SimpleDateFormat) SimpleDateFormat.getDateTimeInstance();
    	ddf.applyPattern("yyyy-MM-dd_");
    	String today = ddf.format(new Date());
    	
    	ArrayList data = new ArrayList(tasks.size());
        Iterator it = tasks.iterator();
        while(it.hasNext()) {
            Task t = (Task) it.next();
            data.add(t.getData());
        }    
        File dataFolder = new File(dataFilePath + "/backup");
    	if(!dataFolder.exists()) {
    		dataFolder.mkdir();
    	}   
        ObjectOutputStream ostr = new ObjectOutputStream(new FileOutputStream(new File(dataFolder, today+taskFileName)));
        ostr.writeObject(data);
        ostr.flush();
        ostr.close();
        ostr = new ObjectOutputStream(new FileOutputStream(new File(dataFolder, today+entryFileName)));
        ostr.writeObject(entries);
        ostr.flush();
        ostr.close();
        writeLogFile();
    }
    public void loadTasks() throws Exception {
    	ObjectInputStream istr = null;
    	File dataFolder = new File(dataFilePath);
    	if(!dataFolder.exists()) {
    		dataFolder.mkdir();
    	}    	
    	File dataFile = new File(dataFolder, taskFileName);
    	if(dataFile.exists()) {
    		istr = new ObjectInputStream(new FileInputStream(dataFile));   
    		ArrayList data = (ArrayList) istr.readObject();        
    		istr.close();
    		Iterator it = data.iterator();
            while(it.hasNext()) {
                TaskData d = (TaskData) it.next();
                d.init();                
                addTask(new Task(d));
            }   
    	}    	
        File entryFile = new File(dataFolder, entryFileName);
        if(entryFile.exists()) {
            istr = new ObjectInputStream(new FileInputStream(entryFile));   
            entries = (ArrayList) istr.readObject();
            istr.close();
        }
        // Remove old entries
        Iterator i = entries.iterator();
		Entry e = null;
		long date = (new Date()).getTime();
        while(i.hasNext()) {
			e = (Entry) i.next();
		    if((date - e.getDate().getTime()) > (entryExpiresAfterDays*24l*3600l*1000l)) {
		    	i.remove();
		    	System.out.println("Removed entry: "+e.getEntryDataString());
		    }
        }
    }
    
    public void addRecorderListener(RecorderListener listener) {
        listeners.add(listener);
    }
    public void removeRecorderListener(RecorderListener listener) {
        listeners.remove(listener);
    }
    public List getRecorderListeners() {
        return new Vector(listeners);
    }
    private void notifyListeners(Task oldTask) {
        Iterator it = listeners.iterator();
        while(it.hasNext()) {
            RecorderListener rl = (RecorderListener) it.next();
            rl.activeTaskChanged(oldTask, activeTask);
        }
    }
    private void notifyListenersAddTask(Task task) {
        Iterator it = listeners.iterator();
        while(it.hasNext()) {
            RecorderListener rl = (RecorderListener) it.next();
            rl.taskAdded(task);
        }
    }
    private void notifyListenersDeleteTask(Task task) {
        Iterator it = listeners.iterator();
        while(it.hasNext()) {
            RecorderListener rl = (RecorderListener) it.next();
            rl.taskDeleted(task);
        }
    }
    public static long getSystemTime() {
    	if(startTime == 0) {
    		startTime = System.currentTimeMillis();
    	}
    	long time = System.currentTimeMillis(); 
    	if(debug_timing == '-') {
    		return time;
    	} else if(debug_timing == ACCELERATED_TIME) {
    		return time + ((time - startTime ) * timeAcceleration);
    	} 
    	return systemTime;
    }
    public static void tickSystemTime(long millis) {
    	systemTime += millis;
    	if(activeTask != null) activeTask.increment();
    }
    public static void setUnitTestTiming() {
    	debug_timing = UNIT_TESTING;
    }
    /*private boolean writeDaySummaryLog(Date d) {        
        SimpleDateFormat ddf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        ddf.applyPattern("yyyy-MM-dd");
        SimpleDateFormat tdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
        tdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date today = new Date();              
        String filename = "day_times_"+ddf.format(d)+".log";
        File summaryFolder = new File(daySummaryFilePath);
    	if(!summaryFolder.exists()) {
    		summaryFolder.mkdir();
    	}   
        File summaryFile = new File(daySummaryFilePath, filename);
        if(!summaryFile.exists()) {
            int rown = 1;
            try {
            //REPORTING_DATE, GROUPNAME, LOGIN, ROW, TASK_ID, HOURS_DONE, HOURS_LEFT, EST_DATE, WORK_TYPE, EXPLANATION, UPDATED
            BufferedWriter sumFile = new BufferedWriter(new FileWriter(summaryFile));
            Iterator it = tasks.iterator();
            while(it.hasNext()) {
                Task t = (Task) it.next();
                long tperd = t.getTimePerDay(d);
                if( tperd > 0) {
                    double time = (double) tperd / (double) 3600000;
                    sumFile.write(ddf.format(d)+","+"sems"+","+"jitkonen"+","+(rown++)+","
                            +t.getTaskID()+","+time
                            +","+"0"+","+"2005-01-01"+","+t.getTaskType()+","+"''"+","+tdf.format(today));
                    sumFile.newLine();
                }
            }
            sumFile.flush();
            sumFile.close();
            if(rown == 1) {
                summaryFile.delete();
            }
            } catch (java.io.IOException ioex) {
                JOptionPane.showMessageDialog(null, ioex.getMessage(), "Error: Saving day summary data failed", JOptionPane.ERROR_MESSAGE);
            }
            return true;
        }
        return false;
    }*/
    
    private class AutoSaver implements Runnable {
        boolean running = true;
        public void run() {
            while(running) {
                try {
                    Thread.sleep(AUTO_SAVE_INTERVAL * 60 * 1000);
                } catch (InterruptedException ex) {}
                try {                  
                	Entry tmpEntry = null;
                    if(activeTask != null && activeTask.running()) {
                        tmpEntry = (Entry) runningEntry.clone();
                        tmpEntry.create(activeTask);
                        entries.add(tmpEntry);
                        /*logFile = new BufferedWriter(new FileWriter(autosaveLogFilePath,false));
                        logFile.write(tmpEntry.getEntryDataString());
                        logFile.newLine();
                        logFile.flush();
                        logFile.close();*/
                    }
                    saveTasks();
                    if(tmpEntry != null) {
                    	entries.remove(tmpEntry);
                    }
                } catch (Exception ex) {}
            }
        }
        public void stop() {
            running = false;
        }
    }
}
