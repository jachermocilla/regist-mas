package jach.msthesis.regmas;

import java.io.Serializable;

import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.ISubject;

public class SwapEntry implements Serializable, IEnlistorMessage, Constants{
	
	/**
	 * The current section assigned
	 */
	private ISection sectionToSwap;
	
	/**
	 * The desired section
	 */
	private ISection desiredSection;
	
	/**
	 * Student number of students who would like to swap
	 */
	private String requester;
	
	
	public SwapEntry(String requester,ISection sectionToSwap, ISection desiredSection){
		this.requester = requester;
		this.sectionToSwap = sectionToSwap;
		this.desiredSection = desiredSection;
	}


	/**
	 * @return the currentSection
	 */
	public ISection getSectionToSwap() {
		return sectionToSwap;
	}


	/**
	 * @param sectionToSwap the sectionToSwap to set
	 */
	public void setSectionToSwap(ISection sectionToSwap) {
		this.sectionToSwap = sectionToSwap;
	}


	/**
	 * @return the desiredSection
	 */
	public ISection getDesiredSection() {
		return desiredSection;
	}


	/**
	 * @param desiredSection the desiredSection to set
	 */
	public void setDesiredSection(ISection desiredSection) {
		this.desiredSection = desiredSection;
	}


	/**
	 * @return the requester
	 */
	public String getRequester() {
		return requester;
	}


	/**
	 * @param requester the requester to set
	 */
	public void setRequester(String requester) {
		this.requester = requester;
	}
	
	public ISubject getSubject(){
		return sectionToSwap.getSubject();
	}
	
	public int getAction(){
		return ACTION_POST_SWAP_REQUEST;
	}
	
}
