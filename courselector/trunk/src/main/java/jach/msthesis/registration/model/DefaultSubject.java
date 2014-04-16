package jach.msthesis.registration.model;

import java.io.Serializable;

/**
 * Default implementation of subject
 * 
 * @author Joseph Anthony C. Hermocilla 
 * @version $Id:DefaultSubject.java 548 2008-09-15 00:52:12Z jach $
 *
 */
public class DefaultSubject extends AbstractSubject implements Serializable  {
	/**
	 * Constructor for default subject
	 * 
	 * @param name	a <code>String</code> that represents the name of the subject
	 * @param year	an <code>int</code> that represents the year level that this subject
	 * 				should be taken
	 * @param sem	an <code>int</code> that represents the sem that this subject should be
	 * 				taken
	 * @param units	an <code>int</code> that represents the number of credit units
	 */
	public DefaultSubject(String name, int year, int sem, int units){				
		this.name=name;
		this.year=year;
		this.sem=sem;
		this.units=units;
	}
	
	
	public DefaultSubject(String name){
		this.name=name;
	}
	
}
