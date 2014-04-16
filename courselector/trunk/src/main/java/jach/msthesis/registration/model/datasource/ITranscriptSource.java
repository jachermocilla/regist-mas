package jach.msthesis.registration.model.datasource;

import jach.msthesis.registration.model.ITranscript;

/**
 * Defines method to be implemented by the sources for transcript
 * @author jachermocilla 
 * @version $Id:ITranscriptSource.java 548 2008-09-15 00:52:12Z jach $
 *
 */
public interface ITranscriptSource {
	/**
	 * Loads a transcript from a specific source
	 * @return
	 */
	public ITranscript	load();
	
	
	/**
	 * Returns the Transcript loaded from this source
	 * @return
	 */
	public ITranscript getTranscript();
}
