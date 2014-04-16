package jach.msthesis.registration.model;

import java.io.Serializable;

/**
 * Encapsulates subject taken objects
 * @author jach
 * @version $Id:SubjectTaken.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public class SubjectTaken extends AbstractSubject implements Serializable{
	
	//sem the subject was taken
	private int semTaken;
	
	//year the subject was taken
	private String acadYearTaken;
	
	//the final grade
	private double grade;
	
	/**
	 * This subject is an extra subject
	 */
	private boolean extra;
	
	/**
	 * Detailed constructor
	 * 
	 * @param acadYearTaken
	 * @param semTaken
	 * @param grade
	 */
	public SubjectTaken(String acadYearTaken, int semTaken, double grade ){
		this.acadYearTaken=acadYearTaken;
		this.semTaken=semTaken;
		this.grade=grade;
	}
	
	/**
	 * Returns the sem this subject was taken
	 * @return
	 */
	public int getSemTaken(){
		return semTaken;
	}
	
	/**
	 * Returns the year this subject was taken
	 * @return
	 */
	public String getAcadYearTaken(){
		return acadYearTaken;
	}
	
	/**
	 * Returns the grade for this subject
	 * @return
	 */
	public double getGrade(){
		return grade;		
	}
	 
	/**
	 * Returns true if this is an extra subject, meaning
	 * not in curriculum
	 * @return
	 */
	public boolean isExtra(){
		return extra;		
	}
	
	/**
	 * Set this subject taken as an extra subject
	 * @param b
	 */
	public void setExtra(boolean b){
		extra=b;				
	}
	
}
