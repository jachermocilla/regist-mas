package jach.msthesis.csp;

public class NumericValue implements Value{
	public double value;
	
	
	public NumericValue(double value){
		this.value=value;
	}
	
	public String toString(){
		return ""+value;
	}
	
}
