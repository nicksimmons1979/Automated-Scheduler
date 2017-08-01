//*****************************************************************************************************************************
// ProcessControlBlock.java - June 22 2017
// Nick Simmons
// Job object for use on priority queue, object attached to queue node
//*****************************************************************************************************************************

// class for job queue item
public class ProcessControlBlock
{
	// job particulars
	private String jobName;
	private String workerName = "";
	private double arrivalTime, dueTime, jobTime, priority, startTime, finishedTime;
	private boolean kitted;
	private int jobRank;
	private String forcedTo;
	
	// create a job block object
	public ProcessControlBlock(String jobName, double arrivalTime, double dueTime, double jobTime, double priority, boolean kitted, int jobRank, String forcedTo)
	{
		this.jobName = jobName;
		this.arrivalTime = arrivalTime;
		this.dueTime = dueTime;
		this.jobTime = jobTime;
		this.jobTime = priority;
		this.kitted = kitted;
		this.jobRank = jobRank;
		this.forcedTo = forcedTo;
	}
	
	// set workers name
	public void setWorkerName(String workerName)
	{
		this.workerName = workerName;
	}
	
	// return workers name
	public String getWorkerName()
	{
		return workerName;
	}
	
	// set workers job start time
	public void setStartTime(double startTime)
	{
		this.startTime = startTime;
	}
	
	// return workers job start time
	public double getStartTime()
	{
		return startTime;
	}
	
	// set workers job finished time
	public void setFinishedTime(double finishedTime)
	{
		this.finishedTime = finishedTime;
	}
	
	// return workers finished job time
	public double getFinishedTime()
	{
		return finishedTime;
	}
	
	// return the rank of current job
	public int getJobRank()
	{
		return jobRank;
	}
	
	// set the rank of the current job
	public void setJobRank(int jobRank)
	{
		this.jobRank = jobRank;
	}
	
	// return job name
	public String getJobName()
	{
		return jobName;
	}
	
	// return job arrival time
	public double getArrivalTime()
	{
		return arrivalTime;
	}
	
	// return job due time
	public double getDueTime()
	{
		return dueTime;
	}
	
	// return job total time required
	public double getJobTime()
	{
		return jobTime;
	}
	
	// return priority level of job
	public double getPriority()
	{
		return priority;
	}
	
	// set job arrival time
	public void setArrivalTime(double arrivalTime)
	{
		this.arrivalTime = arrivalTime;
	}
	
	// set job total time required
	public void setJobTime(double jobTime)
	{
		this.jobTime = jobTime;
	}
	
	// set job due time
	public void setDueTime(double dueTime)
	{
		this.jobTime = dueTime;
	}
	
	// set job name
	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}
	
	// set priority level of job
	public void setPriority(double priority)
	{
		this.priority = priority;
	}
	
	// set kitted value of job
	public void setKitted(boolean kitted)
	{
		this.kitted = kitted;
	}
	
	// return kitted value of job
	public boolean getKitted()
	{
		return kitted;
	}
	
	// set forcedWorkers
	public void setForcedTo (String forcedTo)
	{
		this.forcedTo = forcedTo;
	}
	
	// return all forced to workers
	public String getForcedTo()
	{
		return this.forcedTo;
	}
}