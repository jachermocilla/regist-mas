package jach.msthesis.scheduler.regist;

import jach.msthesis.courselector.ISubject;

import java.util.List;

/**
 * Encapsulates a form 5
 * @author jach
 *
 */

public class Form5 implements IForm5{
	
	//Units obtained
	int unitsObtained;
	
	//List of Subjects in the Form 5
	List subjects;
	
	public Form5(){		
	}
	
	
	
	public int getUnitsObtained(){
		return unitsObtained;
	}
	
	public List getSubjects(){
		return subjects;
	}
	
	public void addSubject(ISubject subject){
		subjects.add(subject);		
		unitsObtained+=subject.getUnitCredit();
	}

}
