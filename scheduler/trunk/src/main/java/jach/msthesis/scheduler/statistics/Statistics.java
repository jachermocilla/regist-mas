package jach.msthesis.scheduler.statistics;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.IForm5;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.IWriteIn;
import jach.msthesis.registration.model.Section;
import jach.msthesis.registration.model.WriteIn;
import jach.msthesis.scheduler.IScheduler;
import jach.msthesis.scheduler.Schedule;


/**
 * This class displays statistics for a given scheduler. 
 * 
 * @author jach
 * @version $Id: Statistics.java 1076 2008-10-26 12:23:25Z jach $
 *
 */


public class Statistics implements IStatistics {
	
	/**
	 * Reference to the scheduler
	 */
	private IScheduler scheduler;
	
	/**
	 * Map: <subject:section, Classlist>
	 */	
	private Map classlists;
	
	/**
	 * Map: <stdnum, Schedule>
	 */
	private Map schedules;
	
	/**
	 * Map: <stdnum, WriteIn>
	 */
	private Map writeins;
	
	/**
	 * Printstream where output will be riderected
	 */
	private PrintStream out = System.out;
	
	/**
	 * Constructor
	 */
	public Statistics(IScheduler scheduler){
		this.scheduler = scheduler;
		this.classlists = scheduler.getClasslists();
		this.schedules = scheduler.getSchedules();
		this.writeins = scheduler.getWriteIns();
	}

	/**
	 * Sets the output stream
	 */
	public void setPrintStream(PrintStream out){
		this.out=out;
	}
	
	/**
	 * Returns the percentage load given the student number
	 */
	public double getPercentageLoad(String stdNum){
		IWriteIn writein = (IWriteIn)writeins.get(stdNum);
		Schedule form5 = (Schedule)schedules.get(stdNum);
		double actual=0;
	
		//We consider only those with writein and schedule
		if ((writein != null) && (form5 != null)){
			
			Iterator iter = writein.getSubjects().iterator();
			while(iter.hasNext()){
				ISubject sub = (ISubject)iter.next();
				actual += sub.getUnitCredit();
			}
			
			//Sometimes there are more subjects in the writein than
			//the allowed so we bound actual to allowed units
			if (actual > writein.getUnitsAllowed())
				actual = writein.getUnitsAllowed();
			
			double pl;

			//Some have zero units like PE 
			if (actual > 0){
				pl = form5.getUnitsObtained()/actual;
			}else if (form5.getSectionAssignments().size() != 0){
				pl = 1;
			}else{
				pl = 0;
			}
			return pl;
		}
		//return -1 if we cannot get the percentage load because
		//form5 or writein does not exist
		return -1;
	}
	
	/**
	 * Returns the average percentage load 
	 */
	public double getAveragePercentageLoad() {
		//Get all writeins
		Iterator ite = writeins.keySet().iterator();
		double sumPL=0;
		double pl=0;
		int count=0;
		while (ite.hasNext()){
			String stdNum = (String)ite.next();
			pl = getPercentageLoad(stdNum);
			if (pl != -1){
				sumPL += pl;
				count++;
			}
		}
		return (sumPL/count);
		//return (sumPL/writeins.size());
		//return (sumPL/schedules.size());
	}
	
	/**
	 * Returns the number of student with full load
	 */
	public int getFullLoadCount() {
		Iterator ite = writeins.keySet().iterator();
		double pl=0;
		int count=0;
		while (ite.hasNext()){
			String stdNum = (String)ite.next();
			pl = getPercentageLoad(stdNum);
			if (pl == 1)
				count++;
		}
		return count;
	}

	/**
	 * Returns the number of students who are underloaded
	 */
	public int getUnderloadCount() {
		Iterator ite = writeins.keySet().iterator();
		double pl=0;
		int count=0;
		while (ite.hasNext()){
			String stdNum = (String)ite.next();
			pl = getPercentageLoad(stdNum);
			//if (pl < 0.8333 && pl > 0)
			if (pl < 1 && pl > 0)
				count++;
		}
		return count;
	}
			
	/**
	 * Returns the number of students who are overloaded
	 */
	public int getOverloadCount(){
		Iterator ite = writeins.keySet().iterator();
		double pl=0;
		int count=0;
		while (ite.hasNext()){
			String stdNum = (String)ite.next();
			pl = getPercentageLoad(stdNum);
			if (pl > 1)
				count++;
		}
		return count;
	}
	
	/**
	 * Returns the number of students with zero load
	 */ 
	public int getZeroLoadCount(){
		Iterator ite = writeins.keySet().iterator();
		double pl=0;
		int count=0;
		while (ite.hasNext()){
			String stdNum = (String)ite.next();
			pl = getPercentageLoad(stdNum);
			if (pl <= 0)
				count++;
		}
		return count;
	}
	
	/**
	 * Returns the percentage class size 
	 */
	public double getPercentageClassSize(String subjectColonSection){
		double allowed;
		double assigned;
		Classlist cl = (Classlist)classlists.get(subjectColonSection);
		ISection section = cl.getSection();
		allowed=section.getClassSize();
		assigned=cl.getStudents().size();
		//System.out.println(subjectColonSection+","+allowed+","+assigned);
		if (allowed == 0){
			return 1;
		}else{
			return (assigned/allowed);
		}
	}
	
	/**
	 * Returns the average percentage class size 
	 */
	public double getAveragePercentageClassSize(){
		Set subjectColonSectionSet = classlists.keySet();
		double sumPCS=0;
		Iterator ite = subjectColonSectionSet.iterator();
		while (ite.hasNext()){			
			String subjectColonSection = (String)ite.next();
			sumPCS += getPercentageClassSize(subjectColonSection);			
		}		
		return (sumPCS/subjectColonSectionSet.size());	
	}
	
	/**
	 * Returns the number of writeins
	 */
	public int getWriteInCount(){
		return writeins.size();
	}
	
	/**
	 * Returns the number of schedules
	 */
	public int getSchedulesCount(){
		return schedules.size();
	}
	
	/**
	 * Returns the number of schedules with writein
	 */
	public int getWriteInWithScheduleCount(){
		Iterator ite = writeins.keySet().iterator();
		IForm5 form5;
		int count=0;
		int nosked=0;
		while(ite.hasNext()){
			String stdNum = (String)ite.next();
			WriteIn wri = (WriteIn)writeins.get(stdNum);
			form5 = (IForm5)schedules.get(stdNum);
			if (form5 != null){
				//if (form5.getUnitsObtained(wri) != 0)
					count++;
			}else{
				System.out.println(stdNum+" has no Form 5!");
				nosked++;
			}
		}
		//System.out.println("No Sked: "+nosked);
		return count;
	}
	
	
	/**
	 * Returns the number of schedules with writein
	 */
	public int getScheduleWithWriteInCount(){
		Iterator ite = schedules.keySet().iterator();
		IWriteIn wi;		
		int count=0;
		while(ite.hasNext()){
			String stdNum = (String)ite.next();
			wi = (IWriteIn)writeins.get(stdNum);
			if (wi != null)
				count++;
		}
		return count;
	}	
	
	/**
	 * Returns the number of slots available for subject
	 * @param subject
	 * @return
	 */
	public int getSlotCount(String subject){
		return scheduler.getOffering().getSlotCount(subject);
	}
	
	/**
	 * Returns the number of students in need of subject
	 * @param subject
	 * @return
	 */
	public int getDemandCount(String subject){
		List l = (List)scheduler.getDemandMap().get(subject);
		return l.size();
	}

	/**
	 * Returns the number of students accommodated
	 * @param subject
	 * @return
	 */
	public int getAccommodatedCount(String subject){
		int accom=0;
		Map classlists = scheduler.getClasslists();
		List lectSections = scheduler.getOffering().getLecSections(subject);
		for(Iterator ite= lectSections.iterator();ite.hasNext();){
			Section lecSection = (Section)ite.next();
			Classlist cl = (Classlist)classlists.get(subject+":"+lecSection.getSectionName());
			if (cl!=null){
				accom+=cl.getStudents().size();
			}
		
			List labSections = scheduler.getOffering().getLabSections(subject,lecSection.getSectionName());
			for(Iterator ite2= labSections.iterator();ite2.hasNext();){
				Section labSection = (Section)ite2.next();
				cl = (Classlist)classlists.get(subject+":"+labSection.getSectionName());
				if (cl!=null){
					accom+=cl.getStudents().size();
				}
			}
		}
		
		
		return accom;
	}
	
	/**
	 * Returns a list<SubjectAssignmentStatistics> for the given subject
	 * @param subject
	 * @return
	 */
	public List getSubjectAssignmentStatistics(String subject){
		List retval = new Vector();
		List s = new Vector(scheduler.getOffering().getAllSubjects().keySet());
		Collections.sort(s);
		for(Iterator ite=s.iterator();ite.hasNext();){
			String sname = (String)ite.next();
			if (sname.startsWith(subject)){
				int demand = getDemandCount(sname);
				int slots = getSlotCount(sname);
				int accom = getAccommodatedCount(sname);
				SubjectAssignmentStatistics stat = new 
					SubjectAssignmentStatistics(sname,slots,demand,accom);
				retval.add(stat);
			}
		}
		return retval;
	}
	
	
	public void showSubjectStats(){
		List s = new Vector(scheduler.getOffering().getAllSubjects().keySet());
		Collections.sort(s);
		out.println("Subject,Slots,Demand,Assigned");
		for(Iterator ite=s.iterator();ite.hasNext();){
			String sname = (String)ite.next();
			int demand = getDemandCount(sname);
			int slots = getSlotCount(sname);
			int accom = getAccommodatedCount(sname);
			out.println(sname +","+slots+","+demand+","+accom);
		}
	}
	
	public void showSubjectStats(String subject){
		List s = getSubjectAssignmentStatistics(subject);
		out.println("Subject,Slots,Demand,Assigned");
		for(Iterator ite=s.iterator();ite.hasNext();){
			SubjectAssignmentStatistics stat = 
				(SubjectAssignmentStatistics)ite.next();
			out.println(stat.getSubject() +","+stat.getSlots()+","+
					stat.getDemand()+","+stat.getAssigned());
		}
	}
	
	public void display(){
		out.println("[Scheduling Statistics]");
		out.println("----------------------------------------------------");
		out.println("Number of writeins: "+getWriteInCount());
		out.println("Number of schedules: "+getSchedulesCount());		
		out.println("Number of writeins with schedule: "+getWriteInWithScheduleCount());
		out.println("Number of schedules with writein: "+getScheduleWithWriteInCount());
		out.println("Average Percentage Load: "+getAveragePercentageLoad());
		out.println("Average Perentage Class Size: "+getAveragePercentageClassSize());
		out.println("Full Load: "+getFullLoadCount());
		out.println("Underload: "+getUnderloadCount());
		out.println("Overload: "+getOverloadCount());
		out.println("Zero Load: "+getZeroLoadCount());
		out.println("----------------------------------------------------");
		showSubjectStats("CMSC");
	}
}
