package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;

/**
 * Transcript interface
 * 
 * @author jachermocilla
 * @version $Id:ITranscript.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public interface ITranscript extends Serializable {
	
	/**
	 * Returns the student number associated with this transcript
	 * 
	 * @return		a <code>String</code> that represents the student number
	 */
	public String getStudentNumber();
	
	
	/**
	 * Returns a list that contains all the grades in this transcript
	 * 
	 * @return		a <code>List</code> containing the grades
	 */
	public List getAllSubjectsTaken();
	
	
	/**
	 * Returns a list that contains all the grades taken in an
	 * academic year and semester
	 * 
	 * @param academicYear		an <code>int</code> that represents the academic year
	 * @param semester			an <code>int</code> that represents the semester
	 * @return					a <code>List</code> that contains the grades
	 */	
	public List getSubjectsTaken(int academicYear, int semester);
	
	/**
	 * Returns a list that contains all the grades taken in an
	 * academic year
	 * 
	 * @param academicYear		an <code>String</code> that represents the academic year
	 * @return					a <code>List</code> that contains the grades
	*/	
	public List getSubjectsTaken(String academicYear);	
	
	
	/**
	 * Returns the grade given a subject name
	 * 
	 * @param subjectName		a <code>String</code> that represents the subject name
	 * @return					a <code>SubjectTaken</code> that represents the grade object
	 */
	public SubjectTaken getSubjectTaken(String subjectName);
	
	/**
	 * Returns the GWA
	 * 
	 * @return					a <code>double</code> that represents the GWA
	 */
	public double getGWA();

	
	/**
	 * Check whether there is a grade for a given subject name
	 * 
	 * @return		<code>true</code> if present, <code>false</code> otherwise
	 */
	public boolean contains(String subjectName);
	
	
	/**
	 * Returns the curriculum associated with this transcript
	 * 
	 * @return		a <code>ICurriculum</code> object
	 */	
	public ICurriculum getCurriculum();
	
	
	/**
	 * Returns true if subjectName has been completed by the student
	 * @param subjectName	
	 * @return	<code>true</code> if completed already or <code>false</code> otherwise
	 */
	public boolean completed(String subjectName);

	
	/**
	 * Returns the current classification of the student
	 * 
	 * @return		a <code>Classification</code> object
	 */
	public Classification getClassification();
		
	
	/**
	 * Returns the total number of units completed
	 * @return
	 */
	public int getTotalUnits();
	
	/**
	 * Returns a map of subjects to take
	 * @return
	 */
	public Map getSubjectsToTake();
		

}
