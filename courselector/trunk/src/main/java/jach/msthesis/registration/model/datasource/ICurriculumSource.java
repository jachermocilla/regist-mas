package jach.msthesis.registration.model.datasource;

import jach.msthesis.registration.model.ICurriculum;

/**
 * Provides methods that allows loading of curriculum from
 * different data sources like text files or databases
 * 
 * @author jachermocilla 
 * @version $Id:ICurriculumSource.java 548 2008-09-15 00:52:12Z jach $
 */
public interface ICurriculumSource {
	/**
	 * Loads a curriculum identified by name from the implementing data source
	 * 
	 * @return			a <code>ICurriculum</code> object
	 */
	public ICurriculum load();	
	
	/**
	 * Returns the curriculum loaded from this curriculum source
	 * @return
	 */
	
	public ICurriculum getCurriculum();
	
}
