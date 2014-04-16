package jach.msthesis.scheduler.constraints;

import jach.msthesis.registration.model.TimeSlot;
import jach.msthesis.scheduler.SkedNode;

import java.util.Iterator;
/**
 * Encapsulates a "no class during lunch" constraint
 * @author jach
 * @version $Id: NoLunchClassConstraint.java 562 2008-09-15 02:35:43Z jach $
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
