package jach.msthesis.scheduler.regist;

import jach.msthesis.courselector.Section;

import java.util.List;
import java.util.Vector;


/**
 * Holds the classlist object
 * @author jach
 *
 */

public class Classlist {	
	//The section associated with the classlist
	Section section;
	
	//The list of student belonging to this section
	List students=new Vector();
	
	public Classlist(Section sect){
		this.section=sect;		
	}
	
	/**
	 * Adds a student to the classlist when there is
	 * enough slot
	 * @param stdNum
	 * @return true if the student was successfully added
	 */
	public boolean addStudent(String stdNum){
		if (students.size() < section.getClassSize()){
			students.add(stdNum);
			return true;
		}
		return false;
	}
	
	/**
	 * Returns the list of students in this classlist 
	 * @return
	 */
	public List getStudents(){
		return students;
	}	
	
	/**
	 * Returns true if the classlist is full
	 * @return true if the classlist is full
	 */
	public boolean full(){
		return (students.size()==section.getClassSize());
	}
}
