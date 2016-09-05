package checkmate.gui;

import java.util.Iterator;

import javax.swing.*;
import javax.swing.table.*;

import checkmate.model.*;
import checkmate.recorder.*;


/**
 *
 * @author  jitkonen
 */
public class TSLTracker extends javax.swing.JFrame implements TaskListener, RecorderListener {
	private static final long serialVersionUID = 1L;
	
	private static final String application_title = "TSL Tracker";
	private static final String version = "2.3.1.1";
    
    private Recorder recorder = null;
    private JMenuItem addTaskMenuItem = null;
    private JMenuItem editTaskMenuItem = null;
    private JMenuItem deleteTaskMenuItem = null;
    private JMenuItem showEntriesMenuItem = null;
    private DetailTableModel detailModel = null;
    private long lastDetailUpdate = 0;
    private EntryDialog entries = null;
    private static final int FRAME_WIDTH = 380;
    private static final int FRAME_HEIGHT = 100; 
    /** Creates new form CheckFrame */
    public TSLTracker(String dataFilePath, int debug_time_factor) {     
        this.setIconImage(new ImageIcon("./tslt.gif").getImage());
        initComponents();
        initRecorder(dataFilePath, debug_time_factor);
        addTaskMenuItem = new JMenuItem("Add task...");
        addTaskMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddTaskDialog atd = new AddTaskDialog(TSLTracker.this, recorder);
                atd.setVisible(true);
            }
        });
        editTaskMenuItem = new JMenuItem("Edit current task...");
        editTaskMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AddTaskDialog atd = new AddTaskDialog(TSLTracker.this, recorder);
                atd.init(recorder.getActiveTask());
                atd.setVisible(true);
            }
        });
        deleteTaskMenuItem = new JMenuItem("Delete current task");
        deleteTaskMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if(JOptionPane.showConfirmDialog(TSLTracker.this, 
                        "Are you sure you want to permanently delete task "+recorder.getActiveTask().getName()+"?", 
                        "Confirmation: Delete current task", 
                        JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                    recorder.deleteActiveTask();
                }
            }
        });
        showEntriesMenuItem = new JMenuItem("Show entries...");
        showEntriesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showEntryDialog();
            }
        });
    }

    public static void main(String args[]) {
    	String dataFilePath = null;
    	int debug_time_factor = 0;
    	int i = 0;
    	while(i < args.length) {
    		if(args[i].equals("-t")) {
    			if(args.length > i) {
    				i++;
    				debug_time_factor = Integer.parseInt(args[i]);
    			}
    			i++;
    		} /*else if(args[i].equals("-d")) {
    			if(args.length > i) {
    				i++;
    				debug_date = args[i];
    			}
    			i++;
    		} */else {
    			dataFilePath = args[i];
    			i++;
    		}   	
    	}
        new TSLTracker(dataFilePath, debug_time_factor).setVisible(true);
    }
    
    private void initComponents() {
        detailPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        detailTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        taskCombo = new javax.swing.JComboBox();
        timeField = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        recordButton = new javax.swing.JToggleButton();
        pauseButton = new javax.swing.JToggleButton();
        manualButton = new javax.swing.JButton();
        detailButton = new javax.swing.JToggleButton();
        showHiddenBox = new JCheckBox();
        mainMenu = new javax.swing.JMenuBar();
        taskMenu = new javax.swing.JMenu();
                
        setTitle(application_title);
        //setBackground(java.awt.Color.black);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        detailPanel.setLayout(new java.awt.BorderLayout());

        detailTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(detailTable);

        detailPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        getContentPane().add(detailPanel, java.awt.BorderLayout.CENTER);

        jPanel3.setLayout(new java.awt.GridLayout(2, 1));

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.X_AXIS));

        //jPanel2.setPreferredSize(new java.awt.Dimension(350, 26));
        //jPanel2.setMinimumSize(new java.awt.Dimension(350, 26));
        taskCombo.setMaximumRowCount(25);
        taskCombo.setForeground((java.awt.Color) javax.swing.UIManager.getDefaults().get("ComboBox.foreground"));
        taskCombo.setBackground((java.awt.Color) javax.swing.UIManager.getDefaults().get("ComboBox.listBackground"));
        taskCombo.setMinimumSize(new java.awt.Dimension(100, 26));
        taskCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                taskComboActionPerformed(evt);
            }
        });

        jPanel2.add(taskCombo);

        timeField.setForeground(java.awt.Color.cyan);
        timeField.setFont(new java.awt.Font("Dialog", 1, 12));
        timeField.setText("  --:--:--");
        timeField.setBackground(java.awt.Color.black);
        timeField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        timeField.setPreferredSize(new java.awt.Dimension(280, 26));
        timeField.setMaximumSize(new java.awt.Dimension(300, 26));
        //timeField.setMinimumSize(new java.awt.Dimension(200, 26));
        jPanel2.add(timeField);

        jPanel3.add(jPanel2);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.X_AXIS));

        jPanel1.setPreferredSize(new java.awt.Dimension(300, 27));
        recordButton.setText("Rec");
        recordButton.setPreferredSize(new java.awt.Dimension(60, 27));
        recordButton.setMaximumSize(new java.awt.Dimension(60, 27));
        recordButton.setMinimumSize(new java.awt.Dimension(60, 27));
        recordButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                recordButtonStateChanged(evt);
            }
        });

        jPanel1.add(recordButton);

        pauseButton.setText("Idle");
        pauseButton.setPreferredSize(new java.awt.Dimension(60, 27));
        pauseButton.setMaximumSize(new java.awt.Dimension(60, 27));
        pauseButton.setMinimumSize(new java.awt.Dimension(60, 27));
        
        jPanel1.add(pauseButton);

        manualButton.setText("Man");
        manualButton.setPreferredSize(new java.awt.Dimension(60, 27));
        manualButton.setMaximumSize(new java.awt.Dimension(60, 27));
        manualButton.setMinimumSize(new java.awt.Dimension(60, 27));
        manualButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manualButtonActionPerformed(evt);
            }
        });
        jPanel1.add(manualButton);

        detailButton.setText("D");
        detailButton.setPreferredSize(new java.awt.Dimension(35, 27));
        detailButton.setMaximumSize(new java.awt.Dimension(45, 27));
        detailButton.setMinimumSize(new java.awt.Dimension(35, 27));
        detailButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailButtonActionPerformed(evt);
            }
        });
        jPanel1.add(detailButton);
        
        showHiddenBox.setSelected(false);
        showHiddenBox.setText("Hidden");
        
        showHiddenBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	updateTaskComboTasks();
            	setDetailsVisible(detailButton.isSelected(), showHiddenBox.isSelected());
            	updateDetails();
            	if(detailButton.isSelected()) {
            		setDetailsVisible(detailButton.isSelected(), showHiddenBox.isSelected());
            	}
            }
        });
        jPanel1.add(showHiddenBox);
        
        jPanel3.add(jPanel1);

        getContentPane().add(jPanel3, java.awt.BorderLayout.NORTH);

        taskMenu.setText("Tasks");
        taskMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                taskMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        mainMenu.add(taskMenu);
        setJMenuBar(mainMenu);

        pack();
        /* code for generating a lot of entries 
        Thread t = new Thread() {
        	public void run() {
        		try {   				
    					Thread.sleep(3000);
    				
    			} catch (Exception ex) {}
        		while(true) {
        			try {
        				long s = (long) (Math.random()*2000);
        				if(recordButton.isSelected()) {
        					Thread.sleep(s);
        				}
        			} catch (Exception ex) {}
        			if(recordButton.isSelected() && pauseButton.isSelected()) {
        				pauseButton.doClick();
        			} else if(Math.random()>0.3) {
        				recordButton.doClick();
        			} else {
        				if(recordButton.isSelected()) {
        					pauseButton.doClick();
        				}
        			}
        		}
        	}
        };
        t.start();*/
    }

    private void detailButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailButtonActionPerformed
        boolean sel = detailButton.isSelected();
        setDetailsVisible(sel, showHiddenBox.isSelected());
        if(sel) {
            updateDetails();
            updateTimeView(null, true);
        }        
    }

    protected void showEntryDialog() {
        if(entries == null) {
        	entries = new EntryDialog(this, this.recorder);
        }
        entries.updateContent(recorder.getEntries());
        entries.invalidate();
        entries.setVisible(true);
    }
    
    
    private void taskMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_taskMenuMenuSelected
        taskMenu.removeAll();
        //taskMenu.add(addTaskMenuItem);
        taskMenu.add(editTaskMenuItem);
        taskMenu.add(deleteTaskMenuItem);
        
        Task at = recorder.getActiveTask();
        boolean onlyOneTask = (recorder.getTasks().size() == 1);
        deleteTaskMenuItem.setEnabled(!onlyOneTask && !(at == null || at.running()));
        
        /*Iterator it = recorder.getTasks().iterator();
        long totalTimeToday = 0;
        while(it.hasNext()) {
            final Task t = (Task) it.next();
            totalTimeToday += t.getTimeToday();
            JMenuItem item = new JMenuItem(t.toString()+" - "+t.getTimeText(true));
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openTaskDialog(t);
                }
            });
            taskMenu.add(item);
        }
        taskMenu.addSeparator();
        int tmin = (int) (totalTimeToday / (60 * 1000)) % 60;
        int thour = (int) totalTimeToday / (3600 * 1000);
        taskMenu.add(new JMenuItem("Total today: "+Task.intToTwoDigitString(thour)
                +":"+Task.intToTwoDigitString(tmin)));
        taskMenu.addSeparator();*/
        taskMenu.add(addTaskMenuItem);
        taskMenu.addSeparator();
        taskMenu.add(showEntriesMenuItem);
    }
    
    private void manualButtonActionPerformed(java.awt.event.ActionEvent evt) {
        TaskDialog td = new TaskDialog(TSLTracker.this, (Task) taskCombo.getSelectedItem(), recorder);
        td.setVisible(true);
    }

    private void taskComboActionPerformed(java.awt.event.ActionEvent evt) {
        Task sel = (Task) taskCombo.getSelectedItem();        
        if(sel != null) {
        	recorder.setActiveTask(sel);
        }
    }
    public void activeTaskChanged(Task oldTask, Task activeTask) {
        updateSelectedTask(oldTask, activeTask);
    }
    private void updateSelectedTask(Task oldTask, Task activeTask) {
        if(oldTask != null) {
            oldTask.removeTaskListener(this);        
        }
        //Task t = recorder.getActiveTask();
        if(activeTask != null) {
            activeTask.addTaskListener(this);
            updateTaskSelector(activeTask);
        
        	if(!activeTask.equals(Task.getIdleTask())) {
        		pauseButton.setEnabled(true);
        		pauseButton.setSelected(false);
        	} else if(!pauseButton.isSelected()) {
        		pauseButton.setEnabled(false);
        	}   
        	if(recordButton.isSelected()) {
        		this.setTitle(activeTask.getName());
        	}
        }
        updateTimeView(activeTask, true);
    }
    private void recordButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_recordButtonStateChanged
        if(recordButton.isSelected()) {
            this.setTitle(recorder.getActiveTask().getName());
            manualButton.setEnabled(false);
        } else {
            this.setTitle(application_title+" "+version);
            manualButton.setEnabled(true);
        }
    }//GEN-LAST:event_recordButtonStateChanged
    
    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {
        recorder.stopTask();
        try {
            recorder.saveTasks();
        } catch (java.io.IOException ioex) {
            JOptionPane.showMessageDialog(this, ioex.getMessage(), "Error: Saving task data failed", JOptionPane.ERROR_MESSAGE);
        }
        recorder.shutDown();
        System.exit(0);
    }
    
    private void initRecorder(String path, int time_factor) {        
        recorder = new Recorder(path, time_factor);
        
        try {
            recorder.loadTasks();
        } catch (Exception ioex) {
            JOptionPane.showMessageDialog(this, ioex.getMessage(), "Error: Loading task data failed", JOptionPane.ERROR_MESSAGE);
        }
        /*if(recorder.getTasks().isEmpty()) {
            Task t = new Task("Overhead");
            recorder.addTask(t);
            t = new Task("Research");
            recorder.addTask(t);
            t = new Task("Teaching");
            recorder.addTask(t);
            t = new Task("Studies");
            recorder.addTask(t);
        }*/
        
        updateTaskComboTasks();
        Task active = (Task) taskCombo.getSelectedItem();
        if(active != null) {
            active.addTaskListener(this);
            recorder.setActiveTask(active);
            updateTimeView(active, true);
        }      
        RecordAction recAct = new RecordAction(recorder);
        recordButton.addActionListener(recAct);
        
        IdleAction idlAct = new IdleAction(recorder);
        pauseButton.addActionListener(idlAct);
        recorder.addRecorderListener(this);       
         
        this.setTitle(application_title+" "+version);
        detailModel = new DetailTableModel(detailTable);
        detailTable.setModel(detailModel);  
        detailTable.setDefaultRenderer(String.class, new DetailTableCellRenderer(detailModel));
        TableColumn col = detailTable.getColumnModel().getColumn(0);
    	col.setPreferredWidth(150);
    	//col.setMinWidth(100);
        for(int i = 1; i< detailTable.getColumnModel().getColumnCount(); i++) { 
        	col = detailTable.getColumnModel().getColumn(i);
        	col.setPreferredWidth(85);
        	//col.setMinWidth(80);
        }
        detailTable.setRowSelectionAllowed(false);
        detailTable.setCellSelectionEnabled(false);
        detailTable.getTableHeader().setReorderingAllowed(true);
        updateDetails();
        setDetailsVisible(false, false);        
    }

	private void updateTaskComboTasks() {
		Iterator tasks = recorder.getTasks().iterator();
        Object selected = taskCombo.getSelectedItem();
		taskCombo.removeAllItems();
        while(tasks.hasNext()) {
        	Task ts = (Task) tasks.next();
        	if(!ts.isDead() && (this.showHiddenBox.isSelected() || !ts.isHidden())) {
        		taskCombo.addItem(ts);
        	}
        }
        if(selected != null) {
        	taskCombo.setSelectedItem(selected);
        }
	}
    private void updateTaskSelector(Task active) {
        if(!active.equals(taskCombo.getSelectedItem())) {
            taskCombo.setSelectedItem(active);            
        } 
    }
    public void taskUpdated(int what, Task aTask) {
        updateTimeView(aTask, false);
    }    
    public void updateTimeView(final Task aTask, boolean force) {
        Task t = null;
        long tt = 0;        
        long ovh = 0;
        Iterator it = recorder.getTasks().iterator();
        while(it.hasNext()) {
            t = (Task) it.next();
            tt += t.getTimeToday();
            if(t.isIdleTask()) {
                ovh = t.getTimeToday();
            }
        }            
        if(ovh > 0) {
            double d = 1 - ((double)ovh / (double) tt);
            d = d * 100;
            ovh = (long) d;
        } else {
            ovh = 100;
        }
        final long ttoday = tt;
        final long overhead = ovh;
		long now = new java.util.Date().getTime();
		final boolean updateDetails = force || (now - lastDetailUpdate > 30000);
		if(updateDetails) {
			lastDetailUpdate = now;	
		}
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(aTask != null) {
	            	timeField.setText(aTask.getTimeText(false)+" ("+Task.msTimeToString(ttoday,false)+") "+overhead+"%");
                }
	            if(updateDetails && detailPanel.isVisible() && detailModel != null) {
	                detailModel.updateTimes(aTask);
	                detailPanel.repaint();
	            }                
            }
        });
    }
    private void updateDetails() {        
        detailModel.updateContent(recorder.getTasks(), Category.getCategories());
    }
    
    public void taskAdded(Task newTask) {
    	updateTaskComboTasks();//taskCombo.addItem(newTask);
        updateDetails();
    }
    public void taskDeleted(Task deletedTask) {
    	updateTaskComboTasks();//taskCombo.removeItem(deletedTask);
        updateDetails();
    }
    private void setDetailsVisible(boolean visible, boolean showHidden) {
        detailModel.showHidden(showHidden);
    	detailPanel.setVisible(visible);
        if(!visible) {
            this.setBounds(this.getBounds().x,this.getBounds().y,FRAME_WIDTH,FRAME_HEIGHT);
        } else {
            this.setBounds(this.getBounds().x,this.getBounds().y, (int) detailTable.getPreferredSize().getWidth(),
                                                                 (int) detailTable.getPreferredSize().getHeight() + FRAME_HEIGHT +20);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton detailButton;
    private javax.swing.JTextField timeField;
    private javax.swing.JMenu taskMenu;
    private javax.swing.JMenuBar mainMenu;
    private javax.swing.JTable detailTable;
    private javax.swing.JComboBox taskCombo;
    private javax.swing.JToggleButton recordButton;
    private javax.swing.JPanel detailPanel;
    private javax.swing.JToggleButton pauseButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton manualButton;
    private javax.swing.JCheckBox showHiddenBox;
    
}
