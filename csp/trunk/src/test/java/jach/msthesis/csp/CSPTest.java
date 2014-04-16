package jach.msthesis.csp;

import java.util.Iterator;

import junit.framework.TestCase;

public class CSPTest extends TestCase {

	protected void setUp() throws Exception {
		BasicSetDomain domain=new BasicSetDomain();	
		int n=5;
		
		for (int i=1;i<=n-2;i++){
			domain.addValue(new NumericValue(i));
		}
		
		
		CSP csp=new CSP();
		csp.setVariableOrderHeuristic(new RandomVariableOrderHeuristic(csp));
		csp.setValueOrderHeuristic(new RandomValueOrderHeuristic(csp));
		
		Solver solver= new BacktrackSolver(csp);
		
		for (int i=1;i<=n;i++){
			Variable v=new GenericVariable("v"+i,domain);
			csp.addVariable(v);
		}

		csp.addConstraint(new NotEqualConstraint());
		
		solver.solve();
		
		for (Iterator ite=csp.getCurrentSolution().getAssignments().iterator();ite.hasNext();){
			Assignment assignment=(Assignment)ite.next();
			System.out.println(assignment);
		}		
	}
	
	public void testNow(){
		
	}

}
