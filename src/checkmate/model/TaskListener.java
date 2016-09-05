/*
 * TaskListener.java
 *
 * Created on 6. helmikuuta 2003, 21:38
 */

package checkmate.model;

/**
 *
 * @author  jitkonen
 */
public interface TaskListener {
    void taskUpdated(int what, Task aTask);    
}
