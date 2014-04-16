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
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.MutableInteger;
import edu.uci.ics.jung.utils.UserData;
import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISection;
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
public class RegistSchedulerImprovedMBM2 extends PrototypeScheduler {

	// enable logging
	boolean logging = false;

	// set level to 2
	int loggingLevel = 2;

	// filter
	private String stdNum = "2003-49187";

	private String subjectToWatch = "STAT 1";

	static Logger logger = Logger.getLogger(RegistSchedulerImprovedMBM2.class);

	public RegistSchedulerImprovedMBM2() {
	}

	/**
	 * 
	 * @param offering
	 */
	public RegistSchedulerImprovedMBM2(IOffering offering, Map writeins) {
		super(offering, writeins);
		BasicConfigurator.configure();
		logger.setLevel(Level.FATAL);
	}

	/**
	 * This method will rank the subjects.
	 * 
	 * @return
	 */
	public Map rankedSubjects() {
		log(1, "Sorting subjects based on rank.");
		List sorted = new Vector(offering.getAllSubjects().keySet());
		Map ranks = new Hashtable();
		Collections.sort(sorted);
		Iterator iteSorted = sorted.iterator();
		while (iteSorted.hasNext()) {
			String subject = (String) iteSorted.next();

			// logger.info(key+":"+rank);
			List lecSections = (List) offering.getLecSections(subject);
			Iterator ite3 = lecSections.iterator();
			int timeSlots = 0;
			while (ite3.hasNext()) {
				ISection lectSect = (ISection) ite3.next();
				timeSlots += lectSect.getTimeSlots().size();
				// logger.info(lectSect.getSectionName());
				List labSections = (List) offering.getLabSections(subject,
						lectSect.getSectionName());
				Iterator ite4 = labSections.iterator();
				while (ite4.hasNext()) {
					ISection labSect = (ISection) ite4.next();
					timeSlots += labSect.getTimeSlots().size();
				}
			}

			float slots = offering.getSlotCount(subject);
			Vector students = (Vector) demandMap.get(subject);
			Float rank;
			if (students.size() != 0)
				rank = new Float(slots / students.size());
			else
				rank = new Float(1);

			// Attempted different ranking schemes!
			// Float rank = new Float(offering.getSubjectRank(key));
			// Float rank = new Float(timeSlots);
			// Float rank = new Float(offering.getSubjectRank(key)/timeSlots);
			// Float rank = new Float(timeSlots/offering.getSubjectRank(key));
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

	public Map rankedStudents(String subject) {
		log(3,
				"Obtaining students interested in subject and sorting based on priority.");
		Vector students = (Vector) demandMap.get(subject);
		Map returnMap = new Hashtable();

		Map priorityMap = new Hashtable();
		Iterator ite1 = students.iterator();
		while (ite1.hasNext()) {
			WriteIn wri = (WriteIn) ite1.next();
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
		Iterator ite2 = priorityMap.keySet().iterator();
		while (ite2.hasNext()) { // for each rank
			Map sameRankMap = new Hashtable();
			Float studentRank = (Float) ite2.next();
			// logger.info(rank);
			List sameRankedStudents = (List) priorityMap.get(studentRank);
			Iterator ite3 = sameRankedStudents.iterator();
			while (ite3.hasNext()) { // for each student on the same rank
				WriteIn studentWI = (WriteIn) ite3.next();
				// Get the schedule of the student
				Schedule sked = (Schedule) schedules.get(studentWI.getStudentNumber());

				// Get the lec sections for the subject
				List lectures = offering.getLecSections(subject);

				// Get the lecture sections to count the number of options
				float numOptions = 0;
				float totalNumOptions = 0;
				Iterator ite4 = lectures.iterator();
				while (ite4.hasNext()) {
					Section lecture = (Section) ite4.next();
					// Passed constraints
					if (sked.passed(lecture)) {
						// Count as option only if section is not full
						String key = lecture.getSubject().getName() + ":"+ lecture.getSectionName();
						Classlist cl = (Classlist) classlists.get(key);
						totalNumOptions++;
						if (cl == null) {
							cl = new Classlist(lecture,null);
						}
						if (!cl.full()) {
							numOptions++;
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
									key = lab.getSubject().getName()+ ":" + lab.getSectionName();
									cl = (Classlist) classlists.get(key);
									if (cl == null) {
										cl = new Classlist(lecture,lab);
									}
									if (!cl.full()) {
										numOptions++;
									}
								}
							}
						}
						classlists.put(key, cl);
					}
				}

				// if (studentWI.getStudentNumber().equals(stdNum)){
				// log(2,subject+" "+" ,Number of options "+numOptions);
				// }

				// Get the schedule of the student

				float currentLoad = sked.getUnitsObtained();
				float allowed = studentWI.getUnitsAllowed();
				float percentageLoad = currentLoad / allowed;
				// logger.info(allowed);

				float percentageNumOptions = numOptions / totalNumOptions;
				// key is based on the number of available options and current
				// load
				Float key = new Float(percentageNumOptions * 0.2
						+ percentageLoad * 0.8);

				List tmpL = (List) sameRankMap.get(key);
				if (tmpL == null) {
					tmpL = new Vector();
				}
				studentWI.setOptionCount(numOptions);
				tmpL.add(studentWI);
				sameRankMap.put(key, tmpL);
			}

			// logger.info(studentWI.getStudentNumber() +" "+ subject +" "+
			// numOptions);
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

		int full = 0;
		int notFull = 0;

		float nSubjects = offering.getAllSubjects().keySet().size();
		int subjectCount = 0;

		Map rankedSub = rankedSubjects();
		List lst = new ArrayList(rankedSub.keySet());
		// Sort the keys of the ranked subjects based on the rank
		Collections.sort(lst);
		Collections.reverse(lst);
		Iterator ite1 = lst.iterator();
		while (ite1.hasNext()) { // for each subject rank
			Float subjectRank = (Float) ite1.next();
			// logger.info(subjectRank);
			List sameRankedSubjects = (List) rankedSub.get(subjectRank);
			// Shuffl the subjects of the same rank
			Collections.shuffle(sameRankedSubjects);
			Iterator ite2 = sameRankedSubjects.iterator();
			while (ite2.hasNext()) { // for each subject in the same rank
				subjectCount++;
				String subject = (String) ite2.next();
				System.out.print("Assigning " + subject + "...");

				int accommodated = 0;
				if (subject.equals(subjectToWatch)) {
					logger.info(subject + ":" + subjectRank);
					Vector students = (Vector) demandMap.get(subject);
					logger.info("Processing " + subjectToWatch + "..demand: "
							+ students.size() + "..slots: "
							+ offering.getSlotCount(subject));
				}

				List interested = (List) demandMap.get(subject);
				// System.out.println("Total demand for "+subject+":
				// "+interested.size());

				// logger.info("\t"+subject);
				// Get the ranked students for the subject
				Map rankedStu = rankedStudents(subject);
				List l2 = new ArrayList(rankedStu.keySet());

				// Sort the keys of the rank of the students
				Collections.sort(l2);
				Iterator ite3 = l2.iterator();
				while (ite3.hasNext()) { // for each student rank
					Float studentRank = (Float) ite3.next();
					// logger.info(studentRank);
					Map optionCountMap = (Map) rankedStu.get(studentRank);
					List l3 = new ArrayList(optionCountMap.keySet());
					Collections.sort(l3);
					// give to those with more!
					Collections.reverse(l3);
					Iterator ite4 = l3.iterator();
					while (ite4.hasNext()) {
						Float numOptionsKey = (Float) ite4.next();
						//logger.info(studentRank+":"+numOptionsKey);
						//System.out.println(studentRank+":"+numOptionsKey);
						//get a list of students for processing
						List l4 = (List) optionCountMap.get(numOptionsKey);
						//System.out.println(l4.size());
						// Randomize
						Collections.shuffle(l4);

						// Populate L
						// TODO: Split students so we don't have a huge graph

						List students = l4;
						// System.out.println("Number of demands for
						// "+subject+": "+students.size()+" for rank "+
						// studentRank+":"+numOptionsKey);
						// System.out.println("Number of slots for "+subject+":
						// "+offering.getSlotCount(subject));
						Collections.shuffle(students);

						int chunkSize = students.size();//50;
						int ssize = students.size();
						int chunks = ssize / chunkSize;

						int n = 0;
						for (int ck = 0; ck <= chunks; ck++) {
							List studss = new Vector();
							// System.out.println("***CK = "+ck);
							Set L = new HashSet();
							Set R = new HashSet();
							DirectedSparseGraph G = new DirectedSparseGraph();
							StringLabeller labeller = StringLabeller
									.getLabeller(G);

							// System.out.println(students.size());
							// System.out.print("Populating L...");
							for (int z = 0; (z < chunkSize) && (n < ssize); z++) {
								// System.out.println("***N = "+n);
								WriteIn wri = (WriteIn) students.get(n);
								/*
								 * TODO: before adding student as vertex make
								 * sure that it does not exceed allowed units
								 */								

								studss.add(wri);
								// System.out.println(wri.getStudentNumber());
								SparseVertex v = new SparseVertex();
								L.add(v);
								G.addVertex(v);
								try {
									labeller
											.setLabel(v, wri.getStudentNumber());
									// System.out.println(labeller.getLabel(v));
								} catch (Exception e) {
									e.printStackTrace();
								}
								n++;
							}

							// Populate R
							// System.out.print("Populating R...");
							List lectures = offering.getLecSections(subject);
							for (Iterator ite10 = lectures.iterator(); ite10
									.hasNext();) {
								Section lecture = (Section) ite10.next();
								String label = lecture.getSectionName();
								List labs = offering.getLabSections(subject,
										lecture.getSectionName());
								if (labs.size() > 0) {
									for (Iterator ite6 = labs.iterator(); ite6
											.hasNext();) {
										Section lab = (Section) ite6.next();
										// System.out.println(lab.getSectionName());
										label = lab.getSectionName();
										// System.out.println(label);
										int count = lab.getClassSize();
										for (int i = 0; i < count; i++) {
											SparseVertex v = new SparseVertex();
											R.add(v);
											G.addVertex(v);
											try {
												labeller.setLabel(v, label
														+ "-" + i);
												// System.out.println(labeller.getLabel(v));
											} catch (Exception e) {
												e.printStackTrace();
											}
										}
									}
								} else {
									int count = lecture.getClassSize();
									for (int i = 0; i < count; i++) {
										SparseVertex v = new SparseVertex();
										R.add(v);
										G.addVertex(v);
										try {
											labeller.setLabel(v, label + "-"
													+ i);
											// System.out.println(labeller.getLabel(v));
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
							// Create the edges
							// System.out.print("Creating E...");
							for (Iterator ite = studss.iterator(); ite
									.hasNext();) {
								WriteIn wri = (WriteIn) ite.next();
								Set candidates = getCandidateSections(wri.getStudentNumber(), subject);
								for (Iterator ite11 = candidates.iterator(); ite11.hasNext();) {
									Section section = (Section) ite11.next();
									//System.out.println(wri.getStudentNumber()+":"+subject+":"+section.getSectionName());
									Vertex l = labeller.getVertex(wri.getStudentNumber());
									int count = section.getClassSize();
									Classlist cL = (Classlist) classlists.get(section.getSubject().getName()+ ":"+ section.getSectionName());
									int start = cL.getStudents().size();
									MutableInteger mi = new MutableInteger(1);
									for (int i = start; i < count; i++) {
										Vertex r = labeller.getVertex(section
												.getSectionName()
												+ "-" + i);
										// System.out.println(labeller.getLabel(l)+"->"+labeller.getLabel(r));
										Edge edge = GraphUtils.addEdge(G, l, r);
										edge.setUserDatum("Capacity", mi,
												UserData.SHARED);
									}
								}
							}
							// Solve it
							// System.out.println("Solving...");
							System.gc();
							MaximumBipartiteMatching mbm = new MaximumBipartiteMatching(
									G, L, R);
							for (Iterator ite13 = mbm.getMatching().iterator(); ite13
									.hasNext();) {
								DirectedEdge e = (DirectedEdge) ite13.next();

								String stdNum = labeller
										.getLabel(e.getSource());
								String tokens[] = labeller
										.getLabel(e.getDest()).split("-");
								String sectName = null;
								String labSectName = null;
								// add to schedule
								ISection sect = null;
								ISection labSect = null;

								if (tokens.length == 3) {
									labSectName = tokens[0] + "-" + tokens[1];
									labSect = offering.getLabSection(subject,
											labSectName);
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
									sect = offering.getLecSection(subject,
											tmp[0]);
								} else {
									sect = offering.getLecSection(subject,
											sectName);
								}

								// Create a sked node for it

								Schedule sked = (Schedule) schedules
										.get(stdNum);
								SkedNode skedNode = new SkedNode(sect);
								sked.addSection(skedNode);
								if (labSect != null) {
									// System.out.println(labSect.getSectionName());
									sked.addSection(new SkedNode(labSect));
								}
								SectionAssignment sectAss = new SectionAssignment(sect,labSect);
								sked.addSectionAssignment(sectAss);
								/*
								 * System.out.println(stdNum + "," + sectName +
								 * "," + (sect.getClassSize() - cL
								 * .getStudents().size()));
								 */
							}
							/*
							 * System.out .println("Number of matches for " +
							 * subject + ": " + mbm.getMatching().size());
							 */
						}

					}// while for options
				}// while for rank
				// TODO: reassign to maximize slots taken

				float percentComplete = ((float) (subjectCount / nSubjects) * 100);
				System.out.println("done. " + percentComplete + " complete.");

				if (subject.equals(subjectToWatch)) {
					logger.info(subjectToWatch + " number of accommodated: "
							+ accommodated);
				}

				Vector students = (Vector) demandMap.get(subject);
				int demand = students.size();
				if ((accommodated == offering.getSlotCount(subject))
						&& (demand != 0)
						&& (offering.getSlotCount(subject) != 0)) {
					logger.info(subject + ":" + demand + ":" + accommodated
							+ ":" + offering.getSlotCount(subject));
					full++;
				} else {
					notFull++;
				}
			}
		}
		logger.info(full + ":" + notFull);
	}

	public void validate() {

	}

	public void log(int level, String message) {
		if (logging && level <= loggingLevel) {
			logger.info("[" + level + "]: " + message);
		}
	}

	public static void main(String args[]) {
		long start = System.currentTimeMillis();
		RegistSchedulerImprovedMBM2 scheduler;
		String prefix = "../regist-data/data/";
		RegistOffering offering = new RegistOffering(prefix + "CLASSES-SAMPLE");
		RegistWriteInSource wis = new RegistWriteInSource(prefix
				+ "WRITEIN-SAMPLE");

		wis.load();
		offering.load();

		scheduler = new RegistSchedulerImprovedMBM2(offering, wis.getWriteIns());
		scheduler.assign();
		long stop = System.currentTimeMillis();
		System.out.println("TimeMillis: " + (stop - start));

		Set studs = scheduler.getStudents();
		int randStud = new Random().nextInt(studs.size());
		String stdNum = (String) studs.toArray()[randStud];
		stdNum = "2001-24910";

		RegistScheduleExporter exporter = new RegistScheduleExporter(scheduler,
				prefix + "FORM5-SAMPLE", "2002", "SECOND");
		exporter.export();

		Statistics stat = new Statistics(scheduler);
		stat.display();

		// scheduler.validate();
		/*
		 * System.out.println("PL("+stdNum+")
		 * :"+stat.getPercentageLoad(stdNum)); Iterator ite =
		 * scheduler.getSchedule(stdNum).getSubjects().iterator(); while
		 * (ite.hasNext()){ Section s = (Section)ite.next();
		 * System.out.println(s.getSubject().getName() +" "+
		 * s.getSectionName()); }
		 */

	}

}
