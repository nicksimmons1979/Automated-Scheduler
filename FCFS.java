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
	static int algorithm = 2;
	static int sjfKey = 1;
	static List<String> workerNames = new ArrayList<String>();
	static List<Integer> workerRanks = new ArrayList<Integer>();
	

	public static void main(String args[]) throws IOException
	{	
		// constants defining simulation characteristics
		final double SIMULATION_TIME = 4000; // hrs
		final double SIMULATION_INCREMENT = 0.01;
	//	final double FULL_DAY = 7.5; // hours in day
		DecimalFormat fmt = new DecimalFormat("0.##"); // statistics output format
		
		// create queues for holding jobs on arrival and worker queues
		ProcessControlBlock pcb = null; // job storage for file->queue
				
		// for computations of statistics
		List<Double> previousTAT = new ArrayList<Double>();
		List<Double> previousWT = new ArrayList<Double>();
		List<ProcessControlBlock> unassignedJobs = new ArrayList<ProcessControlBlock>();
		double averageTurnAroundTime = 0;		
		double averageWaitingTime = 0;

		int workerID = 0;
		int missedDeadlines = 0;				
		String info = "";
		int lastAlternateWorker = 0;
		
		// Create frame and dialog to open pending job list
		JFrame frameScheduler = new JFrame ("Results");
		frameScheduler.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		JTextArea ta = new JTextArea (20, 30);
		
		// open file
		JFileChooser chooser = new JFileChooser();
		int status = chooser.showOpenDialog (null);
		File file = chooser.getSelectedFile();

		// error?
		if (status != JFileChooser.APPROVE_OPTION)
		{
			ta.setText ("No File Chosen");
		}
		
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
		
		// initialize worker queue and load list
		workerCount = workerNames.size();
		Worker[] workers = new Worker[workerCount];
		for (int i = 0; i < workerCount; i++)
		{
			workers[i] = new Worker(workerNames.remove(0), workerRanks.remove(0));
			
		}
		System.out.println(workerCount);
		for (int i = 0; i < workerCount; i++)
		{
			System.out.println(workers[i].getWorkerName() + " " + workers[i].getRank());
		}
			
        // reading data from a csv file and convert to java object, store some statistics
        CsvToJavaObject jobList = new CsvToJavaObject();
        jobList.convertCsvToJava(file.getName());

        // ****************************************************************************
		// Minimize Make span - Load balanced SJF w Priority
		// ****************************************************************************

		// create worker queues from jobList
		for (double currentTime = 0; currentTime < SIMULATION_TIME; currentTime = currentTime + SIMULATION_INCREMENT)
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
					
			// if buffer is empty, transfer job from jobLogQueue to single job buffer
			if (pcb == null)
			{
				// 	create process control block with the next job
				pcb = jobList.getNextJob();
			}
				
			// When arrival time == current time, load a job to the selected worker queue
			if (pcb != null)
			{
				// worker is capable of processing job
				if (workers[workerID].getRank() >= pcb.getJobRank())
				{
					System.out.println("Job name:" + pcb.getJobName() + " Job rank:" + pcb.getJobRank() + " Worker rank:" + workers[workerID].getRank());

					if (pcb.getArrivalTime() <= currentTime)
					{
						// check for priority preemption, force to front of queue
						if (pcb.getPriority() == 0)
						{
							workers[workerID].addWorkerLoad(pcb.getJobTime());
							pcb.setJobTime(0);
						}
						
						// transfer job from joblist to worker queue 
						// **** sorting criterion 2nd argument in .putqueue method **** //
						workers[workerID].putQueue(pcb, sjfKey*pcb.getJobTime()); // change key for sorting here (+ve SJF, -ve LJF)
						workers[workerID].setJobsLoaded(workers[workerID].getJobsLoaded() + 1);
						workers[workerID].addWorkerLoad(pcb.getJobTime());
						pcb = null;
						assigned = true;
					}
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
							// System.out.println("Job name:" + pcb.getJobName() + " Job rank:" + pcb.getJobRank() + " Worker rank:" + workers[workerID].getRank());

							if (pcb.getArrivalTime() <= currentTime)
							{
								// check for priority preemption, force to front of queue
								if (pcb.getPriority() == 0)
								{
									workers[i].addWorkerLoad(pcb.getJobTime());
									pcb.setJobTime(0);
								}
							
								// transfer job from joblist to worker queue 
								workers[i].putQueue(pcb, sjfKey*pcb.getJobTime()); // change key for sorting here (+ve SJF, -ve LJF)
								workers[i].setJobsLoaded(workers[i].getJobsLoaded() + 1);
								workers[i].addWorkerLoad(pcb.getJobTime());
								System.out.println("alternative worker " + (i+1) + " found for job " + pcb.getJobName());
								pcb = null;
								assigned = true;
								lastAlternateWorker = i+1;

								break; // successful target found, stop looking
							}
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
						// compute turn around time for single job
						previousTAT.add((workers[i].getJobLoadTime() + workers[i].getJobTime()) - workers[i].getArrivalTime());

						// 	on-time delivery?
						if ((previousTAT.get(previousTAT.size() - 1) > (workers[i].getDueTime() - workers[i].getArrivalTime())))
						{							
							System.out.println("worker " + workerID + " finished job " + workers[i].getJobName() + " at time " + currentTime + ". Late by " + (currentTime - workers[i].getDueTime()));
							missedDeadlines++;
						}
						
						else
						{
							System.out.println("worker " + workerID + " finished job " + workers[i].getJobName() + " at time " + currentTime + ". Early by " + (currentTime - workers[i].getDueTime()));
						}
						
						// 	load next job if one exists, else wait
						if (!workers[i].isEmpty())
						{
							workers[i].setJob((ProcessControlBlock) workers[i].getWorkerQ().getQueue());
							System.out.println("loaded " + workers[i].getJobName() + " to worker " + (i+1)  + " at " + currentTime);
							workers[i].putLoadSequence(workers[i].getJobName());
							workers[i].setJobLoadTime(currentTime); // worker starts job

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
					workers[i].setJob((ProcessControlBlock) workers[i].getWorkerQ().getQueue());
					System.out.println("loaded " + workers[i].getJobName() + " to worker " + (i+1) + " at " + currentTime);
					workers[i].putLoadSequence(workers[i].getJobName());
					workers[i].setJobLoadTime(currentTime); // worker starts job

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
		
		// compute average waiting time
		while(!previousWT.isEmpty())
		{
			averageWaitingTime += previousWT.remove(0);
		}

		// construct statistics for results	
		if (sjfKey == 1)
		{
			info += ("\nShortest Job First");
		}
		
		else if (sjfKey == -1)
		{
			info += ("\nLongest Job First");
		}
		
		info += ("\nJobs completed:" + totalJobs);
		
		info += ("\nAverage TAT:" + fmt.format(averageTurnAroundTime / totalJobs ) + " hours");
		info += ("\nAverage WT:" + fmt.format(averageWaitingTime / totalJobs ) + " hours");	
		info += ("\nDeadlines missed:" + missedDeadlines);
		
		for (int i = 0; i < workerCount; i++)
		{
			info += ("\nWorker " + (i+1) +" queue size:" + fmt.format(workers[i].getWorkerLoad()));
		}
		
		// display results frame
		frameScheduler.getContentPane().add(ta);
		frameScheduler.pack();
		frameScheduler.setVisible(true);		
		ta.setText(info);
	
		// dump load balanced gantt to csv file
		try
		{
			PrintWriter out = new PrintWriter("loadlist_"+file.getName());
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