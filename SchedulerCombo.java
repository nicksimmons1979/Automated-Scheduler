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

	// create and define drop down combo box, fields and buttons for gui

	public SchedulerCombo()
	{			
		// create, configure text fields
		workerFile = new JTextField(30);
		workerFile.setBackground(Color.white);
		jobFile = new JTextField(30);
		jobFile.setBackground(Color.white);

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
		QuoteListener listener = new QuoteListener();
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

		addWorkerButton.addActionListener(new AddWorkerButtonListener());
		addJobsButton.addActionListener(new AddJobsButtonListener());
		processButton.addActionListener(new ButtonListener());
	}
	
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
			
			else 
			{
				FCFS.jobFile = file.getName();
				jobFile.setText("Job File:" + FCFS.jobFile);
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
	private class LateListener implements ItemListener
	{
		//-----------------------------------------------------------------
		// Updates the style of the label font style.
		//-----------------------------------------------------------------
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