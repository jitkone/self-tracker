/*
 * Entry.java
 *
 * Created on 4. helmikuuta 2003, 9:26
 */

package checkmate.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 *
 * @author  jitkonen
 */
public class Entry implements Serializable, Cloneable {
    static final long serialVersionUID = -2989177045163035324L;
    
    Date date = null;
    long recordedTime = 0;    
    long totalTime = 0;
    String taskName = null;
    String categoryName = null;
    String dataString = null;
    
    public Entry(Task t) {
        taskName = t.getName();
        categoryName = t.getCategory().getName();
        date = new Date();
    }
    public void create(Task task) {
        recordedTime = task.getSessionTime();        
        totalTime = task.getTime();
    }
    public void manualCreate(long recTime, long totalT) {
        recordedTime = recTime;        
        totalTime = totalT;
    }
    public String getTaskName() {
        return taskName;
    }
    public String getCategoryName() {
        return categoryName;
    }
    public Date getDate() {
        return date;
    }
    public long getRecordedTime() {
        return recordedTime;
    }
    
    public String getEntryDataString() {
        if(dataString == null) {
            SimpleDateFormat df = new SimpleDateFormat();
            df.applyPattern("yyyy,MM,w,dd,dd.MM.yyyy,HH:mm:ss");
            dataString = df.format(date)
                +","+getTaskName()
				+","+getCategoryName()
                +","+(getRecordedTime()/1000) //seconds
                +","+((double) getRecordedTime() / (double) (60 * 1000)) // minutes
                +","+((double) getRecordedTime() / (double) (3600 * 1000)) // hours                
                +","+((double) totalTime / (double) (3600 * 1000)); //hours
        }
        return dataString;
    }
    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException cnse) {
        }
        return null;
        
    }
}
