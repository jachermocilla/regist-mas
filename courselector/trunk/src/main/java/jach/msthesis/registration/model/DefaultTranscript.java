package jach.msthesis.registration.model;

import java.io.Serializable;

/**
 * Default Transcript
 * @author jach
 * @version $Id:DefaultTranscript.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public class DefaultTranscript extends AbstractTranscript implements Serializable {
	
	/**
	 * Default constructor 
	 *
	 */
	public DefaultTranscript(){}
	
	
	/**
	 * Constructor with parameters
	 * 
	 * @param studentNumber
	 * @param curriculum
	 */
	public DefaultTranscript(String studentNumber,ICurriculum curriculum){
		this.studentNumber=studentNumber;
		this.curriculum=curriculum;
	}	
}
