import java.util.ArrayList;
import java.util.List;

public class Worker
{
	private String workerName;
	private double workerLoad;
	private int workerRank;
	private List<ProcessControlBlock> workerQ;
	private ProcessControlBlock job;
	private int jobsLoaded;

	public List<String> loadSequence = new ArrayList<String>();
	private boolean busy;
	private double idleTime;
	
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
	
	public void setFinishedTime(double finishedTime)
	{
		job.setFinishedTime(finishedTime);
	}
	
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
	
	public void setStartTime(double startTime)
	{
		job.setStartTime(startTime);
	}
	
	public double getStartTime()
	{
		return (double) job.getStartTime();
	}

	
	public void setIdleTime(double idleTime)
	{
		this.idleTime += idleTime;
	}
	
	public double getIdleTime()
	{
		return idleTime;
	}
	public void putLoadSequence(String name)
	{
		loadSequence.add(name);
	}
	
	public String getLoadSequence()
	{
		return loadSequence.remove(0);
	}
	
	public String getWorkerName()
	{
		return workerName;
	}
	
	public void setWorkerName(String workerName)
	{
		this.workerName = workerName;
	}
	
	public double getWorkerLoad()
	{
		return workerLoad;
	}
	
	public void addWorkerLoad(double workerLoad)
	{
		this.workerLoad += workerLoad;
	}
	
	public void setRank(int workerRank)
	{
		this.workerRank = workerRank;
	}
	
	public int getRank()
	{
		return workerRank;
	}
	
	public void putQueue(ProcessControlBlock pcb)
	{
		workerQ.add(pcb);
	}
	
	public ProcessControlBlock getQueue()
	{
		return workerQ.remove(0);
	}
	
	public Object getJob()
	{
		return job;
	}
	
	public void setJob(ProcessControlBlock job)
	{
		this.job = job;
	}
	
	public double getJobTime()
	{
		return job.getJobTime();
	}
	
	public boolean isEmpty()
	{
		return workerQ.isEmpty();
	}
	
	public void setJobsLoaded(int jobsLoaded)
	{
		this.jobsLoaded = jobsLoaded;
	}
	
	public int getJobsLoaded()
	{
		return jobsLoaded;
	}
	
	public double getArrivalTime()
	{
		return job.getArrivalTime();
	}
	
	public double getDueTime()
	{
		return job.getDueTime();
	}
	
	public String getJobName()
	{
		return job.getJobName();
	}
	
	public List<ProcessControlBlock> getWorkerQ()
	{
		return workerQ;
	}
	
	public void setBusy(boolean busy)
	{
		this.busy = busy;
	}
	
	public boolean isBusy()
	{
		return busy;
	}
}