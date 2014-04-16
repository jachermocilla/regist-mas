package jach.msthesis.scheduler.constraints;


import jach.msthesis.registration.model.TimeSlot;
import jach.msthesis.scheduler.SkedNode;

import java.util.Iterator;
/**
 * Encapsulates a "no seven to ten" constraint
 * @author jach
 * @version $Id: NoSevenToTenConstraint.java 562 2008-09-15 02:35:43Z jach $
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
