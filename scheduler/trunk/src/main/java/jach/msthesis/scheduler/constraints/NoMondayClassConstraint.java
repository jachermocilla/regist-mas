package jach.msthesis.scheduler.constraints;


import jach.msthesis.registration.model.TimeSlot;
import jach.msthesis.scheduler.SkedNode;

import java.util.Iterator;

/**
 * Constraint for no classes on monday
 * @author jach
 * @version $Id: NoMondayClassConstraint.java 562 2008-09-15 02:35:43Z jach $
 */

public class NoMondayClassConstraint extends Constraint {
	int friSlots[]=new int[]{0,6,12,18,24,30,36,42,48,54,60,66};
	public boolean isSatisfied(SkedNode node){
		Iterator nodeSlots=node.getSection().getTimeSlots().iterator();
		while(nodeSlots.hasNext()){
			TimeSlot nodeSlot=(TimeSlot)nodeSlots.next();
			for(int i=0;i < friSlots.length;i++){
				if (friSlots[i]==nodeSlot.getSlot())
					return false;
			}
		}
		return true;
	}
}
