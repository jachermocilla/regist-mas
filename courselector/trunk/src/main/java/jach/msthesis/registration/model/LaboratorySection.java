package jach.msthesis.registration.model;

import java.io.Serializable;

public class LaboratorySection extends Section implements Serializable{
	/**
	 * Reference to the lecture section
	 */
	ISection lecture;
	
	public LaboratorySection(){}
	
	public LaboratorySection(Section s){
		this.subject = s.subject;
		this.sectionName = s.sectionName;
		this.time = s.time;
		this.days = s.days;
		this.room = s.room;
		this.location = s.location;
		this.instructor = s.instructor;
		this.classSize = s.classSize;
	}
	
	
	public LaboratorySection(ISubject subject,String sectionName,
			String time, String days, String room,String location,
			String instructor,int classSize)
	{
		super(subject,sectionName,time,days,room,location,
				instructor,classSize);
	}
	
	public ISection getLectureSection(){
		return lecture;
	}
	
	public void setLectureSection(ISection lecture){
		this.lecture = lecture;
	}
	
	public boolean isLab(){
		return true;
	}
}
