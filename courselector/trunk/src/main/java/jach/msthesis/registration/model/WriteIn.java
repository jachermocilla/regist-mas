package jach.msthesis.registration.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates students write-in information
 * 
 * @author jach
 * @version $Id: WriteIn.java 705 2008-09-23 01:11:40Z jach $
 * 
 */

public class WriteIn implements IWriteIn, Serializable {
	// Student
	private String studentNumber;

	// The priority of the student
	// used for sorting
	int priority;

	// The number of units allowed
	int unitsAllowed;

	// The list of desired subjects
	private List desiredSubjects = new ArrayList();

	/* Current number of options */
	float count;

	/**
	 * Default constructor
	 */
	public WriteIn() {
	}

	/**
	 * Detailed constructor
	 * 
	 * @param studentNumber
	 * @param priority
	 * @param unitsAllowed
	 */
	public WriteIn(String studentNumber, int priority, int unitsAllowed) {
		this.studentNumber = studentNumber;
		this.priority = priority;
		this.unitsAllowed = unitsAllowed;
	}

	/**
	 * Returns the student number for this writein
	 * 
	 * @return
	 */
	public String getStudentNumber() {
		return studentNumber;
	}

	/**
	 * Returns the priority of this student
	 * 
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Returns the allowed units for this writein
	 */
	public int getUnitsAllowed() {
		return unitsAllowed;
	}

	/**
	 * Returns a list<ISubject> of subjects
	 */
	public List getSubjects() {
		return desiredSubjects;
	}

	/**
	 * Add a subject to the desired subjects
	 * 
	 * @param subject
	 */
	public void addSubject(ISubject subject) {
		desiredSubjects.add(subject);
	}

	/**
	 * Returns ISubject information given the subject name
	 */
	public ISubject getSubjectInfo(String name) {
		Iterator ite = desiredSubjects.iterator();
		while (ite.hasNext()) {
			ISubject sub = (ISubject) ite.next();
			if (sub.getName().equals(name))
				return sub;
		}
		return null;
	}

	/**
	 * Sets the number of options count
	 * 
	 * @param count
	 */
	public void setOptionCount(float count) {
		this.count = count;
	}

	/**
	 * Returns the number of options count
	 * 
	 * @return
	 */
	public float getOptionCount() {
		return count;
	}

	/**
	 * Returns the priority of the subject ISubjects
	 * 
	 * @param subject
	 * @return
	 */
	public int getSubjectPriority(ISubject subject) {
		return desiredSubjects.indexOf(subject);
	}
}
