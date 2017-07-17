//*****************************************************************************************************************************
// CsvToJavaObject.java - June 22 2017
// Nick Simmons
// Opens a csv file, imports each line as object stored in an array list
//*****************************************************************************************************************************

import java.io.BufferedReader;
import java.util.Comparator;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JTextArea;

// class to import and manipulate csv file data
public class CsvToJavaObject
{
	// storage for each object imported
//	private PriorityQueue jobList = new PriorityQueue();
	private List<ProcessControlBlock> jobList = new ArrayList<ProcessControlBlock>();
	private int itemsLoaded = 0;
	private int workersLoaded = 0;
	
	// load jobs csv file to object
	public void convertJobsCsvToJava(String csvFileToRead)
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

				// job ready to build
				if (jobObject.getKitted() == true)
				{
					// put job list master queue
					jobList.add(jobObject);
					itemsLoaded++;
				}
			}
			
			// dump joblist pre priority sort
			System.out.println("Pre Sort");
			for (int i=0; i < jobList.size();i++)
			{
				ProcessControlBlock temp = jobList.get(i);
				System.out.println("job:" + temp.getJobName() +" priority:"+ temp.getPriority()+ " jobtime:"+temp.getJobTime()+ " arrivaltime:"+temp.getArrivalTime()+ " duetime:"+temp.getDueTime());
			}
			
			
			// secondary sort by job time	
			if (FCFS.algorithm == 1)
			{
				Collections.sort(jobList, CsvToJavaObject.compareAlgo1());
			}
			else if (FCFS.algorithm == 2)
			{
				Collections.sort(jobList, CsvToJavaObject.compareAlgo2());
			}
			else if (FCFS.algorithm == 3)
			{
				Collections.sort(jobList, CsvToJavaObject.compareAlgo3());
			}

			else if (FCFS.algorithm == 4)
			{
				Collections.sort(jobList, CsvToJavaObject.compareAlgo4());
			}

			// dump joblist pre priority sort
			System.out.println("\nPost Sort");
			for (int i=0; i < jobList.size();i++)
			{
				ProcessControlBlock temp = jobList.get(i);
				System.out.println("job:" + temp.getJobName() +" priority:"+ temp.getPriority()+ " jobtime:"+temp.getJobTime()+ " arrivaltime:"+temp.getArrivalTime()+ " duetime:"+temp.getDueTime());
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
	
	// load worker csv to global variable
	public void convertWorkersCsvToJava(String csvFileToRead)
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
				String[] workerStats = line.split(splitBy);

				// store name, rank to list
				FCFS.workerNames.add(workerStats[0]);
				FCFS.workerRanks.add(Integer.parseInt(workerStats[1]));
				
				workersLoaded++;
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
	
	// returns and removes next job from array list
	public ProcessControlBlock getNextJob()
	{
		if (itemsLoaded != 0)
		{
			ProcessControlBlock temp;
			temp = (ProcessControlBlock) jobList.remove(0);
//			jobList.remove(0);
			itemsLoaded--;
			return temp;
		}
		else
			return null;
	}
	
	// put job end of list
	public void addNextJob(ProcessControlBlock job)
	{
		jobList.add(job);
		itemsLoaded++;
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
	
	public int getWorkersLoaded()
	{
		return workersLoaded;
	}
	
	// Shortest Job First - DONE
	// sort ArrivalTime->priority->jobTime
	public static Comparator<ProcessControlBlock> compareAlgo1()
	{   
		Comparator<ProcessControlBlock> comp = new Comparator<ProcessControlBlock>()
		{
			public int compare(ProcessControlBlock job1, ProcessControlBlock job2)
			{   
				System.out.println("call algo:"+ FCFS.algorithm);
				int result =  new Float(job1.getArrivalTime()).compareTo(job2.getArrivalTime());
				if (result == 0)
				{
					result = new Float(job1.getPriority()).compareTo(job2.getPriority());
				}
				if (result == 0)
				{
					result = new Float(job1.getJobTime()).compareTo(job2.getJobTime());
				}
				return result;
			}        
		};
		
		return comp;
	}
	
	// Longest Job First - DONE
	// sort ArrivalTime->priority->-1*jobTime
	public static Comparator<ProcessControlBlock> compareAlgo2()
	{   
		Comparator<ProcessControlBlock> comp = new Comparator<ProcessControlBlock>()
		{
			public int compare(ProcessControlBlock job1, ProcessControlBlock job2)
			{  
				System.out.println("call algo:"+ FCFS.algorithm);
				int result =  new Float(job1.getArrivalTime()).compareTo(job2.getArrivalTime());
				if (result == 0)
				{
					result = new Float(job1.getPriority()).compareTo(job2.getPriority());
				}
				if (result == 0)
				{
					result = new Float(job2.getJobTime()).compareTo(job1.getJobTime());
				}
				return result;
			}        
		};
		
		return comp;
	} 
	
	// Earliest Due Date - DONE
	// sort ArrivalTime->priority->-1*dueTime
	public static Comparator<ProcessControlBlock> compareAlgo3()
	{   
		Comparator<ProcessControlBlock> comp = new Comparator<ProcessControlBlock>()
		{
			public int compare(ProcessControlBlock job1, ProcessControlBlock job2)
			{   
				System.out.println("call algo:"+ FCFS.algorithm);
				int result =  new Float(job1.getArrivalTime()).compareTo(job2.getArrivalTime());
				if (result == 0)
				{
					result = new Float(job1.getPriority()).compareTo(job2.getPriority());
				}
				if (result == 0)
				{
					result = new Float(job1.getDueTime()).compareTo(job2.getDueTime());
				}
				return result;
			}        
		};
		
		return comp;
	} 
	
	// Farthest Due Date - DONE
	// sort ArrivalTime->priority->dueTime
	public static Comparator<ProcessControlBlock> compareAlgo4()
	{   
		Comparator<ProcessControlBlock> comp = new Comparator<ProcessControlBlock>()
		{
			public int compare(ProcessControlBlock job1, ProcessControlBlock job2)
			{   
				System.out.println("call algo:"+ FCFS.algorithm);
				int result =  new Float(job1.getArrivalTime()).compareTo(job2.getArrivalTime());				if (result == 0)
				{
					result = new Float(job1.getPriority()).compareTo(job2.getPriority());
				}
				if (result == 0)
				{
					result = new Float(job2.getDueTime()).compareTo(job1.getDueTime());
				}
				return result;
			}        
		};
		
		return comp;
	} 

}

