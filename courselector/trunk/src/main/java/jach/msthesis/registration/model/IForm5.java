package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * This interface defines a form 5
 * @author jach
 * @version $Id: IForm5.java 894 2008-10-01 16:31:20Z jach $
 *
 */
public interface IForm5 extends Serializable {
	
	/**
	 * Returns the units assigned in a form 5
	 * @return an int that represents the number of units
	 */
	public int getUnitsObtained();
	
	/**
	 * Returns reference to the writein 
	 * @return
	 */
	public IWriteIn getWriteIn();
	
	/**
	 * Returns the section assignment given a subject name
	 */
	public SectionAssignment getSectionAssignment(String subject);
	
	public boolean removeSectionAssignment(String subject);
	
	public Map getSectionAssignments();
	
	public boolean addSectionAssignment(SectionAssignment sectAss);
	
	/**
	 *  Returns a list<ISubject> of unassigned subjects
	 */
	public List getUnassigned();
	
}
