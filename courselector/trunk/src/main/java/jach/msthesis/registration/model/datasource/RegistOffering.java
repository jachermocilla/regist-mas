package jach.msthesis.registration.model.datasource;

import jach.msthesis.registration.model.DefaultSubject;
import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.LaboratorySection;
import jach.msthesis.registration.model.Section;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.io.*;

/**
 * This class implements IOffering for loading classes offering
 * based on the format used by Ms. Guinto.
 * 
 * Sample line entry.
 *     
 *     AECO 111,B-1L,16,1-4,Mon,CEM 201,TBA,TBA
 * 
 * @author jach
 * @version $Id:DudaiOffering.java 548 2008-09-15 00:52:12Z jach $
 */

public class RegistOffering implements IOffering {

	/**
	 * The path to the text file.
	 */
	private String path;
	
	/**
	 * The offering map <subjectname,<sectionname, section>>
	 */ 
	private Map offering=new Hashtable();

	/**
	 * Map<subject name, Section> of lecture sections
	 */
	private Map lectSections=new Hashtable();
	

	/**
	 * Map<subject name, Section> of laboratory sections
	 */
	private Map labSections=new Hashtable();
	
	/**
	 * Constructor
	 * @param path
	 */
	public RegistOffering(String path){
		this.path=path;
	}
	
	/**
	 * Loads the data
	 */
	public void load(){		
		BufferedReader in=null;		
		String tokens[]=null;		
		String line;
		//<sectioname, section>
		Map sectionMap=null;
						
		try{
			in=new BufferedReader(new FileReader(path));			
		}catch(FileNotFoundException ff){
			System.out.println(path+": Path not found.");
			return;
		}
		try{
			line=in.readLine();			
			while(line != null){
				//Ignore comment lines
				if (!line.startsWith("#")){
					//System.out.println(line);
					tokens=line.split(",");
										
					
					//Special processing must be done for PE 2 subjects
					//because type of PE is in the section field
					String tmp[];
					if (tokens[0].equals("PE 2")){
						//split the section field
						tmp = tokens[1].split("-");
						//concatenate PE 2 and type of PE
						tokens[0] = tokens[0]+"-"+tmp[0];
						//Section now extracted
						if (tmp.length == 2)
							tokens[1] = tmp[1];
						//System.out.println(tokens[0]+":"+tokens[1]);
					}

					//Create default subject
					DefaultSubject subject=new DefaultSubject(tokens[0]);
					
					
					//Get the class size
					String classsize[] = tokens[2].split("-");
					tokens[2]=classsize[0];
										
					
					//Create new section object
					Section section=new Section(subject,tokens[1],tokens[3],
					tokens[4],tokens[5],tokens[6],tokens[7],Integer.parseInt(tokens[2]));
					
						
					//create a section map
					if (offering.containsKey(tokens[0])){
						sectionMap=(Map)offering.get(tokens[0]);
					}else{
						sectionMap=new Hashtable();	
					}
					
					if (tokens[1].contains("-")){
						section.getSubject().setHasLaboratory(true);
					}
					
					sectionMap.put(tokens[1],section);
					
					
					//put it in the offering
					offering.put(tokens[0], sectionMap);
				}
				//Get the next line
				line=in.readLine();
			}			
		}catch(IOException ioe){
			ioe.printStackTrace();
		}				
	}

	/**
	 * Returns a list<Section> of lecture sections
	 */
	public List getLecSections(String subjectName) {
		if (lectSections.containsKey(subjectName)){
			return (List)lectSections.get(subjectName);
		}
		Vector v=new Vector();
		//Iterator ite=((List)offering.get(subjectName)).iterator();
		Map sections = (Map)offering.get(subjectName);
		Iterator ite = sections.keySet().iterator();
		while (ite.hasNext()){
			String s=(String)ite.next();
			if (!s.contains("-")){
				Section sect = (Section)sections.get(s);
				v.add(sect);
			}
		}
		lectSections.put(subjectName, v);
		return v;
	}

	/**
	 * Returns the total number of slots available
	 */
	public int getSlotCount(String subjectName){
		int slots=0;
		//Iterator ite=((List)offering.get(subjectName)).iterator();
		Map sections = (Map)offering.get(subjectName);
		Iterator ite = sections.keySet().iterator();
		while (ite.hasNext()){
			String s=(String)ite.next();
			if (!s.contains("-")){
				Section sect = (Section)sections.get(s);
				slots+=sect.getClassSize();
			}
		}
		return slots;
	}
	
	/**
	 * Return a list<Section> of lab sections given subject and lec section
	 * name
	 */
	public List getLabSections(String subjectName, String lectureSection){
		Section lecture=null;
		String key=subjectName+"-"+lectureSection;
		if (labSections.containsKey(key)){
			return (List)labSections.get(key);
		}
		Vector v=new Vector();
		//Iterator ite=((List)offering.get(subjectName)).iterator();
		Map sections = (Map)offering.get(subjectName);
		Iterator ite = sections.keySet().iterator();
		while (ite.hasNext()){
			String s=(String)ite.next();
			//the dash represents that the section is a lab/recit section
			//assumes that lecture section comes first
			if (s.startsWith(lectureSection+"-")){
				Section sect = (Section)sections.get(s);
				v.add(sect);
			}
		}
		labSections.put(key, v);
		return v;
	}
	
	/**
	 * Return a list<Section> given only the lecture Section object
	 * @param lec
	 * @return
	 */
	public List getLabSections(Section lec) {
		return getLabSections(lec.getSubject().getName(),lec.getSectionName());
	}
	
	/**
	 * Returns true if the given subject name is in the offering.
	 */
	public boolean isOffered(String subjectName){
		return offering.containsKey(subjectName);
	}

	/**
	 * Returns all the sections(lab and lec) for the given subject name
	 */
	public List getSections(String subjectName){
		return (List)offering.get(subjectName);
	}
	
	/**
	 * Returns the entire offering map<subjectname,<sectionname, section>>
	 * @return
	 */
	public Map getAllSubjects(){
		return offering;
	}
	
	/**
	 * Returns the rank of a subject. Current implementation
	 * computes the number of sections as the rank, PE subjects
	 * have higher ranks.
	 */
	public float getSubjectRank(String subjectName){
		
		if (subjectName.startsWith("PE ")){
			return 10000;
		}
		Map m=(Map)offering.get(subjectName);
		return m.size();
	}
	
	/**
	 * Returns an ISection for the given subject name and lab section name
	 */
	public ISection getLabSection(String subjectName, String labSection){
		Map sections = (Map)offering.get(subjectName);
		return ((ISection)sections.get(labSection));
	}
	
	/**
	 * Returns an ISection for the given subject name and lec section name
	 */
	public ISection getLecSection(String subjectName, String lecSection){
		Map sections = (Map)offering.get(subjectName);
		if (sections != null)
			return ((ISection)sections.get(lecSection));
		else
			return  null;
	}	
}
