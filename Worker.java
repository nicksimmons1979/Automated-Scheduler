import java.util.ArrayList;
import java.util.List;

public class Worker
{
	private String workerName;
	private double workerLoad;
	private int workerRank;
	private PriorityQueue workerQ;
	private ProcessControlBlock job;
	private int jobsLoaded;
	private double jobLoadTime;
	public List<String> loadSequence = new ArrayList<String>();

	public Worker(String workerName, int workerRank)
	{
		this.workerName = workerName;
		this.workerRank = workerRank;
		this.workerQ = new PriorityQueue();
		this.job = null;
		this.jobsLoaded = 0;
		this.jobLoadTime = 0;
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
	
	public void putQueue(Object o, float key)
	{
		workerQ.putQueue(o, key);
	}
	
	public Object getQueue()
	{
		return workerQ.getQueue();
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
	
	public void setJobLoadTime(double jobLoadTime)
	{
		this.jobLoadTime = jobLoadTime;
	}
	
	public double getJobLoadTime()
	{
		return jobLoadTime;
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
	
	public PriorityQueue getWorkerQ()
	{
		return workerQ;
	}
	

}