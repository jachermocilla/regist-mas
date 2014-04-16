package jach.msthesis.regmas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jach.msthesis.registration.model.IForm5;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.Section;
import jach.msthesis.registration.model.SectionAssignment;
import jach.msthesis.registration.model.WriteIn;
import jach.msthesis.scheduler.IScheduler;
import jach.msthesis.scheduler.constraints.NoConflictConstraint;
import jach.msthesis.scheduler.regist.RegistSchedulerImproved;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;

/**
 * The Student Agent 
 * @author jach
 *
 */


public class StudentAgent extends Agent implements Constants{
	/**
	 * The student number 
	 */
	String studentNumber;
	
	/**
	 * Reference to the scheduler agent
	 */
	AID schedulerAgent;
	
	/**
	 * The writein
	 */
	WriteIn writein;

	/**
	 * The schedule
	 */
	IForm5 schedule;

	/**
	 * The logger
	 */
	static Logger logger;
	
	
	int action=0;
	
	/**
	 * Initialization
	 */
	protected void setup() {
		//INitialize the logger
		logger = jade.util.Logger.getMyLogger(this.getClass().getName());
		logger.setLevel(Logger.SEVERE);
		
		logger.info("Student agent started: "+getLocalName());
		//studentNumber = (String)(String)getArguments()[0];
		
		/**
		 * Register the agent to the directory facilitator
		 */
		DFAgentDescription dfd = new DFAgentDescription();
	    dfd.setName(getAID());
	    ServiceDescription sd = new ServiceDescription();
	    sd.setType(getLocalName());
	    sd.setName("Student Agent");
	    dfd.addServices(sd);
	    try {
	      DFService.register(this, dfd);
	    }
	    catch (FIPAException fe) {
	      fe.printStackTrace();
	    }
	    
	    //Add main student agent behaviour
	    addBehaviour(new StudentAgentBehaviour());	    	    	    
	}
	
	
	
	
	/**
	 * This is the enlist behaviour
	 * @author jach
	 *
	 */
	public class EnlistBehaviour extends CyclicBehaviour {
		/**
		 * The enlistor agent
		 */
		AID enlistorAgent;
		
		/**
		 * The subject
		 */
		ISubject sub;
		
		/**
		 * Messages
		 */
		ACLMessage msg,reply;
		
		/**
		 * Flags
		 */
		boolean giveUp,done;
		
		/**
		 * Number of attempts to enlist
		 */
		int attempts;
		
		/**
		 * Set threshold for the number of tries
		 * Give up if attempts exceeded threshold
		 */
		static final int THRESH=1;
		
		
		/**
		 * Main constructor
		 * @param sub
		 */
		public EnlistBehaviour(ISubject sub){
			this.sub = sub;
		}
		
		/**
		 * The interesting stuff
		 */
		public void action(){
			
			//increment number of attempts
			attempts++;

			//Don't enlist if adding a subject will exceed the allowed units
			if (schedule.getUnitsObtained()+3 > (schedule.getWriteIn().getUnitsAllowed())){
				done=true;
			}
			
			if (attempts > THRESH || done){				
				return;
			}
			
			logger.info(getLocalName()+":"+"Attempting to enlist in "+sub.getName());
			//Search for the enlistor of the subject
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
	        sd.setType(sub.getName());
	        template.addServices(sd);
	        try {
	        	DFAgentDescription[] result = DFService.search(myAgent, template);
	        	//Ok we got the enlistor
	        	if (result.length !=0 ){
	        		logger.info(getLocalName()+ ": "+ sub.getName() +" agent found.");
	        		enlistorAgent = result[0].getName();
	        		
	        		//Request for sections with available slots at this moment
	        		msg = new ACLMessage(ACLMessage.REQUEST);
	        		msg.setContentObject(new EnlistorMessage(ACTION_GET_SECTIONS_WITH_SLOTS,null));
	        		msg.addReceiver(enlistorAgent);
	        		send(msg);
	        		
	        		reply = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
	        		Vector sections = (Vector)reply.getContentObject();
	        		//Oops, all sections for this subject are full
	        		if (sections.size() == 0){
	        			logger.info(getLocalName()+":" + "No slots available for "+sub.getName());
	        		}else{//ok we have some sections
		        		//Randomize the sections. 
		        		Collections.shuffle(sections);

		        		NoConflictConstraint constraint = new NoConflictConstraint(schedule.getSectionAssignments());
		        		//Look for the first section assignment that does not 
		        		//conflict with the current form 5
	        			for(Iterator ite = sections.iterator();ite.hasNext();){
	        				SectionAssignment sectAss = (SectionAssignment)ite.next();
	        				if (!constraint.isSatisfied(sectAss.getLecture())){
	        					//System.out.println(getLocalName()+":"+"Conflict in lecture of "+sectAss.getLecture().getSectionName());
	        					continue;
	        				}else if ((sectAss.getLab()!=null) && (!constraint.isSatisfied(sectAss.getLab()))){
        						//System.out.println(getLocalName()+":"+"Conflict in lab of "+sectAss.getLab().getSectionName());
        						continue;
	        				}else{
	        					//Ok no conflict in lab or lecture
	        					//send message to enlist to this section assingment
	        					//msg.
	        					msg = new ACLMessage(ACLMessage.REQUEST);
	        	        		msg.setContentObject(new EnlistorMessage(ACTION_ENLIST,sectAss));
	        	        		msg.addReceiver(enlistorAgent);
	        	        		send(msg);
	        	        		reply = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
	        	        		if (reply != null && reply.getContent().equals(MSG_CONFIRM)){
	        	        			logger.info(getLocalName()+":"+"enlist to "+sectAss.getSection().getSubject().getName()+" "+sectAss.getSection().getSectionName());
	        	        			schedule.addSectionAssignment(sectAss);
	        	        			
	        	        			done=true;
	        	        		}
	        					break;
	        				}
	        			}
	        		}
	        	}
			}catch (Exception fe) {
	        	fe.printStackTrace();
	        }
		}
	}
	
	public class CancelSlotBehaviour extends SimpleBehaviour implements Constants{
		AID enlistorAgent;
		SectionAssignment sectAss;
		ACLMessage msg,reply;
		boolean giveUp,done;
		int attempts;
		static final int THRESH=1;
		String subjectName;
		String sectionName;
		
		public CancelSlotBehaviour(SectionAssignment sectAss){
			this.sectAss = sectAss;
			this.subjectName = sectAss.getSection().getSubject().getName();
			this.sectionName = sectAss.getSection().getSectionName();			
		}
		
		public void action(){
			attempts++;

			logger.info(getLocalName()+":"+"Attempting to cancel "+subjectName+":"+sectionName);
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
	        sd.setType(subjectName);
	        template.addServices(sd);
	        try {
	        	DFAgentDescription[] result = DFService.search(myAgent, template);				        	
	        	if (result.length !=0 ){
	        		logger.info(getLocalName()+ ": "+ result[0].getName().getLocalName() +" agent found.");
	        		enlistorAgent = result[0].getName();	        		
	        		msg = new ACLMessage(ACLMessage.REQUEST);
	        		msg.setContentObject(new EnlistorMessage(ACTION_CANCEL_SLOT,sectAss));
	        		msg.addReceiver(enlistorAgent);
	        		send(msg);
	        		reply = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
	        		if (reply != null && reply.getContent().equals(MSG_CONFIRM)){
	        			schedule.removeSectionAssignment(subjectName);
	        		}
	        	}
			}catch (Exception fe) {
	        	fe.printStackTrace();
	        }
		}
		
		public boolean done(){
			return true;
		}
	}
	
	public class PostSwapRequestBehaviour extends SimpleBehaviour implements Constants{
		SwapEntry swapEntry;
		AID enlistorAgent;
		ACLMessage msg,reply;
		public PostSwapRequestBehaviour(SwapEntry swapEntry){
			this.swapEntry = swapEntry;
		}
		
		public void action(){
			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd = new ServiceDescription();
	        sd.setType(swapEntry.getSubject().getName());
	        template.addServices(sd);
	        try {
	        	DFAgentDescription[] result = DFService.search(myAgent, template);				        	
	        	if (result.length !=0 ){
	        		enlistorAgent = result[0].getName();	        		
	        		msg = new ACLMessage(ACLMessage.REQUEST);
	        		msg.setContentObject(swapEntry);
	        		msg.addReceiver(enlistorAgent);
	        		send(msg);
	        		reply = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
	        		if (reply != null && reply.getContent().equals(MSG_CONFIRM)){
	        			logger.info(getLocalName()+" swap request added.");
	        		}
	        	}
			}catch (Exception fe) {
	        	fe.printStackTrace();
	        }

		}
		
		public boolean done(){
			return true;
		}
		
	}
	
	private class StudentAgentBehaviour extends CyclicBehaviour {
		
		ACLMessage msg,reply;
		SectionAssignment toCancel;
		SwapEntry swapEntry;
		int passCount=0;
		
		//enlist
		protected boolean enlist(AID enlistorAgent, ISubject sub){			
			return false;
		}
		
		public void action(){
			//TODO: Implement deliberation of for the next action
			//action=deliberate(Desire, Beliefs);
			
			switch (action){
				case ACTION_SEARCH_MAIN_SCHEDULER:
					logger.info(getLocalName()+" searching for main scheduler...");
					DFAgentDescription template = new DFAgentDescription();
			        ServiceDescription sd = new ServiceDescription();
			        sd.setType("scheduler");
			        template.addServices(sd);
			        try {
			        	DFAgentDescription[] result = DFService.search(myAgent, template);				        	
			        	if (result.length !=0 ){
			        		logger.info(getLocalName()+ ":scheduler agent found.");
			        		schedulerAgent = result[0].getName();
			        	}		            
			        }catch (FIPAException fe) {
			        	fe.printStackTrace();
			        }
			        action=ACTION_GET_FORM5;
			        break;
				case ACTION_GET_FORM5:					
					try{
						logger.info(getLocalName()+" requesting for Form 5..");
						msg = new ACLMessage(ACLMessage.REQUEST);
						msg.setContent(MSG_GET_FORM5);
						msg.addReceiver(schedulerAgent);
						send(msg);	
						reply = blockingReceive(MessageTemplate.MatchPerformative(ACLMessage.INFORM));
						if (reply !=null){							
							schedule=(IForm5)reply.getContentObject();
							logger.info(getLocalName()+" schedule received, units obtained: "+schedule.getUnitsObtained()+
									", units allowed: "+schedule.getWriteIn().getUnitsAllowed());					
							
							for (Iterator ite=schedule.getWriteIn().getSubjects().iterator();ite.hasNext();){
								ISubject sub=(ISubject)ite.next();
								SectionAssignment sectAss = (SectionAssignment)schedule.getSectionAssignment(sub.getName());
								if (sectAss != null){
									logger.info(sub.getName()+" "+sectAss.getSection().getSectionName());
									toCancel = sectAss;
								}else{
									logger.info(sub.getName()+" NOT ENLISTED");
								}								
							}														
							
						}
					}catch(Exception ioe){
						ioe.printStackTrace();
					}
					
					//action=ACTION_POST_SWAP_REQUEST;
					action=ACTION_ENLIST;
					
					break;
				case ACTION_ENLIST:
					List unassigned = schedule.getUnassigned();
					passCount++;
					if (unassigned.size() == 0){
						action=ACTION_HAPPY;
						break;
					}else if (passCount == 1){
						Map assignments=schedule.getSectionAssignments();
						List subjects=new ArrayList(assignments.keySet());
						if (subjects.size() > 1){
							Collections.shuffle(subjects);
							for (int i=0;i<(subjects.size()-1);i++){
								String subject = (String)subjects.get(i);
								SectionAssignment sectAss=schedule.getSectionAssignment(subject);
								myAgent.addBehaviour(new CancelSlotBehaviour(sectAss));
							}
						}
						/*
						 for(Iterator ite2=assignments.keySet().iterator();ite2.hasNext();){
							String subject=(String)ite2.next();
							SectionAssignment sectAss=schedule.getSectionAssignment(subject);
							myAgent.addBehaviour(new CancelSlotBehaviour(sectAss));
						}
						*/
						//for each unassigned subject, attempt to enlist
						for (Iterator ite = unassigned.iterator();ite.hasNext();){
							ISubject sub = (ISubject)ite.next();
							myAgent.addBehaviour(new EnlistBehaviour(sub));
						}						
					}else{
						action=ACTION_HAPPY;
					}
					break;
				case ACTION_CANCEL_SLOT:
					myAgent.addBehaviour(new CancelSlotBehaviour(toCancel));
					action=ACTION_END;
					break;
				case ACTION_POST_SWAP_REQUEST:
					SwapEntry entry = new SwapEntry(getLocalName(),toCancel.getSection(),null);
					myAgent.addBehaviour(new PostSwapRequestBehaviour(entry));
					action=ACTION_END;
					break;
				case ACTION_HAPPY:
					//At this point student agent submits his schedule
					/*
					System.out.println("HAPPY");
					
					for (Iterator ite=schedule.getSectionAssignments().keySet().iterator();ite.hasNext();){
						String key=(String)ite.next();
						SectionAssignment sectAss = schedule.getSectionAssignment(key);
						String sectName=sectAss.getSection().getSectionName();
						System.out.println(getLocalName()+":"+key+" "+sectName);
					}
					*/
					try{
						msg = new ACLMessage(ACLMessage.REQUEST);
						msg.setContentObject(schedule);						
						msg.addReceiver(schedulerAgent);
						send(msg);
					}catch(Exception e){
						e.printStackTrace();
					}
					action=ACTION_END;
					myAgent.doDelete();
					break;
			}			
		}	
				
	}
}
