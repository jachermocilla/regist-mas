package jach.msthesis.csp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class RandomVariableOrderHeuristic implements VariableOrderHeuristic{
	CSP csp;
	
	public RandomVariableOrderHeuristic(CSP csp){
		this.csp = csp;
	}
	
	public Variable getNextVariable(){
		List unassigned=new ArrayList();
		Set assigned=csp.getCurrentSolution().getVariables();
		for (Iterator ite=csp.getVariables().iterator();ite.hasNext();){
			Variable variable=(Variable)ite.next();
			if(!assigned.contains(variable)){
				unassigned.add(variable);
			}
		}
		Collections.shuffle(unassigned);
		return (Variable)unassigned.get(0);
	}
	
}
