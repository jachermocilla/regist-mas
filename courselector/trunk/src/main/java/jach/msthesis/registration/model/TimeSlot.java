package jach.msthesis.registration.model;


import java.io.Serializable;
import java.util.*;
/**
 * Abstracts a time slot for easier conflict checking
 * <pre>
 * 						day
 * 			(0)		(1)		(2)		(3)		(4)
 * 			M		T		W		Th		F	 S
 * h	(0)7-8		0		1		2		3		4	5
 * o	(1)8-9		6		7		8		9		10	11	
 * ur	(2)9-10		12		13		14		15		15	16
 * 
 *  timeslot=day+(hour*5)
 * examples: 12=2+(2*5),8=3+(1*5)
 * 
 * </pre>
 * @author jachermocilla
 * @version $Id:TimeSlot.java 548 2008-09-15 00:52:12Z jach $
 */

public class TimeSlot implements Serializable {
	public static final Integer MONDAY=new Integer(0);
	public static final Integer TUESDAY=new Integer(1);
	public static final Integer WEDNESDAY=new Integer(2);
	public static final Integer THURSDAY=new Integer(3);
	public static final Integer FRIDAY=new Integer(4);
	public static final Integer SATURDAY=new Integer(5);
	
	private int slot;	
	private int time;
	private int day;

	
	private static Map days=new Hashtable();
	
	static{
		days.put("M", TimeSlot.MONDAY);		
		days.put("T", TimeSlot.TUESDAY);
		days.put("W", TimeSlot.WEDNESDAY);
		days.put("Th", TimeSlot.THURSDAY);
		days.put("F", TimeSlot.FRIDAY);		
		days.put("Mon", TimeSlot.MONDAY);		
		days.put("Tues", TimeSlot.TUESDAY);
		days.put("Wed", TimeSlot.WEDNESDAY);
		days.put("Thurs", TimeSlot.THURSDAY);
		days.put("Fri", TimeSlot.FRIDAY);
		days.put("Sat", TimeSlot.SATURDAY);
	}
	
	public TimeSlot(String strDay, String strTime){
		init();
		computeTime(strTime);
		computeDay(strDay);
		slot=day+time*6;
	}

	private void init(){
	}
	
	public int getSlot(){
		return slot;
	}

	public static boolean isValidDay(String strDay){
		return days.containsKey(strDay);
	}

	/**
	 * Converts time to 24 hour format accepted format "10-1","11-12"
	 * @param strTime
	 * @return
	 */
	public static String to24Hour(String strTime){
		//split start and end hour
		String tokens[]=strTime.split("-");
		String start=tokens[0];
		String end=tokens[1];
		int startHour=Integer.parseInt(start);
		int endHour=Integer.parseInt(end);
		if (startHour >=1 && startHour < 7)
			startHour+=12;
		if (endHour >=1 && endHour < 8)
			endHour+=12;
		
		return startHour+"-"+endHour;
	}
	
	
	private void computeTime(String strTime){
		String tokens[]=strTime.split("-");
		String temp=tokens[0];
		int startHour=Integer.parseInt(temp);
		if (startHour >=1 && startHour < 7)
			startHour+=12;
		time=startHour-7;
	}
	
	private void computeDay(String strDay){		
		day=((Integer)days.get(strDay)).intValue();
	}
}
