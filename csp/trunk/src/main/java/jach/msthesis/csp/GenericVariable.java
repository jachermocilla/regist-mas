package jach.msthesis.csp;

public class GenericVariable implements Variable, Comparable {
	String name;
	Domain domain;
	
	public GenericVariable(String name, Domain domain){
		this.name=name;
		this.domain=domain;
	}
	
	public GenericVariable(String name){
		this.name=name;
	}
	
	public String getName(){
		return name;
	}
	
	public int compareTo(Object o){
		return 0;
	}
	
	public String toString(){
		return name;
	}
	
	public void setDomain(Domain domain){
		this.domain=domain;
	}
	
	public Domain getDomain(){
		return domain;
	}
	
}
