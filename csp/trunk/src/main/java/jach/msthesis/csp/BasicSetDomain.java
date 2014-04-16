package jach.msthesis.csp;

import java.util.HashSet;
import java.util.Set;

public class BasicSetDomain implements Domain{
	Set values=new HashSet();
	
	public void addValue(Value value){
		values.add(value);
	}
	
	public Set getValues(){
		return values;
	}
	

}
