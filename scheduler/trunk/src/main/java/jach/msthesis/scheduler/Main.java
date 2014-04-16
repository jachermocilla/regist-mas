package jach.msthesis.scheduler;


import java.util.Iterator;
import java.util.Random;
import java.util.Set;



import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.regist.RegistSchedulerImproved;
import jach.msthesis.scheduler.regist.RegistSchedulerImprovedMBM2;
import jach.msthesis.scheduler.regist.RegistSchedulerImprovedMBM3;
import jach.msthesis.scheduler.statistics.Statistics;
import jach.msthesis.scheduler.util.RegistScheduleExporter;

/**
 * Main application for scheduling
 * 
 * @author jach
 * @version $Id: Main.java 1076 2008-10-26 12:23:25Z jach $
 */

public class Main {
	public static void main(String args[]){		
		if (args.length < 4){
			System.out.println("Regist II Scheduler\n");
			System.out.println("java -jar scheduler.jar <scheduler index> <writeins> <classes> <form5>");
			System.out.println("Available schedulers: ");
			System.out.println("    [0] Search-based (No backtrack)");
			System.out.println("    [1] Maximal Bipartite Matching 2");
			System.out.println("    [2] Maximal Bipartite Matching 3");
			System.out.println();
			System.exit(0);
		}

		/*
		 * Load the data course offering and write-ins 
		 */
		RegistOffering offering=new RegistOffering(args[2]);
		RegistWriteInSource wis = new RegistWriteInSource(args[1]);		
		wis.load();
		offering.load();
		
		/*
		 * Placeholder for the scheduler to use
		 */
		IScheduler scheduler;
		
		/*
		 * Initiliaze array of available schedulers
		 */
		IScheduler schedulers[]=new IScheduler[3];		
		schedulers[0]=new RegistSchedulerImproved(offering,wis.getWriteIns());
		schedulers[1]=new RegistSchedulerImprovedMBM2(offering,wis.getWriteIns());
		schedulers[2]=new RegistSchedulerImprovedMBM3(offering,wis.getWriteIns());
		
		/*
		 * Set scheduler specified as parameter
		 */
		scheduler = schedulers[Integer.parseInt(args[0])];
		
		/*
		 * Do the secitioning
		 */
		long start = System.currentTimeMillis();
		scheduler.assign();		
		long stop = System.currentTimeMillis();
		System.out.println("Time in ms: " + (stop - start));
		
		/*
		 * Export the result in an external file
		 */
		RegistScheduleExporter exporter = new RegistScheduleExporter(scheduler,
				args[3],"2002","SECOND");		
		exporter.export();
		
		/*
		 * Display some statistics
		 */
		Statistics stat=new Statistics(scheduler);
		stat.display();
		
	}
}
