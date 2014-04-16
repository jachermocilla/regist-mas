package jach.msthesis.scheduler.util;

/**
 * This class generates a random transcript(in MyTranscript Format) for
 * a student given a curriculum
 * 
 * @author jachermocilla
 *
 */

import java.util.*;
import java.io.*;


import jach.msthesis.courselector.*;

public class TranscriptGenerator {
	
	/**
	 * The source curriculum
	 */
	private ICurriculum curriculum;
	

	private String outputFolder="";
	
	
	public TranscriptGenerator(){}
	
	public TranscriptGenerator(ICurriculum curriculum,String outputFolder){
		this.curriculum=curriculum;
		this.outputFolder=outputFolder;
	}
	
	
	public void generate(int count){
		PrintWriter out;
		for (int i=0;i<count;i++){
			
			
			String stdNum;
			Random r=new Random();
			int year=r.nextInt(11)+1996;
			int num=r.nextInt(88888)+11111;
			stdNum=year+"-"+num;

			try{
				out=new PrintWriter(new FileWriter(outputFolder+"/"+stdNum+".trn"));
				out.println(stdNum);
				
			    int classification=r.nextInt(2)+1;
			    
			    System.out.println(stdNum);
			    //System.out.println(classification);
			    
			    Iterator iter=curriculum.getAllSubjects().iterator();
			    while(iter.hasNext()){
			    	ISubject s=(ISubject)iter.next();
			    	if (s.getYear() <= classification){
			    		out.println(s.getName()+","+(year+s.getYear()-1)+","+s.getSem()+",3.0");	    		
			    	}
			    }		
			    out.flush();
			    out.close();
			}catch(Exception ioe){
				ioe.printStackTrace();
				
			}
		}
	}
	
	public static void main(String args[]){
		RegistCurriculumSource currSource=new RegistCurriculumSource("../reg-agent-web/WebContent/data/bscs-1996.cur");
		currSource.load();
		
		TranscriptGenerator tg=new TranscriptGenerator(currSource.getCurriculum(),"/home/jach/transcripts");
		
		tg.generate(100);
	}
	
}
