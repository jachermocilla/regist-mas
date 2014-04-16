package jach.msthesis.scheduler.util;


import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.WriteIn;
import jach.msthesis.registration.model.datasource.RegistWriteInSource;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Class for exporting portions of the writein data for specific subjects
 * 
 * @author jach
 * 
 */

public class RegistWriteInExporter implements IExporter {

	/**
	 * Map: <stdnum, WriteIn>
	 */
	private Map writeins = new Hashtable();

	/**
	 * The file name to dump to
	 */
	private String fname;

	/**
	 * The semester
	 */
	private String sem;

	/**
	 * The year
	 */
	private String year;

	/**
	 * The list of subjects to export
	 */
	private List subjects = new Vector();

	/**
	 * Constructor
	 * 
	 * @param writeins
	 * @param fname
	 * @param sem
	 * @param year
	 */
	public RegistWriteInExporter(Map writeins, String fname, String sem,
			String year) {
		this.writeins = writeins;
		this.fname = fname;
		this.sem = sem;
		this.year = year;
	}

	/**
	 * Adds  filter subject
	 * @param subject
	 */
	public void addSubject(String subject) {
		this.subjects.add(subject);
	}

	/**
	 * Actual export
	 */
	public void export() {
		try {
			//Prepare the files
			FileWriter fw = new FileWriter(fname);
			PrintWriter out = new PrintWriter(fw);

			//For each writein
			for (Iterator ite = writeins.keySet().iterator(); ite.hasNext();) {
				String stdNum = (String) ite.next();
				WriteIn wri = (WriteIn) writeins.get(stdNum);
				int priority = wri.getPriority();
				int unitsAllowed = wri.getUnitsAllowed();
				List subList = wri.getSubjects();
				
				
				unitsAllowed=0;
				String outLines[]=new String[10];
				int cnt = 1;
				int count=0;
				for (Iterator ite2 = subList.iterator(); ite2.hasNext();) {
					ISubject sub = (ISubject) ite2.next();

					for (int j=0;j<subjects.size();j++){
						String sname = (String) this.subjects.get(j);
						if (sub.getName().startsWith(sname)) {
							unitsAllowed+=sub.getUnitCredit();
							//System.out.println("Units Allowed"+unitsAllowed);
							String outLine = stdNum + "," + year + "," + sem + ","
								+ sub.getName() + ",," + sub.getUnitCredit()
								+ ",REGD," + (cnt++) + ","; // + unitsAllowed + ","	+ priority;
							outLines[count++]=outLine;
						}
					}					
				}
				for (int i=0;i<count;i++){
					String outLine = outLines[i]+unitsAllowed + ","	+ priority;
					out.println(outLine);
					System.out.println(outLine);
				}
			}
			out.close();
			fw.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	public static void main(String args[]) {
		String prefix = "../regist-data/data/";
		RegistWriteInSource wis = new RegistWriteInSource(prefix
				+ "WRITEIN-2006-SECOND");
		wis.load();
		RegistWriteInExporter exporter = new RegistWriteInExporter(wis
				.getWriteIns(), prefix + "WRITEIN-SAMPLE", "SECOND", "2006");
		exporter.addSubject("CMSC");
		exporter.export();
	}

}
