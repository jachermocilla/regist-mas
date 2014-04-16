package jach.msthesis.regmas;

import java.io.Serializable;

import jach.msthesis.registration.model.SectionAssignment;

public class EnlistorMessage implements Serializable, IEnlistorMessage{
	/**
	 * Action
	 */
	int action;
	
	/**
	 * Section assignmen to process
	 */
	SectionAssignment sectAss;
	
	public EnlistorMessage(int action, SectionAssignment sectAss){
		this.action=action;
		this.sectAss=sectAss;		
	}
	
	
	/**
	 * @return the action
	 */
	public int getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(int action) {
		this.action = action;
	}
	/**
	 * @return the sectAsss
	 */
	public SectionAssignment getSectionAssignment() {
		return sectAss;
	}
	/**
	 * @param sectAsss the sectAsss to set
	 */
	public void setSecttionAssignment(SectionAssignment sectAss) {
		this.sectAss = sectAss;
	}
	
	
	
}
