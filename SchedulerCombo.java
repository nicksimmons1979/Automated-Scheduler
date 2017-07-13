//*****************************************************************************************************************************
// SchedulerCombo.java - June 22 2017
// Nick Simmons
// Combination drop box for JPanel
//*****************************************************************************************************************************

import java.awt.*;
import java.awt.event.*;
import java.io.File;

import javax.swing.*;

// class to create combination box for algorithm selection
@SuppressWarnings("serial")
public class SchedulerCombo extends JPanel
{
	// create objects for gui
	private JButton processButton; // push bottom to execute sort
	private JButton addWorkerButton;
	private JButton addJobsButton;;
	JFrame frameWorkers = new JFrame ("Input Files");
	JTextArea taWorkers = new JTextArea (20, 30);
	private JRadioButton sjf, ljf, edd, fdd;
	private String info = "";

	// create and define drop down combo box, fields and buttons for gui

	public SchedulerCombo()
	{			
		// create, configure button
		processButton = new JButton ("Process");
		processButton.setBackground (Color.white);
		addWorkerButton = new JButton ("Open Worker File");
		addWorkerButton.setBackground (Color.white);
		addJobsButton = new JButton ("Open Job File");
		addJobsButton.setBackground (Color.white);
		
		// radio buttons
		sjf = new JRadioButton ("Shortest Job First", true);
		sjf.setBackground(Color.lightGray);
		ljf = new JRadioButton ("Longest Job First", true);
		ljf.setBackground(Color.lightGray);
		edd = new JRadioButton ("Earliest Due Date", true);
		edd.setBackground(Color.lightGray);
		fdd = new JRadioButton ("Farthest Due Date", true);
		fdd.setBackground(Color.lightGray);
		ButtonGroup group = new ButtonGroup();
		group.add(sjf);
		group.add(ljf);
		group.add(edd);
		group.add(fdd);
		
		// radio listeners
		QuoteListener listener = new QuoteListener();
		sjf.addActionListener (listener);
		ljf.addActionListener (listener);
		edd.addActionListener (listener);
		fdd.addActionListener (listener);

		// setup panel and even listeners
		setPreferredSize (new Dimension (350, 100));
		setBackground (Color.lightGray);

		add(addWorkerButton);
		add(addJobsButton);
		add(processButton);
		add(sjf);
		add(ljf);
		add(edd);
		add(fdd);
		
		//add (processButton);

		addWorkerButton.addActionListener(new AddWorkerButtonListener());
		addJobsButton.addActionListener(new AddJobsButtonListener());
		processButton.addActionListener(new ButtonListener());
		
		//output window for adding workers
		frameWorkers.setDefaultCloseOperation (JFrame.HIDE_ON_CLOSE);
		frameWorkers.getContentPane().add(taWorkers);
		frameWorkers.pack();
		frameWorkers.setVisible(true);	
	}
	
	// represents the action listener for process button
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			if ((FCFS.workerFile != null) && (FCFS.jobFile != null))
			{
				FCFS.wait = false;
			}
		}
	}	
	
	// represents the action listener for add worker button
	private class AddWorkerButtonListener implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			// open file
			JFileChooser chooser = new JFileChooser();
			int status = chooser.showOpenDialog (null);
			File file = chooser.getSelectedFile();

			// error?
			if (status != JFileChooser.APPROVE_OPTION)
			{
				taWorkers.setText ("No Worker File Chosen");
			}
			
			else 
			{
				FCFS.workerFile = file.getName();
				info = "Worker File:" + FCFS.workerFile + "\nJob File:" + FCFS.jobFile;
				taWorkers.setText (info);
			}
		}
	}
	
	// represents the action listener for add jobs button
	private class AddJobsButtonListener implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			// open file
			JFileChooser chooser = new JFileChooser();
			int status = chooser.showOpenDialog (null);
			File file = chooser.getSelectedFile();

			// error?
			if (status != JFileChooser.APPROVE_OPTION)
			{
				taWorkers.setText ("No Jobs File Chosen");
			}
			
			else 
			{
				FCFS.jobFile = file.getName();
				info = "Worker File:" + FCFS.workerFile + "\nJob File:" + FCFS.jobFile;
				taWorkers.setText (info);
			}
		}
	}
	
	// radio button control
	private class QuoteListener implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			Object source = event.getSource();
			
			// do nothing, flag already set in FCFS
			if (source == sjf)
			{
				FCFS.algorithm = 1;
			}
		
			// flip flag in FCFS
			else if (source == ljf)
			{
				FCFS.algorithm = 2;
			}
			
			else if (source == edd)
			{
				FCFS.algorithm = 3;
			}
			
			else if (source == fdd)
			{
				FCFS.algorithm = 4;
			}
		}
	}
}