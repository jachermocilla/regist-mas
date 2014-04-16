package jach.msthesis.registration.model;

import java.io.Serializable;

/**
 * Provides methods for prerequisites  
 * 
 * @author jachermocilla
 * @version $Id:IPrerequisite.java 548 2008-09-15 00:52:12Z jach $
 */

public interface IPrerequisite extends Serializable{
	
	/**
	 * Returns a textual description of the prerequisite
	 * 
	 * @return		a <code>String</code> that describes the
	 * 				prerequisite
	 */
	public String getDescription();
	
}
