package jach.msthesis.scheduler.regist;

import jach.msthesis.courselector.ISubject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Encapsulates students write-in information 
 * @author jach
 *
 */

public class WriteIn implements IWriteIn{
	//Student
	String studentNumber;
	
	//The priority of the student
	//used for sorting
	int priority;
	
	//The number of units allowed
	int unitsAllowed;
	
	List desiredSubjects=new ArrayList();
	
	public WriteIn(){}
	
	public WriteIn(String studentNumber,int priority,int unitsAllowed){
		this.studentNumber=studentNumber;
		this.priority=priority;
		this.unitsAllowed=unitsAllowed;
	}
	
	public String getStudentNumber(){
		return studentNumber;
	}
	
	public int getPriority(){
		return priority;
	}
	
	public int getUnitsAllowed(){
		return unitsAllowed;
	}
	
	public List getSubjects(){
		return desiredSubjects;
	}
	
	public void addSubject(ISubject subject){
		desiredSubjects.add(subject);		
	}
	
	public ISubject getSubjectInfo(String name){
		Iterator ite=desiredSubjects.iterator();
		while (ite.hasNext()){
			ISubject sub=(ISubject)ite.next();
			if (sub.getName().equals(name))
				return sub;
		}
		return null;
	}
}
