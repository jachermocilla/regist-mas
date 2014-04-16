package jach.msthesis.scheduler.constraints;

import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.TimeSlot;
import jach.msthesis.scheduler.SkedNode;

import java.util.*;

/**
 * Default implementation of IConstraint. Given
 * a list of unwanted slots, the algorithm
 * checks if the time slot of the node is in it,  returning false
 *  
 * @author jach
 * @version $Id: Constraint.java 778 2008-09-26 16:05:31Z jach $
 *
 */


public class Constraint implements IConstraint {	
	public static NoFridayClassConstraint NOFRIDAYS=new NoFridayClassConstraint();
	public static NoMondayClassConstraint NOMONDAYS=new NoMondayClassConstraint();
	public static NoSevenToTenConstraint NOSEVENTOTEN=new NoSevenToTenConstraint();
	public static NoLunchClassConstraint NOLUNCH=new NoLunchClassConstraint();
		
	//The taboo list contains timeslots that must not be given
	private List taboo=new Vector();
	
	/**
	 * Adds a taboo slot to the taboo list.
	 * @param slot
	 */
	public void addTabooSlot(int slot){
		taboo.add(new Integer(slot));
	}
	
	/**
	 * Returns true if the contraints has been satisfied.
	 */
	public boolean isSatisfied(SkedNode node) {
		//Get an iterator for the list of time slots for node
		Iterator nodeSlots=node.getSection().getTimeSlots().iterator();
		while(nodeSlots.hasNext()){
			TimeSlot nodeSlot=(TimeSlot)nodeSlots.next();
			//Obtain an iterator to the taboo list
			Iterator ite=taboo.iterator();
			while (ite.hasNext()){
				Integer val=(Integer)ite.next();
				//return false if time slot is in taboo
				if (nodeSlot.getSlot()==val.intValue())
					return false;
			}
		}
		//return true because all timeslots are not in taboo
		return true;
	}
	
	/**
	 * Returns true if the contraints has been satisfied.
	 */
	public boolean isSatisfied(ISection section) {
		//Get an iterator for the list of time slots for node
		Iterator nodeSlots=section.getTimeSlots().iterator();
		while(nodeSlots.hasNext()){
			TimeSlot nodeSlot=(TimeSlot)nodeSlots.next();
			//Obtain an iterator to the taboo list
			Iterator ite=taboo.iterator();
			while (ite.hasNext()){
				Integer val=(Integer)ite.next();
				//return false if time slot is in taboo
				if (nodeSlot.getSlot()==val.intValue())
					return false;
			}
		}
		//return true because all timeslots are not in taboo
		return true;
	}
	
	public boolean isSatisfied(){
		return false;
	}
}
