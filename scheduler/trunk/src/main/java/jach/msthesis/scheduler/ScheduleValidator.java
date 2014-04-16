package jach.msthesis.scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;


import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.DefaultSubject;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.IWriteIn;
import jach.msthesis.registration.model.Section;
import jach.msthesis.registration.model.SectionAssignment;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.statistics.Statistics;
import jach.msthesis.scheduler.util.RegistScheduleExporter;

/**
 * This class parses the result of scheduler to provide statistics and
 * validation data for comparison with other assignment algorithms.
 * 
 * @author jach
 * @version $Id: ScheduleValidator.java 1076 2008-10-26 12:23:25Z jach $
 */

public class ScheduleValidator extends PrototypeScheduler {

	/**
	 * Constructor
	 */
	public ScheduleValidator(IOffering offering, Map writeins) {
		super(offering, writeins);
	}

	/**
	 * Loads form 5 data given file name
	 */
	public void loadForm5(String path) {
		BufferedReader in = null;
		String line;
		String[] tokens = null;
		Map nowritein = new java.util.Hashtable();

		try {
			in = new BufferedReader(new FileReader(path));
			line = in.readLine();
			while (line != null) {
				if (!line.startsWith("#")) {

					tokens = line.split(",");
					String stdNum = tokens[0];
					// for dudai's data: String sname = tokens[1];
					String sname = tokens[3];
					// for dudai's String lab = tokens[2];
					String lab = tokens[4];

					// Get students schedule
					Schedule sked = (Schedule) this.schedules.get(stdNum);
					// Create the schedule object if it does not exist yet
					if (sked == null) {
						sked = new Schedule();
					}
					schedules.put(stdNum, sked);

					// Skip, not assigned a section
					if (lab.equals("")) {
						line = in.readLine();
						continue;
					}

					// a lab!
					tokens = lab.split("-");
					String lec = tokens[0];

					// split pe2
					String tmp[];
					if (sname.equals("PE 2")) {
						tmp = lab.split("-");
						sname = sname + "-" + tmp[0];
						if (tmp.length > 1)
							lec = tmp[1];
					}

					// Get the lecture section
					ISection lectSect = offering.getLecSection(sname, lec);
					// Create subject section
					if (lectSect == null) {
						lectSect = new Section(new DefaultSubject(sname), lec);
					}

					// check if there is writein information
					IWriteIn wi = (IWriteIn) writeins.get(stdNum);
					if (wi == null) {
						System.out.println(stdNum + " has no writein!");
						nowritein.put(stdNum, stdNum);
						line = in.readLine();
						continue;
					}

					sked.setWriteIn(wi);
					// add section to schedule
					//sked.addSection(new SkedNode(lectSect));
					SectionAssignment sectAss = new SectionAssignment();
					sectAss.setLecture(lectSect);
					

					// Add student to classlist
					String k = sname + ":" + lec;
					Classlist cl = (Classlist) classlists.get(k);
					if (cl == null) {
						cl = new Classlist(lectSect,null);
					}
					cl.addStudent(stdNum);

					//We have labs
					if ((tokens.length == 2) && (!sname.startsWith("PE 2"))) {
						ISection labSect = offering.getLabSection(sname, lab);
						if (labSect == null) {
							labSect = new Section(new DefaultSubject(sname), lab);
						}

						// add lab section to schedule
						//sked.addSection(new SkedNode(sect));
						sectAss.setLab(labSect);
						

						// Add student to lab classlist
						k = sname + ":" + lab;
						cl = (Classlist) classlists.get(k);
						if (cl == null) {
							cl = new Classlist(lectSect,labSect);
						}
						cl.setLabSection(labSect);
						cl.addStudent(stdNum);
					}
					sked.addSectionAssignment(sectAss);
					//for backwards compatibility
					sked.addSection(new SkedNode(sectAss.getLecture()));
					if (sectAss.getLab() != null){
						sked.addSection(new SkedNode(sectAss.getLab()));
					}					
					
					classlists.put(k, cl);					
					schedules.put(stdNum, sked);
				}
				line = in.readLine();
			}
			System.out.println("No writein: " + nowritein.size());
			/*
			 * Iterator ite = writeins.keySet().iterator();
			 * while(ite.hasNext()){ String stdNum = (String)ite.next(); //Get
			 * students schedule Schedule sked =
			 * (Schedule)this.schedules.get(stdNum); if (sked == null){ sked =
			 * new Schedule(21); schedules.put(stdNum, sked); } }
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Load form 5 for freshman, whew!
	 * 
	 * @param path
	 */
	public void loadForm5Freshman(String path) {
		BufferedReader in = null;
		String line;
		String[] tokens = null;
		Map nowritein = new java.util.Hashtable();

		try {
			in = new BufferedReader(new FileReader(path));
			line = in.readLine();
			while (line != null) {
				if (!line.startsWith("#")) {
					// System.out.println(line);

					tokens = line.split(",");
					String stdNum = tokens[0];
					String sname = tokens[1];
					// String sname = tokens[3];
					String lab = tokens[2];
					// String lab = tokens[4];

					if (lab.equals("")) {
						Schedule sked = (Schedule) this.schedules.get(stdNum);
						if (sked == null) {
							sked = new Schedule();
						}
						schedules.put(stdNum, sked);
						line = in.readLine();
						continue;
					}

					tokens = lab.split("-");
					String lec = tokens[0];

					String tmp[];
					if (sname.equals("PE 2")) {
						tmp = lab.split("-");
						sname = sname + "-" + tmp[0];
						// System.out.println(stdNum +" "+sname+":"+lec+"
						// <<<<<!");
						if (tmp.length > 1)
							lec = tmp[1];
					}

					ISection sect = offering.getLecSection(sname, lec);

					if (sect == null) {
						sect = new Section(new DefaultSubject(sname), lec);
						// System.out.println(sname+":"+lec+" not in
						// offering!");
					}

					// check if there is writein information
					IWriteIn wi = (IWriteIn) writeins.get(stdNum);

					if (wi == null) {
						// System.out.println(stdNum + " has no writein!");
						nowritein.put(stdNum, stdNum);
						line = in.readLine();
						continue;
					}

					// Get students schedule
					Schedule sked = (Schedule) this.schedules.get(stdNum);
					if (sked == null) {

						sked = new Schedule();
					}

					// add section to schedule
					sked.addSection(new SkedNode(sect));

					// Add student to classlist
					String k = sname + ":" + lec;
					Classlist cl = (Classlist) classlists.get(k);
					if (cl == null) {
						//cl = new Classlist(sect);
					}
					cl.addStudent(stdNum);
					classlists.put(k, cl);

					if ((tokens.length == 2) && (!sname.startsWith("PE 2"))) {
						sect = offering.getLecSection(sname, lab);
						if (sect == null) {
							sect = new Section(new DefaultSubject(sname), lab);
							// System.out.println(sname+":"+lec+" not in
							// offering!");
						}

						// add lab section to schedule
						sked.addSection(new SkedNode(sect));

						// Add student to lab classlist
						k = sname + ":" + lab;
						cl = (Classlist) classlists.get(k);
						if (cl == null) {
							//cl = new Classlist(sect);
						}
						cl.addStudent(stdNum);
						classlists.put(k, cl);
					}
					// Put in schedules map
					schedules.put(stdNum, sked);
				}
				line = in.readLine(); // get next line
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		if (args.length < 3) {
			System.out.println("Regist II Schedule Validator\n");
			System.out
					.println("java -jar scheduler.jar <writeins> <classes> <form5>");
			System.exit(0);
		}

		ScheduleValidator scheduler;
		RegistOffering offering = new RegistOffering(args[1]);
		RegistWriteInSource wis = new RegistWriteInSource(args[0]);

		wis.load();
		offering.load();

		long start = System.currentTimeMillis();
		scheduler = new ScheduleValidator(offering, wis.getWriteIns());
		scheduler.loadForm5(args[2]);
		long stop = System.currentTimeMillis();
		System.out.println("Time in ms: " + (stop - start));
		Statistics stat = new Statistics(scheduler);
		stat.display();
	}
}
