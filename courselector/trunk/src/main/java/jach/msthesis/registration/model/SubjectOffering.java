package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.List;

/**
 * This class encapsulates a subject offering that contains
 * lecture section and lab section
 * @author jach
 *
 */

public class SubjectOffering implements Serializable {
	ISection lecture;
	
	/**
	 * List<ISection> for laboratories
	 */
	List labs;
	
	public SubjectOffering(){		
	}
	
	public SubjectOffering(ISection lecture){
		this.lecture = lecture;
	}
	
	public ISection getLectureSection(){
		return lecture;
	}
	
	public List getLabSections(){
		return labs;
	}
	
	public void addLabSection(ISection labSection){
		labs.add(labSection);
	}
	
	
	
}
