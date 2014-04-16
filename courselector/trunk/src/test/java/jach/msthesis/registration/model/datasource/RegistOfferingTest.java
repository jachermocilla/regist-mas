package jach.msthesis.registration.model.datasource;

import jach.msthesis.registration.model.ISection;

import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RegistOfferingTest extends TestCase {
	RegistOffering offering;
	
	protected void setUp() throws Exception {
		super.setUp();
		String prefix = "/mnt/data/msthesis-workspace/regist-data/data/";
		offering = new RegistOffering(prefix + "CLASSES-2004-SECOND");
		offering.load();
	}
	
	public void testGetLecSections(){
		List lectures = (List)offering.getLecSections("CMSC 21");
		for (Iterator ite=lectures.iterator();ite.hasNext();){
			ISection section = (ISection)ite.next();
			this.assertEquals(section.getSectionName(), "T");
		}
	}

	public void testGetLabSections(){
		List lectures = (List)offering.getLabSections("CMSC 21","T");
		for (Iterator ite=lectures.iterator();ite.hasNext();){
			ISection section = (ISection)ite.next();
			System.out.println(section.getSectionName());
		}
	}
	
}
