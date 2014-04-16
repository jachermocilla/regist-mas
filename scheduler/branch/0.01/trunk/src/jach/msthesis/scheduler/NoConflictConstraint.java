package jach.msthesis.scheduler;

import jach.msthesis.courselector.TimeSlot;

import java.util.Iterator;

/**
 * Encapsulates a "no conflict" constraint. This is
 * accomplished by checking the time slots.
 * The node specified is checked against all
 * predecessors if there are any conflicts
 * 
 * @author jach
 *
 */

public class NoConflictConstraint extends Constraint {
		
	public boolean isSatisfied(SkedNode node){
		
		SkedNode pred=node.getParent();

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
