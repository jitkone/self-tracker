/*
 * TaskData.java
 *
 * Created on 21. helmikuuta 2003, 9:20
 */

package checkmate.model;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
/**
 *
 * @author  jitkonen
 */
public class TaskData implements Cloneable, java.io.Serializable {
    static final long serialVersionUID = 2003022100001L;
     
    private String taskName = "";
    private String categoryName = "Misc";
    private String taskID = null;
    private String taskType = "T";
    private String sortKey = null;
    private long taskTime = 0;
    private long sessionTime = 0;
    private Date lastEntryDate = null;
    
    public static final int AT_LEAST = 1;
    public static final int AT_MOST = 2;
    public static final int EXACT = 0;
    
    private int spendingLevel = 0;
    private int spendingLevelType = AT_LEAST;
    
    protected boolean idleTask = false;   
    private boolean hidden = false;   
    private boolean dead = false;
    
    private long timetoday = -1;
    private long weektime = -1;
    private long monthtime = -1;
    private long yeartime = -1;
    
    private Hashtable timePerDay = new Hashtable();    
    
    /** Creates a new instance of TaskData */
    public TaskData() {  
        init();
    }  
    public TaskData(String name) {
        this();
        taskName = name;
    }  
    public TaskData(String name, long time, long session, String skey) {
        this();
        taskName = name;
        taskTime = time;
        sessionTime = session;
        sortKey = skey;
    }  
    public void init() {      
    	timetoday = -1;
        weektime = -1;
        monthtime = -1;
        yeartime = -1;
        if(timePerDay == null) {
            timePerDay = new Hashtable();
        }
        if(taskID == null || taskID.length() < 6) {
        	generateTaskID();
        }
        if(getMonthTime() == 0 && spendingLevel == 0) {
        	hidden = true;
        }
    }
    public void incrementTime(long millis) {
        taskTime += millis;
        sessionTime +=millis;
        incrementTimePerDay(millis);        
    }
    public void rollbackSession() {
        taskTime -= sessionTime;
        incrementTimePerDay(-sessionTime);
    }
    public long getTimeToday() {
    	if(timetoday < 0) {
    		timetoday = findTimePerDay(getToday());
    	} 
    	return timetoday;
    }
    
    public long getWeekTime() {
    	if(weektime < 0) {
    		weektime = getNDaysTime(7);
    	} 
    	return weektime;    	
    }
    public long getMonthTime() {
    	if(monthtime < 0) {
    		monthtime = getNDaysTime(31);
    	} 
    	return monthtime;
    }
    public long getYearTime() {
    	if(yeartime < 0) {
    		yeartime = getNDaysTime(365);
    	} 
    	return yeartime;
	}
    
    public long getTimePerDay(Date d) {
        Calendar day = getDay(d);
        long value = this.findTimePerDay(day);        
        return value;       
    }
    
    public synchronized long getNDaysTime(int n) {
        long total = 0;
        Calendar c = getToday();        
        for(long i=0; i<n; i++) {            
            total += findTimePerDay(c);
            c.add(Calendar.DAY_OF_YEAR, -1);            
        }      
        return total;
    }
    private String getTimePerDayKey(Calendar c) {
        StringBuffer sb = new StringBuffer();
        sb.append(c.get(Calendar.YEAR)).append("-").append(c.get(Calendar.MONTH))
          .append("-").append(c.get(Calendar.DAY_OF_MONTH));
        return sb.toString();
    }
    private long findTimePerDay(Calendar c) {
        Date date = c.getTime();
        // Kludge to handle daylight savings
        int hod = c.get(Calendar.HOUR_OF_DAY);           
        if(hod == 1) date.setTime(date.getTime() - 3600L*1000L);
        if(hod == 23) date.setTime(date.getTime() + 3600L*1000L);
        // end of kludge
        long time = 0;
        try {
            time = ((Long) timePerDay.get(date)).longValue();
        } catch(NullPointerException npe) {
        }
        try {
            String key = getTimePerDayKey(c);
            time += ((Long) timePerDay.get(key)).longValue();
        } catch(NullPointerException npee) {
        }       
       return time; 
    }
    
    private Calendar getToday() {    	
        return getDay(new Date());
    }
    private Calendar getDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY,0);
        c.set(Calendar.MINUTE,0);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        return c;
    }
    
    private void incrementTimePerDay(long t) {        
        Long time = null;
        String key = getTimePerDayKey(getToday());
        Object o = timePerDay.get(key);
        if(o == null) {
            time = new Long(t);
        } else {            
            time = new Long(((Long) o).longValue() + t);
        }
        timePerDay.put(key, time);
        
        timetoday += t;
        weektime += t;
        monthtime += t;
        yeartime += t;      
    }
    
    void decreaseTimePerDay(Date day, long t) {
    	String key = getTimePerDayKey(getDay(day));
    	Object o = timePerDay.get(key);    	
    	if(o != null) {
    		long l = ((Long) o).longValue();
    		timePerDay.remove(key);
    		long time = l-t;
    		if(time < 0) time = 0;
    		timePerDay.put(key, new Long(time));
    		taskTime -= t;
    		
    		timetoday = -1;
    	    weektime = -1;
    	    monthtime = -1;
    	    yeartime = -1;
    	}
    }
    public void resetSessionTime() {
        sessionTime = 0;
    }
    public long getTime() {
        return taskTime;
    }
    public long getSessionTime() {
        return sessionTime;
    }
    public String getTaskName() {
        return taskName;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public String getTaskType() {
        return taskType;
    }
    public String getTaskID() {
        if(taskID != null) return taskID;
        return taskName;
    }
    public synchronized void generateTaskID() {
        taskID = String.valueOf(System.currentTimeMillis() - 1167901539257L);
        try {
        	Thread.sleep(100);
        } catch (Exception e) {
        	
        }
    }
    public void setTaskName(String name) {
        taskName = name;
    }
    public void setCategoryName(String name) {
        categoryName = name;
    }
	public void setSortKey(String skey) {
		sortKey = skey;
	}
	public String getSortKey() {
		if(sortKey == null) {
			return this.taskName;
		}
		return sortKey;
	}
	public void setCategoryName(Category category) {
		categoryName = category.getName();
	}
	public int getTargetSpendingLevel() {
    	return spendingLevel;
    }
	public int getTargetSpendingLevelType() {
    	return spendingLevelType;
    }
	public String getTargetSpendingLevelString() {
    	if(spendingLevel <= 0) return "";
		String c = "=";
    	if(spendingLevelType == AT_LEAST) {
    		c = ">";
    	} else if(spendingLevelType == AT_MOST) {
    		c = "<";
    	}
		return c+spendingLevel;
    }
	public void setTargetSpendingLevel(String level) {
    	if(level == null || level.length() == 0) {
    		spendingLevel = 0;
    		return;
    	}
		String parsed = level;
		if(level.charAt(0) == '>') {
    		spendingLevelType = AT_LEAST;
    		parsed = level.substring(1);
    	} else if(level.charAt(0) == '<') {
    		spendingLevelType = AT_MOST;
    		parsed = level.substring(1);
    	} else if(level.charAt(0) == '=') {
    		spendingLevelType = EXACT;
    		parsed = level.substring(1);
    	} 
		try {
			int tsl = Integer.parseInt(parsed); 
		
			if(tsl < 0) tsl = 0;
			if(tsl > 100) tsl = 0;
			spendingLevel = tsl;
		} catch (NumberFormatException e) {
			spendingLevel = 0;
		}
    }
	
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cnse) {
            return null;
        }
    }
	public boolean isHidden() {
		return (dead || hidden);
	}
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	public boolean isDead() {
		return dead;
	}
	public void setDead(boolean killed) {
		this.dead = killed;
		if(killed) this.hidden = true;
	}
	public Date getLastEntryDate() {
		return lastEntryDate;
	}
	public void setLastEntryDate(Date d) {
		this.lastEntryDate = d;
	}
	public void addLastEntryDate() {
		if (this.lastEntryDate != null) {
			//System.out.println("Last entry date for "+this.getTaskName()+" IS: "+this.lastEntryDate.toString());
			return;
		}
		Calendar c = getToday();        
        for(long i=0; i<700; i++) {            
            if(findTimePerDay(c) > 0) {  
            	this.lastEntryDate = c.getTime();
            	//System.out.println("SET last entry date for "+this.getTaskName()+" : "+this.lastEntryDate.toString());
            	return;
            }
            c.add(Calendar.DAY_OF_YEAR, -1);            
        }      
        this.lastEntryDate = c.getTime();      
    	
	}
}