package jach.msthesis.csp;

import java.util.Iterator;

public class BacktrackSolver implements Solver {
	CSP csp;
	
	public BacktrackSolver(CSP csp){
		this.csp = csp;
	}
	
	
	public void solve(){
		backtrack(csp);
	}
	
	Solution backtrack(CSP csp){
		return _backtrack(csp);
	}
	
	Solution _backtrack(CSP csp){
		if (csp.solutionIsComplete()){
			return csp.getCurrentSolution();
		}
		
		Variable variable = csp.getNextUnassignedVariable();
		for (Iterator ite=csp.getValuesIterator(variable);ite.hasNext();){			
			Value value=(Value)ite.next();
			Assignment assignment=new Assignment(variable,value);
			csp.getCurrentSolution().addAssignment(assignment);
			if (csp.solutionIsConsistent()){
				Solution result =  _backtrack(csp);
				if (result != null){
					return result;
				}
			}else{
				csp.getCurrentSolution().removeAssignment(assignment);
			}
		}
		return null;
	}
}
