package jach.msthesis.registration.model;

import java.io.Serializable;
import java.util.*;

/**
 * Helper class that checks whether all the prerequisites have been
 * satisfied.
 * 
 * @author jachermocilla
 * @version $Id:PrereqValidator.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public class PrereqValidator implements Serializable{
	
	public static boolean satisfied(String subjectName,ITranscript transcript)
	{
		boolean retval=true;
				
		//Get the ISubject object given the subject name
		ISubject subject=transcript.getCurriculum().getSubject(subjectName);
		
		//Get a list of the prerequisites
		List prereqs=subject.getPrerequisites();
		Iterator ite=prereqs.iterator();
		while (ite.hasNext()){
			IPrerequisite prereq=(IPrerequisite)ite.next();			
			//check if prereq is an instance of defaultsubject
			if (prereq instanceof DefaultSubject){
				String subj=prereq.getDescription();
				if(!transcript.completed(subj))
					return false;
			}else if (prereq instanceof Classification){ //for classfication based prereq
				String classification=prereq.getDescription();
				if (!transcript.getClassification().getDescription().equals(classification))
					return false;
			}
		}		
		return retval;
	}
	
}
