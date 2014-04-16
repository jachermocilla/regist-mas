package jach.msthesis.registration.model;



import java.io.Serializable;
import java.util.List;

/**
 *  Writein information interface
 *  
 *  @author jach
 *  @version $Id: IWriteIn.java 705 2008-09-23 01:11:40Z jach $
 */


public interface IWriteIn extends Serializable {
	
	/**
	 * This method returns the allowed units 
	 */	
	public int getUnitsAllowed();
	
	/**
	 * This method should return the list of subjects in the writein
	 * @return
	 */
	public List getSubjects();	
	
	/**
	 * Returns an ISubject object given the subject name
	 * @param name
	 * @return
	 */
	public ISubject getSubjectInfo(String name);
	
}
