package jach.msthesis.scheduler;

import java.util.*;
import jach.msthesis.courselector.*;
import jach.msthesis.registration.model.IOffering;
import jach.msthesis.registration.model.ISection;
import jach.msthesis.registration.model.Section;
import jach.msthesis.registration.model.SuggestedSubject;
import jach.msthesis.scheduler.constraints.IConstraint;
import jach.msthesis.scheduler.constraints.NoConflictConstraint;

/**
 * Encapsulates the scheduler that generates all possible schedules given a set
 * of subjects to take
 * 
 * @author jach
 * @version $Id:AllScheduleOptionGenerator.java 846 2008-09-30 02:17:08Z jach $
 * 
 */
public class AllScheduleOptionGenerator {

	/**
	 * The SkedNode object that represents the root node
	 */
	private SkedNode root = new SkedNode();

	/**
	 * The suggested subjects
	 */
	private List suggestedSubjects;

	/**
	 * Reference to the offering object
	 */
	private IOffering offering;

	/**
	 * minimum number of units
	 */
	private int minUnits = 9;

	/**
	 * A list of constraints
	 */
	private List constraints = new Vector();

	/**
	 * A list containing all the skedules
	 */
	private List allSked = null;

	/**
	 * Default constructor
	 */
	public AllScheduleOptionGenerator() {
		init();
	}

	/**
	 * Constructor
	 * 
	 * @param suggestedSubjects
	 *            a <code>List</code> that contains the subjects
	 * @param offering
	 */
	public AllScheduleOptionGenerator(List suggestedSubjects, IOffering offering) {
		this.suggestedSubjects = suggestedSubjects;
		this.offering = offering;
		init();
	}

	/**
	 * Initialize method which simply adds a no conflict constraint
	 */
	private void init() {
		// Set No Conflict constraint as default constraint
		constraints.add(new NoConflictConstraint());
		// constraints.add(new NoSevenToTenConstraint());
		// constraints.add(new NoLunchClassConstraint());
		// constraints.add(new NoFridayClassConstraint());
	}

	/**
	 * Check if adding a node satisfies all the specified constraint in the
	 * constraint list. Each node before being added to the tree is checked.
	 */
	private boolean passed(SkedNode node) {
		Iterator ite = constraints.iterator();
		while (ite.hasNext()) {
			IConstraint constraint = (IConstraint) ite.next();
			if (!constraint.isSatisfied(node))
				return false;
		}
		return true;
	}

	/*
	 * private void printQ(List q){
	 * System.out.println("---------------------------"); System.out.println("#
	 * items in q:"+q.size()); Iterator it=q.iterator(); while(it.hasNext()){
	 * SkedNode node=(SkedNode)it.next(); System.out.println(node.getName()); }
	 * System.out.println("---------------------------"); }
	 */

	/**
	 * Builds the tree of possible schedules. This method returns the leaf nodes
	 * so that it will be easier to reconstruct the schedule
	 */
	private List buildTree() {
		// Queue
		LinkedList q = new LinkedList();
		// points to the current node
		SkedNode current = null;
		// current units scheduled so far
		int currentUnits = 0;

		// Run garbage collector to get memory
		System.gc();

		// the root first
		q.add(root);
		for (int i = 0; (i < suggestedSubjects.size())
				&& (currentUnits < minUnits); i++) {
			SuggestedSubject subject = (SuggestedSubject) suggestedSubjects
					.get(i);

			// add to the current units allocated
			currentUnits += subject.getUnitCredit();

			// transfer what is in q to leaves for processing
			// q becomes empty and new soon to be leaves will be added later on
			// it
			LinkedList leaves = new LinkedList();
			while (!q.isEmpty()) {
				leaves.add(q.remove());
			}

			Iterator z = leaves.iterator();
			// process each leaf node
			while (z.hasNext()) {
				// Get a leaf node to process
				current = (SkedNode) z.next();
				Iterator ite = offering.getLecSections(subject.getName())
						.iterator();
				while (ite.hasNext()) {
					ISection lec = (ISection) ite.next();
					SkedNode lecNode = new SkedNode(lec);
					current.addChild(lecNode);
					if (!passed(lecNode)) {
						// proceed to next lecture section
						// since this lecture is in conflict already,
						// there is no need to process its labs
						current.removeChild(lecNode);
						continue;
					}

					// Get the laboratory sections, if any
					List labs = offering.getLabSections(lec.getSubject()
							.getName(), lec.getSectionName());

					// There are lab sections!
					if (labs.size() > 0) {
						Iterator ite2 = labs.iterator();

						// A lab becomes a child if no conflict was detected
						// and it is added to the leaf queue q
						while (ite2.hasNext()) {
							Section lab = (Section) ite2.next();
							SkedNode labNode = new SkedNode(lab);
							lecNode.addChild(labNode);
							if (!passed(labNode)) {
								lecNode.removeChild(labNode);
							} else {
								q.add(labNode);
							}
						}

						// Only remove lecture node if there were no
						// lab sections added
						if (lecNode.getChildren().size() < 1)
							lecNode.getParent().removeChild(lecNode);
					} else {
						// lecture node becomes leaf node
						// if there are no labs
						q.add(lecNode);
					}
				}
			}// loop that iterates over the currrentLevelNodes
		}
		return q;
	}

	/**
	 * Get all sked nodes
	 * 
	 * @return a <code>List</code> that contains the sked nodes
	 */
	public List getAllSked() {
		// do only if not done yet because operation is
		// expensive
		if (allSked == null) {
			allSked = new Vector();
			// build the tree and get leaf nodes
			Iterator ite = buildTree().iterator();

			// for each leaf node, we trace back up to the
			// root whose parent is null
			while (ite.hasNext()) {
				SkedNode node = (SkedNode) ite.next();
				Vector sked = new Vector();
				while (node.getParent() != null) {
					sked.add(0, node);
					node = node.getParent();
				}
				allSked.add(sked);
			}
		}
		return allSked;
	}

	/**
	 * Sets the minimum units to be scheduled
	 * 
	 * @param minUnits
	 */
	public void setMinUnits(int minUnits) {
		this.minUnits = minUnits;
	}

	/**
	 * Returns the minimum units to be scheduled
	 * 
	 * @return
	 */
	public int getMinUnits() {
		return minUnits;
	}

	/**
	 * Prints the schedule on the screen
	 * 
	 */
	public void printSked() {
		int count = 0;
		Iterator ite = getAllSked().iterator();

		while (ite.hasNext()) {
			System.out.print(++count + ")");
			List sked = (List) ite.next();
			Iterator ite2 = sked.iterator();
			while (ite2.hasNext()) {
				SkedNode section = (SkedNode) ite2.next();
				System.out.print("[" + section.getName() + "]" + " -> ");
			}
			System.out.println();
		}
		System.out.println("Total Schedules:" + getAllSked().size());
	}

	/**
	 * Returns the root node
	 * 
	 * @return a <code>SkedNode</code> that represents the root
	 */
	public SkedNode getRoot() {
		return root;
	}

	/**
	 * Adds a constraint to the constraint list
	 * 
	 * @param constraint
	 */
	public void addConstraint(IConstraint constraint) {
		constraints.add(constraint);
	}

	/**
	 * Clears constraint list
	 * 
	 */
	public void clearConstraints() {
		constraints = new Vector();
		constraints.add(new NoConflictConstraint());
	}

	public void setRoot(SkedNode root) {
		this.root = root;
	}

}
