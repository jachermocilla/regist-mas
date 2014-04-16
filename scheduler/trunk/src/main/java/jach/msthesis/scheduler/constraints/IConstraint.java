package jach.msthesis.scheduler.constraints;

import java.io.Serializable;

import jach.msthesis.registration.model.ISection;
import jach.msthesis.scheduler.SkedNode;


/**
 * Interface for Constraints like no conflict
 * 
 * @author jachermocilla
 * @version $Id: IConstraint.java 777 2008-09-26 15:52:20Z jach $ 
 *
 */

public interface IConstraint extends Serializable{

	/**
	 * Method that must be implemented
	 * 
	 * @return 	true or false depending whether the constraint is satisfied or not
	 */
	public boolean isSatisfied(SkedNode leafNode);
	
	public boolean isSatisfied(ISection section);
	
	public boolean isSatisfied();
}
