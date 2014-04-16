package jach.msthesis.scheduler.regist;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;


import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.IWriteIn;
import jach.msthesis.registration.model.datasource.IWriteInSource;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.PrototypeScheduler;
import jach.msthesis.scheduler.Schedule;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.RollingFileAppender;

public class Regist2Scheduler extends PrototypeScheduler{

		//Map of section options
		Map sectionOptionsMap = new Hashtable();
		
		//Map: <subject, list of students interested>
		Map subjectMap=new Hashtable();
		
		//Offering data	
		IOffering offering;
		
		//Writein Source
		IWriteInSource wis;
		
		public Regist2Scheduler(){}
		
		static Logger logger = Logger.getLogger(Regist2Scheduler.class);
		
		
		/**
		 * @param offering
		 */
		public Regist2Scheduler(IWriteInSource wis, IOffering offering ){
			this.wis = wis;
			this.offering=offering;
			BasicConfigurator.configure();
			logger.setLevel(Level.INFO);
		}
		
		public void init(){
			//logger.setLevel(Level.INFO);
			logger.info("Initializing subject map...");
			
			//Writeins
			this.writeins = wis.getWriteIns();
			
			//initialize the subjectMap to be used
			//for storing interested students
			Iterator ite=this.offering.getAllSubjects().keySet().iterator();
			while(ite.hasNext()){
				String subject=(String)ite.next();
				//logger.debug(subject);
				subjectMap.put(subject, new Vector());
			}
			
			//initialize schedules
			ite = writeins.keySet().iterator();
			while (ite.hasNext()){
				//Get the next student from writein data
				String stdNum = (String)ite.next();
				//logger.debug(stdNum);
				IWriteIn wi = (IWriteIn)writeins.get(stdNum);
				
				//Create a new schedule with units allowed based on the writein
				Schedule sked = new Schedule(wi);
				
				//put it in the schedules
				schedules.put(stdNum, sked);
			
				/*
				 * Iterate over all the subjects in the students writein and add
				 *	student to list who wants to take the subject
				 */					
				Iterator ite2 = wi.getSubjects().iterator();
				while (ite2.hasNext()){
					ISubject subject = (ISubject)ite2.next();
					
					/*
					 * Get the list of interested students for the subject 
					 */
					List interestedStudents = (List)subjectMap.get(subject.getName());
					if (interestedStudents != null){
						interestedStudents.add(wi);
						subjectMap.put(subject.getName(),interestedStudents);
					}				
				}
			}
	}
	
	public void assign(){
		
		
	}
		
	public static void main(String args[]){		
		long start = System.currentTimeMillis();
		Regist2Scheduler scheduler;
		String prefix="../courselector/data/";
		RegistOffering offering=new RegistOffering(prefix+"CLASSES-2006-FIRST");
		RegistWriteInSource wis = new RegistWriteInSource(prefix+"WRITEIN-2006-FIRST");
	
		wis.load();
		offering.load();
	
		scheduler=new Regist2Scheduler(wis,offering);
		scheduler.init();
		
	}
}