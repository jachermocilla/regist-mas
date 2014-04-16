package jach.msthesis.csp;

import java.util.Iterator;

public class NotEqualConstraint implements Constraint{
	CSP csp;
	
	public NotEqualConstraint(){
	}
	
	
	public boolean isSatisfied(){
		Solution current = csp.getCurrentSolution();
		Assignment lastAssignment=csp.getCurrentSolution().getLastAssignment();
		for (Iterator ite=current.getAssignments().iterator();ite.hasNext();){
			Assignment previousAssignment=(Assignment)ite.next();
			if (!lastAssignment.getVariable().equals(previousAssignment.getVariable())){
				if (lastAssignment.getValue().equals(previousAssignment.getValue())){
					return false;
				}
			}
		}
		return true;
	}
	
	
	//This should be called in CSP
	public void setCSP(CSP csp){
		this.csp=csp;
	}
	
}
