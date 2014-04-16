package jach.msthesis.registration.model;



import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * Holds the classlist object, a list of students. A classlist is 
 * associated with subject:section combination and is used in the
 * Prototype scheduler
 * 
 * @author jach
 * @version $Id: Classlist.java 888 2008-10-01 14:38:55Z jach $
 * 
 */
public class Classlist implements Serializable {	
	
	/**
	 * The section associated with the classlist
	 */
	private ISection lectureSection;
	
	/**
	 * The section associated with the classlist
	 */
	private ISection labSection;
	
	private ISection section;
	
	/**
	 * The Map<StudentNumber> of student belonging to this section
	 */
	Map students=new HashMap();
	
	
	
	/**
	 * Constructor for classlist given a section
	 * @param sect
	 */
	public Classlist(ISection lectureSection, ISection labSection){
		this.labSection = labSection;
		this.lectureSection = lectureSection;
		if (labSection != null){
			section = labSection;
		}else{
			section = lectureSection;
		}
	}
	
	/**
	 * Adds a student to the classlist when there is
	 * enough slot
	 * 
	 * @param stdNum
	 * @return true if the student was successfully added
	 */
	public boolean addStudent(String stdNum){
		if (students.size() < section.getClassSize()){
			if (!students.containsKey(stdNum)){
				students.put(stdNum,stdNum);
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
	
	public boolean removeStudent(String stdNum){
		if (students.containsKey(stdNum)){
			students.remove(stdNum);
			return true;
		}else{
			return false;
		}
	}
	
	
	/**
	 * Returns the list<StudentNumber> of students in this classlist 
	 * @return
	 */
	public Map getStudents(){
		return students;
	}	
	
	/**
	 * Returns true if the classlist is full
	 * @return true if the classlist is full
	 */
	public boolean full(){
		return (students.size()==section.getClassSize());
	}
	
	/**
	 * Returns the ISection associated with this classlist.
	 * @return
	 */
	public ISection getSection(){
		if (labSection != null){
			return labSection;
		}else{
			return lectureSection;
		}
		
	}
	
	public ISection getLectureSection(){
		return lectureSection;
	}
	
	public ISection getLabSection(){
		return labSection;
	}
	
	public void setLectureSection(ISection lectureSection){
		this.lectureSection = lectureSection;		
	}
	
	public void setLabSection(ISection labSection){
		this.labSection = labSection;		
	}
}
