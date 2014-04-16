package jach.msthesis.scheduler.regist;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import jach.msthesis.courselector.DefaultSubject;
import jach.msthesis.courselector.DudaiOffering;
import jach.msthesis.courselector.IOffering;
import jach.msthesis.courselector.ISection;
import jach.msthesis.courselector.ISubject;
import jach.msthesis.courselector.Section;
import jach.msthesis.scheduler.SkedNode;


/**
 * This class implements the original algorithm in assigning sections
 * to students in Regist
 * @author jach
 *
 */
public class RegistScheduler {
	boolean logging=true;
	int loggingLevel = 2;
	
	Map sectionOptionsMap = new Hashtable();
	
	//Map: <subject, list of students interested>
	Map subjectList=new Hashtable();
	
	//Map: <subject:section, Classlist>
	Map classlists=new Hashtable();
	
	//Map: <stdnum, Schedule>
	Map schedules = new Hashtable();
	
	//Map: <stdnum, WriteIn>
	Map writeins = new Hashtable();
	
	//Offering data	
	IOffering offering;

	//filter
	private String stdNum="2001-56024";
	
	public RegistScheduler(){}
	
	/**
	 * 
	 * @param offering
	 */
	public RegistScheduler(IOffering offering){
		this.offering=offering;
		Iterator ite=this.offering.getAllSubjects().keySet().iterator();
		//Initialize sibjectList 
		while(ite.hasNext()){
			String subject=(String)ite.next();
			subjectList.put(subject, new Vector());
		}		
	}
	
	
	public void loadWriteIn(String path){
		BufferedReader in=null;
		String line;
		String[] tokens=null;
						
		try{
			in=new BufferedReader(new FileReader(path));
			line=in.readLine();
			while(line != null){				
				if (!line.startsWith("#")){
					//System.out.println(line);
					tokens=line.split(",");
					String stdNum=tokens[0];
					WriteIn wri=(WriteIn)writeins.get(stdNum);
					if (wri == null){
						wri=new WriteIn(tokens[0],Integer.parseInt(tokens[9]),
								Integer.parseInt(tokens[8]));
						writeins.put(stdNum,wri);
					}
					//Create default subject with term and year to 0 for now.					
					DefaultSubject ds=new DefaultSubject(tokens[3],0,0,Integer.parseInt(tokens[5]));					
					wri.addSubject(ds);
					schedules.put(tokens[0], new Schedule(Integer.parseInt(tokens[8])));
					//student added to list who wants to take the subject
					Vector v=(Vector)subjectList.get(tokens[3]);
					if (v != null){
						v.add(wri);
						subjectList.put(tokens[3],v);
					}
				}
				line=in.readLine();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	/**
	 * Performs the assignment of students to section
	 *
	 */
	public void assign(){
		
		List studs=new Vector(writeins.keySet());
		int randStud = new Random().nextInt(studs.size());
		stdNum=(String)studs.get(randStud);
		
		//stdNum="2001-56024";
		
		log(2,"Student: "+stdNum);		
				
		log(1,"Sorting subjects based on rank.");
		List sorted=new Vector(offering.getAllSubjects().keySet());
		//Collections.sort(sorted);
		Collections.sort(sorted,new Comparator(){
			public int compare(Object o1, Object o2){
				String s1=(String) o1;
				String s2=(String) o2;
				float t1 = offering.getSubjectRank(s1);
				float t2 = offering.getSubjectRank(s2);
				return ((int)(t1-t2));
			}
		});
		
		//TODO: Select random subject of the same rank
		
		//Iterator ite=this.offering.getAllSubjects().keySet().iterator();
		Iterator ite=sorted.iterator();
				
		log(1,"Processing each subject.");
		//For each subject
		while(ite.hasNext()){
			String subject=(String)ite.next();
			//if (subject.startsWith("CMSC"))
			//	System.out.println("Processing subject: "+subject);
			log(3,"Obtaining students interested in subject and sorting based on priority.");
			Vector students=(Vector)subjectList.get(subject);			
			
			//Sort students according to priority
			Collections.sort(students,new Comparator(){
				public int compare(Object o1, Object o2){
					WriteIn s1=(WriteIn) o1;
					WriteIn s2=(WriteIn) o2;
					return (s1.getPriority()-s2.getPriority());
				}
			});
			
			//TODO: Select a random student for those with the same priority
			
			Iterator ite2=students.iterator();
			//For each student
			while (ite2.hasNext()){
				
				//Get the writein information for the student
				
				WriteIn wi=(WriteIn)ite2.next();				
				
				if (wi.getStudentNumber().equals(stdNum)){
					log(2,"Processing "+ subject + " Rank: "+offering.getSubjectRank(subject));
				}
				
				
				//Get all lec sections for the subject
				Iterator ite3=offering.getLecSections(subject).iterator();
				//For each lec section
				while (ite3.hasNext()){
					//Get the schedule of the student
					Schedule sked=(Schedule)schedules.get(wi.getStudentNumber());
					
					//units must not exceed allowed
					ISubject temp=wi.getSubjectInfo(subject);					
					int unt=temp.getUnitCredit()+sked.getUnitsObtained(wi);					
					if (unt > wi.getUnitsAllowed()){
						if (wi.getStudentNumber().equals(stdNum)){
							log(2,"\t\t\tMax units allowed exceeded!");
						}
						break;
					}
					
					Section lecture=(Section)ite3.next();
					if (wi.getStudentNumber().equals(stdNum)){
						log(2,"\t\tProcessing lec section: "+lecture.getSectionName());
					}
					
										
					//Create a classlist of the section if it does not exist yet
					String key=lecture.getSubject().getName()+":"+lecture.getSectionName();
					Classlist lecCl=(Classlist)classlists.get(key);					
					if(lecCl==null){
						classlists.put(key,new Classlist(lecture));
					}
					lecCl=(Classlist)classlists.get(key);
					
					//proceed to next section if this section full
					if (lecCl.full()){
						if (wi.getStudentNumber().equals(stdNum)){
							log(2,"\t\t\tSection is full");
						}
						continue;
					}					
					
					
					//Create a sked node for it
					SkedNode lecnode=new SkedNode(lecture);
					
					//temporarily add the lecnode
					sked.addSection(lecnode);
					
					if (!sked.passed(lecnode)){
						//proceed to next lecture section
						//since this lecture is in conflict already,
						//there is no need to process its labs
						if (wi.getStudentNumber().equals(stdNum)){
							log(2,"\t\t\tSchedule conflict with this lecture!");
						}
						sked.removeLastSectionAdded();
						continue;
					}
					
					//ok the lec node was added, however, it is not
					//a guaranteed slot unless a lab is found
				
					
					//now we get the lab sections
					List labs=offering.getLabSections(lecture.getSubject().getName(),lecture.getSectionName());					
					
					boolean labFound=false;
					int labCount=labs.size();
					Classlist labCl=null;
					
					//this lecture got labs!
					if (labCount > 0){
						Iterator ite4=labs.iterator();
						
						//try to find a lab
						while (ite4.hasNext()){
							Section lab=(Section)ite4.next();
							
							if (wi.getStudentNumber().equals(stdNum)){
								log(2,"\t\t\tProcessing lab section: "+lab.getSectionName());
							}
							
//							Create a classlist of the section if it does not exist yet
							String key2=lab.getSubject().getName()+":"+lab.getSectionName();
							labCl =(Classlist)classlists.get(key2);					
							if(labCl==null){
								classlists.put(key2,new Classlist(lab));								
							}
							labCl=(Classlist)classlists.get(key2);
							
							//Create a node for the lab
							SkedNode labNode=new SkedNode(lab);
							
							//Add it to the sked
							sked.addSection(labNode);
							
							//Did it satisfy the constraint?
							if(!sked.passed(labNode)){
								if (wi.getStudentNumber().equals(stdNum)){
									log(2,"\t\t\t\tSchedule conflict with this lab!");
								}
								//remove this lab section from sked
								sked.removeLastSectionAdded();								
							}else{				
								
								//Lets remove the lecture from the sked!!!
								//sked.removeLastSectionAdded();	
								//sked.removeLastSectionAdded();
								//sked.addSection(labNode);
								if (labCl.addStudent(wi.getStudentNumber())) {
									lecCl.addStudent(wi.getStudentNumber());
									if (wi.getStudentNumber().equals(stdNum)){
										log(2,"\t\t\t\tEnlisted!");
									}
//									We found a lab! great
									labFound=true;
									break;
								}else{
									if (wi.getStudentNumber().equals(stdNum)){
										log(2,"\t\t\t\tSection is full!");
									}
									sked.removeLastSectionAdded();	
									//sked.removeLastSectionAdded();
								}	
							}
						}		
						
						//got one
						if (labFound==true){
							break;
						}else{
							sked.removeLastSectionAdded();
						}
					}else{
						if (lecCl.addStudent(wi.getStudentNumber())){
							if (wi.getStudentNumber().equals(stdNum)){
								log(2,"\t\t\t\tEnlisted!");
							}
						}else{
							if (wi.getStudentNumber().equals(stdNum)){
								log(2,"\t\t\t\tSection is full!");
							}
							sked.removeLastSectionAdded();	
							sked.removeLastSectionAdded();
						}
						break;
					}
					
				}
			}
		}	
	}
	
	public void assign2(){
		/**
		 * Foreach r in R
		 * 		Let Yr = subset of Vt such that for each element y,
		 * 			[not conflict(X,y) and not full(y)]
		 * 		
		 * 		Order Yr based on desirability
		 * 
		 * Foreach c in {1,2,3,4,5,6}
		 * 		Let q = subset of R belonging to c
		 * 		Order Q based on |Yq|
		 * 		Foreach q in Q
		 * 		If not empty Yq
		 * 			Let z = Yq[0]
		 * 			Let wz = wz U q
		 * 			Let Xq = Xq U z
		 * 			If (full(z))
		 * 				Foreach d in Q - {q]
		 * 					Let Yd = Yd -{z}
		 */
		List studs=new Vector(writeins.keySet());
		int randStud = new Random().nextInt(studs.size());
		//stdNum=(String)studs.get(randStud);		

		log(2,"Student: "+stdNum);				
		log(1,"Sorting subjects based on rank.");
		List sortedSubjects=new Vector(offering.getAllSubjects().keySet());
		//Collections.sort(sortedSubjects);
		Collections.sort(sortedSubjects,new Comparator(){
			public int compare(Object o1, Object o2){
				String s1=(String) o1;
				String s2=(String) o2;
				float t1 = offering.getSubjectRank(s1);
				float t2 = offering.getSubjectRank(s2);
				return ((int)(t1-t2));
			}
		});
		
			
		Iterator sortedSubjectsIterator=sortedSubjects.iterator();
		while(sortedSubjectsIterator.hasNext()){
			//Map: <stdnum, list of section options>
			sectionOptionsMap = new Hashtable();
			String subject=(String)sortedSubjectsIterator.next();
			//System.out.println(subject+":"+offering.getSubjectRank(subject));
			
			log(3,"Obtaining students interested in subject");			
			Vector interestedStudents=(Vector)subjectList.get(subject);
			Iterator interestedStudentsIterator = interestedStudents.iterator();
			while(interestedStudentsIterator.hasNext()){
				//Get the writein information
				WriteIn wi=(WriteIn)interestedStudentsIterator.next();				
				String  stdnum = wi.getStudentNumber();

				//Get the schedule
				Schedule sked=(Schedule)schedules.get(stdnum);
				List sectionOptions = getSectionOptions(subject,sked);
				
				sectionOptionsMap.put(stdnum, sectionOptions );				
			}
			
			Map prioritized = prioritize(subject);
			
			//For each priority
			for (int i = 1; i <= 6;i++){
				//System.out.println("Priority: "+i);
				Integer key = new Integer(i);
				List l = (List)prioritized.get(key);
				
				if (l != null){
					//printList("Unsorted",l);
					Collections.sort(l,new Comparator(){
						public int compare(Object o1, Object o2){
							String s1=(String) o1;
							String s2=(String) o2;
							List l1 = (List)sectionOptionsMap.get(s1);
							List l2 = (List)sectionOptionsMap.get(s2);
						    //System.out.println("s1:"+s1+":"+l1.size());
						    //System.out.println("s2:"+s2+":"+l2.size());
							int t1 = l1.size(); 
							int t2 = l2.size();
							return ((int)(t1-t2));
						}
					});		
					//printList("Sorted in #options",l);
					Iterator ite = l.iterator();
					while(ite.hasNext()){
						String std = (String)ite.next();
						List l1 = (List)sectionOptionsMap.get(std);							
												
						if (l1.size() > 0){
							int j=0;						
							Section choice = null;
							Classlist cl=null;
							do {
								if (j == l1.size())
									break;
								choice = (Section)l1.get(j++);
								String k=choice.getSubject().getName()+":"+choice.getSectionName();
								//if (std.equals(stdNum)){
								//		System.out.println(std+":"+k);
								//}
								cl=(Classlist)classlists.get(k);	
								if (cl == null){
									cl = new Classlist(choice);
									classlists.put(k,cl);
									//continue;
								}
								cl=(Classlist)classlists.get(k);
								if (!cl.full()){
									if (std.equals(stdNum)){
										System.out.println(k+": Classlist is not full:"+cl.getStudents().size());
									}
									break;
								}else{
									if (std.equals(stdNum)){
										System.out.println(k+": Classlist is full:"+cl.getStudents().size());
									}
									continue;
								}
								
							}while(true);
							
							//if (j == l1.size())
							//	continue;
							
							if (std.equals(stdNum))
							System.out.println("Found:"+std+":"+subject+":"+choice.getSectionName());
							Schedule sked=(Schedule)schedules.get(std);
							
							//add the lecture if choice is lab
							if (choice.isLab()){
								//TODO: Add section node
								Section lecSection=null;
								Iterator lecSectionsIterator=offering.getLecSections(subject).iterator();
								while(lecSectionsIterator.hasNext()){
									lecSection=(Section)lecSectionsIterator.next();
									if (choice.getSectionName().startsWith(lecSection.getSectionName()))
										break;
								}
								if (std.equals(stdNum))
									System.out.println("Added lecture/lab: "+lecSection.getSectionName());
								sked.addSection(new SkedNode(lecSection));
								sked.addSection(new SkedNode(choice));
							}else{
								if (std.equals(stdNum))
									System.out.println("Added lecture"+choice.getSectionName());
								sked.addSection(new SkedNode(choice));
							}
							if (std.equals(stdNum))
								System.out.println(sked.getSubjects().size());

							schedules.put(std,sked);
							cl.addStudent(std);							
						}else{
							//System.out.println(std+":"+subject);
						}
					}
				}
			}
		}
	}
	
	
	public Map prioritize(String subject){
		Map retval = new Hashtable();
		
		Vector interestedStudents=(Vector)subjectList.get(subject);
		Iterator interestedStudentsIterator = interestedStudents.iterator();
		while(interestedStudentsIterator.hasNext()){
			//Get the writein information
			WriteIn wi=(WriteIn)interestedStudentsIterator.next();
			Integer key = new Integer(wi.getPriority());
			
			// l: list of writeins
			List l = (List)retval.get(key);
			if (l == null){
				l = new ArrayList();
			}
			l.add(wi.getStudentNumber());
			retval.put(key, l);
		}
		return retval;		
	}
	
	private List getSectionOptions(String subject, Schedule sked){
		List retval = new ArrayList();
		
		Iterator lecSectionsIterator=offering.getLecSections(subject).iterator();
		while(lecSectionsIterator.hasNext()){
			Section lecSection=(Section)lecSectionsIterator.next();			
			//System.out.println(subject+":"+lecSection.getSectionName());
			
			//Create a classlist of the section if it does not exist yet
			String key=lecSection.getSubject().getName()+":"+lecSection.getSectionName();
			Classlist lecSectionClasslist=(Classlist)classlists.get(key);					
			if(lecSectionClasslist==null){
				classlists.put(key,new Classlist(lecSection));
			}
			lecSectionClasslist=(Classlist)classlists.get(key);
			
			//proceed to next section if this section full
			if (lecSectionClasslist.full()){
				//System.out.println("Lec section is full");
				continue;
			}
			
			//Create a sked node for it
			SkedNode lecNode=new SkedNode(lecSection);
			
			
			//temporarily add the lecnode to the schedule
			sked.addSection(lecNode);			
			if (!sked.passed(lecNode)){
				//proceed to next lecture section
				//since this lecture is in conflict already,
				//there is no need to process its labs
				//System.out.println("Conflict detected:"+subject+":"+lecSection.getSectionName()+":"+lecSection.getTime());
				sked.removeLastSectionAdded();
				continue;
			}
			//System.out.println("No Conflict:"+subject+":"+lecSection.getSectionName()+":"+lecSection.getTime());
			
			
			List labs=offering.getLabSections(lecSection.getSubject().getName(),
							lecSection.getSectionName());
			
			boolean labFound=false;
			int labCount=labs.size();
								
			//this lecture got labs!
			if (labCount > 0){
				Iterator ite4=labs.iterator();
				Section lab=null;
				
				//try to find a lab
				while (ite4.hasNext()){
					lab=(Section)ite4.next();
					
//					Create a classlist of the section if it does not exist yet
					String k=lab.getSubject().getName()+":"+lab.getSectionName();
					Classlist labSectionClasslist=(Classlist)classlists.get(k);					
					if(labSectionClasslist==null){
						classlists.put(k,new Classlist(lab));
						//System.out.println(k);
						
					}
					
					
					
					//Create a node for the lab
					SkedNode labNode=new SkedNode(lab);
					
					//Add it to the sked
					sked.addSection(labNode);
					
					//Did it satisfy the constraint?
					if(!sked.passed(labNode)){
						//remove this lab section from sked
						sked.removeLastSectionAdded();
						sked.removeLastSectionAdded();
						continue; //to the next lab
					}else{				
						//We found a lab! great
						labFound=true;
						retval.add(lab);
						sked.removeLastSectionAdded();
						sked.removeLastSectionAdded();
						continue;
					}
				}		
			}else{
				retval.add(lecSection);
				sked.removeLastSectionAdded();
			}				
			
		}
		
		return retval;
	}
	
	
	public void printList(String name,List l){
			System.out.print(name + ": < ");
			Iterator ite = l.iterator();
			while (ite.hasNext()){
				String s = (String)ite.next();
				System.out.print(s+",");
			}
			System.out.println(" >");
			
	}
	
	public void printMap(String name,Map m){
		System.out.print(name + ": < ");
		Iterator ite = m.keySet().iterator();
		while (ite.hasNext()){
			String s = (String)ite.next();
			System.out.print(s+",");
		}
		System.out.println(" >");
		
	}
	
	public SkedNode getRoot(String stdnum){
		Schedule sked = (Schedule)schedules.get(stdnum);
		return sked.getRoot();
	}
	
		
	public void printSked(){
		Iterator ite=schedules.keySet().iterator();
		while (ite.hasNext()){
			String std=(String)ite.next();
			//System.out.print(stdNum+",");
			Schedule sked=(Schedule)schedules.get(std);
			Iterator ite2=sked.getSubjects().iterator();
			while(ite2.hasNext()){
				ISection section=(ISection)ite2.next();
				if (std.equals(stdNum))
					System.out.println(section.getSubject().getName()+"("+section.getSectionName()+")");
			}
			//System.out.println();
		}
	}
	
	
	public void generateForm5(){
		Iterator ite=schedules.keySet().iterator();
		int count=0;
		while (ite.hasNext()){
			String std=(String)ite.next();
			//System.out.print(stdNum+",");
			Schedule sked=(Schedule)schedules.get(std);
			Iterator ite2=sked.getSubjects().iterator();
			while(ite2.hasNext()){
				ISection section=(ISection)ite2.next();
				WriteIn wri=(WriteIn)writeins.get(std);
				ISubject sub=wri.getSubjectInfo(section.getSubject().getName());
				System.out.println(std+","+section.getSubject().getName()+","+section.getSectionName()+","+sub.getUnitCredit());
			}
			//System.out.println();
			count++;
		}
		System.out.println(count);
	}
	
	
	public void computeUnitsObtainedPerAllowedUnits(){
		int a,b,c,d,e,f;
		a=b=c=d=e=f=0;
		int sumPL=0;
		int underloaded=0;
		int fullLoad=0;
		
		Iterator ite=new Vector(writeins.keySet()).iterator();
		while (ite.hasNext()){
			String std=(String)ite.next();
			Schedule sked=(Schedule)schedules.get(std);
			WriteIn writein=(WriteIn)writeins.get(std);
			
			float writeinTotal=0;
			Iterator ite1=writein.getSubjects().iterator();
			while (ite1.hasNext()){
				ISubject s=(ISubject)ite1.next();
				writeinTotal += s.getUnitCredit();
			}
			
			if (writeinTotal > writein.getUnitsAllowed())
				writeinTotal = writein.getUnitsAllowed();
			
			float p=(sked.getUnitsObtained(writein)/writeinTotal)*100;
			sumPL+=(int)p;
			
			if ((p >= 81) && (p<=100)){
				f++;
			}
			else if ((p>=61) && (p<=80)){
				e++;
			}
			else if ((p>=41) && (p<=60)){
				d++;
			}
			else if ((p>=21) && (p<=40)){
				c++;
				
			}
			else if ((p>=0) && (p<=20)){
				b++;				
			}
			
			if(p < 83.33){
				underloaded++;
				//System.out.println(std + ":" + p);
			}
			
			if (p==100)
				fullLoad++;
			
		}
		
		int total=a+b+c+d+e+f;
		/*
		System.out.println("81-100 : " + f + ", " + ((f/(float)total)*100));
		System.out.println("61-80  : " + e + ", " + ((e/(float)total)*100));
		System.out.println("41-60  : " + d + ", " + ((d/(float)total)*100));
		System.out.println("21-40  : " + c + ", " + ((c/(float)total)*100));
		System.out.println("1-20   : " + b + ", " + ((b/(float)total)*100));
		*/
		System.out.println("Students: "+total);
		System.out.println("Percent Full Load: "+(fullLoad/(float)total)*100);
		System.out.println("Percent Underload: "+(underloaded/(float)total)*100);
		
		System.out.println("SumPL: "+sumPL);
		System.out.println("APL: "+sumPL/((float)total*100));
		
	}
	
	
	public void statistics(){
		Iterator ite=schedules.keySet().iterator();
		int count=0;
		while (ite.hasNext()){
			String std=(String)ite.next();

			Schedule sked=(Schedule)schedules.get(std);
			WriteIn writein=(WriteIn)writeins.get(std);

if (std.equals(stdNum))
{
	
	float writeinTotal=0;
	Iterator ite1=writein.getSubjects().iterator();
	while (ite1.hasNext()){
		ISubject s=(ISubject)ite1.next();
		writeinTotal += s.getUnitCredit();
	}
	
	if (writeinTotal > writein.getUnitsAllowed())
		writeinTotal = writein.getUnitsAllowed();
	
	float p=(sked.getUnitsObtained(writein)/writeinTotal)*100;
	System.out.println("Percentage Load: "+p);
	
			System.out.println("WRITEIN");
			Iterator ite2=writein.getSubjects().iterator();
			while (ite2.hasNext()){
				ISubject s=(ISubject)ite2.next();
				System.out.println("\t"+s.getName());
			}
			
			
			
			System.out.println("FORM 5");
			ite1=sked.getSubjects().iterator();
			while (ite1.hasNext()){
				ISection s=(ISection)ite1.next();
				System.out.println("\t"+s.getSubject().getName()+":"+s.getSectionName());
				Classlist l = (Classlist)classlists.get(s.getSubject().getName()+":"+s.getSectionName());
				Iterator iter = l.getStudents().iterator();
				int i=1;
				while (iter.hasNext()){
					String str = (String)iter.next();
					System.out.println(i+")"+str);
					i++;
				}
			}
			
			
			
			
}			

//			int unitsWanted=writein.getUnitsAllowed();
			//int unitsObtained=sked.getUnitsObtained(writein);
			
			//System.out.println(std+",allowed units:"+unitsWanted+", assigned units"+unitsObtained);
			
//			count++;
//}
			
	}
	
		System.out.println(count);		
		
	}
	
	
	public void log(int level, String message){
		if (logging && level <= loggingLevel){
			System.out.println("["+level+"]: "+message);
		}
	}
	
	public static void main(String args[]){
		long start = System.currentTimeMillis();
		RegistScheduler regist;
		String prefix="../courselector/data/";
		DudaiOffering offering=new DudaiOffering(prefix+"CLASSES-2002-SECOND");
		regist=new RegistScheduler(offering);
		regist.loadWriteIn(prefix+"WRITEIN-2002-SECOND");	
		regist.assign();
		long stop = System.currentTimeMillis();
		//regist.statistics();
		regist.printSked();
		regist.statistics();
		regist.computeUnitsObtainedPerAllowedUnits();		
		//regist.generateForm5();
		System.out.println("TimeMillis: " + (stop - start));
		
	}	
	
	
	
}
