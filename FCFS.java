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
	static String workerFile = null;
	static String jobFile = null;
	static boolean lateAvoidance = false;
	static double criticalSafetyFactory = 2.0;
	static List<String> workerNames = new ArrayList<String>();
	static List<Integer> workerRanks = new ArrayList<Integer>();

	public static void main(String args[]) throws IOException
	{	
		// constants defining simulation characteristics
		final double SIMULATION_TIME = 3000; // hrs
		final double SIMULATION_INCREMENT = 0.01;
		DecimalFormat fmt = new DecimalFormat("0.##"); // statistics output format
		
		// create queues for holding jobs on arrival and worker queues
		ProcessControlBlock pcb = null; // job storage for file->queue
				
		// for computations of statistics
		List<ProcessControlBlock> unassignedJobs = new ArrayList<ProcessControlBlock>();
		List<ProcessControlBlock> missedJobs = new ArrayList<ProcessControlBlock>();
		List<ProcessControlBlock> completedJobs = new ArrayList<ProcessControlBlock>();
		double averageTurnAroundTime = 0;		
		double averageWaitingTime = 0;

		// worker details

		int lastAlternateWorker = 0;
		int workerID = 0;				
		String info = "";
		
		// Create frame and dialog to open pending job list
		JFrame frameScheduler = new JFrame ("Results");
		frameScheduler.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		JTextArea ta = new JTextArea (50, 30);
		
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

		// ****************************************************************************
		//  scheduler section 	
		// ****************************************************************************
		
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
       				unassignedJobs.add(pcb);
       				pcb = null; // ditch job for now
       			}
       		} 		
       	}
 
       	// ****************************************************************************      	
       	// simulator section 
       	// ****************************************************************************
       	
		for (double currentTime = 0; currentTime < SIMULATION_TIME; currentTime = currentTime + SIMULATION_INCREMENT)
		{
			// check queue for critical ratio of CRITICAL_SAFETY_FACTOR (hours left before due / job time)
			if (lateAvoidance)
			{
				for (int i = 0; i < workerCount; i++)
				{
					for (int j = 0; j < workers[i].getWorkerQ().size(); j++)
					{
						double criticalRatio = ((workers[i].getWorkerQ().get(j).getDueTime() - currentTime) / workers[i].getWorkerQ().get(j).getJobTime());
						
						if (criticalRatio < criticalSafetyFactory)
						{
							// take some action, push job to front of queue
							System.out.println("PUSH! job " + workers[i].getWorkerQ().get(j).getJobName() + " is going to be late.");
							ProcessControlBlock tempBlock;
							tempBlock = workers[i].getWorkerQ().remove(j);
							workers[i].getWorkerQ().add(0, tempBlock);
						}
					}
				}
			}					
			
			// process all workers simultaneously
			for (int i = 0; i < workerCount; i++)
			{
				// check if the worker is busy or idle
				// worker is not busy
				if (!workers[i].isBusy())
				{
					// load a job to the worker
					// is there a job for the worker
					if (!workers[i].isEmpty())
					{
						// is it time to start the job?
						if (currentTime >= workers[i].getWorkerQ().get(0).getArrivalTime())
						{
							// load job from worker queue to worker
							workers[i].setJob((ProcessControlBlock) workers[i].getQueue());
							// store job start time
							workers[i].setStartTime(currentTime);
							// store job name for load list
							workers[i].loadSequence.add(workers[i].getJobName());
							// set worker to busy
							workers[i].setBusy(true);
							System.out.println("Worker " + workers[i].getWorkerName() + " started job " + workers[i].getJobName() + " at " + currentTime);
						}
					}

					// there is no job for worker, worker spins
					else
					{
					}
				}
				
				// worker is busy
				else
				{
					// is the job finished?
					if (currentTime >= workers[i].getStartTime() + workers[i].getJobTime())
					{
						// record finished details
						workers[i].setFinishedTime(currentTime);
						
						// is the job late
						if (workers[i].getFinishedTime() > workers[i].getDueTime())
						{
							System.out.println(workers[i].getJobName() + " finished at " + workers[i].getFinishedTime());
							System.out.println(workers[i].getJobName() + " is late by " + (workers[i].getFinishedTime() - workers[i].getDueTime()));
							missedJobs.add((ProcessControlBlock)workers[i].getJob());
						}
						
						// job is on-time
						else
						{
							System.out.println(workers[i].getJobName() + " finished at " + workers[i].getFinishedTime());
							System.out.println(workers[i].getJobName() + " is early by " + (workers[i].getFinishedTime() - workers[i].getDueTime()));
						}
						
						// worker is now idle
						workers[i].setBusy(false);
						// store job to finished queue
						completedJobs.add((ProcessControlBlock)workers[i].getJob());							
					}
				}
			}
		}      
			
		// ****************************************************************************
		// Compute and display results
		// ****************************************************************************
		
		// compute idle times
		for (int i = 0; i < workerCount; i++)
		{
			// time last job finished - workers total load
			if (workers[i] != null)
			{
				workers[i].setIdleTime(workers[i].getFinishedTime() - workers[i].getWorkerLoad());
			}
		}		
		
		// compute average turn around time
		int totalJobs = 0;
		for (int i = 0; i < workerCount; i++)
		{
			totalJobs += workers[i].getJobsLoaded();
		}		
		
		// compute statistics for TAT, WT
		for (int i = 0; i < completedJobs.size(); i++)
		{
			averageTurnAroundTime += (completedJobs.get(i).getFinishedTime() - completedJobs.get(i).getArrivalTime());
	//		System.out.println(completedJobs.get(i).getJobName() + " TAT:" + (completedJobs.get(i).getFinishedTime() - completedJobs.get(i).getArrivalTime()));
			averageWaitingTime += (completedJobs.get(i).getStartTime() - completedJobs.get(i).getArrivalTime());
	//		System.out.println(completedJobs.get(i).getJobName() + " WT:" + (completedJobs.get(i).getStartTime() - completedJobs.get(i).getArrivalTime()));

		}
		
		averageTurnAroundTime /= totalJobs;
		averageWaitingTime /= totalJobs;
		
		// construct output string for results	
		if (algorithm == 1)
		{
			info += ("Shortest Job First");
		}
		
		else if (algorithm == 2)
		{
			info += ("Longest Job First");
		}
		
		else if (algorithm == 3)
		{
			info += ("Earliest Due Date");
		}

		else if (algorithm == 4)
		{
			info += ("Farthest Due Date");
		}
		
		info += "\nAuto reschedule:" + lateAvoidance;
		
		if (lateAvoidance)
		{
			info += "\nSafety Buffer:" + criticalSafetyFactory + "x";
		}
		
		info += ("\nJobs loaded to from file:" + jobList.getItemsFromFile());
		info += ("\nJobs loaded to workers:" + totalJobs);
		info += ("\nJobs completed:" + (completedJobs.size() - unassignedJobs.size()));
		info += ("\nDeadlines missed:" + missedJobs.size());
		info += ("\nJobs unassigned:" + unassignedJobs.size());
		
		info += ("\n\nAverage TAT:" + fmt.format(averageTurnAroundTime) + " hours");
		info += ("\nAverage WT:" + fmt.format(averageWaitingTime) + " hours");	

		info += "\n";
		
		info += "\nTotal workers:" + workerCount;
		
		for (int i = 0; i < workerCount; i++)
		{
			info += ("\n" + (workers[i].getWorkerName()) +" queue size:" + fmt.format(workers[i].getWorkerLoad()) + " hours");
		}

		info += "\n";

		for (int i = 0; i < workerCount; i++)
		{
			info += ("\n" + (workers[i].getWorkerName()) +" idle for:" + fmt.format(workers[i].getIdleTime()) + " hours");
		}
		
		info += "\n";
		
		for (int i = 0; i < missedJobs.size(); i++)
		{
			info += ("\n" + missedJobs.get(i).getJobName() + " late by " + (missedJobs.get(i).getFinishedTime() - missedJobs.get(i).getDueTime()));
		}
		
		info += "\n";
		
		for (int i = 0; i < unassignedJobs.size(); i++)
		{
			info += ("\n" + unassignedJobs.get(i).getJobName() + " unassigned, no capable worker");
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