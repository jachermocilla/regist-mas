package jach.msthesis.registration.model;

import java.io.Serializable;

/**
 * Implementation of a default curriculum with no subjects 
 * 
 * @author jachermocilla
 * @version $Id:DefaultCurriculum.java 548 2008-09-15 00:52:12Z jach $
 *
 */
public class DefaultCurriculum extends AbstractCurriculum implements Serializable{
	
	/**
	 * Default constructor
	 *
	 */
	public DefaultCurriculum(){
		super();
	}
	
	
	/**
	 * Constructor
	 * 
	 * @param name	a <code>String</code> that names the curriculum
	 */
	public DefaultCurriculum(String name){
		this.name=name;		
	}	
}
