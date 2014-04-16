package jach.msthesis.courselector;

import jach.msthesis.registration.model.SuggestedSubject;
import jach.msthesis.registration.model.datasource.RegistOffering;
import jach.msthesis.registration.model.datasource.MyTranscriptSource;
import jach.msthesis.registration.model.datasource.RegistCurriculumSource;

import java.util.*;

/**
 * Main
 * @author jach
 * @version $Id:Main.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public class Main {
	public static void main(String args[]){		
		String prefix="data/";
		RegistCurriculumSource curriculumSource=new RegistCurriculumSource(prefix+"bscs-1996.cur");		
		RegistOffering offering=new RegistOffering(prefix+"CLASSES-2002-SECOND");		
		MyTranscriptSource transcriptSource=new MyTranscriptSource(prefix+"1996-47938.trn",curriculumSource.getCurriculum());
		
		offering.load();
		transcriptSource.load();
		
		CourseSelector sel=new CourseSelector(transcriptSource.getTranscript(),offering);		
		Iterator ite=sel.getRankedSuggestedSubjects().iterator();
		while(ite.hasNext()){
			SuggestedSubject s=(SuggestedSubject)ite.next();
			System.out.println(s.getName()+"-"+s.getRank());
		}
	}		
}
