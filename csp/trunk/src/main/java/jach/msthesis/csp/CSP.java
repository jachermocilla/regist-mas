package jach.msthesis.csp;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CSP {
	Set variables = new HashSet();
	Set constraints = new HashSet();
	Solution currentSolution=new Solution();
	VariableOrderHeuristic variableOrderHeuristic;
	ValueOrderHeuristic valueOrderHeuristic;
	
	public void addVariable(Variable variable){
		variables.add(variable);
	}
	
	public void addConstraint(Constraint constraint){
		constraints.add(constraint);
		constraint.setCSP(this);
	}
	
	public Solution getCurrentSolution(){
		return currentSolution;
	}
	
	
	public boolean solutionIsComplete(){
		if (currentSolution.assignments.size() == variables.size())
			return true;
		return false;
	}
	
	public boolean solutionIsConsistent(){
		for (Iterator ite=constraints.iterator();ite.hasNext();){
			Constraint constraint=(Constraint)ite.next();
			if (!constraint.isSatisfied()){
				return false;
			}
		}
		return true;
	}

	public void setVariableOrderHeuristic(VariableOrderHeuristic variableOrderHeuristic){
		this.variableOrderHeuristic = variableOrderHeuristic;
	}
	
	public void setValueOrderHeuristic(ValueOrderHeuristic valueOrderHeuristic){
		this.valueOrderHeuristic = valueOrderHeuristic;
	}
	
	public Variable getNextUnassignedVariable(){
		return variableOrderHeuristic.getNextVariable();
	}
	
	public Iterator getValuesIterator(Variable variable){
		return valueOrderHeuristic.getValuesIterator(variable);
	}
	
	public Set getVariables(){
		return variables;
	}
}
