//*****************************************************************************************************************************
// Worker.java - June 22 2017
// Nick Simmons
// Worker class, store details about worker ability, current job and queued up jobs
//*****************************************************************************************************************************

import java.util.ArrayList;
import java.util.List;

//Worker class, store details about worker ability, current job and queued up jobs
public class Worker
{
	private String workerName; // worker identifier
	private double workerLoad; // total job load in hours
	private int workerRank; // worker ability level
	private List<ProcessControlBlock> workerQ; // individual queue created from master queue, initial sequence of production
	private ProcessControlBlock job; // current job worker is on
	private int jobsLoaded; // how many jobs loaded to worker

	private List<ProcessControlBlock> loadSequence = new ArrayList<ProcessControlBlock>(); // actual sequence of production
	private boolean busy; // does the worker currently have a job
	private double idleTime; // total hours the worker is idle
	
	// create and initialize a worker object
	public Worker(String workerName, int workerRank)
	{
		this.workerName = workerName;
		this.workerRank = workerRank;
		this.workerQ = new ArrayList<ProcessControlBlock>();
		this.job = null;
		this.jobsLoaded = 0;
		this.busy = false;
		this.idleTime = 0;
	}
	
	// store the hour the current job was finished
	public void setFinishedTime(double finishedTime)
	{
		job.setFinishedTime(finishedTime);
	}
	
	// return the hour the current job was finished
	public double getFinishedTime()
	{
		if (job != null)
		{
			return job.getFinishedTime();
		}
		
		else
		{
			return 0;
		}
	}
	
	// store the hour the current job was started
	public void setStartTime(double startTime)
	{
		job.setStartTime(startTime);
	}
	
	// return the hour the current job was started
	public double getStartTime()
	{
		return (double) job.getStartTime();
	}

	// update the hours of worker idle time
	public void setIdleTime(double idleTime)
	{
		this.idleTime += idleTime;
	}
	
	// return the workers total idle time
	public double getIdleTime()
	{
		return idleTime;
	}
	
	// store the current job to the actual build sequence
	public void putLoadSequence(ProcessControlBlock job)
	{
		loadSequence.add(job);
	}
	
	// return the actual build sequence
	public List<ProcessControlBlock> getLoadSequence()
	{
		return loadSequence;
	}
	
	// return the workers identifier
	public String getWorkerName()
	{
		return workerName;
	}
	
	// set the workers identifier
	public void setWorkerName(String workerName)
	{
		this.workerName = workerName;
	}
	
	// return the workers total loaded hours
	public double getWorkerLoad()
	{
		return workerLoad;
	}
	
	// update the hours of worker load
	public void addWorkerLoad(double workerLoad)
	{
		this.workerLoad += workerLoad;
	}
	
	// set the workers rank, or ability, level
	public void setRank(int workerRank)
	{
		this.workerRank = workerRank;
	}
	
	// return the workers rank, or ability level
	public int getRank()
	{
		return workerRank;
	}
	
	// store a job to the workers individual queue
	public void putQueue(ProcessControlBlock pcb)
	{
		workerQ.add(pcb);
	}
	
	// return the next item from the workers individual queue
	public ProcessControlBlock getQueue()
	{
		return workerQ.remove(0);
	}
	
	// return the job currently being worked on by the worker
	public Object getJob()
	{
		return job;
	}
	
	// set the current job for the worker
	public void setJob(ProcessControlBlock job)
	{
		this.job = job;
	}
	
	// return the hours required of the current job
	public double getJobTime()
	{
		return job.getJobTime();
	}
	
	// check of the workers individual queue is empty
	public boolean isEmpty()
	{
		return workerQ.isEmpty();
	}
	
	// store the number of jobs queued to the worker
	public void setJobsLoaded(int jobsLoaded)
	{
		this.jobsLoaded = jobsLoaded;
	}
	
	// return the number of jobs queued to the worker
	public int getJobsLoaded()
	{
		return jobsLoaded;
	}
	
	// return the arrival time in hours of the current job
	public double getArrivalTime()
	{
		return job.getArrivalTime();
	}
	
	// return the due date in hours of the current job
	public double getDueTime()
	{
		return job.getDueTime();
	}
	
	// return the name of the current job
	public String getJobName()
	{
		return job.getJobName();
	}
	
	// return the workers individual queue
	public List<ProcessControlBlock> getWorkerQ()
	{
		return workerQ;
	}
	
	// set to true when the worker is building, false when the worker is idle
	public void setBusy(boolean busy)
	{
		this.busy = busy;
	}
	
	// returns whether or not the worker is currently building
	public boolean isBusy()
	{
		return busy;
	}
}