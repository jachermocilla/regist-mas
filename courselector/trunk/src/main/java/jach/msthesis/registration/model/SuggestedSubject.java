package jach.msthesis.registration.model;

import java.io.Serializable;


/**
 * Encapsulates a suggested subject including the sections
 * @author jachermocilla
 * @version $Id:SuggestedSubject.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public class SuggestedSubject extends AbstractSubject implements Serializable{
	private int rank;	

	/**
	 * Detailed constructor.
	 * @param subject
	 * @param rank
	 */
	public SuggestedSubject(ISubject subject,int rank){
		this.name=subject.getName();
		this.units=subject.getUnitCredit();
		this.rank=rank;
	}
	
	/**
	 * Returns the rank.
	 * @return
	 */
	public int getRank(){
		return rank;
	}
}
