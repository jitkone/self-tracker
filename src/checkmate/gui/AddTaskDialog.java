/*
 * AddTaskDialog.java
 *
 * Created on 26. helmikuuta 2003, 18:07
 */

package checkmate.gui;

import java.util.*;

import javax.swing.JOptionPane;

import checkmate.model.*;
import checkmate.recorder.Recorder;


/*
 * @author  jitkonen
 */
public class AddTaskDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	private Recorder recorder = null;
    private Task task = null;
    
    private javax.swing.JTextField nameField;
    private javax.swing.JCheckBox idleBox;
    private javax.swing.JCheckBox hideBox;
    private javax.swing.JTextField spendingLevelField;
	private javax.swing.JTextField sortKeyField;
	private javax.swing.JComboBox categoryField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel5;
	private javax.swing.JPanel sortPanel;
	private javax.swing.JLabel sortLabel;
	private javax.swing.JPanel categoryPanel;
	private javax.swing.JLabel categoryLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel1;
    
    /** Creates new form AddTaskDialog */
    public AddTaskDialog(java.awt.Frame parent, Recorder rec) {
        super(parent, true);
        initComponents();
        recorder = rec;
    }
    public void init(Task t) {
        task = t;
        nameField.setText(t.getName());
        spendingLevelField.setText(t.getTargetSpendingLevelString());
        sortKeyField.setText(t.getSortKey());
               
        categoryField.setSelectedItem(t.getCategory());
        
        idleBox.setSelected(t.isIdleTask());
        hideBox.setSelected(t.isHidden());
        
    }
    
    private void initComponents() {//GEN-BEGIN:initComponents
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jPanel5 = new javax.swing.JPanel();
		sortPanel = new javax.swing.JPanel();
		sortLabel = new javax.swing.JLabel();
		categoryPanel = new javax.swing.JPanel();
		categoryLabel = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        spendingLevelField = new javax.swing.JTextField();
        spendingLevelField.setEditable(true);
        sortKeyField = new javax.swing.JTextField();
		categoryField = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        idleBox = new javax.swing.JCheckBox();
        hideBox = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        
        categoryField.setEditable(true);
        getContentPane().setLayout(new java.awt.BorderLayout(0, 5));

        setName("taskDialog");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel1.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.Y_AXIS));

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.X_AXIS));

        jLabel1.setText("Task name");
        jLabel1.setPreferredSize(new java.awt.Dimension(75, 17));
        jLabel1.setMinimumSize(new java.awt.Dimension(75, 17));
        jLabel1.setMaximumSize(new java.awt.Dimension(75, 17));
        jPanel4.add(jLabel1);

        nameField.setText("Task1");
        nameField.setPreferredSize(new java.awt.Dimension(80, 21));
        nameField.setMargin(new java.awt.Insets(0, 0, 0, 5));
        nameField.setMinimumSize(new java.awt.Dimension(50, 21));
        jPanel4.add(nameField);

        jPanel2.add(jPanel4);

        jPanel5.setLayout(new javax.swing.BoxLayout(jPanel5, javax.swing.BoxLayout.X_AXIS));
		sortPanel.setLayout(new javax.swing.BoxLayout(sortPanel, javax.swing.BoxLayout.X_AXIS));
		categoryPanel.setLayout(new javax.swing.BoxLayout(categoryPanel, javax.swing.BoxLayout.X_AXIS));
		
        jLabel2.setText("Target spending level");
        jLabel2.setPreferredSize(new java.awt.Dimension(75, 17));
        jLabel2.setMinimumSize(new java.awt.Dimension(75, 17));
        jLabel2.setMaximumSize(new java.awt.Dimension(75, 17));
		
		sortLabel.setText("SortID");
		sortLabel.setPreferredSize(new java.awt.Dimension(75, 17));
		sortLabel.setMinimumSize(new java.awt.Dimension(75, 17));
		sortLabel.setMaximumSize(new java.awt.Dimension(75, 17));
		
		categoryLabel.setText("Category");
		categoryLabel.setPreferredSize(new java.awt.Dimension(75, 17));
		categoryLabel.setMinimumSize(new java.awt.Dimension(75, 17));
		categoryLabel.setMaximumSize(new java.awt.Dimension(75, 17));
		
        jPanel5.add(jLabel2);
		spendingLevelField.setText("-");
        sortKeyField.setText("9999");
        jPanel5.add(spendingLevelField);
		sortPanel.add(sortLabel);
		sortPanel.add(sortKeyField);
		categoryPanel.add(categoryLabel);
		categoryPanel.add(categoryField);
        jPanel2.add(jPanel5);
		jPanel2.add(sortPanel);
		jPanel2.add(categoryPanel);
        jPanel1.add(jPanel2, java.awt.BorderLayout.NORTH);

        idleBox.setText("Idle task");
        jPanel3.add(idleBox);
        
        hideBox.setText("Hide");
        jPanel3.add(hideBox);

        jButton1.setText("OK");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jPanel3.add(jButton1);

        jPanel1.add(jPanel3, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);
        
        Iterator e = Category.getCategories().iterator();
        while(e.hasNext()) {
        	Object o = e.next();
        	categoryField.addItem(o);
        }
        
        pack();
    }//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        if(task == null) {
            try {
        	TaskData d = new TaskData(nameField.getText());
            d.generateTaskID();
            d.setCategoryName(categoryField.getSelectedItem().toString());
            Task t = new Task(d);
            t.setSortKey(sortKeyField.getText());
            try {
            	d.setTargetSpendingLevel(spendingLevelField.getText());        	
            } catch (Exception e) {}
            if(idleBox.isSelected()) {
                Task.setIdleTask(t);
            }
            if(hideBox.isSelected()) {
                t.setHidden(true);
            } else {
            	t.setHidden(false);
            }
            recorder.addTask(t);
            } catch (Exception e) {
            	System.out.println(e.getMessage());
            	e.printStackTrace();
            }
        } else { 
            TaskData d = task.getData();
            d.setTaskName(nameField.getText());
			task.setSortKey(sortKeyField.getText());
			Object o = categoryField.getSelectedItem();
			if(o instanceof String) {
				task.setCategory(Category.get((String) o)); 
			} else {
				task.setCategory((Category) o); 
			}			
			try {
            	d.setTargetSpendingLevel(spendingLevelField.getText());        	
            } catch (Exception e) {
            	JOptionPane.showMessageDialog(null, e.getStackTrace(), "Error: ", JOptionPane.ERROR_MESSAGE);
            }
            if(idleBox.isSelected()) {
                Task.setIdleTask(task);
            }
            if(hideBox.isSelected()) {
                task.setHidden(true);
            } else {
            	task.setHidden(false);
            }
            recorder.updateTask(task);
        }
        closeDialog(null);
    }
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
}
