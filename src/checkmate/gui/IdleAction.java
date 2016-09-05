/*
 * IdleAction.java
 *
 * Created on 24. helmikuuta 2003, 12:23
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
public class IdleAction extends AbstractAction {
	private static final long serialVersionUID = 1L;
	Recorder rec = null;
    /** Creates a new instance of IdleAction */
    public IdleAction(Recorder r) {
        rec = r;
    }
    
    public void actionPerformed(ActionEvent actionEvent) {
        if(rec != null) {
            Object source = actionEvent.getSource();
            if(source instanceof JToggleButton) {
                if(((JToggleButton) source).isSelected()) {
                    rec.setIdleTaskActive();
                } else {
                    rec.setNonIdleTaskActive();
                }
            }
        }
    }
    
}
