package jach.msthesis.scheduler.statistics;

/**
 * This class encapsulates assignment statistics per subject
 * @author jach
 *
 */

public class SubjectAssignmentStatistics {

	/**
	 * The subject
	 */
	private String subject;
	
	
	/**
	 * The number of slots for this subject
	 */
	private int slots;
	
	/**
	 * The demand for this subject.
	 * Number of students who have this 
	 * subject in their writein
	 * 
	 */
	private int demand;
	
	/**
	 * The number of assigned slots to students
	 */	
	private int assigned;

	/**
	 * Constructor
	 * @param subject
	 * @param slots
	 * @param demand
	 * @param assigned
	 */
	public SubjectAssignmentStatistics(String subject, int slots, int demand,
				int assigned){		
		this.subject = subject;
		this.slots = slots;
		this.demand = demand;
		this.assigned = assigned;
		
	}

	/**
	 * Returns the number of assigned slots
	 * @return the assigned
	 */
	public int getAssigned() {
		return assigned;
	}

	/**
	 * Returns the demand for subject
	 * @return the demand
	 */
	public int getDemand() {
		return demand;
	}
	
	/**
	 * Return the number of slots 
	 * for this subject
	 * @return
	 */
	public int getSlots(){
		return slots;
	}
	
	/**
	 * Returns the subject name
	 * @return
	 */
	public String getSubject(){
		return subject;
	}	
}
