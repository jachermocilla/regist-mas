package jach.msthesis.registration.model;

import java.io.Serializable;


/**
 * Placeholder for assignment of section
 * @author jach
 *
 */

public class SectionAssignment implements Serializable{
	ISection lecture;
	ISection lab;
	ISection section;
	
	public SectionAssignment(){}
	
	
	public SectionAssignment(ISection lecture, ISection lab) {
		this.lecture = lecture;
		this.lab = lab;
	}
	/**
	 * @return the lab
	 */
	public ISection getLab() {
		return lab;
	}
	/**
	 * @param lab the lab to set
	 */
	public void setLab(ISection lab) {
		this.lab = lab;
	}
	/**
	 * @return the lecture
	 */
	public ISection getLecture() {
		return lecture;
	}
	/**
	 * @param lecture the lecture to set
	 */
	public void setLecture(ISection lecture) {
		this.lecture = lecture;
	}
	
	public ISection getSection(){
		if (lab != null){
			return lab;
		}else{
			return lecture;
		}			
	}
	
	public String toString(){
		String retval="";
		retval+= this.lecture.getSubject().getName()+":";
		retval+= this.lecture.getSectionName()+":";
		if (this.lab != null){
			retval+=this.lab.getSectionName();
		}
		return retval;
	}
			
}
