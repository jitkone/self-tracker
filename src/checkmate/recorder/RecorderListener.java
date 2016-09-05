/*
 * RecorderListener.java
 *
 * Created on 6. helmikuuta 2003, 21:35
 */

package checkmate.recorder;

import checkmate.model.Task;
/**
 *
 * @author  jitkonen
 */
public interface RecorderListener {
    void activeTaskChanged(Task oldTask, Task activeTask);
    void taskAdded(Task newTask);
    void taskDeleted(Task newTask);
}
