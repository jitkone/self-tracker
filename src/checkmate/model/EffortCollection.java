/*
 * Created on 12-Oct-2005
 *
 */
package checkmate.model;

import java.util.Date;

/**
 * 
 */
public abstract class EffortCollection {

	public String getTimeAsString() {
		 if(getTime() != 0) {
			 return msTimeToString(getTime(), false);
		 } else {
			 return "";
		 }
    }
	public String getName() {
        return null;
    }
	public long getTime() {
        return 0;
    }
	public long getTimePerDay(Date d) {
        return 0; 
    }
    public long getSessionTime() {
        return 0;
    }
    public String getSessionTimeAsString() {
        if(getSessionTime() != 0) {
        	return msTimeToString(getSessionTime(), true);
        } else {
        	return "";
        }
    }
    public long getTimeToday() {
        return 0;
    }   
    public String getTimeTodayAsString(boolean seconds) {
    	if(getTimeToday() != 0) {
    		return msTimeToString(getTimeToday(), seconds);
        } else {
        	return "";
        }
    	
    }
    public long getWeekTime() {
        return 0;
    }
    public String getWeekTimeAsString() {
    	long time = getWeekTime();
    	if(time != 0) {
    		return msTimeToString(time, false);
        } else {
        	return "";
        }
    	
    }
    public long getMonthTime() {
        return 0;
    }
    public String getMonthTimeAsString() {
    	long time = getMonthTime();
    	if(time != 0) {
    		return msTimeToString(time, false);
        } else {
        	return "";
        }
    }	
    
    public String getYearTimeAsString() {
    	long time = getYearTime();
    	if(time != 0) {
    		return msTimeToString(time, false);
        } else {
        	return "";
        }
    }	
    public long getYearTime() {
        return 0;
    }
    public int getTargetSpendingLevel() {
    	return 0;
    }
    public String getTargetSpendingLevelString() {
    	return "";
    }
    public int getTargetSpendingLevelType() {
    	return 0;
    }
    
    public void setTargetSpendingLevel() {
    }
    
    public boolean isHidden() {
		return false;
	}
	public void setHidden(boolean hidden) {
	}
	
	public static String msTimeToString(long ms, boolean withSeconds) {
        long sec = ms/1000;
        long s = sec % 60;
        int h = (int) sec/3600;
        int m = (int) (sec / 60) % 60;
        boolean negative = false;
        if(m<0) { 
        	m *= -1;
        	negative = true;
        }
        if(withSeconds) {
            return intToTwoDigitString(h, negative)+":"+intToTwoDigitString(m)
                    +":"+intToTwoDigitString(s);
        } 
        return intToTwoDigitString(h, negative)+":"+intToTwoDigitString(m);
    }
	public static String intToTwoDigitString(long i) {
        if(i < 10 && i>-1) {
            return "0"+String.valueOf(i);
        }
        return String.valueOf(i);                  
    }
	private static String intToTwoDigitString(long i, boolean negateIfZero) {
        if(i == 0 && negateIfZero) {
            return "-0";
        }
        return intToTwoDigitString(i);                  
    }
}
