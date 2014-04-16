package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;

/**
 * Defines a lecture or lab section
 * @author jach
 * @version $Id:ISection.java 548 2008-09-15 00:52:12Z jach $
 *
 */
public interface ISection extends Serializable {
	/**
	 * Returns the ISubject associated with this section
	 * @return
	 */
	public ISubject getSubject();
	
	/**
	 * Returns the name of the section, Example A, A-1L, etc.
	 * @return
	 */
	public String getSectionName();
	
	/**
	 * Returns a list<TimeSlots> for this sections. See TimeSlot class.
	 * @return
	 */
	public List getTimeSlots();
	
	/**
	 * Returns true if this section is a lab section, false otherwise.
	 * @return
	 */
	public boolean isLab();
	
	/**
	 * Returns the class size for this section, the  total number of students
	 * that can be accommodated in this section.
	 * @return
	 */
	public int getClassSize();
}
