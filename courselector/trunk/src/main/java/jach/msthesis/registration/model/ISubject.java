package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;

/**
 * Provides methods for accessing subject information needed
 * in determining the subjects to take by the student. 
 * 
 * @author Joseph Anthony C. Hermocilla
 * @version $Id:ISubject.java 548 2008-09-15 00:52:12Z jach $
 * 
 */

public interface ISubject extends Serializable{

	/**
	 * Returns the name of the subject, for example 
	 * <code>CMSC 11</code>.
	 * 
	 * @return		the name of the subject
	 */
	public String getName();
	
	/**
	 * Returns the semester that this subject should be taken
	 * by the student as indicated in the curriculum.
	 * <p>
	 * Possible return values are <code>1</code> and <code>2</code>
	 * for first and second semester respectively.
	 * 
	 * 
	 * @return		an <code>int</code> that represents the 
	 * 				semester that this subject should be taken. 
	 */
	public int getSem();

	/**
	 * Returns the year level of the students that this subject
	 * should be taken.
	 * 
	 * @return		an <code>int</code> that represents the year
	 * 				level that this subject should be taken
	 */
	public int getYear();

			
	/**
	 * Returns the unit credits for this subject
	 * 
	 * @return		an <code>int</code> that represents the
	 * 				credit unit for this subject
	 */
	public int getUnitCredit();
	
	
	/**
	 * Returns a list of prerequisites for
	 * this subject.
	 * 
	 * @return		a <code>List</code> that contains the 
	 * 				prerequisites.
	 */	
	public List getPrerequisites();
	
	/**
	 * Returns true when this subject has laboratory
	 * component
	 */
	public boolean hasLaboratory();
	
	/**
	 * Returns true when this subject has recitation
	 * component
	 */
	public boolean hasRecitation();
	
	public void setHasLaboratory(boolean hasLab);
	
	public void setHasRecitation(boolean hasRecit);
}
