package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;
/**
 * Implementation of the ISection interface.
 * @author jachermocilla
 * @version $Id:Section.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public class Section implements ISection, Serializable{
	
	protected ISubject subject;
	protected String sectionName;
	protected int classSize;
	protected String time;
	protected String days;
	protected String room;
	protected String location;
	protected String instructor;
	protected List timeSlots;
	
	public Section(){}
	
	/**
	 * Constructor that accepts complete information about a section.
	 * 
	 * @param subject
	 * @param sectionName
	 * @param time
	 * @param days
	 * @param room
	 * @param location
	 * @param instructor
	 * @param classSize
	 */
	public Section(ISubject subject,String sectionName,
			String time, String days, String room,String location,
			String instructor,int classSize){
		this.subject=subject;
		this.sectionName=sectionName;
		this.classSize=classSize;
		this.time=time;
		this.days=days;
		this.room=room;
		this.location=location;
		this.instructor=instructor;		
	}
	
	/**
	 * Constructor that accepts subject and section name
	 * @param subject
	 * @param sectionName
	 */
	public Section(ISubject subject,String sectionName){
		this.subject=subject;
		this.sectionName=sectionName;
	}
	
	//getter methods
	public ISubject getSubject(){return subject;}
	public String getSectionName(){return sectionName;}
	public int getClassSize(){return classSize;}
	public String getTime(){return time;}
	public String getDays(){return days;}
	public String getRoom(){return room;}
	public String getLocation(){return location;}
	public String getInstructor(){return instructor;}
	
	
	/**
	 * This method returns a list of timeslots for this section
	 * 
	 */
	public List getTimeSlots(){
		TimeSlot slot=null;		
		if (timeSlots==null){
			timeSlots=new Vector();
			Vector temp=splitDays();
			Vector temp2=splitTime();			
			for (int i=0;i<temp.size();i++){
				//System.out.println("DAY"+subjectName+"-"+sectionName+temp.elementAt(i));
				for (int j=0;j<temp2.size();j++){
					//System.out.println("TIME"+subjectName+"-"+sectionName+temp2.elementAt(j));
					slot=new TimeSlot((String)temp.elementAt(i),(String)temp2.elementAt(j));
					timeSlots.add(slot);
				}
			}
		}
		return timeSlots;
	}

	/**
	 * Adjust the time by "rounding up" 
	 * 
	 * @return
	 */
	private String adjustTime(){
		String retval="";
		int i=0,j=0;
		//TODO:Clean up TBA processing
		//right now, set TBA to 7-8...
		if (time.equals("TBA")){
			return "7-8";
		}
		
		//split start and end hour
		String tokens[]=time.split("-");
		
		//split start hour to hour:minute
		String temp1[]=tokens[0].split(":");
		
		//Get start hour only, no minutes
		j=Integer.parseInt(temp1[0].trim());
		
		//split end hour to hour:minute
		String temp2[]=tokens[1].split(":");
		
		//round up by one when there is a minute field
		if (temp2.length > 1){
			i=(Integer.parseInt(temp2[0].trim()))+1;
		}else{
			i=(Integer.parseInt(temp2[0].trim()));
		}
		
		//return adjusted time
		retval=((j+"-"+i));
		return retval;		
	}
	
	
	/**
	 * Splits the time into individual hours such that a time in 10-1
	 * will be split into {"10-11","11-12","12-13"}
	 * 
	 * @return
	 */
	private Vector splitTime(){
		Vector retval=new Vector();
		
		String temp=TimeSlot.to24Hour(adjustTime());
		String tokens[]=temp.split("-");
		int end=Integer.parseInt(tokens[1]);;
		int start=Integer.parseInt(tokens[0]);
		
		int duration=end-start;
		if ((duration) > 1){
			for (int i=0;i < duration;i++){
				end=start+1;
				String entry=start+"-"+end;
				retval.add(entry);
				start=end;
			}
		}else{
			retval.add(temp);
		}		
		return retval;
	}

	/**
	 * Split Days is a bit tricky
	 * possible formats
	 *  Mon, Tues, Wed, Thurs, Fri
	 *  MW, MWF, TTh,M-F
	 * 
	 * @return
	 */
	
	private Vector splitDays(){
		
		Vector retval=new Vector();
		//TODO:Clean up TBA processing
		//may it Sat
		if (days.equals("TBA")){
			retval.add("Sat");
			return retval;
		}
		
		//Check if it is a recognizable format by TimeSlot
		//so that no more processing will be done
		if(TimeSlot.isValidDay(days)){
			retval.add(days);
			return retval;
		}
		
		//Coming here means format is not recognized
		boolean done=false;
		int i=0;
		while (!done){
			if (i>=days.length()){
				break;
			}
			char c=days.charAt(i);
			switch (c){
				case 'M':
				case 'W':
				case 'F': retval.add(c+"");break;
				case 'T': 	
							if ((i+1) == days.length()){
								retval.add("T");
								break;
							}								
							c=days.charAt(i+1);
							if (c=='h'){
								retval.add("Th");
								i++;
							}else{
								retval.add("T");
							}
							break;
				case '-':
							retval.add("T");
							retval.add("W");
							retval.add("Th");
							retval.add("F");
							done=true;
							break;
			}
			i++;			
		}
		return retval;		
	}
	
	/**
	 * Returns true if this section is a lab section, false otherwise
	 */
	public boolean isLab(){
		if ((sectionName.indexOf('-') == -1) && (!sectionName.startsWith("PE 2"))){
			return false;
		}
		return true;
	}
	
	public String toString(){
		String retval="";
		retval+=this.subject.getName()+":";
		retval+=this.sectionName;
		return retval;
	}
}
