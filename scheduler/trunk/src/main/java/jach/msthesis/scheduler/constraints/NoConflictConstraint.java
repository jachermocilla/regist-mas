package jach.msthesis.scheduler.constraints;


import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.Section;
import jach.msthesis.registration.model.SectionAssignment;
import jach.msthesis.registration.model.TimeSlot;
import jach.msthesis.scheduler.SkedNode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Encapsulates a "no conflict" constraint. This is
 * accomplished by checking the time slots.
 * The node specified is checked against all
 * predecessors if there are any conflicts
 * 
 * @author jach
 * @version $Id:NoConflictConstraint.java 846 2008-09-30 02:17:08Z jach $
 *
 */

public class NoConflictConstraint extends Constraint {
	
	/**
	 *  List<SectionAssignments>
	 */	
	Map sectionAssignments;
	
	public NoConflictConstraint(){
	}
	
	public NoConflictConstraint(Map sectionAssignments){
		this.sectionAssignments = sectionAssignments;
	}
	
	
	public boolean isSatisfied(ISection target){
		List l= new Vector(sectionAssignments.keySet());
		for (Iterator ite=l.iterator();ite.hasNext();){
			String k=(String)ite.next();
			SectionAssignment sectAss = (SectionAssignment)sectionAssignments.get(k);			
			
			ISection lecture=sectAss.getLecture();
			for (Iterator sectionTimeSlots=lecture.getTimeSlots().iterator();sectionTimeSlots.hasNext();){
				TimeSlot sectionTimeSlot=(TimeSlot)sectionTimeSlots.next();
				for (Iterator targetTimeSlots=target.getTimeSlots().iterator();targetTimeSlots.hasNext();){
					TimeSlot targetTimeSlot=(TimeSlot)targetTimeSlots.next();
					if (sectionTimeSlot.getSlot() == targetTimeSlot.getSlot()){
						return false;
					}	
				}
			}
			
			ISection lab=sectAss.getLab();
			if (lab != null){
				for (Iterator sectionTimeSlots=lab.getTimeSlots().iterator();sectionTimeSlots.hasNext();){
					TimeSlot sectionTimeSlot=(TimeSlot)sectionTimeSlots.next();
					for (Iterator targetTimeSlots=target.getTimeSlots().iterator();targetTimeSlots.hasNext();){
						TimeSlot targetTimeSlot=(TimeSlot)targetTimeSlots.next();
						if (sectionTimeSlot.getSlot() == targetTimeSlot.getSlot()){
							return false;
						}	
					}
				}
			}
		}
		return true;
	}
	
	public boolean isSatisfied(SkedNode node){		
		SkedNode pred=node.getParent();

		if (pred == null)
		   return true;
		
		//while not at the root
		while (pred.getParent()!=null){
			//get the time slots for the node
			Iterator nodeSlots=node.getSection().getTimeSlots().iterator();			
			while(nodeSlots.hasNext()){
				TimeSlot nodeSlot=(TimeSlot)nodeSlots.next();
				//get the time slots for predecessor
				Iterator predSlots=pred.getSection().getTimeSlots().iterator();
				while (predSlots.hasNext()){					
					TimeSlot predSlot=(TimeSlot)predSlots.next();
					if (predSlot.getSlot() == nodeSlot.getSlot()){
						return false;		//conflict
					}
				}
			}
			pred=pred.getParent();		//move up
		}
		return true;
	}
}
