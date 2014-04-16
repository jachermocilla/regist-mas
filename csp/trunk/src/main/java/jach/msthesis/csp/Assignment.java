package jach.msthesis.csp;

public class Assignment {
	Variable variable;
	Value value;
	
	
	
	public Assignment(Variable variable, Value value){
		this.variable=variable;
		this.value=value;
	}
	
	public Value getValue() {
		return value;
	}

	public Variable getVariable() {
		return variable;
	}

	
	public String toString(){
		return "<"+variable+","+value+">";
	}
	
	public boolean isConsistent(){
		return false;
	}
}
