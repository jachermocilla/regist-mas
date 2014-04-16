package jach.msthesis.scheduler.regist;

import java.util.List;

public interface IWriteIn {
	/**
	 * This method returns the desired units
	 * @return
	 */	
	public int getUnitsAllowed();
	
	/**
	 * This method should return the list of subjects in the writein
	 * @return
	 */
	public List getSubjects();	
}
