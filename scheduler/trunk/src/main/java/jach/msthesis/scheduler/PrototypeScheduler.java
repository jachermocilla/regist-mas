package jach.msthesis.scheduler;



import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.IWriteIn;
import jach.msthesis.registration.model.Section;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * ProtoypeScheduler is the base class for all schedulers. All schedulers
 * should extend this class
 * 
 * @author jach
 * @version $Id:PrototypeScheduler.java 846 2008-09-30 02:17:08Z jach $
 */

public class PrototypeScheduler implements IScheduler {

	/**
	 * Map: <subject:section, Classlist>
	 */
	protected Map classlists=new Hashtable();
	
	/**
	 * Map: <stdnum, Schedule>
	 */
	protected Map schedules = new Hashtable();
	
	/**
	 * Map: <stdnum, WriteIn>
	 */
	protected Map writeins;
	
	/** 
	 * Reference to course offerings
	 */
	protected IOffering offering;
	
	/**
	 * Demand Map: <subject, list_of_students_interested>
	 */
	protected Map demandMap = new Hashtable();
	
	/**
	 * Description for the scheduler
	 */
	protected String desc="Prototype Scheduler";
	
	/**
	 * Default constructor
	 */
	public PrototypeScheduler(){}
	
	/**
	 * Constructor
	 */
	public PrototypeScheduler(IOffering offering, Map writeins){
		this.offering = offering;
		this.writeins = writeins;
		/*
		 * Initialize immediately
		 */
		init();
	}
	
	/**
	 * Returns the description of the scheduler
	 * @return
	 */
	public String getDescription(){
		return desc;
	}
	
	
	/**
	 * Initialization which prepares the demand map.
	 * Must be explicitly called
	 */
	public void init(){
		//initialize the demand map
		Iterator ite=this.offering.getAllSubjects().keySet().iterator();
		while(ite.hasNext()){
			String subject=(String)ite.next();
			demandMap.put(subject, new Vector());
		}
		
		//initialize schedules
		ite = writeins.keySet().iterator();
		while (ite.hasNext()){
			//Get the next student from writein data
			String stdNum = (String)ite.next();			
			IWriteIn wi = (IWriteIn)writeins.get(stdNum);			
			
			//Create a new schedule with units allowed based on the writein
			Schedule sked = new Schedule(wi);			
			
			//put it in the schedules
			schedules.put(stdNum, sked);
		
			//student added to list who wants to take the subject
			Iterator ite2 = wi.getSubjects().iterator();
			while (ite2.hasNext()){
				ISubject subject = (ISubject)ite2.next();				
				List interestedStudents = (List)demandMap.get(subject.getName());
				if (interestedStudents != null){
					interestedStudents.add(wi);
					demandMap.put(subject.getName(),interestedStudents);
				}				
			}
		}
		
		//Initializa the classlists
		Iterator subjectIterator=offering.getAllSubjects().keySet().iterator();
		while(subjectIterator.hasNext()){
			String subjectName=(String)subjectIterator.next();
			List lectureSections = offering.getLecSections(subjectName);
			for (Iterator ite3=lectureSections.iterator();ite3.hasNext();){
				Section lectureSection=(Section)ite3.next();
				List labSections=offering.getLabSections(subjectName, lectureSection.getSectionName());
				if(labSections.size() == 0){
					String key=subjectName+":"+lectureSection.getSectionName();
					//System.out.println("Lecture:"+key);
					classlists.put(key,new Classlist(lectureSection,null));
					continue;
				}else{
					for(Iterator ite4=labSections.iterator();ite4.hasNext();){
						Section labSection=(Section)ite4.next();
						String key=subjectName+":"+labSection.getSectionName();
						//System.out.println("Lab:"+key);
						classlists.put(key,new Classlist(lectureSection,labSection));
					}
				}
			}
		}
	}
	
	/**
	 * Returns the classlist for the subject and section (lec or lab)
	 */
	public Classlist getClasslist(String subject, String section) {
		String key = subject+":"+section;
		return (Classlist)classlists.get(key);
	}

	/**
	 * Returns a Map<sectionaName, Classlist> of classlists for a subject 
	 */
	public Map getAllClasslistsForSubject(String subject){
		Map retval = new HashMap();
		List enlistors = new ArrayList(getClasslists().keySet());
		for (Iterator ite=enlistors.iterator();ite.hasNext();){
			String key = (String)ite.next();
			//System.out.println(key);
			String tokens[] = key.split(":");
			if (tokens[0].equals(subject)){
				Classlist cl = getClasslist(tokens[0],tokens[1]);
				retval.put(tokens[1],cl);
			}
		}
		return retval;
	}
	
	
	/**
	 * Returns the schedule given a student number
	 */
	public Schedule getSchedule(String stdNum) {
		return (Schedule)schedules.get(stdNum);
	}

	/**
	 * Returns the set<StudentNumber> of the students used in this scheduler
	 */
	public Set getStudents() {
		return writeins.keySet();
	}

	/**
	 * Returns a set<Student Number> of students with schedules
	 */
	public Set getStudentsWithSchedule(){
		return schedules.keySet();
	}
	
	/**
	 * Returns the set of sections (lec and lab)
	 */
	public Set getSubjectSections() {
		return classlists.keySet();
	}
	
	/**
	 * Returns the writein given a student number
	 */
	public IWriteIn getWriteIn(String stdNum) {
		return (IWriteIn)writeins.get(stdNum);	
	}

	/**
	 * Returns the map of all classlists
	 */
	public Map getClasslists(){
		return classlists;
	}
	
	/**
	 * Returns the map of all schedules
	 */
	public Map getSchedules(){
		return schedules;
	}
	
	/**
	 * Returns the map of all writeins
	 */
	public Map getWriteIns(){
		return writeins;
	}
	
	/**
	 * Do the assignment within this method
	 */
	public void assign(){}
	
	/**
	 * Returns the offering data used by the scheduler 
	 */	
	public IOffering getOffering(){
		return offering;
	}
	
	/**
	 * Returns the demand map
	 */
	public Map getDemandMap(){
		return demandMap;
	}
}
