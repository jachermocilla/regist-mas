package jach.msthesis.scheduler;


/**
 * Interface for Constraints like no conflict
 * 
 * @author jachermocilla
 *
 */

public interface IConstraint {

	/**
	 * Method that must be implemented
	 * 
	 * @return 	true or false depending whether the constraint is satisfied or not
	 */
	public boolean isSatisfied(SkedNode leafNode);
	
}
