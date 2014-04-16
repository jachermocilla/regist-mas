package jach.msthesis.courselector;

import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.ITranscript;
import jach.msthesis.registration.model.PrereqValidator;
import jach.msthesis.registration.model.SuggestedSubject;

import java.util.*;

/**
 * Implements the CNOR algorithm. The algorithm determines the subjects
 * that a student shoud take.
 * 
 * @author jachermocilla
 * @version $Id:CourseSelector.java 548 2008-09-15 00:52:12Z jach $
 */

public class CourseSelector {
	
	/**
	 * Reference to the transcript of a student.
	 */
	ITranscript transcript;
	
	/**
	 * Reference to the course offering for the semester.
	 */
	IOffering offering;	
		
	/**
	 * Map<String,SuggestedSubject> of suggested subjects or recommended courses
	 */
	Map suggested=null;
	
	/**
	 * Constructor
	 * 
	 * @param ts The transcript source
	 * @param offering The course offering
	 */
	public CourseSelector(ITranscript transcript,IOffering offering){
		this.transcript=transcript;
		this.offering=offering;
	}
	
	/**
	 * Sets the transcript
	 * @param transcript
	 */
	public void setTranscript(ITranscript transcript){
		this.transcript=transcript;
	}	
	
	/**
	 * Sets the offering
	 * @param offering
	 */
	public void setOffering(IOffering offering){
		this.offering=offering;
	}
		
	
	/**
	 * Returns a map<String, SuggestedSubject> of of suggested subjects
	 * 
	 * @return
	 */
	public Map getSuggestedSubjects(){		
		if (suggested!=null)
			return suggested;
		else
			suggested=new Hashtable();		
		/**
		 * Include in suggested subjects the subject to take that
		 * are offered and whose prerequisites have been satisfied
		 */
		Iterator ite=transcript.getSubjectsToTake().values().iterator();
		while(ite.hasNext()){
			ISubject s=(ISubject)ite.next();			
			if ( (offering.isOffered(s.getName())) && PrereqValidator.satisfied(s.getName(), transcript)){
				suggested.put(s.getName(),new SuggestedSubject(s,transcript.getCurriculum().getFloat(s.getName())));			
			}
		}
		return suggested;
	}
	
	/**
	 * Returns a list<SuggestedSubject> of suggested subjects
	 * @return
	 */
	public List getRankedSuggestedSubjects(){		
		getSuggestedSubjects();
		List l=new Vector(suggested.values());
		
		Collections.sort(l,new Comparator(){
			public int compare(Object o1, Object o2){
				SuggestedSubject s1=(SuggestedSubject) o1;
				SuggestedSubject s2=(SuggestedSubject) o2;
				return (s1.getRank()-s2.getRank());
			}
		});
		return l;
	}
}
