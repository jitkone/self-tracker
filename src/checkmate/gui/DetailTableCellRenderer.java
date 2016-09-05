/*
 * DetailTableCellRenderer.java
 *
 * Created on 9. kesäkuuta 2003, 18:46
 */

package checkmate.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JTable;

/**
 *
 * @author  jitkonen
 */
public class DetailTableCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	DetailTableModel model;
    
    /** Creates a new instance of DetailTableCellRenderer */
    public DetailTableCellRenderer(DetailTableModel mod) {
        super();
        model = mod;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                   boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(row == model.getSelectedRow()) {
			c.setBackground(DetailTableModel.SELECTED_HIGHLIGHT_C);        
			} else {
			c.setBackground(Color.white);
		}
        Font f = c.getFont();
        f = new Font(f.getFontName(), Font.PLAIN, 13);
        if(row == model.getNumberOfRows() && column == DetailTableModel.WEEK_COL) {            
            c.setForeground(model.getWeekColor());            
            c.setFont(new Font(f.getFontName(), Font.BOLD, f.getSize()));
        } else if(row == model.getNumberOfRows() && column == DetailTableModel.MONTH_COL) {            
            c.setForeground(model.getMonthColor());            
            c.setFont(new Font(f.getFontName(), Font.BOLD, f.getSize()));
        }  else if(row == model.getNumberOfRows() && column == DetailTableModel.TODAY_COL) {            
            c.setForeground(model.getDayColor());            
            c.setFont(new Font(f.getFontName(), Font.BOLD, f.getSize()));
        } else if(row == model.getNumberOfRows()) {
            c.setForeground(DetailTableModel.NO_HIGHLIGHT_C);
            c.setFont(new Font(f.getFontName(), Font.BOLD, f.getSize()));
        } else if(row < model.getNumberOfCategoryRows()) {
            c.setForeground(DetailTableModel.NO_HIGHLIGHT_C);
            c.setFont(new Font("Arial", Font.BOLD, f.getSize()));
        } else {
        	Color highlight = Color.black;
        	if(column == DetailTableModel.TODAY_COL) {
        		highlight = model.highlightTodayTargetSpendingLevel(row);
        	} else if(column == DetailTableModel.WEEK_COL) {
        		highlight = model.highlightWeeklyTargetSpendingLevel(row);
        	} else if(column == DetailTableModel.MONTH_COL) {
        		highlight = model.highlightMonthlyTargetSpendingLevel(row);
        	}
        	/*if( highlight == DetailTableModel.NO_HIGHLIGHT) {
        		c.setForeground(DetailTableModel.NO_HIGHLIGHT_C);
        	} else if(highlight == DetailTableModel.HIGH_HIGHLIGHT) {
        		c.setForeground(DetailTableModel.GREEN_HIGHLIGHT_C);
        	} else if(highlight == DetailTableModel.LOW_HIGHLIGHT) {
        		c.setForeground(DetailTableModel.RED_HIGHLIGHT_C);
        	}*/
        	c.setForeground(highlight);
            c.setFont(new Font("Serif", Font.PLAIN, f.getSize()-1));
        }
        
        return c;
    }
}
