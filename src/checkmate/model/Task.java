/*
 * Task.java
 *
 * Created on 4. helmikuuta 2003, 19:04
 */

package checkmate.model;

import java.util.*;

/**
 *
 * @author  jitkonen
 */
public class Task extends EffortCollection implements Comparable {    
    static final long serialVersionUID = 2003052200001L;    
    private TaskData data = null;
    private long incrementStart = 0;    
    private boolean running = false;
    private Collection listeners = new Vector();
    private TaskIncrementer taskIncrementer = null;   
    private Category category = null;
    
    public static final int START = 1;
    public static final int INCREMENT = 2;
    public static final int STOP = 3;
    private static long removableAfterDays = 5*365; // 5 years, the time removed entries are saved for statistics
    private static Task idleTask = null;
    public static void setIdleTask(Task t) {
        if(Task.idleTask != null) {
            Task.idleTask.data.idleTask = false;
        }
        Task.idleTask = t;
        t.data.idleTask = true;
    }
    /** Creates a new instance of Task */
    public Task(String name, Category c) {
        data = new TaskData();
        data.setTaskName(name);
        if(idleTask == null) {
            setIdleTask(this);
        }
        this.setCategory(c);
    }
    
    public Task(TaskData theData) {
        data = theData;
         if(idleTask == null || data.idleTask) {
            setIdleTask(this);
         }
        setCategory(Category.get(data.getCategoryName())); 
    }
    
    public static Task getIdleTask() {
        return idleTask;
    }
    public boolean isIdleTask() {
        return this == idleTask;
    }
    public String getName() {
        return data.getTaskName();
    }
    public String getTaskID() {
        return data.getTaskID();
    }
    public String getTaskType() {
        return data.getTaskType();
    }
    public Category getCategory() {
    	return category;
    }
    public long getTime() {
        return data.getTime();
    }
    public long getTimePerDay(Date d) {
        return data.getTimePerDay(d); 
    }
    
    public long getSessionTime() {
        return data.getSessionTime();
    }
    
    public long getTimeToday() {
        return data.getTimeToday();
    }   
    
    public long getWeekTime() {
        return data.getWeekTime();
    }
    
    public long getMonthTime() {
        return data.getMonthTime();
    }
    
    public long getYearTime() {
        return data.getYearTime();
    }
    
    public TaskData getData() {
        return data;
    }
    
    public int getTargetSpendingLevel() {
    	return data.getTargetSpendingLevel();
    }
    public String getTargetSpendingLevelString() {
    	return data.getTargetSpendingLevelString();
    }
    public int getTargetSpendingLevelType() {
    	return data.getTargetSpendingLevelType();
    }
    
    public void addMinutes(int minutes) {
        data.incrementTime(minutes*60*1000);
        notifyListeners(INCREMENT);
    }
    
    public void increment() {
        if(running) {
            long now = checkmate.recorder.Recorder.getSystemTime();
            long inc = (now - incrementStart);
            incrementStart = now;
            data.incrementTime(inc);            
            notifyListeners(INCREMENT);
        }
    }
    public void removeEntry(Date day, long time) {
    	data.decreaseTimePerDay(day, time);
    }
    private void resetSessionTime() {
        data.resetSessionTime();
    }
    public boolean equals(Object o) {
        if(o instanceof Task) {
            return ((Task) o).getName().equals(data.getTaskName());
        }
        return false;
    }
    
    public boolean running() {
        return running;
    }
    
    public void start() {   
        resetSessionTime();
        incrementStart = checkmate.recorder.Recorder.getSystemTime();
        running = true;        
        notifyListeners(START);
        taskIncrementer = new TaskIncrementer();
        Thread incrementer = new Thread(taskIncrementer, "Task: "+data.getTaskName());
        incrementer.start();
        this.setHidden(false);
        this.data.setLastEntryDate(new Date());
    }
    
    public void stop() {
        if(taskIncrementer != null) {
            taskIncrementer.stop();
        }
        if(running) {            
            increment();
            running = false;
            notifyListeners(STOP);
        }        
    }
    
    public void stopAndRollback() {
        if(taskIncrementer != null) {
            taskIncrementer.stop();            
        }
        if(running) {            
            running = false;            
            data.rollbackSession();                                
            notifyListeners(STOP);
        }
    }
    
    public String toString() { 
        return data.getTaskName() + "  ["+category.getName()+"]";
    }
    
    public String getTimeText(boolean withTotal) {
        return this.getTimeTodayAsString(true);            
    }
        
    public void addTaskListener(TaskListener listener) {
        listeners.add(listener);
    }
    public void removeTaskListener(TaskListener listener) {
        listeners.remove(listener);
    }
    public Collection getTaskListeners() {
        return new Vector(listeners);
    }
    private void notifyListeners(int what) {
        Iterator it = listeners.iterator();
        while(it.hasNext()) {
            TaskListener tl = (TaskListener) it.next();
            tl.taskUpdated(what, this);
        }
    }
    
    public String getSortKey() {
    	return data.getSortKey();    
    }
	public void setSortKey(String sk) {
		data.setSortKey(sk);    
	}	
	
	public void setCategory(Category cat) {
		category = cat;
		if(category == null) {
			category = Category.DEFAULT;
		}
		category.addTask(this);
		data.setCategoryName(category.getName());
	}
	
	public boolean isHidden() {
		return data.isHidden();
	}
	public void setHidden(boolean hidden) {
		data.setHidden(hidden);
	}
	public boolean isDead() {
		return data.isDead();
	}
	public void kill() {
		data.setDead(true);
	}
	public boolean isRemovable(Date today) {
		Date last = data.getLastEntryDate();
		if(last != null && isDead()) {			
			return ((today.getTime() - last.getTime()) > (removableAfterDays*24l*3600l*1000));
		}
		return false;
	}
	public int compareTo(Object o) throws ClassCastException {
		Task t = (Task) o;
		int value = getSortKey().compareToIgnoreCase(t.getSortKey());
		if(value == 0) value = getName().compareToIgnoreCase(t.getName());
		return value;
	}
    private class TaskIncrementer implements Runnable {
        boolean incrementerRunning = true;
        public void run() {
            while(incrementerRunning) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {}
                increment();
            }
        }
        public void stop() {
            incrementerRunning = false;
        }
    }
}
