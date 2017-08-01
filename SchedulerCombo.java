//*****************************************************************************************************************************
// SchedulerCombo.java - June 22 2017
// Nick Simmons
// Combination drop box for JPanel
//*****************************************************************************************************************************

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.event.*;

// class to create combination box for algorithm selection
@SuppressWarnings("serial")
public class SchedulerCombo extends JPanel
{
	// create objects for gui
	private JButton processButton; // push bottom to execute sort
	private JButton addWorkerButton;
	private JButton addJobsButton;
	private JCheckBox lateBox;
	private JTextField workerFile;
	private JTextField jobFile;
	private JRadioButton sjf, ljf, edd, fdd;
	private JSlider crSlider;

	// create and define fields and buttons for gui
	public SchedulerCombo()
	{			
		// create, configure text fields
		workerFile = new JTextField(30);
		workerFile.setBackground(Color.white);
		workerFile.setText("Worker File:");
		jobFile = new JTextField(30);
		jobFile.setBackground(Color.white);
		jobFile.setText("Job File:");

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
		
		// check box
		lateBox = new JCheckBox ("Use safety buffer to reschedule");
		lateBox.setBackground (Color.lightGray);
		
		// slider for critical ratio
		crSlider = new JSlider (JSlider.HORIZONTAL, 1, 5, 2);
		crSlider.setMajorTickSpacing (1);
		crSlider.setMinorTickSpacing (0);
		crSlider.setPaintTicks (true);
		crSlider.setPaintLabels (true);
		crSlider.setAlignmentX (Component.LEFT_ALIGNMENT);
		crSlider.setBackground(Color.lightGray);
		
		// slider listener
		SliderListener sliderListener = new SliderListener();
		crSlider.addChangeListener (sliderListener);
		
		// radio listeners
		AlgoListener listener = new AlgoListener();
		sjf.addActionListener (listener);
		ljf.addActionListener (listener);
		edd.addActionListener (listener);
		fdd.addActionListener (listener);

		// check box listener
		LateListener boxListener = new LateListener();
		lateBox.addItemListener (boxListener);
		
		// setup panel and even listeners
		setPreferredSize (new Dimension (350, 225));
		setBackground (Color.lightGray);

		// add to gui
		add(addWorkerButton);
		add(addJobsButton);
		add(processButton);
		add(sjf);
		add(ljf);
		add(edd);
		add(fdd);
		add(lateBox);
		add(crSlider);
		add(workerFile);
		add(jobFile);

		// bind listers to gui objects
		addWorkerButton.addActionListener(new AddWorkerButtonListener());
		addJobsButton.addActionListener(new AddJobsButtonListener());
		processButton.addActionListener(new ButtonListener());
	}
	
	// create slider listener for safety buffer slider
	private class SliderListener implements ChangeListener
	{
		public void stateChanged (ChangeEvent event)
		{
			FCFS.criticalSafetyFactory = (double)crSlider.getValue();
		}
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
				workerFile.setText ("No Worker File Chosen");
			}
			
			// display worker file in gui
			else 
			{
				FCFS.workerFile = file.getName();
				workerFile.setText("Worker File:" + FCFS.workerFile);
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
				jobFile.setText ("No Jobs File Chosen");
			}
			
			// display job file in gui
			else 
			{
				FCFS.jobFile = file.getName();
				jobFile.setText("Job File:" + FCFS.jobFile);
			}
		}
	}
	
	// radio button control
	private class AlgoListener implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			Object source = event.getSource();
			
			// set algo to shortest job first
			if (source == sjf)
			{
				FCFS.algorithm = 1;
			}
		
			// set algo to longest job first
			else if (source == ljf)
			{
				FCFS.algorithm = 2;
			}
			
			// set algo to earliest due date
			else if (source == edd)
			{
				FCFS.algorithm = 3;
			}
			
			// set algo to farthest due date
			else if (source == fdd)
			{
				FCFS.algorithm = 4;
			}
		}
	}
	
	// listener for late avoidance check box
	private class LateListener implements ItemListener
	{
		public void itemStateChanged (ItemEvent event)
		{
			if (lateBox.isSelected())
			{
				FCFS.lateAvoidance = true;
			}
			
			else
			{
				FCFS.lateAvoidance = false;
			}
		}
	}
}