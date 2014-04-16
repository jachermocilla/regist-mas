package jach.msthesis.scheduler.regist;

import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.IWriteIn;
import jach.msthesis.registration.model.Section;
import jach.msthesis.registration.model.WriteIn;
import jach.msthesis.registration.model.datasource.IWriteInSource;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.MaximumBipartiteMatching;
import jach.msthesis.scheduler.PrototypeScheduler;
import jach.msthesis.scheduler.Schedule;
import jach.msthesis.scheduler.SkedNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.MutableInteger;
import edu.uci.ics.jung.utils.UserData;

public class RegistSchedulerImprovedMBM extends PrototypeScheduler{

	//enable logging	
	boolean logging=false;
	
	//set level to 2
	int loggingLevel = 2;
	
	
	//Map of section options
	Map sectionOptionsMap = new Hashtable();
	
	//Map: <subject, list of students interested>
	Map subjectMap=new Hashtable();
	
	//Offering data	
	IOffering offering;
	
	//Writein Source
	IWriteInSource wis;
	
	//filter
	private String stdNum="2003-49187";
	
	private String subjectToWatch = "STAT 1";
	
	public RegistSchedulerImprovedMBM(){
		
	}
	
	/**
	 * 
	 * @param offering
	 */
	public RegistSchedulerImprovedMBM(IWriteInSource wis, IOffering offering ){
		this.wis = wis;
		this.offering=offering;
	}
	
	public void init(){
		
		//Writeins
		this.writeins = wis.getWriteIns();
		
		//initialize the subjectMap to be used
		//for storing interested students
		Iterator ite=this.offering.getAllSubjects().keySet().iterator();
		while(ite.hasNext()){
			String subject=(String)ite.next();
			subjectMap.put(subject, new Vector());
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
				//System.out.println(subject.getName());
				List interestedStudents = (List)subjectMap.get(subject.getName());
				if (interestedStudents != null){
					interestedStudents.add(wi);
					subjectMap.put(subject.getName(),interestedStudents);
				}				
			}
		}
	}
	

	/**
	 * This method will rank the subjects
	 * @return
	 */
	public Map rankedSubjects(){
		log(1,"Sorting subjects based on rank.");
		List sorted=new Vector(offering.getAllSubjects().keySet());
		Map ranks = new Hashtable();
		Collections.sort(sorted);
		Iterator iteSorted = sorted.iterator();
		while(iteSorted.hasNext()){
			String key = (String)iteSorted.next();			
			//System.out.println(key+":"+rank);			
			
			List lecSections = (List)offering.getLecSections(key);			
			Iterator ite3 = lecSections.iterator();
			int timeSlots = 0;
			while (ite3.hasNext()){
				ISection lectSect = (ISection)ite3.next();
				timeSlots += lectSect.getTimeSlots().size();
				//System.out.println(lectSect.getSectionName());
				List labSections = (List)offering.getLabSections(key,lectSect.getSectionName());
				Iterator ite4 = labSections.iterator();
				while (ite4.hasNext()){
					ISection labSect = (ISection)ite4.next();
					//timeSlots += labSect.getTimeSlots().size();
				}				
			}
			
			float slots=offering.getSlotCount(key);
			Vector students=(Vector)subjectMap.get(key);
			Float rank;
			if (students.size() != 0)
				rank =new Float(slots/students.size());
			else
				rank = new Float(1);
			
			//rank = new Float(offering.getSubjectRank(key));
			rank = new Float(timeSlots);
			//Float rank = new Float(offering.getSubjectRank(key)/timeSlots);

			//Float rank = new Float(timeSlots/offering.getSubjectRank(key));
			//String tmp = timeSlots+""+(int)offering.getSubjectRank(key);
			//Float rank = new Float(Float.parseFloat(tmp));
			//System.out.println(rank);
			
			List sameRankList = (List)ranks.get(rank);
			if (sameRankList == null)
				sameRankList = new ArrayList();
			sameRankList.add(key);
			ranks.put(rank, sameRankList);
			
			//System.out.println(key+","+rank+","+timeSlots);
		}
		return ranks;
	}
	
	
	
	/**
	 * Performs the assignment of students to section
	 */
	public void assign(){
		List studs=new Vector(writeins.keySet());
		int randStud = new Random().nextInt(studs.size());
		stdNum=(String)studs.get(randStud);
		
		Map rankedSub = rankedSubjects();		
		List subs=new ArrayList(rankedSub.keySet());
		//Sort the keys of the ranked subjects based on the rank
		Collections.sort(subs);
		Iterator ite1 = subs.iterator();
		while (ite1.hasNext()){ //for each subject rank
			Float subjectRank = (Float)ite1.next();
			//System.out.println(subjectRank);
			List sameRankedSubjects = (List)rankedSub.get(subjectRank);
			//Shuffl the subjects of the same rank
			Collections.shuffle(sameRankedSubjects);
			Iterator ite2 = sameRankedSubjects.iterator();
			while (ite2.hasNext()){ //for each subject in the same rank
				String subject = (String)ite2.next();
				//System.out.println(subject);
				
				//At this point we get the list of students interested in 
				//the subject. We create an instance of bipartite graph
				//matching problem and solve it. Goal is to maximize number
				//of students assigned to subject
				
				//Let G = (V, E),
				//Let L, R be subsets of V such that L union R equals V
				//Let L be the set of students interested in a subject
				//Let R be the set of sections with available slots
				//let E = { (l,r) | l is an element of L, r is an element of R,
				//					there is no conflict in the schedule of l
				//					in case r is added to l's schedule }
				

				
				Set L = new HashSet();
				Set R = new HashSet();		
				DirectedSparseGraph G = new DirectedSparseGraph();
				StringLabeller labeller = StringLabeller.getLabeller(G);
				
				//Populate L				
				List students=(List)subjectMap.get(subject);
				System.out.println("Number of demands for "+subject+": "+students.size());
				System.out.println("Number of slots for "+subject+": "+offering.getSlotCount(subject));
				
				Collections.shuffle(students);
				//System.out.println(students.size());
				System.out.print("Populating L...");
				for(Iterator ite=students.iterator();ite.hasNext();){
					WriteIn wri = (WriteIn)ite.next();
					//System.out.println(wri.getStudentNumber());
					SparseVertex v = new SparseVertex();					
					L.add(v);
					G.addVertex(v);					
					try{
						labeller.setLabel(v,wri.getStudentNumber());
						//System.out.println(labeller.getLabel(v));
					}
					catch(Exception e){e.printStackTrace();}
				}
				
				//Populate R
				System.out.print("Populating R...");
				List lectures = offering.getLecSections(subject);
				for(Iterator ite5=lectures.iterator();ite5.hasNext();){
					Section lecture = (Section)ite5.next();
					String label = lecture.getSectionName();
					List labs=offering.getLabSections(subject,lecture.getSectionName());
					if (labs.size() > 0){					
						for (Iterator ite6=labs.iterator();ite6.hasNext();){
							Section lab = (Section)ite6.next();
							//	System.out.println(lab.getSectionName());
							label = lab.getSectionName();
							//System.out.println(label);
							int count=lab.getClassSize();
							for(int i=0;i < count; i++){
								SparseVertex v = new SparseVertex();					
								R.add(v);
								G.addVertex(v);
								try{
									labeller.setLabel(v,label+"-"+i);
									//System.out.println(labeller.getLabel(v));
								}
								catch(Exception e){e.printStackTrace();}
							}
						}
					}else{
						int count = lecture.getClassSize();
						for(int i=0;i<count;i++){
							SparseVertex v = new SparseVertex();					
							R.add(v);
							G.addVertex(v);
							try{
								labeller.setLabel(v,label+"-"+i);
								//System.out.println(labeller.getLabel(v));
							}
							catch(Exception e){e.printStackTrace();}
						}
					}						
				}
				
				//Create the edges
				System.out.print("Creating E...");
				for(Iterator ite=students.iterator();ite.hasNext();){
					WriteIn wri = (WriteIn)ite.next();
					Set candidates = getCandidateSections(wri.getStudentNumber(),subject);
					for(Iterator ite8=candidates.iterator();ite8.hasNext();){
						Section section=(Section)ite8.next();
						//System.out.println(wri.getStudentNumber()+":"+subject+":"+section);
						Vertex l = labeller.getVertex(wri.getStudentNumber());
						
						int count=section.getClassSize();
						int start;
						MutableInteger mi= new MutableInteger(1);
						start=0;
						for(int i=start;i<count;i++){
							Vertex r = labeller.getVertex(section.getSectionName()+"-"+i);
							//System.out.println(labeller.getLabel(l)+"->"+labeller.getLabel(r));
							Edge edge = GraphUtils.addEdge(G, l, r);
							edge.setUserDatum("Capacity",mi, UserData.SHARED);
						}
					}
				}
				
				//Solve it
				System.out.println("Solving...");
				System.gc();
				MaximumBipartiteMatching mbm = new MaximumBipartiteMatching(G,L,R);				
				/*
				for (Iterator ite=mbm.getMatching().iterator();ite.hasNext();){
					DirectedEdge e=(DirectedEdge)ite.next();
					System.out.println(labeller.getLabel(e.getSource())+","+labeller.getLabel(e.getDest()));
				}
				*/				
				System.out.println("Number of matches for "+subject+": "+mbm.getMatching().size());
			}
		}
	}
	
	public Set getCandidateSections(String stdNum,String subject){
		Set retval = new HashSet();
		Schedule sked = (Schedule)schedules.get(stdNum);
//		Get the lec sections for the subject
		List lectures = offering.getLecSections(subject);
		for (Iterator ite=lectures.iterator();ite.hasNext();){
			Section lecture = (Section)ite.next();
			if (sked.passed(lecture)){
				String key=lecture.getSubject().getName()+":"+lecture.getSectionName();
				Classlist cl=(Classlist)classlists.get(key);
				if(cl==null){
					cl=new Classlist(lecture,null);
				}
				cl=(Classlist)classlists.get(key);
				if (!cl.full()){
					List labs=offering.getLabSections(subject,lecture.getSectionName());
					if (labs.size() > 0){							
						for (Iterator ite5 = labs.iterator();ite5.hasNext();){
							Section lab = (Section)ite5.next();
							if (sked.passed(lab)){
//								Create a classlist of the section if it does not exist yet
								key=lab.getSubject().getName()+":"+lab.getSectionName();
								cl =(Classlist)classlists.get(key);					
								if(cl==null){
									cl = new Classlist(lecture,lab);
								}
								if (!cl.full()){
									retval.add(lab);
								}
							}
						}						
					}else{
						retval.add(lecture);						
					}
					classlists.put(key, cl);
				}				
			}
		}
		System.gc();
		return retval;
	}
	
	public void validate(){		
	}
	
	public void log(int level, String message){
		if (logging && level <= loggingLevel){
			System.out.println("["+level+"]: "+message);
		}
	}
	
	
	public static void main(String args[]){		
		long start = System.currentTimeMillis();
		RegistSchedulerImprovedMBM scheduler;
		//String prefix="../courselector/data/";
		String prefix="";
		RegistOffering offering=new RegistOffering(prefix+"CMSC-2002-SECOND");
		RegistWriteInSource wis = new RegistWriteInSource(prefix+"WRITEIN-2004-SECOND");
		
		wis.load();
		offering.load();
		
		scheduler=new RegistSchedulerImprovedMBM(wis,offering);
		scheduler.init();
		
		scheduler.assign();
		long stop = System.currentTimeMillis();
		
		Set studs = scheduler.getStudents(); 
		int randStud = new Random().nextInt(studs.size());
		String stdNum=(String)studs.toArray()[randStud];
		//stdNum="2006-28177";
		
		
		//scheduler.validate();
/*		
		System.out.println("Number of writeins: "+scheduler.getWriteInCount());
		System.out.println("Number of writeins with schedule: "+scheduler.getWriteInWithScheduleCount());
		

		System.out.println("PL("+stdNum+") :"+scheduler.getPercentageLoad(stdNum));
		System.out.println("APL: "+scheduler.getAveragePercentageLoad());
		System.out.println("Full Load: "+scheduler.getFullLoadCount()/(float)scheduler.getStudents().size());
		System.out.println("Underload: "+scheduler.getUnderloadCount()/(float)scheduler.getStudents().size());
		System.out.println("overload: "+scheduler.getOverloadCount()/(float)scheduler.getStudents().size());
		
		System.out.println("APCS: "+scheduler.getAveragePercentageClassSize());
		
	
		
		
		Iterator ite = scheduler.getSchedule(stdNum).getSubjects().iterator();
		while (ite.hasNext()){
			Section s = (Section)ite.next();
			System.out.println(s.getSubject().getName() +" "+s.getSectionName());
		}
*/	
		System.out.println("TimeMillis: " + (stop - start));
	}	
	
}
