package checkmate.gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 *
 * @author  jitkonen
 */
public class EntryTableCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = 1L;
	EntryTableModel model;
    
    /** Creates a new instance of DetailTableCellRenderer */
    public EntryTableCellRenderer(EntryTableModel mod) {
        super();
        model = mod;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                                                   boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		if(row == model.getSelectedRow()) {
			c.setBackground(new Color(150,150,255));        
		} else {
			c.setBackground(Color.white);
		}
        /*Font f = c.getFont();
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
            c.setForeground(Color.black);
            c.setFont(new Font(f.getFontName(), Font.BOLD, f.getSize()));
        } else {
            c.setForeground(Color.black);
        }*/
        
        return c;
    }
}
