package jach.msthesis.scheduler;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jach.msthesis.registration.model.DefaultForm5;
import jach.msthesis.registration.model.IForm5;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.ISubject;
import jach.msthesis.registration.model.IWriteIn;
import jach.msthesis.scheduler.constraints.IConstraint;
import jach.msthesis.scheduler.constraints.NoConflictConstraint;

/**
 * Encapsulates a student's schedule which is used for checking constraints.
 * Schedule builds a tree of SkedNodes that represent the subjects
 * 
 * @author jach
 * @version $Id: Schedule.java 778 2008-09-26 16:05:31Z jach $
 * 
 */

public class Schedule extends DefaultForm5 implements Serializable {
	/**
	 * The root node
	 */
	private SkedNode root = new SkedNode();

	/**
	 * Points to the last sked node added Set initially to root
	 */
	private SkedNode lastNodeAdded = root;

	/**
	 * Constraint list. This list is used to check for constraint satisfaction
	 */
	private List constraints = new Vector();

	
	public Schedule(){}
	
	List sections = new Vector();
	
	/**
	 * Constructor
	 * 
	 */
	public Schedule(IWriteIn writein) {
		this.writein = writein;
		constraints.add(new NoConflictConstraint());
	}

	/**
	 * Check if adding a node satisfies all the specified constraint in the
	 * constraint list. Each node before being added to the tree is checked.
	 */
	public boolean passed(SkedNode node) {
		// iterate over the list of specified constraints
		Iterator ite = constraints.iterator();
		while (ite.hasNext()) {
			IConstraint constraint = (IConstraint) ite.next();
			if (!constraint.isSatisfied(node))
				return false;
		}
		return true;
	}

	/**
	 * Check if a section satisfies a constraint
	 */
	public boolean passed(ISection section) {
		SkedNode node = new SkedNode(section);
		lastNodeAdded.addChild(node);
		Iterator ite = constraints.iterator();
		while (ite.hasNext()) {
			IConstraint constraint = (IConstraint) ite.next();
			if (!constraint.isSatisfied(node)) {
				// System.out.println(section.getSectionName());
				lastNodeAdded.removeChild(node);
				return false;
			}
		}
		lastNodeAdded.removeChild(node);
		return true;
	}

	/**
	 * Add a section to the schedule through node
	 */
	public void addSection(SkedNode node) {
		if (!sections.contains(node.getSection())) {
			// Add node as child of lastNodeAdded
			lastNodeAdded.addChild(node);

			// Set the node as the new lastNodeAdded
			lastNodeAdded = node;

			// ok add the section
			sections.add(node.getSection());
			// System.out.println("subjects:"+subjects.size());

			// TODO: add code to update units allocated
			// so that no excess will be allocated
			// if (!node.getSection().isLab())
			// unitsAssigned+=node.getSection().get
		}
	}

	/**
	 * Removes the last section added
	 */
	public void removeLastSectionAdded() {
		// First get the parent of lastNodeAdded
		SkedNode temp = lastNodeAdded.getParent();

		// ok its not the root yet
		if (temp != null) {
			// remove the last node added
			temp.removeChild(lastNodeAdded);

			// synchronize nodes and subjects
			sections.remove(lastNodeAdded.getSection());

			// last node added parent becomes the new last node added
			lastNodeAdded = temp;
		}

	}

	/**
	 * Returns the number of units in this schedule based on the writein
	 */
	/*
	public int getUnitsObtained() {
		int uo = 0;
		Iterator ite = sections.iterator();
		while (ite.hasNext()) {
			ISection sect = (ISection) ite.next();
			if (!sect.isLab()) {
				ISubject sub = (ISubject) writein.getSubjectInfo(sect.getSubject()
						.getName());
				if (sub != null) {
					uo += sub.getUnitCredit();
				}
			}
		}
		return uo;
	}
	*/

	/**
	 * Returns the root SkedNode associated with this schedule
	 */
	public SkedNode getRoot() {
		return root;
	}

	
		
	
	
}
