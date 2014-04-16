package jach.msthesis.csp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RandomValueOrderHeuristic implements ValueOrderHeuristic{
	CSP csp;
	
	public RandomValueOrderHeuristic(CSP csp){
		this.csp = csp;
	}
	
	public Iterator getValuesIterator(Variable variable){
		Domain domain=variable.getDomain();
		List l=new ArrayList(domain.getValues());
		Collections.shuffle(l);
		return l.iterator();
	}

}
