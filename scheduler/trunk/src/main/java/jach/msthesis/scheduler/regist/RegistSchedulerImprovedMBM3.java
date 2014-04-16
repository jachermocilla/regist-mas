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

import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.MutableInteger;
import edu.uci.ics.jung.utils.UserData;

import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.Section;
import jach.msthesis.registration.model.SectionAssignment;
import jach.msthesis.registration.model.WriteIn;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.MaximumBipartiteMatching;
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
public class RegistSchedulerImprovedMBM3 extends PrototypeScheduler {

	// enable logging
	boolean logging = false;

	// set level to 2
	int loggingLevel = 2;

	private String subjectToWatch = "STAT 1";

	static Logger logger = Logger.getLogger(RegistSchedulerImprovedMBM3.class);

	private String stdNum;
	
	/**
	 * Default constructor
	 *
	 */
	public RegistSchedulerImprovedMBM3(){}

	/**
	 * Constructor 
	 * @param offering
	 */
	public RegistSchedulerImprovedMBM3(IOffering offering, Map writeins) {
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
				rank = new Float(slots/demands.size());
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
	
	public Set getCandidateSections(String stdNum, String subject) {
		Set retval = new HashSet();
		Schedule sked = (Schedule) schedules.get(stdNum);
		
		int allowed = sked.getWriteIn().getUnitsAllowed();
		int curr = sked.getUnitsObtained();
		if ((curr + 3) > allowed){ // quick hack only..
			//System.out.println(sked.getWriteIn()+" exceeded");
			return retval;
		}
		
		// Get the lec sections for the subject
		List lectures = offering.getLecSections(subject);
		for (Iterator ite = lectures.iterator(); ite.hasNext();) {
			Section lecture = (Section) ite.next();
			if (sked.passed(lecture)) {
				String key = lecture.getSubject().getName() + ":"+ lecture.getSectionName();
				Classlist cl = (Classlist) classlists.get(key);
				if (cl == null) {
					cl = new Classlist(lecture,null);
				}
				if (!cl.full()) {
					List labs = offering.getLabSections(subject, lecture.getSectionName());
					if (labs.size() > 0) {
						
						for (Iterator ite5 = labs.iterator();ite5.hasNext();) {
							Section lab = (Section) ite5.next();
							if (sked.passed(lab)) {
								// Create a classlist of the section if it does
								// not exist yet
								key = lab.getSubject().getName() + ":"+ lab.getSectionName();
								cl = (Classlist) classlists.get(key);
								if (cl == null) {
									cl = new Classlist(lecture,lab);
								}
								if (!cl.full()) {
									retval.add(lab);
									classlists.put(key, cl);
								}
							}
						}
					} else {
						retval.add(lecture);
						classlists.put(key, cl);
					}
				}				
			}
		}
		System.gc();
		return retval;
	}
	
	
	/**
	 * Performs the assignment of students to section
	 */
	public void assign() {
		List studs = new Vector(writeins.keySet());
		int randStud = new Random().nextInt(studs.size());
		stdNum = (String) studs.get(randStud);
		int subjectCount = 0;
		float nSubjects = offering.getAllSubjects().keySet().size();

		Map rankedSub = getRankedSubjects();
		List lst = new ArrayList(rankedSub.keySet());
		// Sort the keys of the ranked subjects based on the rank
		Collections.sort(lst);
		Collections.reverse(lst);
		
		String trace="CMSC 141";

		for (Iterator ite1 = lst.iterator();ite1.hasNext();) { // for each subject rank
			Float subjectRank = (Float) ite1.next();
			//logger.fatal(subjectRank);
			List sameRankedSubjects = (List) rankedSub.get(subjectRank);
			// Shuffle the subjects of the same rank
			Collections.shuffle(sameRankedSubjects);			
			for (Iterator ite2 = sameRankedSubjects.iterator();ite2.hasNext();) { // for each subject in the same rank
				String subject = (String) ite2.next();				
				subjectCount++;
				System.out.println("Assigning " + subject + "...");
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
					for (Iterator ite4 = l3.iterator();ite4.hasNext();) {//for each number of options
						Float numOptionsKey = (Float) ite4.next();
						List l4 = (List) optionCountMap.get(numOptionsKey);
						//if (subject.equals("BIO 70"))
						//	System.out.println("Rank: "+studentRank+":"+numOptionsKey);
						//System.out.println(l4.size());
						// Randomize
						Collections.shuffle(l4);

						// Populate L
						// TODO: Split students so we don't have a huge graph

						List students = l4;
						Collections.shuffle(students);

						int chunkSize = 50;
						int ssize = students.size();
						int chunks = ssize / chunkSize;

						int n = 0;
						for (int ck = 0; ck <= chunks; ck++) {
							List studss = new Vector();
							Set L = new HashSet();
							Set R = new HashSet();
							DirectedGraph G = new DirectedSparseGraph();
							
							StringLabeller labeller = StringLabeller.getLabeller(G);
							labeller.clear();
							
							//System.out.println("Chunk number: "+ck);
							if (subject.equals(trace))
								System.out.println("Populating L (Students Set)...");
							for (int z = 0; (z < chunkSize) && (n < ssize); z++) {
								WriteIn wri = (WriteIn) students.get(n);
								
								studss.add(wri);
								SimpleDirectedSparseVertex v;
								v=(SimpleDirectedSparseVertex)G.addVertex(new SimpleDirectedSparseVertex());
								L.add(v);								
								try {
									labeller.setLabel(v, wri.getStudentNumber());
									if (subject.equals(trace))
										System.out.println(v+" : "+labeller.getLabel(v));
								} catch (Exception e) {
									e.printStackTrace();
								}
								n++;
							}

							// Populate R
							if (subject.equals(trace))
								System.out.println("Populating R...(Slot Set)");
							List lectures = offering.getLecSections(subject);
							for (Iterator ite10 = lectures.iterator(); ite10.hasNext();) {
								Section lecture = (Section) ite10.next();
								String label = lecture.getSectionName();
								List labs = offering.getLabSections(subject,lecture.getSectionName());
								if (labs.size() > 0) {
									for (Iterator ite6 = labs.iterator(); ite6.hasNext();) {
										Section lab = (Section) ite6.next();
										// System.out.println(lab.getSectionName());
										label = lab.getSectionName();
										// System.out.println(label);
										
										int count = lab.getClassSize();
										for (int i = 0; i < count; i++) {
											SimpleDirectedSparseVertex v;
											v=(SimpleDirectedSparseVertex)G.addVertex(new SimpleDirectedSparseVertex());
											R.add(v);
											try {
												labeller.setLabel(v, label+ "-" + i);
												if (subject.equals(trace))
													System.out.println(v+" : "+labeller.getLabel(v));
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								} else {
									int count = lecture.getClassSize();
									for (int i = 0; i < count; i++) {
										SimpleDirectedSparseVertex v;
										v=(SimpleDirectedSparseVertex)G.addVertex(new SimpleDirectedSparseVertex());
										R.add(v);
										try {
											labeller.setLabel(v, label + "-"+ i);
											if (subject.equals(trace))
												System.out.println(v+" : "+labeller.getLabel(v));
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
							// Create the edges
							//System.out.print("Creating E...");
							
							//for (Iterator ite = studss.iterator(); ite.hasNext();) {
							for(Iterator ite=L.iterator();ite.hasNext();){
								Vertex l = (Vertex)ite.next();
								Set candidates = getCandidateSections(labeller.getLabel(l), subject);
								for (Iterator ite11 = candidates.iterator(); ite11.hasNext();) {
									Section section = (Section) ite11.next();
									//System.out.println(wri.getStudentNumber()+":"+subject+":"+section.getSectionName());
									int count = section.getClassSize();
									Classlist cL = (Classlist) classlists.get(section.getSubject().getName()+ ":"+ section.getSectionName());
									int start = cL.getStudents().size();
									MutableInteger mi = new MutableInteger(1);
									for (int i = start; i < count; i++) {
										SimpleDirectedSparseVertex r = (SimpleDirectedSparseVertex)labeller.getVertex(section.getSectionName()+ "-" + i);
										//System.out.println(labeller.getLabel(l)+"->"+labeller.getLabel(r));
										DirectedSparseEdge edge = (DirectedSparseEdge)GraphUtils.addEdge(G, l, r);
										edge.setUserDatum("Capacity", mi,UserData.SHARED);
									}
								}
							}
							// Solve it
							//System.out.println("Solving...");
							System.gc();
							MaximumBipartiteMatching mbm = new MaximumBipartiteMatching(G, L, R);
							Set assignments=mbm.getMatching();
							for (Iterator ite13 = assignments.iterator(); ite13.hasNext();) {

								DirectedEdge e = (DirectedEdge) ite13.next();
								
								if (subject.equals(trace)){
									System.out.println(labeller.getLabel(e.getSource())+"->"+labeller.getLabel(e.getDest()));
								}
								
								if (!labeller.getGraph().getVertices().contains(e.getSource())){
									System.out.println("Vertex not in graph!"+e.getSource());
									continue;								
								}
								String stdNum;
																	
								stdNum=labeller.getLabel(e.getSource());
								String tokens[] = labeller.getLabel(e.getDest()).split("-");
								String sectName = null;
								String labSectName = null;
								// add to schedule
								ISection sect = null;
								ISection labSect = null;

								if (tokens.length == 3) {
									labSectName = tokens[0] + "-" + tokens[1];
									labSect = offering.getLabSection(subject,labSectName);
									sectName = tokens[0];
								} else {
									sectName = tokens[0];
								}

								// TODO: add the student to the classlist and
								// the section to the schedule
								// add to classlist

								String key;
								Classlist cL;								
								if (tokens.length == 3) {
									key = subject + ":" + labSectName;
									cL = (Classlist) classlists.get(key);
									cL.addStudent(stdNum);
									classlists.put(key, cL);
								}else{
									key = subject + ":" + sectName;
									cL = (Classlist) classlists.get(key);
									cL.addStudent(stdNum);
									classlists.put(key, cL);
								}

								if (sectName.contains("-")) {
									String tmp[] = sectName.split("-");
									sect = offering.getLecSection(subject,tmp[0]);
								} else {
									sect = offering.getLecSection(subject,
											sectName);
								}

								// Create a sked node for it

								Schedule sked = (Schedule) schedules.get(stdNum);
								SkedNode skedNode = new SkedNode(sect);
								sked.addSection(skedNode);
								if (labSect != null) {
									// System.out.println(labSect.getSectionName());
									sked.addSection(new SkedNode(labSect));
								}
								SectionAssignment sectAss = new SectionAssignment(sect,labSect);
								sked.addSectionAssignment(sectAss);
							}
						}//chunk
					}
				}
				float percentComplete = ((float) (subjectCount / nSubjects) * 100);
				System.out.println("done. " + percentComplete + " complete.");
			}
		}
	}

	public void log(int level, String message){
		if (logging && level <= loggingLevel) {
			logger.info("[" + level + "]: " + message);
		}
	}

	public static void main(String args[]) {
		long start = System.currentTimeMillis();
		RegistSchedulerImprovedMBM3 scheduler;
		String prefix = "../regist-data/data/";
		RegistOffering offering = new RegistOffering(prefix + "CLASSES-SAMPLE");
		RegistWriteInSource wis = new RegistWriteInSource(prefix
				+ "WRITEIN-SAMPLE");

		wis.load();
		offering.load();

		scheduler = new RegistSchedulerImprovedMBM3(offering, wis.getWriteIns());
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
