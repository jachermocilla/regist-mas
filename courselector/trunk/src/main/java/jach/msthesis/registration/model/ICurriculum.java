package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;


/**
 * Provides methods for curriculum 
 *  
 * @author Joseph Anthony C. Hermocilla
 * @version $Id:ICurriculum.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public interface ICurriculum extends Serializable{

	/**
	 * Returns the name of the curriculum
	 * @return		a <code>String</code> that represents the name of the curriculum
	 */
	public String getName();
	
	
	/**
	 * Returns all subjects belonging to a curriculum
	 * @return		a <code>List</code> that contains all the subjects belongin in the
	 * 				curriculum
	 */
	public List getAllSubjects();

	
	/**
	 * Returns the subjects that must be taken in a particular year level 
	 * and curriculum.
	 * 
	 * @param year		an <code>int</code> that represents the year level
	 * @param sem		an <code>int</code> that represents the semester
	 * @return			a <code>List</code> that contains the subjects
	 */	
	public List getSubjects(int year, int sem);
	
	
	/**
	 * Returns the subjects that must be taken in a particular year level
	 * @param year		an <code>int</code> that represents the year level
	 * @return			a <code>List</code> that contains the subjects
	 */	
	public List getSubjects(int year);
	
	/**
	 * Returns am ISubject given a name
	 * @param name		a <code>String</code> that represents the subject name
	 * @return			an <code>ISubject</code> that represents the subject object
	 */
	public ISubject getSubject(String name);
		
		
	
	
	/**
	 * Returns the total units needed to finish 
	 * @return
	 */
	public int getTotalUnits();
	
	
	/**
	 * Returns the number of units for a given year. Used to
	 * determine the classification of the student
	 * @param year		the year
	 * @return			the number of units for that year.
	 */
	public int getTotalUnits(int year);
	
	
	/**
	 * Returns <code>true</code> if the subject is in the curriculum,
	 * <code>false</code> otherwise 
	 * 
	 * @param name		a <code>String</code> that represents the subject name
	 * @return			<code>true</code> if the subject is in the curriculum,
	 * 					<code>false</code> otherwise
	 */
	public boolean contains(String name);
	
	/**
	 * Returns a map of critical subjects;
	 * @return
	 */
	public Map getCriticalSubjects();
	
	
	/**
	 * Returns true if subject is critical
	 * @param subjectName
	 * @return
	 */
	public boolean isCritical(String subjectName);
		
	/**
	 * Returns the float time for a subject
	 * @param subjectName
	 * @return 		an <code>int</code> that represents the float for the
	 * 				subject
	 */
	public int getFloat(String subjectName);
	
}
