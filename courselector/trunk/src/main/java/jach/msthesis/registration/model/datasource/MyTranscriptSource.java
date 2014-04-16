package jach.msthesis.registration.model.datasource;

import jach.msthesis.registration.model.DefaultTranscript;
import jach.msthesis.registration.model.ICurriculum;
import jach.msthesis.registration.model.ITranscript;
import jach.msthesis.registration.model.SubjectTaken;

import java.io.*;

/**
 * Encapsulates transcript source, basic format
 * @author jach
 * @version $Id:MyTranscriptSource.java 548 2008-09-15 00:52:12Z jach $
 *
 */

public class MyTranscriptSource implements ITranscriptSource {

	//path to transcript file
	private String path;
	
	//curriculum 
	private ICurriculum curri;
	
	//default transcript
	private DefaultTranscript dt;
	
	/**
	 * Default constructor
	 */
	public MyTranscriptSource(){}

	/**
	 * Constructor with path and curriculum.
	 * @param path
	 * @param curri
	 */
	public MyTranscriptSource(String path,ICurriculum curri){
		this.curri=curri;
		this.path=path;		
	}
	
	/**
	 * Loads transcript information
	 */
	public ITranscript load() {
		if (dt != null)
			return dt;
		else
			dt = new DefaultTranscript();
		
		dt.setCurriculum(curri);
		BufferedReader in=null;		
		String tokens[]=null;		
		String line;
						
		try{
			in=new BufferedReader(new FileReader(path));			
		}catch(FileNotFoundException ff){
			System.out.println(path+": Path not found.");
			return null;
		}
		
		try{
			
			dt.setStudentNumber(line=in.readLine());			
			while ((line=in.readLine())!=null){
				//System.out.println(line);
				tokens=line.split(",");
				SubjectTaken taken=new SubjectTaken(tokens[1],Integer.parseInt(tokens[2]),
						Double.parseDouble(tokens[3]));
				taken.setName(tokens[0]);
				dt.addSubjectTaken(taken);	
			}			
		}catch(IOException ioe){
			ioe.printStackTrace();
		}
		return dt;
	}
	
	/**
	 * Returns the transcript object
	 */
	public ITranscript getTranscript(){
		return dt;		
	}

}
