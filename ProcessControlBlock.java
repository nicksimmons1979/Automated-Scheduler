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
	float arrivalTime, dueTime, jobTime, priority, finishedTime;
	boolean kitted;
	int jobRank;
	
	// create a job block object
	public ProcessControlBlock(String jobName, float arrivalTime, float dueTime, float jobTime, float priority, boolean kitted, int jobRank)
	{
		this.jobName = jobName;
		this.arrivalTime = arrivalTime;
		this.dueTime = dueTime;
		this.jobTime = jobTime;
		this.jobTime = priority;
		this.kitted = kitted;
		this.jobRank = jobRank;
	}
	
	public void setFinishedTime(float finishedTime)
	{
		this.finishedTime = finishedTime;
	}
	
	public float getFinishedTime()
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
	public float getArrivalTime()
	{
		return arrivalTime;
	}
	
	// return job due time
	public float getDueTime()
	{
		return dueTime;
	}
	
	// return job total time required
	public float getJobTime()
	{
		return jobTime;
	}
	
	// return priority level of job
	public float getPriority()
	{
		return priority;
	}
	
	// set job arrival time
	public void setArrivalTime(float arrivalTime)
	{
		this.arrivalTime = arrivalTime;
	}
	
	// set job total time required
	public void setJobTime(float jobTime)
	{
		this.jobTime = jobTime;
	}
	
	// set job due time
	public void setDueTime(float dueTime)
	{
		this.jobTime = dueTime;
	}
	
	// set job name
	public void setJobName(String jobName)
	{
		this.jobName = jobName;
	}
	
	// set priority level of job
	public void setPriority(float priority)
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