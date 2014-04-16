package jach.msthesis.registration.model.datasource;


import jach.msthesis.registration.model.DefaultSubject;
import jach.msthesis.registration.model.WriteIn;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Hashtable;
import java.util.Map;
import java.util.Vector;

/**
 * Encapsulates the Regist writein source
 * 
 * @author jach
 * @version $Id: RegistWriteInSource.java 966 2008-10-09 07:04:20Z jach $
 * 
 */

public class RegistWriteInSource implements IWriteInSource {

	// The path to the text file
	private String path;

	// The writein map<stdnum, WriteIn>
	private Map writeins = new Hashtable();

	/**
	 * Constructor with path to text file
	 * 
	 * @param path
	 */
	public RegistWriteInSource(String path) {
		this.path = path;
	}

	/**
	 * Return the writeins map
	 */
	public Map getWriteIns() {
		return writeins;
	}

	/**
	 * Load the writein information
	 */
	public void load() {
		BufferedReader in = null;
		String line;
		String[] tokens = null;
		int lc=0,wic=0;
		

		try {
			in = new BufferedReader(new FileReader(path));
			line = in.readLine();
			while (line != null) {				
				if (!line.startsWith("#")) {
					lc++;
					// System.out.println(line);
					tokens = line.split(",");
					String stdNum = tokens[0];
					WriteIn wri = (WriteIn) writeins.get(stdNum);
					
					if (wri == null) {
						wic++;
						wri = new WriteIn(tokens[0], Integer.parseInt(tokens[9]), Integer.parseInt(tokens[8]));
						writeins.put(stdNum, wri);
					}
					// Create default subject with term and year to 0 for now.
					if (tokens[5].equals(""))
						tokens[5] = "0";

					DefaultSubject ds = new DefaultSubject(tokens[3], 0, 0,
							Integer.parseInt(tokens[5]));
					wri.addSubject(ds);
				}
				line = in.readLine();
			}
			System.out.println("Write-in lines read: "+ lc);
			System.out.println("Write-in created: "+ wic);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
