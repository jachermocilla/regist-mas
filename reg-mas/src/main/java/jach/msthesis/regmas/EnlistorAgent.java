package jach.msthesis.regmas;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jach.msthesis.registration.model.Classlist;
import jach.msthesis.registration.model.SectionAssignment;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import jach.msthesis.scheduler.regist.RegistSchedulerImproved;
import jach.msthesis.scheduler.statistics.Statistics;
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
import jade.lang.acl.UnreadableException;
import jade.util.Logger;

/**
 * The Enlistor Agent.
 * The enlistor agent processes requests for enlistment and cancellation of
 * slots. An enlistor agent is assigned single subject. The enlistor agent
 * has access to the classlists of the sections for the subject.
 * @author jach
 */


public class EnlistorAgent extends Agent implements Constants{
	/**
	 * A Map<subjectname,Classlist>
	 */
	Map classlists;
	
	/**
	 * The subject name that this enlistor agent can process 
	 */
	String subject;

	/**
	 * Reference to the logger
	 */
	static Logger logger;
	
	/**
	 * The swap Map<sectionToSwap,List<SwapEntry>>
	 */
	Map swapRequestsMap=new HashMap();
	
	/**
	 * Setup method
	 */
	protected void setup() {
		logger = jade.util.Logger.getMyLogger(this.getClass().getName());
		logger.setLevel(Logger.SEVERE);
		
		//Save data passed by the scheduer agent which started this
		//enlistor agent
		subject = (String)getArguments()[0];
		classlists = (Map)getArguments()[1];


		//Publish this agent to the directory facilitator
		DFAgentDescription dfd = new DFAgentDescription();
	    dfd.setName(getAID());
	    ServiceDescription sd = new ServiceDescription();
	    sd.setType(subject);
	    sd.setName(subject+" enlistor");
	    dfd.addServices(sd);
	    try {
	      DFService.register(this, dfd);
	    }
	    catch (FIPAException fe) {
	      fe.printStackTrace();
	    }

	    //Add the main behaviour for the enlistor
		addBehaviour(new EnlistorAgentBehaviour());
	}
	
	/**
	 * Behavior for enlisting.
	 * @author jach
	 */
	private class EnlistBehaviour extends Behaviour{
		AID studentAgent;
		SectionAssignment sectAss;
		ACLMessage msg,reply;
		
		public EnlistBehaviour(AID studentAgent, SectionAssignment sectAss){
			this.studentAgent = studentAgent;			
			this.sectAss = sectAss;
		}
		
		public void action(){
			try {				
				logger.info(getLocalName()+": Enlist request from "+studentAgent.getLocalName()+" "+sectAss);
				String key=sectAss.getSection().getSectionName();
				Classlist cl = (Classlist)classlists.get(key);				
				if (!cl.full()){
					cl.addStudent(studentAgent.getLocalName());
					reply = new ACLMessage(ACLMessage.INFORM);
					reply.addReceiver(studentAgent);
					reply.setContent(MSG_CONFIRM);
					send(reply);
					logger.info(getLocalName()+": Enlist request from "+studentAgent.getLocalName()+" "+sectAss+" success");
				}else{
					logger.info(getLocalName()+": Enlist request from "+studentAgent.getLocalName()+" "+sectAss+" failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}					
		}
		
		public boolean done(){
			return true;
		}
	}

	/**
	 * Behavior for cancelling
	 * @author jach
	 */
	private class CancelSlotBehaviour extends Behaviour{
		AID studentAgent;
		SectionAssignment sectAss;
		ACLMessage msg,reply;
		
		public CancelSlotBehaviour(AID studentAgent, SectionAssignment sectAss){
			this.studentAgent = studentAgent;			
			this.sectAss = sectAss;
		}
		
		public void action(){
			try {				
				logger.info(getLocalName()+": Cancel request from "+studentAgent.getLocalName()+" "+sectAss);
				String key=sectAss.getSection().getSectionName();
				Classlist cl = (Classlist)classlists.get(sectAss.getSection().getSectionName());				
				if (cl.removeStudent(studentAgent.getLocalName())){
					reply = new ACLMessage(ACLMessage.INFORM);
					reply.addReceiver(studentAgent);
					reply.setContent(MSG_CONFIRM);
					send(reply);
					logger.info(getLocalName()+": Cancel request from "+studentAgent.getLocalName()+" "+sectAss+" success");
				}else{
					logger.info(getLocalName()+": Cancel request from "+studentAgent.getLocalName()+" "+sectAss+" failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}					

		}
		
		public boolean done(){
			return true;
		}
	}

	/**
	 * Behavior that returns sections with available slots to student agents
	 * @author jach
	 *
	 */
	private class GetSectionsWithSlotsBehaviour extends SimpleBehaviour{
		AID requester;
		
		/**
		 * This constructor accepts an AID paramenter which represents that
		 * student agent that issued the request
		 * @param requester
		 */
		public GetSectionsWithSlotsBehaviour(AID requester){
			this.requester = requester;
		}
		
		/**
		 * Returns a List<SectionAssignment> with free slots
		 * @return
		 */
		private Vector getSectionsWithFreeSlots(){
			Vector retval = new Vector();
			List l = new Vector(classlists.keySet());
			for (Iterator ite=l.iterator();ite.hasNext();){
				String k=(String)ite.next();
				Classlist cl = (Classlist)classlists.get(k);
				//ok the classlist is not full
				if(!cl.full()){
					logger.info(getLocalName()+":"+cl.getSection().getSectionName()+","+(cl.getSection().getClassSize()-cl.getStudents().size()));
					SectionAssignment sectAss = new SectionAssignment();
					if (cl.getLabSection() != null){
						sectAss.setLab(cl.getLabSection());
						sectAss.setLecture(cl.getLectureSection());
					}else{
						sectAss.setLecture(cl.getLectureSection());
					}
					retval.add(sectAss);
				}
			}
			return retval;
		}
		
		/**
		 * The main action
		 */
		public void action(){
			try{
				ACLMessage reply = new ACLMessage(ACLMessage.INFORM);		
				reply.setContentObject(getSectionsWithFreeSlots());
				reply.addReceiver(requester);
				send(reply);
			}catch(IOException ioe){
				ioe.printStackTrace();
			}
		}
		
		public boolean done(){return true;}
	}
	
	public class PostSwapRequestBehaviour extends SimpleBehaviour implements Constants{
		SwapEntry swapEntry;
		AID requester;
		ACLMessage msg,reply;
		public PostSwapRequestBehaviour(AID requester, SwapEntry swapEntry){
			this.swapEntry = swapEntry;
			this.requester = requester;
		}
		
		public void action(){
	        try {
	        	/*
	        	 * TODO: Fetch the list of SwapEntry for current section
	        	 * and add this new swap entry
	        	 */
	        	
	        	List swapRequestList = (List)swapRequestsMap.get(swapEntry.getSectionToSwap()); 
	        	if (swapRequestList == null){
	        		swapRequestList = new Vector();
	        	}
	        	swapRequestList.add(swapEntry);
	        	swapRequestsMap.put(swapEntry.getSectionToSwap(), swapEntry);
	        	
	       		msg = new ACLMessage(ACLMessage.INFORM);
	       		msg.setContent(MSG_CONFIRM);
	       		msg.addReceiver(requester);
	       		send(msg);
			}catch (Exception fe) {
	        	fe.printStackTrace();
	        }

		}
		
		public boolean done(){
			return true;
		}
		
	}
	
	
	/**
	 * Main behaviour for enlistor agent
	 * @author jach
	 *
	 */
	
	private class EnlistorAgentBehaviour extends CyclicBehaviour implements Constants{
		int action=0;
		
		public void action() {
			//logger.info(getLocalName()+" enlistor waiting for enlist/cancel requests....");
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			ACLMessage msg = myAgent.receive(mt);
			ACLMessage reply;
			if (msg != null) {
				IEnlistorMessage cont=null;
				try{
					cont = (IEnlistorMessage)msg.getContentObject();
					switch(cont.getAction()){
						case ACTION_GET_SECTIONS_WITH_SLOTS:
							myAgent.addBehaviour(new GetSectionsWithSlotsBehaviour(msg.getSender()));
							break;
						case ACTION_CANCEL_SLOT:
							EnlistorMessage content= (EnlistorMessage)msg.getContentObject();
							myAgent.addBehaviour(new CancelSlotBehaviour(msg.getSender(),content.getSectionAssignment()));
							break;
						case ACTION_ENLIST:
							content= (EnlistorMessage)msg.getContentObject();
							myAgent.addBehaviour(new EnlistBehaviour(msg.getSender(),content.getSectionAssignment()));
							break;
						case ACTION_POST_SWAP_REQUEST:							
							SwapEntry swapEntry=(SwapEntry)msg.getContentObject();
							myAgent.addBehaviour(new PostSwapRequestBehaviour(msg.getSender(),swapEntry));
							break;
					}
				}catch(Exception e){
					e.printStackTrace();		
				}
				
        	}
		}		
	}
}
