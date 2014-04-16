package jach.msthesis.scheduler;

import jach.msthesis.courselector.TimeSlot;

import java.util.Iterator;

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
