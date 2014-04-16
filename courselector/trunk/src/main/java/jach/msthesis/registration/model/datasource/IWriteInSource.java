package jach.msthesis.registration.model.datasource;

import java.util.Map;

/**
 * Interface for the source of writein information
 * @author jach
 * @version $Id: IWriteInSource.java 555 2008-09-15 01:06:19Z jach $
 *
 */
public interface IWriteInSource {
	/**
	 * Load the writein 
	 */
	public void load();
	
	/**
	 * Returns a map of writeins 
	 */
	public Map getWriteIns();
}
