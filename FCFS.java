//*****************************************************************************************************************************
// FCFS.java - June 22 2017
// Nick Simmons
// Job Scheduler
// Uses shortest job first with load balanced minimal makespan
//*****************************************************************************************************************************

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.io.*;

public class FCFS
{
	// global variables for JFrame communication, bad method, investigate return values for action listeners
	static int workerCount;
	static boolean wait = true;
	static int algorithm = 1;
	static List<String> workerNames = new ArrayList<String>();
	static List<Integer> workerRanks = new ArrayList<Integer>();
	static String workerFile = null;
	static String jobFile = null;

	public static void main(String args[]) throws IOException
	{	
		// constants defining simulation characteristics
		final double SIMULATION_TIME = 300; // hrs
		final double SIMULATION_INCREMENT = 0.01;
		DecimalFormat fmt = new DecimalFormat("0.##"); // statistics output format
		
		// create queues for holding jobs on arrival and worker queues
		ProcessControlBlock pcb = null; // job storage for file->queue
				
		// for computations of statistics
		List<Double> previousTAT = new ArrayList<Double>();
		List<Double> previousWT = new ArrayList<Double>();
		List<ProcessControlBlock> unassignedJobs = new ArrayList<ProcessControlBlock>();
		double averageTurnAroundTime = 0;		
		double averageWaitingTime = 0;
		double hoursTillDue = 0;

		int workerID = 0;
		int missedDeadlines = 0;				
		String info = "";
		int lastAlternateWorker = 0;
		
		// Create frame and dialog to open pending job list
		JFrame frameScheduler = new JFrame ("Results");
		frameScheduler.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		JTextArea ta = new JTextArea (20, 30);
		
		// Create frame to select scheduler and employees
		JFrame frame = new JFrame ("Automated Scheduler v1.0");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new SchedulerCombo());
		frame.pack();
		frame.setVisible(true);	
		
		// wait until scheduler selected
		while (wait)
		{
			try 
			{
				Thread.sleep(100);
			}
			catch (InterruptedException e)
			{
				
			}
		}
		
		// read in workers from file
        CsvToJavaObject workerList = new CsvToJavaObject();
        workerList.convertWorkersCsvToJava(workerFile);
        
        // reading data from a csv file and convert to java object, store some statistics
        CsvToJavaObject jobList = new CsvToJavaObject();
        jobList.convertJobsCsvToJava(jobFile);
        		
		// initialize worker queue and load list
		workerCount = workerNames.size();
		Worker[] workers = new Worker[workerCount];
		for (int i = 0; i < workerCount; i++)
		{
			workers[i] = new Worker(workerNames.remove(0), workerRanks.remove(0));		
		}
			
       	// create ordered worker queues from jobList
       	while (!jobList.isEmpty())
       	{					
       		boolean assigned = false;
			
       		// find worker with least amount of work
       		double smallest = workers[0].getWorkerLoad();
       		double largest = workers[0].getWorkerLoad();
       		int smallestIndex = 0;
				
       		for(int i = 0; i < workerCount; i++)
       		{
       			if(workers[i].getWorkerLoad() > largest)
       			{
       				largest = workers[i].getWorkerLoad();
       			}

       			else if (workers[i].getWorkerLoad() < smallest)
       			{
       				smallest = workers[i].getWorkerLoad();
       				smallestIndex = i;
       			}
       		}
       		workerID = smallestIndex;
       		System.out.println("worker selected:"+ (workerID+1));
       		
       		// setup individual worker queues
       		// pull job from queue for assignment
       		pcb = jobList.getNextJob();	

       		// worker is capable of processing job
       		if (workers[workerID].getRank() >= pcb.getJobRank())
       		{
				workers[workerID].putQueue(pcb);
       			workers[workerID].setJobsLoaded(workers[workerID].getJobsLoaded() + 1);
       			workers[workerID].addWorkerLoad(pcb.getJobTime());
       			pcb = null;
       			assigned = true;
       		}		
				
       		// lowest queue size worker not capable, find another worker
       		// fix balances algo, doesn't balance nicely, still stacking unfairly to capable workers
       		else
       		{
       			// cycle through all workers
       			if ((lastAlternateWorker + 1) >= workerCount) 
					lastAlternateWorker = 0;
					
       			// start at last alternate worker + 1, ensures one worker doesn't get piled on
       			for (int i = (lastAlternateWorker); i < workerCount; i++)
       			{
       				if (workers[i].getRank() >= pcb.getJobRank())
       				{
       					// transfer job from joblist to worker queue 
       					workers[i].putQueue(pcb);
       					workers[i].setJobsLoaded(workers[i].getJobsLoaded() + 1);
       					workers[i].addWorkerLoad(pcb.getJobTime());
       					System.out.println("alternative worker " + (i+1) + " found for job " + pcb.getJobName());
       					pcb = null;
       					assigned = true;
       					lastAlternateWorker = i+1;

       					break; // successful target found, stop looking			
       				}	
       			}
					
       			if (assigned == false)
       			{
       				System.out.println(pcb.getJobName() + " ditched, failure to assign");
       				info += "Failure to assign " + pcb.getJobName() + " no capable worker\n"; 
       				unassignedJobs.add(pcb);
       				pcb = null; // ditch job for now
       			}
       		} 		
       	}
 
		// process queues
		for (double currentTime = 0; currentTime < SIMULATION_TIME; currentTime = currentTime + SIMULATION_INCREMENT)
		{
			// process all workers simultaneously
			for (int i = 0; i < workerCount; i++)
			{				

				
				// if there is a job running on worker
				if (workers[i].getJob() != null)
				{
					// if job on worker is finished
					if ((workers[i].getJobLoadTime() + workers[i].getJobTime()) <= currentTime)
					{
						System.out.println("Job " + workers[i].getJobName() + " finished at " + (workers[i].getJobLoadTime() + workers[i].getJobTime()));
						
						// compute turn around time for single job
						previousTAT.add((workers[i].getJobLoadTime() + workers[i].getJobTime()) - workers[i].getArrivalTime());
						System.out.println("Tat size:"+previousTAT.size());
						
						
						
						// 	on-time delivery?
						// compute hours until due
						hoursTillDue = workers[i].getDueTime() - workers[i].getArrivalTime() - workers[i].getJobTime();
						
						if (hoursTillDue < 0)
						{							
							missedDeadlines++;
							System.out.println("Job " + workers[i].getJobName() + " late by " + -hoursTillDue);
						}
						
						// 	load next job if one exists, else wait
						if (!workers[i].isEmpty())
						{
							if (currentTime >= workers[i].getWorkerQ().get(0).getArrivalTime())
							{
								workers[i].setJob((ProcessControlBlock) workers[i].getQueue());
								workers[i].putLoadSequence(workers[i].getJobName());
								workers[i].setJobLoadTime(currentTime); // worker starts job
								System.out.println("loaded " + workers[i].getJobName() + " to worker " + (i+1)  + " at " + workers[i].getJobLoadTime());
							}

							// time waiting on queue
							previousWT.add(workers[i].getJobLoadTime() - workers[i].getArrivalTime());
						}
					
						// worker idle, queue is empty
						else
						{
							workers[i].setJob(null);
						}
					}
				}
				
				// if the worker is idle
				else if (!workers[i].isEmpty())
				{
					// get process from queue and dispatch to the cpu
					if (currentTime >= workers[i].getWorkerQ().get(0).getArrivalTime())
					{
						workers[i].setJob((ProcessControlBlock) workers[i].getQueue());
						workers[i].putLoadSequence(workers[i].getJobName());
						workers[i].setJobLoadTime(currentTime); // worker starts job
						System.out.println("loaded " + workers[i].getJobName() + " to worker " + (i+1)  + " at " + workers[i].getJobLoadTime());
					}

					// time waiting on queue
					previousWT.add(workers[i].getJobLoadTime() - workers[i].getArrivalTime());
				}
				
			}
		}
			
		// ****************************************************************************
		// Compute and display results
		// ****************************************************************************
		
		// compute average turn around time
		int totalJobs = 0;
		for (int i = 0; i < workerCount; i++)
		{
			totalJobs += workers[i].getJobsLoaded();
		}
		
		while(!previousTAT.isEmpty())
		{	
			averageTurnAroundTime += previousTAT.remove(0);
		}
		System.out.println("averageTurnAroundTime:"+averageTurnAroundTime);
		
		// compute average waiting time
		while(!previousWT.isEmpty())
		{
			averageWaitingTime += previousWT.remove(0);
		}
		System.out.println("averageWaitingTime:"+averageWaitingTime);


		// construct statistics for results	
		if (algorithm == 1)
		{
			info += ("\nShortest Job First");
		}
		
		else if (algorithm == 2)
		{
			info += ("\nLongest Job First");
		}
		
		else if (algorithm == 3)
		{
			info += ("\nEarliest Due Date");
		}

		else if (algorithm == 4)
		{
			info += ("\nFarthest Due Date");
		}
		
		info += ("\nJobs completed:" + totalJobs);
		
		info += ("\nAverage TAT:" + fmt.format(averageTurnAroundTime / totalJobs ) + " hours");
		info += ("\nAverage WT:" + fmt.format(averageWaitingTime / totalJobs ) + " hours");	
		info += ("\nDeadlines missed:" + missedDeadlines);
		
		for (int i = 0; i < workerCount; i++)
		{
			info += ("\n" + (workers[i].getWorkerName()) +" queue size:" + fmt.format(workers[i].getWorkerLoad()) + " hours");
		}
		
		// display results frame
		frameScheduler.getContentPane().add(ta);
		frameScheduler.pack();
		frameScheduler.setVisible(true);		
		ta.setText(info);
	
		// dump load balanced gantt to csv file
		try
		{
			PrintWriter out = new PrintWriter("loadlist_"+jobFile);
			for (int i = 0; i < workerCount; i++)
			{
				out.print(workers[i].getWorkerName() + ","); // dump worker name cell

				while (!workers[i].loadSequence.isEmpty())
				{
					out.print(workers[i].getLoadSequence() + ","); // dump worker i job list					
				}
				
				out.print("\n"); // dump worker name cell
			}
			
			out.println("\n" + info);
			out.close();
		}
		
		// problem writing file?
		catch(FileNotFoundException e)
		{
			JFrame frameError = new JFrame ("ERROR");
			frameError.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			JTextArea taError = new JTextArea (20, 30);
			frameError.getContentPane().add(taError);
			frameError.pack();
			frameError.setVisible(true);	
			taError.setText("Load list not written. Is the file already open?");	 
		}
	}
}