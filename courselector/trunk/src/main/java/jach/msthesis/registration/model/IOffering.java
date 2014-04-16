package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;


/**
 * Offering Interface for getting class offering
 * @author jachermocilla
 * @version $Id:IOffering.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public interface IOffering extends Serializable{
	
	/**
	 * Returns a list<Section> of lecture sections given the subject name
	 * @param subjectName			
	 * @return
	 */
	public List getLecSections(String subjectName);
	
	/**
	 * Returns a list<Section> of laboratory sections given the subject name
	 * and the lecture section name.
	 * 
	 * @param subjectName
	 * @param lectureSection
	 * @return
	 */
	public List getLabSections(String subjectName, String lectureSection);
	
	/**
	 * Returns a list<Sections> given the subject name
	 * @param subjectName
	 * @return
	 */
	public List getSections(String subjectName);
	
	/**
	 * Returns the rank of a subject given the subject name.
	 * The rank is used during the allocation
	 * @param subjectName
	 * @return
	 */
	public float getSubjectRank(String subjectName);
	
	/**
	 * Returns the entire offering map<subjectname,<sectionname, section>>
	 * @return
	 */
	public Map getAllSubjects();
	
	/**
	 * Returns an ISection for the given subject name and lab section name
	 * @param subjectName 
	 * @param labSection
	 * @return
	 */
	public ISection getLabSection(String subjectName, String labSection);
	
	/**
	 * Returns an ISection for the given subject name and lec section name
	 * @param subjectName
	 * @param lecSection
	 * @return
	 */
	public ISection getLecSection(String subjectName, String lecSection);
	
	/**
	 * Returns the number of slots given the subject name
	 * @param subjectName
	 * @return
	 */
	public int getSlotCount(String subjectName);
	
	/**
	 * Returns a boolean whether the subject is offered.
	 * @param subjectName
	 * @return
	 */
	public boolean isOffered(String subjectName);
		
	/**
	 * Loads data. may not be implemented but should exist in
	 * implementers.
	 */
	public void load();
	

}
