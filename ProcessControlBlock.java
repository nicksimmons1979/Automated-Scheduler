//*****************************************************************************************************************************
// ProcessControlBlock.java - June 22 2017
// Nick Simmons
// Job object for use on priority queue, object attached to queue node
//*****************************************************************************************************************************

// class for job queue item
public class ProcessControlBlock
{
	// job particulars
	String jobName;
	private double arrivalTime, dueTime, jobTime, priority, startTime, finishedTime;
	boolean kitted;
	int jobRank;
	
	// create a job block object
	public ProcessControlBlock(String jobName, double arrivalTime, double dueTime, double jobTime, double priority, boolean kitted, int jobRank)
	{
		this.jobName = jobName;
		this.arrivalTime = arrivalTime;
		this.dueTime = dueTime;
		this.jobTime = jobTime;
		this.jobTime = priority;
		this.kitted = kitted;
		this.jobRank = jobRank;
	}
	
	
	
	public void setStartTime(double startTime)
	{
		this.startTime = startTime;
	}
	
	public double getStartTime()
	{
		return startTime;
	}
	
	
	public void setFinishedTime(double finishedTime)
	{
		this.finishedTime = finishedTime;
	}
	
	public double getFinishedTime()
	{
		return finishedTime;
	}
	
	public int getJobRank()
	{
		return jobRank;
	}
	
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
	
	public void setKitted(boolean kitted)
	{
		this.kitted = kitted;
	}
	
	public boolean getKitted()
	{
		return kitted;
	}
}