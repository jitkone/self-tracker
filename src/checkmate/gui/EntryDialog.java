package checkmate.gui;

import java.util.*;
import javax.swing.*;

import checkmate.model.*;
import checkmate.recorder.*;


import java.awt.BorderLayout;
import java.awt.event.*;
public class EntryDialog extends javax.swing.JDialog {
	private static final long serialVersionUID = 1L;
	private List entryList = null;
    private Recorder recorder;
    private JPanel tablePanel = new JPanel();
    private JTable entryTable = new JTable();
    private JScrollPane entryScroll = new JScrollPane();
    private EntryTableModel tableModel;
    private JButton removeButton = new JButton();
    
    /** Creates new form TaskDialog */
    public EntryDialog(java.awt.Frame parent, Recorder rec) {
        super(parent, true);
        recorder = rec;
        initComponents();
    }
    
    protected void updateContent(List currentEntries) {
    	entryList = new LinkedList();
    	for(int i = currentEntries.size()-1; i >= 0 ; i--) {
    		entryList.add(currentEntries.get(i));
    	}
    	tableModel.updateContent(entryList);
    }
    private void initComponents() {
    	setTitle("TSLTracker Entry History");
    	tablePanel.setLayout(new java.awt.BorderLayout());
    	entryTable = new JTable();
    	entryScroll.setViewportView(entryTable);
    	tablePanel.add(entryScroll, java.awt.BorderLayout.CENTER);
        getContentPane().add(tablePanel, BorderLayout.CENTER);
        
        tableModel = new EntryTableModel(entryTable);
        entryTable.setModel(tableModel);  
        entryTable.setDefaultRenderer(String.class, new EntryTableCellRenderer(tableModel));
        entryTable.getColumnModel().getColumn(0).setPreferredWidth(50);    
        entryTable.getColumnModel().getColumn(1).setPreferredWidth(150);    
        entryTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        entryTable.getColumnModel().getColumn(3).setPreferredWidth(100);       
        entryTable.setRowSelectionAllowed(true);
        entryTable.setCellSelectionEnabled(false);
        entryTable.getTableHeader().setReorderingAllowed(false);
        
        removeButton.setText("Remove");
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                removeSelectedEntry();
            }
        });
        tablePanel.add(removeButton, BorderLayout.SOUTH);
        pack();
    }
    private void removeSelectedEntry() {
    	int i = entryTable.getSelectedRow();
    	if(i >= 0 && i < entryList.size()) {
    		Entry entry = (Entry) entryList.get(i);
    		recorder.removeEntry(entry);
    		this.updateContent(recorder.getEntries());
    	}
    }
    
}
