/*
 * RecordAction.java
 *
 * Created on 6. helmikuuta 2003, 20:51
 */

package checkmate.gui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JToggleButton;

import checkmate.recorder.Recorder;


/**
 *
 * @author  jitkonen
 */
public class RecordAction extends AbstractAction {
    private static final long serialVersionUID = 1L;
	Recorder rec = null;
    /** Creates a new instance of RecordAction */
    public RecordAction(Recorder r) {
        rec = r;
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        if(rec != null) {
            Object source = actionEvent.getSource();
            if(source instanceof JToggleButton) {
                if(((JToggleButton) source).isSelected()) {
                    rec.startTask();
                } else {
                    rec.stopTask();
                }
            }
        }
    }
    
}
