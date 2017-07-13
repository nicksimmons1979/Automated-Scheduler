//*****************************************************************************************************************************
// CsvToJavaObject.java - June 22 2017
// Nick Simmons
// Opens a csv file, imports each line as object stored in an array list
//*****************************************************************************************************************************

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JTextArea;

// class to import and manipulate csv file data
public class CsvToJavaObject
{
	// storage for each object imported
	private List<ProcessControlBlock> jobList = new ArrayList<ProcessControlBlock>();
	private int itemsLoaded = 0;
	
	public void convertCsvToJava(String csvFileToRead)
	{
		// storage for each line
		BufferedReader br = null;
		String line = "";
		String splitBy = ",";

		try
		{
			br = new BufferedReader(new FileReader(csvFileToRead));

			// read and dispose of first line - line headers
			line = br.readLine();
			
			// read entire csv file
			while ((line = br.readLine()) != null)
			{
				// split on comma(',')
				String[] jobs = line.split(splitBy);

				// create job object to store values, each index represents adjacent cell in csv
				ProcessControlBlock jobObject = new ProcessControlBlock(jobs[0],Float.parseFloat((jobs[1])),Float.parseFloat(jobs[2]),Float.parseFloat(jobs[3]), Float.parseFloat(jobs[4]), Boolean.parseBoolean(jobs[5]), Integer.parseInt(jobs[6]));

				// add values from csv to job object
				jobObject.setJobName(jobs[0]);
				jobObject.setArrivalTime(Float.parseFloat((jobs[1])));
				jobObject.setDueTime(Float.parseFloat(jobs[2]));
				jobObject.setJobTime(Float.parseFloat(jobs[3]));
				jobObject.setPriority(Float.parseFloat(jobs[4]));
				jobObject.setKitted(Boolean.parseBoolean(jobs[5]));
				jobObject.setJobRank(Integer.parseInt(jobs[6]));

				// adding job objects to a list
				if (jobObject.getKitted() == true)
				{
					jobList.add(jobObject);
					itemsLoaded++;
				}
			}
		}
		
		// can't find file to open?
		catch (FileNotFoundException e)
		{
			JFrame frameError = new JFrame ("ERROR");
			frameError.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			JTextArea taError = new JTextArea (20, 30);
			frameError.getContentPane().add(taError);
			frameError.pack();
			frameError.setVisible(true);	
			taError.setText("File not found");	 
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// close file
		finally
		{
			if (br != null)
			{
				try
				{
					br.close();
				}
				
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	// dump job list to console
	public void printJobList(List<ProcessControlBlock> jobListToPrint)
	{
		for (int i = 0; i < jobListToPrint.size(); i++)
		{
			System.out.println("jobName= " + jobListToPrint.get(i).getJobName()
					+ " , arrivalTime=" + jobListToPrint.get(i).getArrivalTime()
					+ " , dueTime=" + jobListToPrint.get(i).getDueTime()
					+ " , jobTime="
					+ jobListToPrint.get(i).getJobTime());
		}
	}
	
	// returns next job from array list
	public ProcessControlBlock getNextJob()
	{
		if (itemsLoaded != 0)
		{
			ProcessControlBlock temp;
			temp = jobList.get(0);
			jobList.remove(0);
			itemsLoaded--;
			return temp;
		}
		else
			return null;
	}
	
	// returns size of job array list
	public int size()
	{
		return itemsLoaded;
	}
	
	// is the job array list empty?
	public boolean isEmpty()
	{
		if (itemsLoaded == 0)
			return true;
		else
			return false;
	}
}

