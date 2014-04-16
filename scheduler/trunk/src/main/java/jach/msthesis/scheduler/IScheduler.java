package jach.msthesis.scheduler;



import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.IWriteIn;

import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * This interface describes the methods that must be
 * implemented by different scheduling algorithms
 * 
 * @author jach
 * @version $Id:IScheduler.java 846 2008-09-30 02:17:08Z jach $
 * 
 */

public interface IScheduler {
	
	/**
	 * Returns the writein for a given student given the student number 
	 */
	public IWriteIn getWriteIn(String stdNum);
	
	/**
	 * Returns the schedule for a given student number 
	 */
	public Schedule getSchedule(String stdNum);
	
	/**
	 * Returns the set<StudentNUmber> of student number 
	 */
	public Set getStudents();
	
	/**
	 * Returns the set<StudentNumber> of student number with schedule 
	 */
	public Set getStudentsWithSchedule();
	
	/**
	 * Returns the classlist(list of students) for a given section 
	 */
	public Classlist getClasslist(String subject,String section);
	
	/**
	 * Returns a Map<sectionName,Classlist> of classlists for a subject 
	 */
	public Map getAllClasslistsForSubject(String subject);

	
	/** 
	 * Returns the sections 
	 */
	public  Set getSubjectSections();
	
	/**
	 * Returns a map classlists 
	 */
	public Map getClasslists();
	
	/**
	 * Returns map of writeins
	 */
	public Map getWriteIns();
	
	/**
	 * Returns map of schedules
	 */
	public Map getSchedules();
	
	/**
	 * Initialize method
	 */
	public void init();
	
	/**
	 * Assign method
	 */
	public void assign();
	
	/**
	 * Returns an offering 
	 * @return
	 */	
	public IOffering getOffering();
	
	/**
	 * Returns the demand map
	 * @return
	 */
	public Map getDemandMap();
	
}
