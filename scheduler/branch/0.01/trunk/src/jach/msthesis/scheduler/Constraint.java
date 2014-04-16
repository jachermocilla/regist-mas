package jach.msthesis.scheduler;

import jach.msthesis.courselector.TimeSlot;
import java.util.*;

/**
 * Default implementation of IConstraint. Given
 * a list of unwanted slots, the algorithm
 * checks if the time slot of the node is in it,  returning false
 *  
 * @author jach
 *
 */
public class Constraint implements IConstraint {
	public static NoFridayClassConstraint NOFRIDAYS=new NoFridayClassConstraint();
	public static NoMondayClassConstraint NOMONDAYS=new NoMondayClassConstraint();
	public static NoSevenToTenConstraint NOSEVENTOTEN=new NoSevenToTenConstraint();
	public static NoLunchClassConstraint NOLUNCH=new NoLunchClassConstraint();
	
	
	private List taboo=new Vector();
	
	public void addTabooSlot(int slot){
		taboo.add(new Integer(slot));
	}
	
	public boolean isSatisfied(SkedNode node) {
		Iterator nodeSlots=node.getSection().getTimeSlots().iterator();
		while(nodeSlots.hasNext()){
			TimeSlot nodeSlot=(TimeSlot)nodeSlots.next();
			Iterator ite=taboo.iterator();
			while (ite.hasNext()){
				Integer val=(Integer)ite.next();
				if (nodeSlot.getSlot()==val.intValue())
					return false;
			}
		}
		return true;
	}

}
