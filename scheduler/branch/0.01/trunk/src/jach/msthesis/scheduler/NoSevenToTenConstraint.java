package jach.msthesis.scheduler;

import jach.msthesis.courselector.TimeSlot;

import java.util.Iterator;
/**
 * Encapsulates a "no seven to ten" constraint
 * @author jach
 *
 */
public class NoSevenToTenConstraint extends Constraint {

	public boolean isSatisfied(SkedNode node){
		Iterator nodeSlots=node.getSection().getTimeSlots().iterator();
		while(nodeSlots.hasNext()){
			TimeSlot nodeSlot=(TimeSlot)nodeSlots.next();
			if (nodeSlot.getSlot() < 18)
				return false;
		}
		return true;
	}
}
