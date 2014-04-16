package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;
import com.jach.cpm.*;

/**
 * Provides initial implementation of the ICurriculum interface
 * 
 * @author Joseph Anthony C. Hermocilla
 * @version $Id:AbstractCurriculum.java 548 2008-09-15 00:52:12Z jach $ 
 */


public abstract class AbstractCurriculum implements ICurriculum, Serializable {

	/**
	 * The name of the curriculum
	 */
	protected String name;
	
	
	/**
	 * The map<String,ISubject> of subjects
	 */	
	protected Map subjects = new Hashtable();
	
	
	/**
	 * The total units for the curriculum
	 */
	protected int totalUnits;
		
	/**
	 * A map containing all the critical subjects
	 * computed using jcpm
	 */
	protected Map criticalSubjects=new Hashtable();
	
	/**
	 * A reference to a CPM object which is used for 
	 * finding the critical projects
	 */	
	private CPM cpm;

	/**
	 * Sets the name of the curriculum
	 * @param name	a <code>String</code> that represents the name of the curriculum
	 */
	public void setName(String name){
		this.name=name;
	}
	
	
	/**
	 * Adds a new subject to the curriculum
	 * @param subject	an <code>ISubject</code> that represents the subject to add
	 */
	public void addSubject(ISubject subject){
		subjects.put(subject.getName(),subject);
		totalUnits+=subject.getUnitCredit();
	}
	
	
	//methods from ICurriculum
	
	/**
	 * Return a list of subjects for this curriculum
	 */
	public List getAllSubjects() {	
		return new Vector(subjects.values());
	}

	/**
	 * Returns the name of this curriculum
	 */
	public String getName() {
		return name;
	}

	/**
	 * TODO: Implement method body
	 * 
	 * Returns the list of subjects for the given year and semester 
	 * 
	 */
	public List getSubjects(int year, int sem) {
		return null;
	}

	/**
	 * TODO: Implement method body
	 * 
	 * Return a list of subjects for the given year
	 * 
	 */
	public List getSubjects(int year) {
		return null;
	}
	
	/**
	 * Returns an ISubject for a given subject name
	 * 
	 */
	public ISubject getSubject(String name){
		return (ISubject)subjects.get(name);
	}

	/**
	 * Returns the total units for this curriculum
	 */
	public int getTotalUnits() {
		return totalUnits;
	}
	
	
	/**
	 * Returns the total number of units for the given year.
	 */
	public int getTotalUnits(int year){
		int units=0;
		Iterator ite=subjects.values().iterator();
		while(ite.hasNext()){
			ISubject subj=(ISubject)ite.next();
			if (subj.getYear()<=year)
				units+=subj.getUnitCredit();
		}		
		return units;
	}
	
	
	/**
	 * Returns true if a given subject name is in the list of subjects
	 * for this curriculum.
	 */
	public boolean contains(String name){
		return subjects.containsKey(name);
	}
	
	/**
	 * Sets the critical subjects for this curriculum
	 * @param criticalSubjects
	 */
	public void setCriticalSubjects(Map criticalSubjects){
		this.criticalSubjects=criticalSubjects;		
	}	
	
	
	/**
	 * Returns the a map<String, ISubject> of the critical subjects.
	 */
	public Map getCriticalSubjects(){
		return criticalSubjects;
	}
		
	
	/**
	 * Returns true if the given subject name is a critical subject, or false
	 * otherwise.
	 */
	public boolean isCritical(String subjectName){
		return criticalSubjects.containsKey(subjectName);
	}
	
	/**
	 * Sets the CPM object associated with this curriculum
	 * @param cpm
	 */
	public void setCPM(CPM cpm){
		this.cpm=cpm;
	}
	
	
	/**
	 * Returns the float time for the given subject name
	 */
	public int getFloat(String subjectName){
		return cpm.getTask(subjectName).getFloat();
	}
	
}
