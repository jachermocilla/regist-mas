package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;

/**
 * Abstract transcript
 * 
 * @author jach
 * @version $Id:AbstractTranscript.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public class AbstractTranscript implements ITranscript, Serializable {
	/**
	 * The curriculum
	 */	
	protected ICurriculum curriculum;


	/**
	 * The student number
	 */
	protected String studentNumber;
	
	
	
	/**
	 * Map of grades, <String, SubjectTaken>
	 */
	protected Map subjects=new Hashtable();

	
	/**
	 * Adds a new grade
	 * 
	 * @param grade		a <code>IGrade</code> that represents the grade
	 */
		
	public void addSubjectTaken(SubjectTaken subject){		
		subjects.put(subject.getName(), subject);
	}
	
	/**
	 * Sets the associated curriculum
	 * 
	 * @param curriculum 	an <code>ICurriculum</code> object
	 */
	public void setCurriculum(ICurriculum curriculum){
		this.curriculum=curriculum;		
	}
	
	/**
	 * Sets the student number 
	 * 
	 * @param studentNumber		a <code>String</code> that represents the student
	 * 							number
	 */	
	public void setStudentNumber(String studentNumber){
		this.studentNumber=studentNumber;
	}

	/**
	 * Returns true if the given subject name has been taken, or false
	 * otherwise.
	 */
	public boolean contains(String subjectName) {
		return subjects.containsKey(subjectName);
	}

	/**
	 * Returns a list<SubjectTaken> of subjects.
	 */	
	public List getAllSubjectsTaken() {
		return new Vector(subjects.values());
	}

	/**
	 * TODO: Implement method body.
	 * 
	 * Computes the GWA of a student.
	 */
	public double getGWA() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Returns a SubjectTaken object given a subject name.
	 */
	public SubjectTaken getSubjectTaken(String subjectName) {
		return (SubjectTaken)subjects.get(subjectName);
	}

	/**
	 * TODO: Implement method body
	 * 
	 * Returns a list<SubjectTaken> given year and semester.
	 * 
	 */
	public List getSubjectsTaken(int academicYear, int semester) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: Implement method body
	 * 
	 * Returns a list<SubjectTaken> given the year only
	 */	
	public List getSubjectsTaken(String academicYear) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the student number for this transcript
	 */
	public String getStudentNumber() {
		return studentNumber;
	}
	
	/**
	 * Returns the curriculum associated with this transcript
	 */
	public ICurriculum getCurriculum(){
		return curriculum;		
	}
	
	/**
	 * Same as contains
	 */
	public boolean completed(String subjectName){
		return subjects.containsKey(subjectName);
	}

	/**
	 * Returns the classification of the student based on
	 * the number of units completed.
	 */
	public Classification getClassification(){
		int units=getTotalUnits();
		
		if (units > curriculum.getTotalUnits(3))
			return Classification.SENIOR;
		else if (units > curriculum.getTotalUnits(2))
			return Classification.JUNIOR;
		else if (units > curriculum.getTotalUnits(1))
			return Classification.SOPHOMORE;
		else
			return Classification.FRESHMAN;
	}
	
	/**
	 * Returns the number of completed based on this transcript.
	 */
	public int getTotalUnits(){
		int units=0;
		Iterator ite=subjects.values().iterator();
		while(ite.hasNext()){
			SubjectTaken subTaken=(SubjectTaken)ite.next();
			//Subject is in curriculum
			if (curriculum.contains(subTaken.getName())){
				ISubject sub=(ISubject)curriculum.getSubject(subTaken.getName());
				units+=sub.getUnitCredit();				
			}else if (subTaken.isExtra()){ //for extra subjects not in curriculum
				units+=subTaken.getUnitCredit();
			}
		}
		return units;
	}
	
	/**
	 * Returns a map<subjectName,ISubject> of subjects to take based on the curriculum.
	 * Subjects that are not in the transcript are returned
	 * 
	 */
	public Map getSubjectsToTake(){
		Map toTake=new Hashtable();		
		Iterator ite=curriculum.getAllSubjects().iterator();
		while (ite.hasNext()){
			ISubject s=(ISubject)ite.next();
			if (!this.contains(s.getName())){
				toTake.put(s.getName(), s);
			}
		}
		return toTake;
	}
	
}
