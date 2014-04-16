package jach.msthesis.scheduler.util;

import jach.msthesis.registration.model.datasource.RegistWriteInSource;
import junit.framework.TestCase;

public class RegistWriteInExporterTest extends TestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testExport(){
		String prefix = "../regist-data/data/";
		RegistWriteInSource wis = new RegistWriteInSource(prefix+ "WRITEIN-2004-SECOND");
		wis.load();
		RegistWriteInExporter exporter = new RegistWriteInExporter(wis.getWriteIns(), prefix + "WRITEIN-2004-SECOND-CMSC", "SECOND", "2004");
		exporter.addSubject("CMSC");
		
		exporter.export();
	}

}
