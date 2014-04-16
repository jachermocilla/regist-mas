package jach.msthesis.scheduler;

import java.io.Serializable;
import java.util.*;
import jach.msthesis.courselector.*;
import jach.msthesis.registration.model.ISection;

/**
 * Encapsulates a schedule node which is used for generating all possible
 * schedules that are conflict free
 * 
 * @author jach
 * @version $Id:SkedNode.java 846 2008-09-30 02:17:08Z jach $
 * 
 */
public class SkedNode implements Serializable{

	// The list of children of this node
	private List children = new Vector();

	// The parent node of this node
	private SkedNode parent;

	// The associated section for this node
	private ISection section;

	// flag it this is a lab node
	boolean labNode = false;

	/**
	 * Default constructor
	 */
	public SkedNode() {
	}

	/**
	 * Creates a SkedNode given a section
	 * 
	 * @param section
	 */
	public SkedNode(ISection section) {
		this.section = section;
	}

	/**
	 * Adds a child to this node
	 * 
	 * @param child
	 *            a <code>SkedNode</code> that represents the child
	 */
	public void addChild(SkedNode child) {
		children.add(child);
		child.parent = this;
	}

	/**
	 * Removes a child node
	 * 
	 * @param child
	 *            a <code>SkedNode</node> that represents the child to remove
	 */
	public void removeChild(SkedNode child) {
		children.remove(child);
		child.parent = null;
	}

	/**
	 * Returns the associated section for this SkedNode
	 * 
	 * @return
	 */
	public ISection getSection() {
		return section;
	}

	/**
	 * Returns the parent of this SkedNode
	 * 
	 * @return a <code>SkedNode</code> that represents the parent
	 */
	public SkedNode getParent() {
		return parent;
	}

	/**
	 * Returns the child given an index
	 * 
	 * @param index
	 *            an <code>int</code> for the index
	 * @return a <code>SkedNode</code> that represents the child
	 */
	public SkedNode getChild(int index) {
		return (SkedNode) children.get(index);
	}

	/**
	 * Returns the name of the node by combining the subject and the section of
	 * ISection
	 * 
	 * @return a <code>String</code> that represents the name
	 */
	public String getName() {
		if (section == null)
			return "noname";
		else
			return (section.getSubject().getName() + " " + section
					.getSectionName());
	}

	/**
	 * Returns the children of this SkedNode
	 * 
	 * @return a <code>List</code> that contains the children
	 */
	public List getChildren() {
		return children;
	}

	/**
	 * Returns true if this node represents a lab section, false otherwise
	 * 
	 * @return
	 */
	public boolean isLabNode() {
		return labNode;
	}

	/**
	 * Set this SkedNode as a lab node
	 * 
	 * @param l
	 */
	public void setAsLabNode(boolean l) {
		labNode = l;
	}
}
