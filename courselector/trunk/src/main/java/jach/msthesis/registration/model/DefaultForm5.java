package jach.msthesis.registration.model;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Default implementation of form 5
 * @author jach
 *
 */

public class DefaultForm5 implements IForm5, Serializable {
	
	/**
	 * Reference to writein
	 */	
	protected IWriteIn writein;
	
	/**
	 * Mao<SectionAssignment>
	 */
	protected Map sectionAssignments=new HashMap();
	

	/**
	 * Returns the list of sections for this form 5
	 */
	public Map getSectionAssignments() {
		return sectionAssignments;
	}

	public List getSections() {
		return null;
	}
	
	/**
	 * Returns the number of units in this schedule based on the writein
	 */
	public int getUnitsObtained() {
		int uo = 0;
		List l = new Vector(sectionAssignments.keySet());
		Iterator ite = l.iterator();
		while (ite.hasNext()) {
			String k=(String)ite.next();
			SectionAssignment sectAss = (SectionAssignment) sectionAssignments.get(k);
			ISubject sub = 
				(ISubject) writein.getSubjectInfo(sectAss.getLecture().getSubject()
						.getName());
			if (sub != null) {
					uo += sub.getUnitCredit();
			}
		}
		return uo;
	}
	

	/**
	 * Returns a reference to the writein associated for this form 5
	 */
	public IWriteIn getWriteIn() {
		return writein;
	}
	
	/**
	 * Sets the writein
	 * @param writein
	 */
	public void setWriteIn(IWriteIn writein){
		this.writein = writein;
	}
	
	public SectionAssignment getSectionAssignment(String subject){
		//First check if subject is in writein
		boolean inWriteIn = false;
		for (Iterator ite=writein.getSubjects().iterator();ite.hasNext();){
			ISubject s=(ISubject)ite.next();
			if(s.getName().equals(subject)){
				inWriteIn = true;
				break;
			}
		}
		if (!inWriteIn){
			return null;
		}
		
		List l = new Vector(sectionAssignments.keySet());		
		for (Iterator ite=l.iterator();ite.hasNext();){
			String k=(String)ite.next();
			SectionAssignment sectAss=(SectionAssignment)sectionAssignments.get(k);
			if(sectAss.getLecture().getSubject().getName().equals(subject)){
				return sectAss;
			}
		}	
		return null;
	}
	
	/**
	 * Returns a List<ISection> for a subject. The list
	 * contains only a single
	 */
	/*
	public ISection getSection(String subject){
		//First check if subject is in writein
		boolean inWriteIn = false;
		for (Iterator ite=writein.getSubjects().iterator();ite.hasNext();){
			ISubject s=(ISubject)ite.next();
			if(s.getName().equals(subject)){
				inWriteIn = true;
				break;
			}
		}
		if (!inWriteIn){
			return null;
		}

		List sects = new Vector();
		for (Iterator ite=sections.iterator();ite.hasNext();){
			ISection sect=(ISection)ite.next();
			if(sect.getSubject().getName().equals(subject)){
				sects.add(sect);
			}
		}		
		if (sects.size()==1){
			return (ISection)sects.get(0);
		}else if (sects.size()==2){
			return (ISection)sects.get(1);
		}		
		return null;
		
	}
	*/
	
	/**
	 *  Returns a List<SectionAssignment> of unassigned subjects
	 */
	public List getUnassigned(){
		List retval = new Vector();
		boolean found=false;
		//for each subject in the writein
		for (Iterator ite=writein.getSubjects().iterator();ite.hasNext();){
			ISubject s=(ISubject)ite.next();
			found = false;
			//check the subject if there is a section
			List l = new Vector(sectionAssignments.keySet());			
			for (Iterator ite2=l.iterator();ite2.hasNext();){
				String k=(String)ite2.next();
				SectionAssignment sectAss=(SectionAssignment)sectionAssignments.get(k);
				if(sectAss.getLecture().getSubject().getName().equals(s.getName())){
					found = true;
					break;
				}				
			}
			if (!found){
				retval.add(s);
			}
				
		}
		return retval;
	}
	
	public boolean addSectionAssignment(SectionAssignment sectAss){
		sectionAssignments.put(sectAss.getSection().getSubject().getName(),sectAss);
		return true;
	}
	
	public boolean removeSectionAssignment(String subject){
		sectionAssignments.remove(subject);
		return true;
	}
	
	
}
