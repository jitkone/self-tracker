/*
 * DetailTableModel.java
 *
 * Created on 14. toukokuuta 2003, 20:46
 */

package checkmate.gui;


import java.awt.Color;
import java.util.*;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import checkmate.model.*;


/**
 *
 * @author  jitkonen
 */
public class DetailTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	//private JTable myTable;    
    private List tasks;
    private List visibleTasks;
    private boolean showHiddenTasks = false;
    private Object[] categories;
    final static int COL_NUM = 6;
    final static int NAME_COL = 0;
    final static int TODAY_COL = 1;
    final static int SESSION_COL = 2; 
    final static int WEEK_COL = 3;
    final static int MONTH_COL = 4;
    final static int YEAR_COL = 5;
    
    final static float DAY_NORMAL = 7.25f;
    final static float WEEK_NORMAL = 36.25f;
    final static float MONTH_NORMAL = 152.25f;
    
    final static float HIGH_FACTOR = 1.05f;
    final static float VERY_HIGH_FACTOR = 1.10f;
    final static float LOW_FACTOR = 0.95f;
    final static float VERY_LOW_FACTOR = 0.9f;
    
    static final Color NO_HIGHLIGHT_C = Color.black;
    static final Color GREEN_HIGHLIGHT_C = new Color(0,150,0);
	static final Color RED_HIGHLIGHT_C = new Color(180,0,0);
	static final Color ORANGE_HIGHLIGHT_C = new Color(200,100,0);
	static final Color BLUE_HIGHLIGHT_C = new Color(0,0,200);
	static final Color SELECTED_HIGHLIGHT_C = new Color(220,220,220);
	
    long ttoday = 0;
    long tweek = 0;
    long tmonth = 0;
    long tyear = 0;
    long twEndOfDay = 0;
    long tmEndOfDay = 0;
    
    int selRow = 0;
    //long ttotal = 0;
    /** Creates a new instance of DetailTableModel */
    public DetailTableModel(JTable table) {
        super();
        //myTable = table;
    }   
    protected Color getDayColor() {
        if(ttoday > VERY_HIGH_FACTOR * DAY_NORMAL * 3600f * 1000f) return RED_HIGHLIGHT_C;
        if(ttoday > HIGH_FACTOR * DAY_NORMAL * 3600f * 1000f) return ORANGE_HIGHLIGHT_C;
        if(ttoday >= DAY_NORMAL * 3600f * 1000f) return GREEN_HIGHLIGHT_C;
        if(ttoday < LOW_FACTOR * DAY_NORMAL * 3600f * 1000f) return BLUE_HIGHLIGHT_C;
        return NO_HIGHLIGHT_C;
    }
    protected Color getWeekColor() {
    	if(twEndOfDay > VERY_HIGH_FACTOR * WEEK_NORMAL * 3600f * 1000f) return RED_HIGHLIGHT_C;
        if(twEndOfDay > HIGH_FACTOR * WEEK_NORMAL * 3600f * 1000f) return ORANGE_HIGHLIGHT_C;
        if(twEndOfDay >= WEEK_NORMAL * 3600f * 1000f) return GREEN_HIGHLIGHT_C;
        if(twEndOfDay < LOW_FACTOR * WEEK_NORMAL * 3600f * 1000f) return BLUE_HIGHLIGHT_C;
        return NO_HIGHLIGHT_C;
    }
    protected Color getMonthColor() {
    	if(tmEndOfDay > VERY_HIGH_FACTOR * MONTH_NORMAL * 3600f * 1000f) return RED_HIGHLIGHT_C;
        if(tmEndOfDay > HIGH_FACTOR * MONTH_NORMAL * 3600f * 1000f) return ORANGE_HIGHLIGHT_C;
        if(tmEndOfDay >= MONTH_NORMAL * 3600f * 1000f) return GREEN_HIGHLIGHT_C;
        if(tmEndOfDay < LOW_FACTOR * MONTH_NORMAL * 3600f * 1000f) return BLUE_HIGHLIGHT_C;
        return NO_HIGHLIGHT_C;
    }
    public int getColumnCount() { return COL_NUM; }
    public int getRowCount() { 
        if(visibleTasks != null) {
            return visibleTasks.size()+categories.length+1;
        }
        return 0;
    }
    protected EffortCollection getCollectionOnRow(int r) {
    	EffortCollection t = null;
    	int numC = categories.length;
    	if(r < numC) {
    		t = (EffortCollection) categories[r];
    	} else if(r < visibleTasks.size()+numC) {
    		t = (Task) visibleTasks.get(r-numC);
    	}
    	return t;
    }
    
    public Object getValueAt(int row, int col) { 
        if(visibleTasks == null || visibleTasks.size() == 0) {
            return "";
        }
        EffortCollection t = getCollectionOnRow(row);
             
        if(col == NAME_COL) {
        	if(t == null) {
        		return "Total All";
            } else {
            	String tsp = t.getTargetSpendingLevelString();
            	if(tsp.length() == 0) {
            		return t.getName();
            	} else {
            		return t.getName() + "          "+tsp+"%";
            	}
            }
        } 
        if(col == TODAY_COL) {    
            if(t != null) {
                return getTodayTimeString(t);
            } 
            return Task.msTimeToString(ttoday, false);   
        }
        if(col == SESSION_COL) {
            if(t != null && t.getTimeToday() > 0) {            	
                return t.getSessionTimeAsString();
            }
            return "";
        }
        if(col == WEEK_COL) {
            if(t != null) {
                return getWeekTimeString(t);
            }
            return Task.msTimeToString(tweek, false);
        }
        if(col == MONTH_COL) {
            if(t != null) {
                return getMonthTimeString(t);
            } 
            return Task.msTimeToString(tmonth, false);
        }
        if(col == YEAR_COL) {
            if(t != null) {
                return getYearTimeString(t);
            } 
            return Task.msTimeToString(tyear, false);
        }
        return "";
    }
    
    private String getTodayTimeString(EffortCollection t) {
        long total = ttoday;        
        return t.getTimeTodayAsString(false)+"   "
                +  getPercentageString(t.getTimeToday(), total);
    }
    private String getWeekTimeString(EffortCollection t) {
        long total = tweek;
        return t.getWeekTimeAsString()+"   "
                + getPercentageString(t.getWeekTime(), total);
    }
    private String getMonthTimeString(EffortCollection t) {
        long total = tmonth;
       return t.getMonthTimeAsString()+"   "
                +  getPercentageString(t.getMonthTime(), total);
    }
    private String getYearTimeString(EffortCollection t) {
       long total = tyear;
       return t.getYearTimeAsString()+"   "
                +  getPercentageString(t.getYearTime(), total);
    }
    private String getPercentageString(long share, long total) {
        if(share == 0) return "";
        if(total == 0) return "__%";
        int per = (int) Math.round((double) share  / (double) total * 100);
        if(per > 0) {
            return ""+per+"%";
        }
        return "";
    }
    
    public Color highlightTodayTargetSpendingLevel(int row) {
    	EffortCollection t = getCollectionOnRow(row);
    	return this.highlightTargetSpendingLevel(t, (double) t.getTimeToday(), (double) ttoday);
    }
    public Color highlightWeeklyTargetSpendingLevel(int row) {
    	EffortCollection t = getCollectionOnRow(row);
    	return this.highlightTargetSpendingLevel(t, (double) t.getWeekTime(), (double) tweek);
    }
    public Color highlightMonthlyTargetSpendingLevel(int row) {
    	EffortCollection t = getCollectionOnRow(row);
    	return this.highlightTargetSpendingLevel(t, (double) t.getMonthTime(), (double) tmonth);
    }
    private Color highlightTargetSpendingLevel(EffortCollection t, double tasktime, double totaltime) {
    	int tsl = t.getTargetSpendingLevel();
    	if(tsl == 0 || totaltime == 0) return NO_HIGHLIGHT_C;
    	int current = (int) (( tasktime / totaltime ) * 100);
    	//int delta = current - tsl;
    	if(t.getTargetSpendingLevelType() == TaskData.AT_LEAST) {
    		if(current >= tsl) return GREEN_HIGHLIGHT_C;
    		else if(current >= LOW_FACTOR * tsl) return NO_HIGHLIGHT_C;
    		else if(current >= VERY_LOW_FACTOR * tsl) return ORANGE_HIGHLIGHT_C;
    		else return RED_HIGHLIGHT_C;
    	}
    	if(t.getTargetSpendingLevelType() == TaskData.AT_MOST) {
    		if(current >= VERY_HIGH_FACTOR * tsl) return RED_HIGHLIGHT_C;
    		else if(current >= HIGH_FACTOR * tsl) return ORANGE_HIGHLIGHT_C;
    		else if(current >= tsl) return NO_HIGHLIGHT_C;
    		else return GREEN_HIGHLIGHT_C;
    	}
    	if(t.getTargetSpendingLevelType() == TaskData.EXACT) {
    		if(current >= VERY_HIGH_FACTOR * tsl) return RED_HIGHLIGHT_C;
    		else if(current >= HIGH_FACTOR * tsl) return ORANGE_HIGHLIGHT_C;
    		else if(current >= LOW_FACTOR * tsl) return GREEN_HIGHLIGHT_C;
    		else if(current >= VERY_LOW_FACTOR * tsl) return ORANGE_HIGHLIGHT_C;
    		else return RED_HIGHLIGHT_C;
    	}
    	return NO_HIGHLIGHT_C;
    }
    
    public String getColumnName(int col) {
        if(col == NAME_COL) return "Name ";
        if(col == TODAY_COL) return "Today";
        if(col == SESSION_COL) return "Session";
        if(col == WEEK_COL) return "Week";
        if(col == MONTH_COL) return "Month";
        if(col == YEAR_COL) return "Aca. Year";
        return "null";
    }
    public Class getColumnClass(int c) {        
        return String.class;     
    }
    public boolean isCellEditable(int rowIndex, int columnIndex) {        
        return false;
    }
    
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        
    }
    public void updateTimes(Task task) {                     
        Task t = null;
        ttoday = 0;
        tweek = 0;
        tmonth = 0;
        tyear = 0;
        if(tasks == null || categories == null) {
        	return;
        }
        if(task != null) {
        	selRow = visibleTasks.indexOf(task)+categories.length;
        } 
        Iterator it = tasks.iterator();
        while(it.hasNext()) {
            t = (Task) it.next();
            ttoday += t.getTimeToday();
            tweek += t.getWeekTime();
            tmonth += t.getMonthTime();
            tyear += t.getYearTime();
        }
        Calendar c = Calendar.getInstance();  
        c.setTime(new Date());
        int h = c.get(Calendar.HOUR_OF_DAY);
        int m = c.get(Calendar.MINUTE);
        long tToEndOfDay = 0;
		if(h < 16) {
			h = 15 - h;
			m = 60 - m; 
			tToEndOfDay = (60*h + m)*60*1000;
		}
		twEndOfDay = tweek + tToEndOfDay;
		tmEndOfDay = tmonth + tToEndOfDay;		
    }
    
    public void showHidden(boolean show) {
    	showHiddenTasks = show;
    }
    public void updateContent(Collection taskCollection, Collection categorySet) {   
        tasks = new ArrayList(taskCollection);
        visibleTasks = new ArrayList(tasks.size());
        ListIterator it = tasks.listIterator();
        Task t = null;
        while(it.hasNext()) {
            t = (Task) it.next();
            if(!t.isDead() && (showHiddenTasks || !t.isHidden())) {
            	visibleTasks.add(t);
            }
        }
        categories = categorySet.toArray();
        updateTimes(null);
        fireTableRowsInserted(0, visibleTasks.size());
    }
    public int getNumberOfRows() {
        if(visibleTasks != null) return visibleTasks.size()+categories.length;
        return 0;
    }
    public int getNumberOfCategoryRows() {
        return categories.length;
    }
    public int getSelectedRow() {
    	return selRow;
    }
    
}
