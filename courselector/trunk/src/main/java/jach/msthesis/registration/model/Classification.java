package jach.msthesis.registration.model;

import java.io.Serializable;

/**
 * Implements classification as prerequisites
 * There are several subjects whose prerequisites are based
 * on the classification instead of other subjects.
 * 
 * @author jachermocilla
 * @version  $Id:Classification.java 548 2008-09-15 00:52:12Z jach $
 *
 */
public class Classification implements IPrerequisite, Serializable {
	
	private String classification;
	
	public static final Classification FRESHMAN=new Classification("FRESHMAN"); 
	public static final Classification SOPHOMORE=new Classification("SOPHOMORE");
	public static final Classification JUNIOR=new Classification("JUNIOR");
	public static final Classification SENIOR=new Classification("SENIOR");
	public static final Classification SUPER_SENIOR=new Classification("SUPER_SENIOR");
	
	public Classification(String classification){
		this.classification=classification;		
	}
	
	public String getDescription() {
		return classification;
	}

}
