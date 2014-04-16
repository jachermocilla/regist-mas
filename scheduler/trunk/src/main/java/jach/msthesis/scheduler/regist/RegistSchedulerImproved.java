package jach.msthesis.scheduler.regist;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.Section;
import jach.msthesis.registration.model.SectionAssignment;
import jach.msthesis.registration.model.WriteIn;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.PrototypeScheduler;
import jach.msthesis.scheduler.Schedule;
import jach.msthesis.scheduler.AllScheduleOptionGenerator;
import jach.msthesis.scheduler.SkedNode;
import jach.msthesis.scheduler.statistics.Statistics;
import jach.msthesis.scheduler.util.RegistScheduleExporter;

/**
 * This class attempts to implement the original algorithm in assigning sections
 * to students in Regist
 * 
 * @author
 * 
 */
public class RegistSchedulerImproved extends PrototypeScheduler {

	// enable logging
	boolean logging = false;

	// set level to 2
	int loggingLevel = 2;

	private String subjectToWatch = "STAT 1";

	static Logger logger = Logger.getLogger(RegistSchedulerImproved.class);

	private String stdNum;
	
	/**
	 * Default constructor
	 *
	 */
	public RegistSchedulerImproved(){}

	/**
	 * Constructor 
	 * @param offering
	 */
	public RegistSchedulerImproved(IOffering offering, Map writeins) {
		super(offering, writeins);
		BasicConfigurator.configure();
		logger.setLevel(Level.FATAL);
	}

	/**
	 * This method will rank the subjects.
	 * The ranking is based using a hash table
	 * @return
	 */
	public Map getRankedSubjects() {
		log(1, "Sorting subjects based on rank.");
		
		//Get all the subjects
		List sorted = new Vector(offering.getAllSubjects().keySet());
		Map ranks = new Hashtable();
		
		//Sort the subjects
		Collections.sort(sorted);		
		
		//For each subject
		for (Iterator iteSorted = sorted.iterator();iteSorted.hasNext();) {
			String subject = (String) iteSorted.next();

			//Get the lecture sections
			List lecSections = (List) offering.getLecSections(subject);
			
			
			//contains the current number of timeslots
			int timeSlots = 0;
			
			//for each lecture section
			for (Iterator ite3 = lecSections.iterator();ite3.hasNext();) {
				ISection lectSect = (ISection) ite3.next();
				
				//Add the number of timeslots for the section to the total
				//time slots
				timeSlots += lectSect.getTimeSlots().size();
				
				//Get the lab sections
				List labSections = (List) offering.getLabSections(subject,
						lectSect.getSectionName());
				
				
				//For each lab section 
				for (Iterator ite4 = labSections.iterator();ite4.hasNext();) {
					ISection labSect = (ISection) ite4.next();
					
					//Add the time slots to the total timeslots
					timeSlots += labSect.getTimeSlots().size();
				}
			}

			//Get the slot count for the subject
			float slots = offering.getSlotCount(subject);
			
			//Get the number of students demanding the subject
			Vector demands = (Vector) demandMap.get(subject);
			
			Float rank;
			if (demands.size() != 0)
				//rank = new Float(slots/demands.size());
				rank = new Float(demands.size()/slots);
			else
				rank = new Float(1);
			
			//String key = subject;
			// Attempted different ranking schemes!
			// Float rank = new Float(offering.getSubjectRank(key));
			//rank = new Float(timeSlots);
			//rank = new Float(offering.getSubjectRank(subject)/timeSlots);
			//rank = new Float(timeSlots/offering.getSubjectRank(subject));
			// String tmp = timeSlots+""+(int)offering.getSubjectRank(key);
			// Float rank = new Float(Float.parseFloat(tmp));
			// logger.info(rank);
			List sameRankSubjectList = (List) ranks.get(rank);
			if (sameRankSubjectList == null)
				sameRankSubjectList = new Vector();
			sameRankSubjectList.add(subject);
			ranks.put(rank, sameRankSubjectList);
			// logger.info(key+","+rank+","+timeSlots);
		}
		return ranks;
	}

	/**
	 * Ranked students
	 * @param subject
	 * @return
	 */
	public Map getRankedStudents(String subject) {
		log(3,
				"Obtaining students interested in subject and sorting based on priority.");
		Vector students = (Vector) demandMap.get(subject);
		Map returnMap = new Hashtable();

		Map priorityMap = new Hashtable();
		
		for (Iterator ite1 = students.iterator();ite1.hasNext();) {
			WriteIn
			wri = (WriteIn) ite1.next();
			Float priority = new Float(wri.getPriority());
			List samePriorityList = (List) priorityMap.get(priority);
			if (samePriorityList == null)
				samePriorityList = new Vector();
			samePriorityList.add(wri);
			priorityMap.put(priority, samePriorityList);
		}

		// TODO: sort the students on the same rank based on the number of
		// options available
		//
		
		for (Iterator ite2 = priorityMap.keySet().iterator();ite2.hasNext();) { // for each rank
			Map sameRankMap = new Hashtable();
			Float studentRank = (Float) ite2.next();
			// logger.info(rank);
			List sameRankedStudents = (List) priorityMap.get(studentRank);
			
			for (Iterator ite3 = sameRankedStudents.iterator();ite3.hasNext();) { // for each student on the same rank
				WriteIn studentWI = (WriteIn) ite3.next();
				// Get the schedule of the student
				Schedule sked = (Schedule) schedules.get(studentWI.getStudentNumber());

				// Get the lec sections for the subject
				List lectures = offering.getLecSections(subject);

				// Get the lecture sections to count the number of options
				float numOptions = 0;
				float totalNumOptions = 0;
				Classlist cl;
				String clkey;
				
				for (Iterator ite4 = lectures.iterator();ite4.hasNext();) {
					totalNumOptions++;
					Section lecture = (Section) ite4.next();
					// Passed constraints
					if (sked.passed(lecture)) {
						// Count as option only if section is not full
						clkey = lecture.getSubject().getName() + ":"+ lecture.getSectionName();
						cl = (Classlist) classlists.get(clkey);						
						if (cl == null) {
							cl=new Classlist(lecture,null);
						}
						
						// Get the lab if any
						List labs = offering.getLabSections(subject, lecture.getSectionName());
						if (labs.size() > 0) {
							totalNumOptions++;
							for (Iterator ite5 = labs.iterator();ite5.hasNext();) {
								Section lab = (Section) ite5.next();
								if (sked.passed(lab)) {
									// Create a classlist of the section if it
									// does not exist yet
									clkey = lab.getSubject().getName() + ":" + lab.getSectionName();
									cl = (Classlist) classlists.get(clkey);
									if (cl == null) {
										cl = new Classlist(lecture,lab);
									}
									classlists.put(clkey,cl);
									if (!cl.full()) {
										numOptions++;
									}
								}
							}
						}else{
							classlists.put(clkey,cl);
							if (!cl.full()) {
								numOptions++;
							}
						}
					}
				}


				float currentLoad = sked.getUnitsObtained();
				float allowed = studentWI.getUnitsAllowed();
				float percentageLoad = currentLoad / allowed;

				float percentageNumOptions = numOptions / totalNumOptions;

				ISubject subji = studentWI.getSubjectInfo(subject);
				float subjectPriority = (studentWI.getSubjectPriority(subji)-1)/
										studentWI.getSubjects().size();
				
				Float key = new Float(percentageNumOptions * 0.0
									+ percentageLoad * 0.0
									+ (1-subjectPriority)*1.0);
				
				//key is based on the priority of the subject in the writein								

					
				List tmpL = (List) sameRankMap.get(key);
				if (tmpL == null) {
					tmpL = new Vector();
				}
				studentWI.setOptionCount(numOptions);
				tmpL.add(studentWI);
				sameRankMap.put(key, tmpL);
			}

			returnMap.put(studentRank, sameRankMap);
		}

		return returnMap;
	}
	
	/**
	 * Rank lab sections
	 * @param labs
	 */
	private void rankLabSections(List labs) {
		// Sort lab sections, more slots first
		Collections.sort(labs, new Comparator() {
			public int compare(Object o1, Object o2) {
				
				Section s1 = (Section) o1;
				Section s2 = (Section) o2;

				String key1 = s1.getSubject()
						.getName()
						+ ":" + s1.getSectionName();
				Classlist labCl1 = (Classlist) classlists
						.get(key1);
				/*
				if (labCl1 == null) {
					classlists.put(key1,
							new Classlist(s1));
				}
				labCl1 = (Classlist) classlists
						.get(key1);
				*/
				
				String key2 = s2.getSubject()
						.getName()
						+ ":" + s2.getSectionName();
				Classlist labCl2 = (Classlist) classlists
						.get(key2);
				/*
				if (labCl2 == null) {
					classlists.put(key2,
							new Classlist(s2));
				}
				labCl2 = (Classlist) classlists
						.get(key2);
				*/
				
				return 0;
				/*
				if ((labCl1 != null) && (labCl2 != null)){
					if (labCl1.getStudents().size() >= 10 && labCl2.getStudents().size() >= 10){
						return (labCl1.getStudents().size() - labCl2
								.getStudents().size());
					}else{
						return (labCl2.getStudents().size() - labCl1
								.getStudents().size());
					}
				}else{
					return 0;
				}
				*/	
			}
		});
	}

	/**
	 * Rank lecture sections
	 * @param lectures
	 */
	private void rankLectureSections(List lectures) {
		// Sort lecture sections,more slots first
		// Tries to make sections full
		Collections.sort(lectures, new Comparator() {
			public int compare(Object o1, Object o2) {
				Section s1 = (Section) o1;
				Section s2 = (Section) o2;

				String key1 = s1.getSubject().getName()
						+ ":" + s1.getSectionName();
				Classlist lecCl1 = (Classlist) classlists
						.get(key1);

				/*
				if (lecCl1 == null) {
					classlists.put(key1, new Classlist(s1,null));
				}
				lecCl1 = (Classlist) classlists.get(key1);
				*/
				
				String key2 = s2.getSubject().getName()
						+ ":" + s2.getSectionName();
				Classlist lecCl2 = (Classlist) classlists
						.get(key2);

				/*
				if (lecCl2 == null) {
					classlists.put(key2, new Classlist(s2,null));
				}
				lecCl2 = (Classlist) classlists.get(key2);
				*/
				/*
				if ((lecCl1 != null) && (lecCl2 != null)){
					if (lecCl1.getStudents().size() >= 10 && lecCl2.getStudents().size() >= 10){
						return (lecCl1.getStudents().size() - lecCl2.getStudents().size());
					}else{
						return (lecCl2.getStudents().size() - lecCl1.getStudents().size());
					}
				}else{
					return 0;
				}
				*/
				return 0;
					
			}
		});
	}


	/**
	 * Performs the assignment of students to section
	 */
	public void assign() {
		List studs = new Vector(writeins.keySet());
		int randStud = new Random().nextInt(studs.size());
		stdNum = (String) studs.get(randStud);

		Map rankedSub = getRankedSubjects();
		List l = new ArrayList(rankedSub.keySet());
		// Sort the keys of the ranked subjects based on the rank
		Collections.sort(l);
		//Collections.reverse(l);

		for (Iterator ite1 = l.iterator();ite1.hasNext();) { // for each subject rank
			Float subjectRank = (Float) ite1.next();
			logger.fatal(subjectRank);
			List sameRankedSubjects = (List) rankedSub.get(subjectRank);
			// Shuffle the subjects of the same rank
			Collections.shuffle(sameRankedSubjects);			
			for (Iterator ite2 = sameRankedSubjects.iterator();ite2.hasNext();) { // for each subject in the same rank
				String subject = (String) ite2.next();				
				Map rankedStu = getRankedStudents(subject);
				List l2 = new ArrayList(rankedStu.keySet());
				// Sort the keys of the rank of the students
				Collections.sort(l2);
				//Collections.reverse(l2);				
				for (Iterator ite3 = l2.iterator();ite3.hasNext();) { // for each student rank
					Float studentRank = (Float) ite3.next();
					Map optionCountMap = (Map) rankedStu.get(studentRank);
					List l3 = new ArrayList(optionCountMap.keySet());
					Collections.sort(l3);
					//Highest rank first
					//Collections.reverse(l3);					
					for (Iterator ite4 = l3.iterator();ite4.hasNext();) { //for each number of options
						Float numOptionsKey = (Float) ite4.next();
						List l4 = (List) optionCountMap.get(numOptionsKey);
						// Randomize
						Collections.shuffle(l4);						
						for (Iterator ite5 = l4.iterator();ite5.hasNext();) {  //for each student
							WriteIn studentWI = (WriteIn) ite5.next();							
							enlist(studentWI, subject);
						}// while for each student
					}
				}
			}
		}
	}


	
	
	
	/**
	 * Enlist
	 * @param studentWI
	 * @param subject
	 */
	private void enlist(WriteIn studentWI, String subject) {
		// Get the schedule of the student
		Schedule sked = (Schedule) schedules.get(studentWI
				.getStudentNumber());
		
		/* guard to make sure units must not exceed allowed
		 */
		ISubject temp = studentWI.getSubjectInfo(subject);
		int unt = temp.getUnitCredit()+sked.getUnitsObtained();
		if (unt > studentWI.getUnitsAllowed()) {
			return;
		}

		
		List lectures = offering.getLecSections(subject);

		// Collections.shuffle(lectures);
		rankLectureSections(lectures);

		// less slots first?
		// Collections.reverse(lectures);

		// For each lec section
		for (Iterator ite6 = lectures.iterator();ite6.hasNext();) {
			Section lab=null;
			SectionAssignment sectAss=new SectionAssignment();
			Section lecture = (Section) ite6.next();

			// Create a classlist of the section if it does
			// not exist yet
			String clkey = lecture.getSubject().getName()+ ":" + lecture.getSectionName();
			Classlist cl = (Classlist) classlists.get(clkey);
			
			if (cl == null) {
				cl = new Classlist(lecture,null);
			}
			
			// proceed to next section if this section full
			if (cl.full()) {				
				continue;
			}

			// Create a sked node for it
			SkedNode lecnode = new SkedNode(lecture);

			// temporarily add the lecnode
			sked.addSection(lecnode);

			// proceed to next lecture section
			// since this lecture is in conflict
			// already,
			// there is no need to process its labs
			if (!sked.passed(lecnode)) {	
				sked.removeLastSectionAdded();
				continue;
			}

			sectAss.setLecture(lecture);
			
			// ok the lec node was added, however, it is not
			// a guaranteed slot unless a lab is found

			// now we get the lab sections
			List labs = offering.getLabSections(lecture.getSubject().getName(), lecture.getSectionName());

			boolean labFound = false;

			// this lecture got labs!
			if (labs.size() > 0) {
				//rankLabSections(labs);

				// less slots first?
				// Collections.reverse(labs);
				
				// try to find a lab
				for (Iterator ite7 = labs.iterator();ite7.hasNext();) {
					lab = (Section) ite7.next();					

					// Create a classlist of the section if
					// it does not exist yet
					clkey = lab.getSubject().getName()+ ":" + lab.getSectionName();

					cl = (Classlist) classlists.get(clkey);
					
					if (cl == null) {
						cl = new Classlist(lecture, lab);
					}

					// Create a node for the lab
					SkedNode labNode = new SkedNode(lab);

					// Add it to the sked
					sked.addSection(labNode);

					// Did it satisfy the constraint?
					if (!sked.passed(labNode)) {
						// remove this lab section from sked
						sked.removeLastSectionAdded();
					} else {
						if (cl.addStudent(studentWI.getStudentNumber())) {							
							sectAss.setLab(lab);
							sked.addSectionAssignment(sectAss);
							classlists.put(clkey, cl);
							// We found a lab! great							
							labFound = true;
							break;
						} else {
							sked.removeLastSectionAdded();
						}
					}
				}
				// got one
				if (labFound == true) {
					break;
				} else {
					//remove the lecture node
					sked.removeLastSectionAdded();
				}
			} else {
				if (cl.addStudent(studentWI.getStudentNumber())) {
					sked.addSectionAssignment(sectAss);
					classlists.put(clkey, cl);
					if (studentWI.getStudentNumber().equals(stdNum)) {
						log(2, "\t\t\t\tEnlisted!");
					}					
				} else {
					if (studentWI.getStudentNumber()
							.equals(stdNum)) {
						log(2, "\t\t\t\tSection is full!");
					}
					sked.removeLastSectionAdded();
					sked.removeLastSectionAdded();
				}
				break;
			}
		}
	}


	public void log(int level, String message) {
		if (logging && level <= loggingLevel) {
			logger.info("[" + level + "]: " + message);
		}
	}

	public static void main(String args[]) {
		long start = System.currentTimeMillis();
		RegistSchedulerImproved scheduler;
		String prefix = "../regist-data/data/";
		RegistOffering offering = new RegistOffering(prefix + "CLASSES-SAMPLE");
		RegistWriteInSource wis = new RegistWriteInSource(prefix
				+ "WRITEIN-SAMPLE");

		wis.load();
		offering.load();

		scheduler = new RegistSchedulerImproved(offering, wis.getWriteIns());
		scheduler.assign();
		long stop = System.currentTimeMillis();
		System.out.println("TimeMillis: " + (stop - start));

		Set studs = scheduler.getStudents();
		int randStud = new Random().nextInt(studs.size());
		String stdNum = (String) studs.toArray()[randStud];

		RegistScheduleExporter exporter = new RegistScheduleExporter(scheduler,
				prefix + "FORM5-SAMPLE", "2004", "SECOND");
		exporter.export();

		Statistics stat = new Statistics(scheduler);
		stat.display();

		// scheduler.validate();

		System.out.println("PL(" + stdNum + ") :"
				+ stat.getPercentageLoad(stdNum));
		Iterator ite = scheduler.getSchedule(stdNum).getSections().iterator();
		while (ite.hasNext()) {
			Section s = (Section) ite.next();
			System.out.println(s.getSubject().getName() + " "
					+ s.getSectionName());
		}

	}

}
