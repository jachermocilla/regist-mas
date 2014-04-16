 package jach.msthesis.scheduler.util;

import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.Section;
import jach.msthesis.registration.model.SectionAssignment;
import jach.msthesis.registration.model.WriteIn;
import jach.msthesis.scheduler.IScheduler;
import jach.msthesis.scheduler.Schedule;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Class for exporting Form 5
 * 
 * @author jach
 * 
 */

public class RegistScheduleExporter implements IExporter {

	/**
	 * The schedule
	 */
	private IScheduler scheduler;

	/**
	 * Filename
	 */
	private String fname;

	/**
	 * The year
	 */
	private String year;

	/**
	 * The semester
	 */
	private String sem;

	/**
	 * Constructor
	 * 
	 * @param scheduler
	 * @param fname
	 * @param year
	 * @param sem
	 */
	public RegistScheduleExporter(IScheduler scheduler, String fname,
			String year, String sem) {
		this.scheduler = scheduler;
		this.fname = fname;
		this.year = year;
		this.sem = sem;
	}

	/**
	 * Export code
	 */
	public void export() {
		//Get all students
		List students = new Vector(scheduler.getWriteIns().keySet());

		//Sort according to the student number
		Collections.sort(students);
		try {
			FileWriter fw = new FileWriter(fname);
			PrintWriter out = new PrintWriter(fw);

			//For each student
			for (Iterator ite = students.iterator(); ite.hasNext();) {
				String stdNum = (String) ite.next();

				//Get the form 5
				Schedule sked = (Schedule) scheduler.getSchedules().get(stdNum);
				
				
				//Get the subjects in the form 5
				Map sectAsss = (Map)sked.getSectionAssignments();

				//This student was not accommodated in any subject
				//So just write it out with empty section field
				if (sectAsss.size() == 0) {
					WriteIn wrin = (WriteIn) scheduler.getWriteIns().get(stdNum);
					Iterator ite5 = wrin.getSubjects().iterator();
					while (ite5.hasNext()) {
						ISubject s = (ISubject) ite5.next();
						out.println(stdNum + "," + year + "," + sem + ","
								+ s.getName() + "," + "" + ","
								+ s.getUnitCredit() + ",REGD,1,0,0");
					}
				}

				
				WriteIn wrin = (WriteIn) scheduler.getWriteIns().get(stdNum);
				for (Iterator ite2 = sectAsss.keySet().iterator(); ite2.hasNext();) {
					SectionAssignment sectAss = (SectionAssignment)sectAsss.get((String)ite2.next());
					ISection sect = sectAss.getSection();
					ISubject sub = (ISubject) wrin.getSubjectInfo(sect.getSubject().getName());
					out.println(stdNum + "," + year + "," + sem + ","
									+ sect.getSubject().getName() + ","
									+ sect.getSectionName() + ","
									+ sub.getUnitCredit() + ",REGD,1,0,0");
				}
			}
			out.close();
			fw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
}
