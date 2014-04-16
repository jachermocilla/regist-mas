package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;

/**
 * AbstractSubject provides default implementation of some methods for subjects
 * This class implements ISubject and IPrerequisite interfaces.
 * 
 * @author Joseph Anthony C. Hermocilla
 * @version $Id:AbstractSubject.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public abstract class AbstractSubject implements ISubject, IPrerequisite, Serializable{
	
	/**
	 * The list<IPrerequisite> of prerequisites
	 */
	protected List prerequisites=new Vector();
	
	/**
	 * The name of the subject
	 */
	protected String name;
	

	/**
	 * The semester that this subject should be taken
	 */
	protected int sem;
	
	/**
	 * The year level that this subject should be taken
	 */
	protected int year;
	
	/**
	 * The number of units credited(example 0, 3, 5)
	 */
	protected int units;
	
	private boolean hasLab;
	
	private boolean hasRecit;
	

	/**
	 * Sets the name of this subject
	 * @param name		a <code>String</code> that represents the name of the subject
	 */
	public void setName(String name){
		this.name=name;		
	}

	/**
	 * Sets the semester that this subject should be taken
	 * @param sem		an <code>int</code> that represents the semester
	 */	
	public void setSem(int sem){
		this.sem=sem;
	}
	
	/**
	 * Sets the year level that this subject should be taken
	 * @param year		an <code>int</code> that represents the year level
	 */
	public void setYear(int year){
		this.year=year;
	}
	
	
	/**
	 * Sets the unit credits for this subject
	 * @param units		an <code>int</code> that represents the unit credit for
	 * 					this subject
	 */
	public void setUnitCredit(int units){
		this.units=units;		
	}
	
	
	/**
	 * Adds a new prerequisite for this subject
	 * @param prereq	a <code>IPrerequisite</code> object that represents the 
	 * 					prerequisite
	 */
	public void addPrerequisite(IPrerequisite prereq){
		prerequisites.add(prereq);				
	}
	
	//Method from IPrerequisite
	public String getDescription(){
		return name;		
	}
	
	/**
	 * Returns the name of the subject
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a list<IPrerequisites> of prerequisites
	 */
	public List getPrerequisites() {
		return prerequisites;
	}

	/**
	 * Returns the semester this subject should be taken
	 * as prescribed in the curriculum
	 */
	public int getSem() {
		return sem;
	}

	/**
	 * Returns the unit credit for this subject
	 */
	public int getUnitCredit() {
		return units;
	}

	
	/**
	 * Returns the year level that this subject should be taken
	 * as prescribed in the curriculum
	 */
	public int getYear() {
		return year;
	}
	
	
	/**
	 * Returns a String representation of this object.
	 */
	public String toString(){
		String retval;
		retval=name+":"+year+":"+sem+":"+units+":";
		
		Iterator it=prerequisites.listIterator();
		while(it.hasNext()){
			IPrerequisite prereq=(IPrerequisite)it.next();
			retval+=prereq.getDescription();
			if (it.hasNext())
				retval+=",";
		}		
		return retval;		
	}
	
	/**
	 * Returns true when this subject has laboratory
	 * component
	 */
	public boolean hasLaboratory(){
		return hasLab;
	}
	
	/**
	 * Returns true when this subject has recitation
	 * component
	 */
	public boolean hasRecitation(){
		return hasRecit;
	}
	
	public void setHasLaboratory(boolean hasLab){
		this.hasLab = hasLab;		
	}
	
	public void setHasRecitation(boolean hasRecit){
		this.hasRecit = hasRecit;		
	}
	

}
