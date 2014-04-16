package jach.msthesis.regmas;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.DefaultForm5;
import jach.msthesis.registration.model.IForm5;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.WriteIn;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.IScheduler;
import jach.msthesis.scheduler.PrototypeScheduler;
import jach.msthesis.scheduler.Schedule;
import jach.msthesis.scheduler.regist.RegistSchedulerImproved;
import jach.msthesis.scheduler.statistics.Statistics;
import jach.msthesis.scheduler.util.RegistScheduleExporter;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

/**
 * The Scheduler Agent.
 * This agent performs the initial scheduling and the instantiation 
 * of the enlistor agents. It uses the r2scheduler for generating the 
 * initial schedules.
 * @author jach
 *
 */


public class SchedulerAgent extends Agent implements Constants{
	
	/**
	 * The scheduler
	 */
	IScheduler scheduler;
	
	/**
	 * The offering data
	 */
	RegistOffering offering;
	
	/**
	 * The writein source
	 */
	RegistWriteInSource wis;
	
	/**
	 * Logging
	 */
	static Logger logger;
	
	
	
	int numStudents=1;
	int studentAgentStartedCount=0;
	
	/**
	 * Initialize the agent
	 */
	protected void setup() {
		/**
		 * Initializa the logger
		 */		
		logger = jade.util.Logger.getMyLogger(this.getClass().getName());
		logger.setLevel(Logger.SEVERE);
		
		logger.info("Scheduler agent started: "+getLocalName());
		/**
		 * Publish agent to the directory facilitator
		 */
		DFAgentDescription dfd = new DFAgentDescription();
	    dfd.setName(getAID());
	    ServiceDescription sd = new ServiceDescription();
	    sd.setType("scheduler");
	    sd.setName("Scheduler Agent");
	    dfd.addServices(sd);
	    try {
	      DFService.register(this, dfd);
	    }
	    catch (FIPAException fe) {
	      fe.printStackTrace();
	    }		
	    
	    /**
	     * Add the main behavior
	     */
	    addBehaviour(new SchedulerAgentBehaviour());	    
	}
	
	/**
	 * This is the implementation of the main behavior of the scheduler agent which
	 * uses a state based approach. Initial behavior is to create an initial
	 * schedule.
	 * 
	 * @author jach
	 *
	 */
	private class SchedulerAgentBehaviour extends CyclicBehaviour {
		/**
		 * Action variable
		 */
		int action=ACTION_SCHEDULE;
		
		/**
		 * Properties
		 */
		Properties props;
		
		int doneCount=0;
		float completed;
		
		String prefix;
		
		/**
		 * Main action method
		 */
		public void action() {
			switch(action){
				case ACTION_SCHEDULE:
					logger.info(getLocalName()+" loading regmas.properties...");
					
					/**
					 * Load the properties (data directory, sem, year
					 */
					try{
						props=new Properties();					
						FileInputStream in = new FileInputStream("regmas.properties");
						props.load(in);
					}catch(Exception e){
						e.printStackTrace();
					}
					
					logger.info(getLocalName()+" scheduling...");					
					/**
					 * Prepare data for scheduling
					 */
					prefix = props.getProperty("regmas.data.dir");
					offering = new RegistOffering(prefix + "CLASSES-"+props.getProperty("regmas.year")+"-"+props.getProperty("regmas.sem"));
					wis = new RegistWriteInSource(prefix + "WRITEIN-"+props.getProperty("regmas.year")+"-"+props.getProperty("regmas.sem"));					
					wis.load();
					offering.load();
					
					//Do the scheduling
					//scheduler = new RegistSchedulerImproved(offering, wis.getWriteIns());
					//scheduler.assign();
					scheduler = new PrototypeScheduler(offering, wis.getWriteIns());
					
					logger.info(getLocalName()+" done scheduling");
					//Display some statistics
					Statistics stat=new Statistics(scheduler);
					stat.display();			
					
					//Next action is to start the enlistors
					action=ACTION_START_ENLISTORS;
					break;
				case ACTION_START_ENLISTORS:
					logger.info(getLocalName()+" starting enlistors...");

					/*
					 * We start enlistor agents for each subject and pass to these
					 * enlistor agents the classlists for all the sections on the
					 * subject
					 */
					List subjects = new Vector(scheduler.getOffering().getAllSubjects().keySet());
					for (Iterator ite=subjects.iterator();ite.hasNext();){
						String subject = (String)ite.next();					
						try {
							//Get all the classlists for the section of the subject
							//and start the enlistor agent
							Map classlists = scheduler.getAllClasslistsForSubject(subject);
							AgentController t1;
							Object[] param = new Object[2];
							param[0]=subject;
							param[1]=classlists;		            	
							AgentContainer container = (AgentContainer)getContainerController(); // get a container
							t1 = container.createNewAgent(subject, "jach.msthesis.regmas.EnlistorAgent", param);
							t1.start();
						}catch (Exception any) {
							any.printStackTrace();
						}
					}
					
					//Debug code for starting student agents
					List studs = new Vector(scheduler.getStudents());
					Collections.shuffle(studs);
					numStudents=studs.size();
					System.out.println("Total students: "+numStudents+"...");
					for (int j=0;j < numStudents;j++){
						try{
							AgentController t1;							
							String stdNum=(String)studs.get(j);
							Schedule sked=scheduler.getSchedule(stdNum);
							
							if (sked.getUnassigned().size() != 0){
								//String stdNum="2004-95027";
								AgentContainer container = (AgentContainer)getContainerController(); // get a container
								t1 = container.createNewAgent(stdNum, "jach.msthesis.regmas.StudentAgent", null);
								t1.start();
								studentAgentStartedCount++;
							}
						}catch (Exception any) {
							any.printStackTrace();
						}
					}
					System.out.println("Student agents started: "+studentAgentStartedCount+"...");
					
					//next action is to wait for requests
					action=ACTION_WAIT_FOR_REQUESTS;
					break;
				case ACTION_WAIT_FOR_REQUESTS:
					try{
						//A message is received
						ACLMessage msg = receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
						if (msg != null){
							//create a reply message
							ACLMessage reply = msg.createReply();
							//Message is a request from a student agent for
							//its form 5
							if (msg.getContent().equals(MSG_GET_FORM5)){
								reply.setPerformative(ACLMessage.INFORM);
								logger.info(getLocalName()+" received Form 5 request from "+msg.getSender().getLocalName());
								IForm5 form5 = (IForm5)scheduler.getSchedule(msg.getSender().getLocalName());								
								reply.setContentObject(form5);
								send(reply);								
								logger.info(getLocalName()+" sent Form 5 for "+msg.getSender().getLocalName());
							}else{	
								try {
									if (msg.getContentObject() instanceof IForm5){
										IForm5 sked=(IForm5)msg.getContentObject();
										scheduler.getSchedules().put(msg.getSender().getLocalName(),sked);
										doneCount++;
										completed=(doneCount/(float)studentAgentStartedCount)*100;
										if (((int)(completed%10))==0){
											//System.out.println(completed+"% done.");
										}
									}
								} catch (UnreadableException e) {
									// 	TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}catch(IOException ioe){
						ioe.printStackTrace();
					}
					//action=WAIT_FOR_REQUESTS;
					if (completed  == 100){
						Statistics statis = new Statistics(scheduler);
						statis.display();						
						RegistScheduleExporter exporter = new RegistScheduleExporter(scheduler,prefix+"FORM5-"+props.getProperty("regmas.year")+"-"+props.getProperty("regmas.sem")+"-MAS","2004","SECOND");		
						exporter.export();
						//doneCount++;
						completed++;
					}

					break;
			}
		}
	}
}
