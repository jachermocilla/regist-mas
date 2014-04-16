package jach.msthesis.scheduler.regist;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jach.msthesis.courselector.ISection;
import jach.msthesis.courselector.ISubject;
import jach.msthesis.scheduler.IConstraint;
import jach.msthesis.scheduler.NoConflictConstraint;
import jach.msthesis.scheduler.SkedNode;

/**
 * Encapsulates a student's schedule which is used
 * for checking constraints
 * @author jach
 *
 */

public class Schedule implements IForm5{
	/**
	 * The root node
	 */
	SkedNode root = new SkedNode();
	
	/**
	 * Points to the last sked node added
	 * Set initially to root
	 */
	SkedNode lastNodeAdded=root;
	
	/**
	 * Constraint list 
	 */
	List constraints = new Vector();
	
	/**
	 * List of subjects in the schedule,
	 * a bit redundant since there is already sked node.
	 */
	List subjects=new Vector();
	
	//The units allowed
	int unitsAllowed;
	
	/**
	 * Constructor
	 * @param unitsAllowed The number of units to be enrolled
	 */
	public Schedule(int unitsAllowed){
		this.unitsAllowed=unitsAllowed;
		constraints.add(new NoConflictConstraint());
	}
	
	/**
	 * Check if adding a node satisfies all the specified constraint
	 * in the constraint list. Each node before being added to the
	 * tree is checked.
	 */
	public boolean passed(SkedNode node){		
		Iterator ite=constraints.iterator();
		while (ite.hasNext()){
			IConstraint constraint=(IConstraint)ite.next();
			if (!constraint.isSatisfied(node))
				return false;
		}
		return true;
	}
	
	/**
	 * Add a section to the schedule through node
	 * @param node A sked node containing section
	 */
	public void addSection(SkedNode node){
		//Add node as child of lastNodeAdded
		lastNodeAdded.addChild(node);
		
		//Set the node as the new lastNodeAdded
		lastNodeAdded=node;
		
		//ok add the section
		subjects.add(node.getSection());
		
		//TODO: add code to update units allocated
		//so that no excess will be allocated
		//if (!node.getSection().isLab())
		//	unitsAssigned+=node.getSection().get
	}
	
	/**
	 * Remove the last section added
	 *
	 */
	public void removeLastSectionAdded(){
		//First get the parent of lastNodeAdded
		SkedNode temp=lastNodeAdded.getParent();
		
		//ok its not the root yet
		if (temp!=null){
			//remove the last node added
			temp.removeChild(lastNodeAdded);
			
			//synchronize nodes and subjects
			subjects.remove(lastNodeAdded.getSection());
			
			//last node added parent becomes the new last node added 
			lastNodeAdded=temp;
		}
		
	}
	
	/**
	 * Return a list of subjects in the schedule
	 * @return
	 */
	public List getSubjects(){
		return subjects;
	}
	
	
	public int getUnitsObtained(WriteIn wrin){
		int uo=0;
		
		Iterator ite=subjects.iterator();
		while (ite.hasNext()){
			ISection sect=(ISection)ite.next();
			if (!sect.isLab()){
				ISubject sub=(ISubject)wrin.getSubjectInfo(sect.getSubject().getName());
				uo+=sub.getUnitCredit();
			}
		}
		return uo;
	} 
	
	public int getUnitsObtained(){
		return -1;
	}	
	
	public SkedNode getRoot(){
		
		return root;
	}
	
}
