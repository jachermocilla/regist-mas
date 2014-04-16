package jach.msthesis.scheduler.constraints;


import jach.msthesis.registration.model.TimeSlot;
import jach.msthesis.scheduler.SkedNode;

import java.util.Iterator;

/**
 * Encapsulates a "no class on friday" constraint
 * 
 * @author jach
 * @version $Id: NoFridayClassConstraint.java 562 2008-09-15 02:35:43Z jach $
 *
 */

public class NoFridayClassConstraint extends Constraint{
	int friSlots[]=new int[]{4,10,16,22,28,34,40,46,52,58,64,70};
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
