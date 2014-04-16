package jach.msthesis.registration.model.datasource;

import jach.msthesis.registration.model.Classification;
import jach.msthesis.registration.model.DefaultCurriculum;
import jach.msthesis.registration.model.DefaultSubject;
import jach.msthesis.registration.model.ICurriculum;
import jach.msthesis.registration.model.IPrerequisite;

import java.io.*;
import java.util.*;
import com.jach.cpm.*;
import com.jach.cpm.util.*;


/**
 * Loads a curriculum based on the format used in Regist 
 * 
 * @author jachermocilla
 * @version $Id:RegistCurriculumSource.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public class RegistCurriculumSource implements ICurriculumSource {
	
	/**
	 * The path to the file
	 */
	private String path;

	/**
	 * Placeholder for the curriculum
	 */
	DefaultCurriculum curri;
	
	/**
	 * Constructor
	 */	
	public RegistCurriculumSource(){}
	
	/**
	 * Constructor
	 * 
	 * @param path		a <code>String</code> that represents the path
	 */
	public RegistCurriculumSource(String path){
		this.path=path;
		load();
	}
	
	/**
	 * Sets the source data path
	 * 
	 * @param path		a <code>String</code> that represents the path
	 */	
	/*
	public void setPath(String path){
		this.path=path;
	}
	*/
	

	//from ICurriculumSource
	public ICurriculum load() {
		/**
		 * Placeholder curriculum
		 */

		if (curri != null)
			return curri;
		else
			curri=new DefaultCurriculum();
		
	
		BufferedReader in=null;
		int year=0,sem=0;
		String tokens[]=null;
		Hashtable lookup=new Hashtable();
						
		try{
			in=new BufferedReader(new FileReader(path));			
		}catch(FileNotFoundException ff){
			System.out.println(path+": Path not found.");
			return null;
		}
		
		//put classification prerequisites for later use
		lookup.put("Junior", Classification.JUNIOR);
		lookup.put("Senior", Classification.SENIOR);
				
		try{
			String line=in.readLine();
			curri.setName(line);
			//Ignore first line
			line=in.readLine();			
			while (!line.equals("EndOfList")){
				if (line.startsWith("Year")){					
					tokens=line.split(",");
					year=Integer.parseInt(tokens[0].split(" ")[1]);
					String temp=tokens[1].trim().split(" ")[0];
					if (temp.equals("First"))
						sem=1;						
					else
						sem=2;					
				}else{
					tokens=line.split(",");
					
					//Create a default subject
					DefaultSubject subject=new DefaultSubject(tokens[0],year,
							sem,Integer.parseInt(tokens[1]));
					
					//store reference in a lookup for use later as prerequisites
					lookup.put(tokens[0].trim(), subject);
										
					//Add the prerequisites to the subject
					if (!tokens[3].equals("None")){
						for (int i=3; i < tokens.length;i++){
							subject.addPrerequisite((IPrerequisite)lookup.get(tokens[i].trim()));						
						}
					}
					//System.out.println(subject);
					//Add the subject to the curriculum
					curri.addSubject(subject);
				}				
				line=in.readLine();
			}
		}catch(IOException ioe){
			ioe.printStackTrace();			
		}	
		
		//Get the critical subjects		
		new Converter().convert(path,"temp.cpm");
		CPM cpm=new CPM();
		cpm.loadFile("temp.cpm");
		cpm.process();
		
		curri.setCPM(cpm);
		
		Map criticalSubjects=new Hashtable();		
		Iterator ite=cpm.getCriticalPath().iterator();
		while(ite.hasNext()){
			Task t=(Task)ite.next();			
			criticalSubjects.put(t.getName(),curri.getSubject(t.getName()));
			//System.out.println(t.getName());
		}		
		curri.setCriticalSubjects(criticalSubjects);		
		return curri;
	}
	
	public ICurriculum getCurriculum(){
		return curri;
	}
}
