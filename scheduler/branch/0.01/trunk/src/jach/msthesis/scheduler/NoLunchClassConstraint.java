package jach.msthesis.scheduler;

import jach.msthesis.courselector.TimeSlot;
import java.util.Iterator;
/**
 * Encapsulates a "no class during lunch" constraint
 * @author jach
 *
 */
public class NoLunchClassConstraint extends Constraint {
	public boolean isSatisfied(SkedNode node){
		Iterator nodeSlots=node.getSection().getTimeSlots().iterator();
		while(nodeSlots.hasNext()){
			TimeSlot nodeSlot=(TimeSlot)nodeSlots.next();
			if ((nodeSlot.getSlot() >= 30) && (nodeSlot.getSlot() <= 35))
				return false;
		}
		return true;
	}
}
