//*****************************************************************************************************************************
// FCFS.java - June 22 2017
// Nick Simmons
// Job Scheduler
// Uses shortest job first with load balanced minimal makespan
//*****************************************************************************************************************************

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.io.*;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class FCFS
{	
	// global variables for JFrame communication, bad method, investigate return values for action listeners
	static int workerCount;  // how many workers are available 
	static boolean wait = true; // flag to wait for gui inputs
	static int algorithm = 1; // default algorithm, shortest job first
	static String workerFile = null; // name for loaded worker file
	static String jobFile = null; // name for loaded job file
	static boolean lateAvoidance = false; // try to automate late job rescheduling
	static double criticalSafetyFactory = 2.0; // default safety buffer, 2x job length
	static List<String> workerNames = new ArrayList<String>();  // worker identifiers
	static List<Integer> workerRanks = new ArrayList<Integer>(); // worker ability levels

	@SuppressWarnings("deprecation")
	public static void main(String args[]) throws IOException
	{	
		// constants defining simulation characteristics
		final double SIMULATION_TIME = 2000; // hrs, 1 year
		final double SIMULATION_INCREMENT = 0.01; // hrs
		DecimalFormat fmt = new DecimalFormat("0.##"); // statistics output format
		
		// create queues for holding jobs on arrival and worker queues
		ProcessControlBlock pcb = null; // job storage for file->queue
				
		// for computations of statistics
		List<ProcessControlBlock> unassignedJobs = new ArrayList<ProcessControlBlock>();
		List<ProcessControlBlock> missedJobs = new ArrayList<ProcessControlBlock>();
		List<ProcessControlBlock> completedJobs = new ArrayList<ProcessControlBlock>();
		List<Worker> alternateWorkers = new ArrayList<Worker>();
		double averageTurnAroundTime = 0;		
		double averageWaitingTime = 0;

		// worker details
		int workerID = 0; // identifier for simulation loop				
		String info = ""; // stores summary statistics for results pane
		
		// Create frame and dialog to results
		JFrame frameScheduler = new JFrame ("Results");
		frameScheduler.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		JTextArea ta = new JTextArea (50, 30);
		
		// Create frame to select scheduler and employees
		JFrame frame = new JFrame ("Automated Scheduler v2.0");
		frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(new SchedulerCombo());
		frame.pack();
		frame.setVisible(true);	
		
		// wait until gui inputs
		while (wait)
		{
			try 
			{
				Thread.sleep(100); // pause for 100ms allow check of "wait"
			}
			catch (InterruptedException e)
			{
				
			}
		}
		
		// read in workers from file
		CsvToJavaObject workerList = new CsvToJavaObject();

		// ensure source file is correctly formated 
		try
		{
			workerList.convertWorkersCsvToJava(workerFile);
		}
        
		// complain otherwise
		catch (NumberFormatException e)
		{
			JFrame frameError = new JFrame ("ERROR");
			frameError.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			JTextArea taError = new JTextArea (20, 30);
			frameError.getContentPane().add(taError);
			frameError.pack();
			frameError.setVisible(true);	
			taError.setText("Problem with worker file. Check worker ranks.");	
		}
		
		// complain otherwise
		catch (ArrayIndexOutOfBoundsException e)
		{
			JFrame frameError = new JFrame ("ERROR");
			frameError.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			JTextArea taError = new JTextArea (20, 30);
			frameError.getContentPane().add(taError);
			frameError.pack();
			frameError.setVisible(true);	
			taError.setText("Problem with worker file. Check worker ranks.");	
		}
        
        // reading data from a csv file and convert to java object, store some statistics
		CsvToJavaObject jobList = new CsvToJavaObject();

		// ensure source file is correctly formated
		try
		{
			jobList.convertJobsCsvToJava(jobFile);
		}
       
		// complain otherwise
		catch (NumberFormatException e)
		{
			JFrame frameError = new JFrame ("ERROR");
			frameError.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			JTextArea taError = new JTextArea (20, 30);
			frameError.getContentPane().add(taError);
			frameError.pack();
			frameError.setVisible(true);	
			taError.setText("Problem with job file. Check formatting.");	
		}
		
		// complain otherwise
		catch (ArrayIndexOutOfBoundsException e)
		{
			JFrame frameError = new JFrame ("ERROR");
			frameError.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			JTextArea taError = new JTextArea (20, 30);
			frameError.getContentPane().add(taError);
			frameError.pack();
			frameError.setVisible(true);	
			taError.setText("Problem with job file. Check formatting.");	
		}
        		
		// initialize workers and worker queue
       	workerCount = workerNames.size();
       	Worker[] workers = new Worker[workerCount];
        	
       	for (int i = 0; i < workerCount; i++)
       	{
       		workers[i] = new Worker(workerNames.remove(0), workerRanks.remove(0));		
       	}        
        
		// ****************************************************************************
		//  scheduler section 	
		// ****************************************************************************
		try
		{
			// create ordered worker queues from jobList
			while (!jobList.isEmpty())
			{					
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
       		
				// worker with least amount of work
				workerID = smallestIndex;
				System.out.println("worker selected:"+ (workerID + 1));
       		
				// setup individual worker queues
				// pull job from master queue for assignment
				pcb = jobList.getNextJob();	
       		
				// check the job for forced assignment
				if (!pcb.getForcedTo().contentEquals("any"))
				{	
					// determine eligible workerID
					for (int i = 0; i < workerCount; i++)
					{
						System.out.println("checking " + workers[i].getWorkerName());
						if (pcb.getForcedTo().contains((workers[i].getWorkerName())))
						{
							alternateWorkers.add(workers[i]);
							System.out.println("worker " + workers[i].getWorkerName() + " viable for forced");
						}
					}		
					
					System.out.println("\nalternate worker size: " + alternateWorkers.size());
	       			
					// there is a capable worker in the selection pool
					if (!alternateWorkers.isEmpty())
					{
						// find worker with least amount of work
						smallest = alternateWorkers.get(0).getWorkerLoad();
						largest = alternateWorkers.get(0).getWorkerLoad();
						smallestIndex = 0;
   					
						for(int j = 0; j < alternateWorkers.size(); j++)
						{
							if(alternateWorkers.get(j).getWorkerLoad() > largest)
							{
								largest = alternateWorkers.get(j).getWorkerLoad();
							}

							else if (alternateWorkers.get(j).getWorkerLoad() < smallest)
							{
								smallest = alternateWorkers.get(j).getWorkerLoad();
								smallestIndex = j;
							}
						}
   	       		
						// worker with least amount of work
						workerID = smallestIndex;
	       		
						// find which alternate worker corresponds to the correct worker in main array
						for (int i = 0; i < workerCount; i++)
						{
							if (workers[i].getWorkerName() == alternateWorkers.get(workerID).getWorkerName())
							{
								workerID = i;
								break;
							}
						}
						
						System.out.println(workers[workerID].getWorkerName() + " selected for forced job " + pcb.getJobName());
   	       		
						// assign job 
						pcb.setWorkerName(workers[workerID].getWorkerName()); // bind worker name to job for history
						workers[workerID].putQueue(pcb); // put job from master queue to worker queue
						workers[workerID].setJobsLoaded(workers[workerID].getJobsLoaded() + 1);
						workers[workerID].addWorkerLoad(pcb.getJobTime());
						System.out.println("alternative worker " + workers[workerID].getWorkerName() + " found for forced job " + pcb.getJobName());
						pcb = null;
					
						// empty out alternateWorker queue, reset for next iteration
						while (!alternateWorkers.isEmpty())
						{
							alternateWorkers.remove(0);
						}
					}
				}

				// typical assignment
				else
				{
					// Is worker is capable of processing job?
					if (workers[workerID].getRank() >= pcb.getJobRank())
					{
						pcb.setWorkerName(workers[workerID].getWorkerName()); // bind worker name to job for history
						workers[workerID].putQueue(pcb); // put job from master queue to worker queue
						workers[workerID].setJobsLoaded(workers[workerID].getJobsLoaded() + 1);
						workers[workerID].addWorkerLoad(pcb.getJobTime());
						pcb = null; 
					}		
       		
					// lowest queue size worker not capable, find another worker
					else
					{
						System.out.println("problem with job " + pcb.getJobName() + " rank " + pcb.getJobRank());
						// create list of capable workers
						for (int i = 0; i < workerCount; i++)
						{
							System.out.println("checking " + workers[i].getWorkerName() + " rank " + workers[i].getRank());
							if (workers[i].getRank() >= pcb.getJobRank())
							{
								alternateWorkers.add(workers[i]);
								System.out.println("worker " + workers[i].getWorkerName() + " selected");
							}
						}
						System.out.println("\nalternate worker size: " + alternateWorkers.size());
       			
						// there is a capable worker in the selection pool
						if (!alternateWorkers.isEmpty())
						{
							// find worker with least amount of work
							smallest = alternateWorkers.get(0).getWorkerLoad();
							largest = alternateWorkers.get(0).getWorkerLoad();
							smallestIndex = 0;
       					
							for(int j = 0; j < alternateWorkers.size(); j++)
							{
								if(alternateWorkers.get(j).getWorkerLoad() > largest)
								{
									largest = alternateWorkers.get(j).getWorkerLoad();
								}

								else if (alternateWorkers.get(j).getWorkerLoad() < smallest)
								{
									smallest = alternateWorkers.get(j).getWorkerLoad();
									smallestIndex = j;
								}
							}
       	       		
							// worker with least amount of work
							workerID = smallestIndex;
							System.out.println(workers[workerID].getWorkerName() + " selected for job " + pcb.getJobName());	
       	       		
							// find which alternate worker corresponds to the correct worker in main array
							for (int i = 0; i < workerCount; i++)
							{
								if (workers[i].getWorkerName() == alternateWorkers.get(workerID).getWorkerName())
								{
									workerID = i;
									break;
								}
							}
       	       		
							// assign job 
							pcb.setWorkerName(workers[workerID].getWorkerName()); // bind worker name to job for history
							workers[workerID].putQueue(pcb); // put job from master queue to worker queue
							workers[workerID].setJobsLoaded(workers[workerID].getJobsLoaded() + 1);
							workers[workerID].addWorkerLoad(pcb.getJobTime());
							System.out.println("alternative worker " + workers[workerID].getWorkerName() + " found for job " + pcb.getJobName());
							pcb = null;
   					
							// empty out alternateWorker queue, reset for next iteration
							while (!alternateWorkers.isEmpty())
							{
								alternateWorkers.remove(0);
							}
						}
       			
						// no capable workers available for job
						else
						{
							System.out.println(pcb.getJobName() + " ditched, failure to assign"); 
							unassignedJobs.add(pcb); // record lack of assignment for later use
							pcb = null; // ditch job for now
						}
					} 		
				}
			}
		}
       	
       	catch(ArrayIndexOutOfBoundsException e)
		{
       		
		}
 
       	// ****************************************************************************      	
       	// simulator section 
       	// ****************************************************************************
       	
		// execute worker queues
       	for (double currentTime = 0; currentTime < SIMULATION_TIME; currentTime = currentTime + SIMULATION_INCREMENT)
		{
			// check worker queues for critical ratio of CRITICAL_SAFETY_FACTOR (hours left before due / job time)
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
				if (!workers[i].isBusy())
				{
					// load a job to the worker, if one is available
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
							workers[i].getLoadSequence().add((ProcessControlBlock)workers[i].getJob());
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
			averageWaitingTime += (completedJobs.get(i).getStartTime() - completedJobs.get(i).getArrivalTime());
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
		
		info += ("\nJobs loaded from file:" + jobList.getItemsFromFile());
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
			info += ("\n" + unassignedJobs.get(i).getJobName() + " unassigned no capable worker");
		}
		
		// display results frame
		frameScheduler.getContentPane().add(ta);
		frameScheduler.pack();
		frameScheduler.setVisible(true);		
		ta.setText(info);
		
		// warn user of excel file write delay
		JFrame frameExcel = new JFrame ("Saving Files");
		frameExcel.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
		JTextArea taExcel = new JTextArea (20, 30);
		frameExcel.getContentPane().add(taExcel);
		frameExcel.pack();
		frameExcel.setVisible(true);	
		taExcel.setText("Writing production plan...\nPlease Wait.");
	
		// dump load list to csv file
		try
		{
			PrintWriter out = new PrintWriter("loadlist_"+ workerFile + "_" + jobFile);
			for (int i = 0; i < workerCount; i++)
			{
				out.print(workers[i].getWorkerName() + ","); // dump worker name cell

				for (int j = 0; j < workers[i].getLoadSequence().size(); j++)
				{
					out.print(workers[i].getLoadSequence().get(j).getJobName() + ","); // dump worker i job list					
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

		// dump production plan excel
		try
		{
			// create a new file
			FileOutputStream out = new FileOutputStream("production_plan_" + workerFile + "_" + jobFile + ".xlsx");
			// create a new workbook
			XSSFWorkbook wb = new XSSFWorkbook();
			// create a new sheet
			XSSFSheet s = (XSSFSheet) wb.createSheet();
			// declare a row object reference
			XSSFRow r = null;
			// declare a cell object reference
			XSSFCell c = null;
			// set the sheet name in Unicode
			wb.setSheetName(0, "Production Plan " + jobFile);
			// create cell data format
			XSSFDataFormat df = wb.createDataFormat();
			// create 2 fonts objects
			XSSFFont f = wb.createFont();
			//set font 1 to 12 point type
			f.setFontHeightInPoints((short) 12);
			//make it black
			f.setColor((short)0x0);
			// set font to Calibri
			f.setFontName("Calibri");			

			// create cell styles, 0 = blank cell, [1,workerCount] = worker colours
			XSSFCellStyle cellStyles[] = new XSSFCellStyle[workerCount + 1];	
			for (int i = 0; i < workerCount + 1; i++)
			{
				 cellStyles[i] = wb.createCellStyle();
	
			}

			// set cell highlight colours, +14 provides the nice visual range, no black or too dark colours
			short colour = (short)(IndexedColors.LEMON_CHIFFON.getIndex() + 14);
			
			// set cell style attributes, top bottom borders, for filled cells
			for (int i = 0; i < workerCount; i++)
			{
				cellStyles[i].setFont(f);
				cellStyles[i].setDataFormat(df.getFormat("#,##0.0"));
				cellStyles[i].setBorderBottom(CellStyle.BORDER_THIN);
				cellStyles[i].setBottomBorderColor(IndexedColors.BLACK.getIndex());
				cellStyles[i].setBorderLeft(CellStyle.BORDER_NONE);
				cellStyles[i].setLeftBorderColor(IndexedColors.BLACK.getIndex());
				cellStyles[i].setBorderRight(CellStyle.BORDER_NONE);
				cellStyles[i].setRightBorderColor(IndexedColors.BLACK.getIndex());
				cellStyles[i].setBorderTop(CellStyle.BORDER_THIN);
				cellStyles[i].setTopBorderColor(IndexedColors.BLACK.getIndex());
				cellStyles[i].setFillForegroundColor(colour);
				cellStyles[i].setFillPattern(CellStyle.SOLID_FOREGROUND);
				colour++; // increment colours for each worker
			}
	    
			// total number of columns to create
			short maxLoad = (int)SIMULATION_TIME * 2; // 2 -> half hour cells
		
			// create a sheet			
			for (int rownum = 0; rownum < workerCount+10; rownum++)
			{
				// 	create a row
				r = (XSSFRow) ((org.apache.poi.ss.usermodel.Sheet) s).createRow(rownum);
	
				// create maxLoad+1 cells per row
				for (int cellnum = 0; cellnum < maxLoad; cellnum++)
				{
					// create a string cell
					c = r.createCell(cellnum);
				}
			}
		
			cellStyles[workerCount].setFont(f);
			
			// put start time
			r = s.getRow(0);
			c = r.getCell(0);
			c.setCellType(CellType.STRING);
			c.setCellValue(LocalDateTime.now().toString());
			c.setCellStyle(cellStyles[workerCount]);

			// put in hours
			int week = 0;
			boolean flag = true; // creates a flip flop conditional
			for (int i = 1; i < maxLoad; i++)
			{
				r = s.getRow(0);
				c = r.getCell(i);
				c.setCellType(CellType.NUMERIC);
				c.setCellStyle(cellStyles[workerCount]);
				
				// store hour value to cell
				c.setCellValue((i-1)/2);
				// c.setCellValue(LocalDateTime.now().getHour());
				
				// store bi-weekly marker
				if (((i-1)/2) % 75 == 0)
				{
					c.setCellValue("Week " + week);
					if (flag == false)
					{
						flag = true;
						week +=2 ;
					}
					
					else
					{
						flag = false;
					}
				}
			}
		
			// put in worker names
			for (int i = 0; i < workerCount; i++)
			{
				r = s.getRow(i+1);
				c = r.getCell(0);
				c.setCellType(CellType.STRING);
				c.setCellStyle(cellStyles[i]);
				c.setCellValue(workers[i].getWorkerName());
			}

			// put in jobs
			for (int i = 0; i < workerCount; i++)
			{
				int startCell = 0;
				int finishCell = 0;
				double startCellTemp = 0;
				double finishCellTemp = 0;
				
				for (int j = 0; j < workers[i].getLoadSequence().size(); j++) 
				{
					// compute starting, finish cell, work in half hour units
					startCellTemp = 2 * (workers[i].getLoadSequence().get(j).getStartTime() + 1); // +1 to account for worker column
					finishCellTemp = 2 * (workers[i].getLoadSequence().get(j).getJobTime()); // extra factor of 2 to double cells
					startCellTemp = Math.round(startCellTemp);
					finishCellTemp = Math.round(finishCellTemp);
					finishCellTemp += startCellTemp;

					startCell = (int) startCellTemp;
					finishCell = (int) finishCellTemp;

					// write start cell
					r = s.getRow(i+1);
					c = r.getCell(startCell);
					c.setCellType(CellType.STRING);
					c.setCellValue(workers[i].getLoadSequence().get(j).getJobName());	
					cellStyles[i].setBorderLeft(CellStyle.BORDER_THIN);
					cellStyles[i].setBorderRight(CellStyle.BORDER_NONE);
					cellStyles[i].setBorderTop(CellStyle.BORDER_THIN);
					cellStyles[i].setBorderBottom(CellStyle.BORDER_THIN);
					c.setCellStyle(cellStyles[i]);
					
					// write center fill in cells
					for (int l=startCell+1; l < finishCell; l++)
					{
						c = r.getCell(l);
						c.setCellType(CellType.STRING);
						cellStyles[i].setBorderLeft(CellStyle.BORDER_NONE);
						cellStyles[i].setBorderRight(CellStyle.BORDER_NONE);

						c.setCellStyle(cellStyles[i]);
					}
					
					// write finish cell
					c = r.getCell(finishCell);
					c.setCellType(CellType.STRING);
					c.setCellStyle(cellStyles[i]);
					
					cellStyles[i].setBorderLeft(CellStyle.BORDER_THIN);
					cellStyles[i].setBorderRight(CellStyle.BORDER_THIN);

					c.setCellStyle(cellStyles[i]);
				}
			}		
		
			// put in stats
			r = s.getRow(workerCount+1);
			c = r.getCell(0);
			c.setCellValue(info); 
	
			cellStyles[workerCount].setWrapText(true);
			cellStyles[workerCount].setAlignment(CellStyle.ALIGN_CENTER);
			cellStyles[workerCount].setVerticalAlignment(CellStyle.VERTICAL_TOP);
			c.setCellStyle(cellStyles[workerCount]);
			
			// creates tall cell for statistics
			r.setHeight((short)100000);
			s.autoSizeColumn(0);

			// write the workbook to the output stream
			wb.write(out);
			out.close();
			wb.close();
			
			System.out.println("file write done");
			taExcel.setText("Production plan finished.");
		}
		
		// problem writing production plan
		catch(FileNotFoundException e)
		{
			JFrame frameError = new JFrame ("ERROR");
			frameError.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
			JTextArea taError = new JTextArea (20, 30);
			frameError.getContentPane().add(taError);
			frameError.pack();
			frameError.setVisible(true);	
			taError.setText("Production plan not written. Is the file already open?");	 
		}		
	}	
}
