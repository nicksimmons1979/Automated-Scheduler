//*****************************************************************************************************************************
// SchedulerCombo.java - June 22 2017
// Nick Simmons
// Combination drop box for JPanel
//*****************************************************************************************************************************

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// class to create combination box for algorithm selection
@SuppressWarnings("serial")
public class SchedulerCombo extends JPanel
{
	// create objects for gui
	private JButton processButton; // push bottom to execute sort
	private JButton addWorkerButton;
	private JButton removeWorkerButton;
	private JTextField workerField; // field to store number of workers
	private JTextField workerName;
	private JTextField workerRank;
	private JLabel nameLabel; // label to display purpose of text field
	private JLabel rankLabel;
	JFrame frameWorkers = new JFrame ("Add Workers");
	JTextArea taWorkers = new JTextArea (20, 30);
	
	private JRadioButton sjf, ljf;

	// create and define drop down combo box, fields and buttons for gui

	public SchedulerCombo()
	{			
		// create, configure button
		processButton = new JButton ("Process");
		processButton.setBackground (Color.white);
		addWorkerButton = new JButton ("Add Worker");
		addWorkerButton.setBackground (Color.white);
		removeWorkerButton = new JButton ("Remove Worker");
		removeWorkerButton.setBackground (Color.white);
		
		// radio buttons
		sjf = new JRadioButton ("Shortest Job First", true);
		sjf.setBackground(Color.lightGray);
		ljf = new JRadioButton ("Longest Job First", true);
		ButtonGroup group = new ButtonGroup();
		ljf.setBackground(Color.lightGray);
		group.add (sjf);
		group.add (ljf);
		
		// radio listeners
		QuoteListener listener = new QuoteListener();
		sjf.addActionListener (listener);
		ljf.addActionListener (listener);
		
		
		// create, configure text field
		//workerField = new JTextField(2);		
		//label = new JLabel ("Workers");
		workerName = new JTextField(6);
		workerRank = new JTextField(2);
		nameLabel = new JLabel ("Worker Name");
		rankLabel = new JLabel ("Worker Rank"); 

		// setup panel and even listeners
		setPreferredSize (new Dimension (350, 100));
		setBackground (Color.lightGray);
		add (nameLabel);
		add (workerName);
		add (rankLabel);
		add (workerRank);
		add (addWorkerButton);
		add (removeWorkerButton);
		add (processButton);
		add (sjf);
		add (ljf);
		
		//add (processButton);
		//workerName.addActionListener(new NameListener());
		addWorkerButton.addActionListener(new AddButtonListener());
		removeWorkerButton.addActionListener(new RemoveButtonListener());
		processButton.addActionListener(new ButtonListener());
		
		//output window for adding workers
		frameWorkers.setDefaultCloseOperation (JFrame.HIDE_ON_CLOSE);
		frameWorkers.getContentPane().add(taWorkers);
		frameWorkers.pack();
		frameWorkers.setVisible(true);	

	}
		
	// create action listener for the text field, no action at moment
	private class NameListener implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			// make sure a selection was made
			if (FCFS.algorithm != 0)
			{
				// extract and store worker count in global variable
				String text = workerField.getText();
				FCFS.workerCount = Integer.parseInt (text);
				FCFS.wait = false;
			}

		}
	}
	
	// represents the action listener for process button
	private class ButtonListener implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			if (!FCFS.workerNames.isEmpty())
			{
				FCFS.wait = false;
			}
		}
	}	
	
	// represents the action listener for add worker button
	private class AddButtonListener implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			String info = "";
			// add worker name from gui
			String name;
			String rank;	
	
			if ((!workerName.getText().isEmpty()) && (!workerRank.getText().isEmpty()))
			{
				name = workerName.getText();
				rank = workerRank.getText();
				System.out.println("in loop");
				FCFS.workerNames.add(name);
				FCFS.workerRanks.add(Integer.parseInt(rank));
			}	

			// create output string
			if (!FCFS.workerNames.isEmpty())
			{
				for (int i = 0; i < FCFS.workerNames.size();i++)
				{
					info += "Worker:"+ FCFS.workerNames.get(i) + " Rank:" + FCFS.workerRanks.get(i) + "\n"; 
				}
			
			
				taWorkers.setText(info);
				// taWorkers.setText("added " + FCFS.workerNames.get(FCFS.workerNames.size()-1) + " rank " + FCFS.workerRanks.get(FCFS.workerNames.size()-1));
				System.out.println("added " + FCFS.workerNames.get(FCFS.workerNames.size()-1) + " rank " + FCFS.workerRanks.get(FCFS.workerNames.size()-1));
				for (int i = 0; i < FCFS.workerNames.size(); i++)
				{
					System.out.println("worker:" + FCFS.workerNames.get(i) + " rank:" + FCFS.workerRanks.get(i));
				}
			}
		}
	}
	
	// represents the action listener for add worker button
	private class RemoveButtonListener implements ActionListener
	{
		public void actionPerformed (ActionEvent event)
		{
			String info = "";

			if (!FCFS.workerNames.isEmpty())
			{
				String name = FCFS.workerNames.get(FCFS.workerNames.size()-1);
				FCFS.workerNames.remove(FCFS.workerNames.size()-1);
				
				Integer rank = FCFS.workerRanks.get(FCFS.workerRanks.size()-1);
				FCFS.workerRanks.remove(FCFS.workerRanks.size()-1);
				
				// create output string
				for (int i = 0; i < FCFS.workerNames.size();i++)
				{
					info += "Worker:"+ FCFS.workerNames.get(i) + " Rank:" + FCFS.workerRanks.get(i) + "\n"; 
				}
				
				taWorkers.setText(info);
	//			taWorkers.setText("Removed " + name);
				System.out.println("Removed " + name);
			}
			for (int i = 0; i < FCFS.workerNames.size(); i++)
			{
				System.out.println("worker:" + FCFS.workerNames.get(i) + " rank:" + FCFS.workerRanks.get(i));
			}
		}
	}
	
	private class QuoteListener implements ActionListener
	{
		//-----------------------------------------------------------------
		// Sets the text of the label depending on which radio
		// button was pressed.
		//-----------------------------------------------------------------
		public void actionPerformed (ActionEvent event)
		{
			Object source = event.getSource();
			
			// do nothing, flag already set in FCFS
			if (source == sjf)
			{
				FCFS.sjfKey = 1;
				System.out.println(FCFS.sjfKey);
			}
		
			// flip flag in FCFS
			else if (source == ljf)
			{
				FCFS.sjfKey = -1;
				System.out.println(FCFS.sjfKey);
			}
		}
	}
}