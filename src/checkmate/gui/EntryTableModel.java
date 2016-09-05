package checkmate.gui;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import checkmate.model.*;


/**
 *
 * @author  jitkonen
 */
public class EntryTableModel extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
	private JTable myTable;    
    private List entryList;
    private SimpleDateFormat tdf = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
    final static int COL_NUM = 4;
    final static int NUM_COL = 0;
    final static int NAME_COL = 2;
    final static int DATE_COL = 1;
    final static int LENGTH_COL = 3; 
    
    /*final static int WEEK_COL = 3;
    final static int MONTH_COL = 4;
    final static int TOTAL_COL = 5;
    final static float DAY_NORMAL = 7.25f;
    final static float WEEK_NORMAL = 36.25f;
    final static float MONTH_NORMAL = 152.25f;
    */
    /*long ttoday = 0;
    long tweek = 0;
    long tmonth = 0;
    long twEndOfDay = 0;
    long tmEndOfDay = 0;
    */
    
    /** Creates a new instance of DetailTableModel */
    public EntryTableModel(JTable table) {
        super();
        myTable = table;
        tdf.applyPattern("yyyy-MM-dd HH:mm:ss");
    }   
    /* protected Color getDayColor() {
        if(ttoday > 1.20f * DAY_NORMAL * 3600f * 1000f) return Color.red;
        if(ttoday > 1.10f * DAY_NORMAL * 3600f * 1000f) return Color.orange;
        if(ttoday < 0.90f * DAY_NORMAL * 3600f * 1000f) return Color.black;
        return Color.green;
    }
    protected Color getWeekColor() {
        if(twEndOfDay > 1.20f * WEEK_NORMAL * 3600f * 1000f) return Color.red;
        if(twEndOfDay > 1.10f * WEEK_NORMAL * 3600f * 1000f) return Color.orange;
        if(twEndOfDay < 0.90f * WEEK_NORMAL * 3600f * 1000f) return Color.black;
        return Color.green;
    }
    protected Color getMonthColor() {
        if(tmEndOfDay > 1.10f * MONTH_NORMAL * 3600f * 1000f) return Color.red;
        if(tmEndOfDay > 1.05f * MONTH_NORMAL * 3600f * 1000f) return Color.orange;
        if(tmEndOfDay < 0.95f * MONTH_NORMAL * 3600f * 1000f) return Color.black;
        return Color.green;
    }
    */
    public int getColumnCount() { return COL_NUM; }
    public int getRowCount() { 
        if(entryList != null) {
            return entryList.size();
        }
        return 0;
    }
    public Object getValueAt(int row, int col) { 
        if(entryList == null || entryList.size() == 0) {
            return "";
        }
        Entry e = null;
        
        if(row < entryList.size()) {
            e = (Entry) entryList.get(row);
        }       
        if(col == NAME_COL) {
            if(e != null) {
                return e.getTaskName();
            }            
        } else if(col == DATE_COL) {    
            if(e != null) {
                return tdf.format(e.getDate());
            }
        } else if(col == LENGTH_COL) {
            if(e != null) {
            	return Task.msTimeToString(e.getRecordedTime(),true);
            }
        } else if(col == NUM_COL) {
            return String.valueOf(row);
        }
        return "";        
    }
       
    public String getColumnName(int col) {
    	if(col == NUM_COL) return "Num";
        if(col == NAME_COL) return "Task Name";
        if(col == DATE_COL) return "Date";
        if(col == LENGTH_COL) return "Time";
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
    
    public void updateContent(Collection entries) {   
        entryList = new ArrayList(entries);   
        fireTableRowsInserted(0, entryList.size());
    }
    public int getNumberOfRows() {
        if(entryList != null) return entryList.size();
        return 0;
    }
    public int getSelectedRow() {
    	return myTable.getSelectedRow();
    }
}
