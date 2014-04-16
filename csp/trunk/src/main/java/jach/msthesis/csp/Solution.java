package jach.msthesis.csp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Solution {
	Set assignments = new HashSet();
	Assignment lastAssignment;

	public Solution(){}
	
	
	public void addAssignment(Assignment assignment){
		assignments.add(assignment);
		lastAssignment=assignment;
	}
	
	public void removeAssignment(Assignment assignment){
		assignments.remove(assignment);
	}
	
	public Set getVariables(){
		Set retval = new HashSet();
		for (Iterator ite=assignments.iterator();ite.hasNext();){
			Assignment assignment=(Assignment)ite.next();
			retval.add(assignment.getVariable());
		}
		return retval;
	}
	
	public Value getValue(Variable variable){
		for (Iterator ite=assignments.iterator();ite.hasNext();){
			Assignment assignment=(Assignment)ite.next();
			if (assignment.getVariable() == variable){
				return assignment.getValue();
			}
		}
		return null;
	}
	
	public Set getAssignments(){
		return assignments;
	}
 
	public Assignment getLastAssignment(){
		return lastAssignment;
	}
	
	
}
