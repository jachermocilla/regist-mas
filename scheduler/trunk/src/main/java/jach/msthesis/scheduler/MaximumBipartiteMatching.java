package jach.msthesis.scheduler;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.MutableInteger;
import edu.uci.ics.jung.utils.UserData;

/**
 * This class implements an algorithm to solve Maximum Bipartite Matching. It
 * uses the Edmonds-Karp algorithm using Jung.
 * 
 * @author jach
 * @version $Id: MaximumBipartiteMatching.java 1076 2008-10-26 12:23:25Z jach $
 * 
 */

public class MaximumBipartiteMatching {
	/**
	 * The network
	 */
	private DirectedGraph network;

	/**
	 * Edmonds-Karp algoritm
	 */
	private EdmondsKarpMaxFlow ek;

	/**
	 * The source vertex
	 */
	private Vertex source;

	/**
	 * The sink vertex
	 */
	private Vertex sink;

	/**
	 * Constructor 
	 * 
	 * @param network
	 * @param source
	 * @param sink
	 */
	public MaximumBipartiteMatching(DirectedGraph network, Vertex source,
			Vertex sink) {
		this.network = network;
		this.source = source;
		this.sink = sink;

		ek = new EdmondsKarpMaxFlow(network, source, sink, "Capacity", "FLOW");
		ek.evaluate();
	}

	/**
	 * Constructor 
	 * 
	 * @param network
	 * @param L
	 * @param R
	 */
	public MaximumBipartiteMatching(DirectedGraph network, Set L, Set R) {
		this.source = new SparseVertex();
		this.sink = new SparseVertex();

		network.addVertex(source);
		network.addVertex(sink);
		
		//assign outgoing links from source vertex
		for (Iterator ite = L.iterator(); ite.hasNext();) {
			Vertex l = (Vertex) ite.next();
			Edge edge = GraphUtils.addEdge(network, this.source, l);
			edge.setUserDatum("Capacity", new MutableInteger(1),
					UserData.SHARED);
		}

		//assign incoming links to sink
		for (Iterator ite = R.iterator(); ite.hasNext();) {
			Vertex r = (Vertex) ite.next();
			Edge edge = GraphUtils.addEdge(network, r, this.sink);
			edge.setUserDatum("Capacity", new MutableInteger(1),
					UserData.SHARED);
		}

		//Solve the network problem
		ek = new EdmondsKarpMaxFlow(network, source, sink, "Capacity", "FLOW");
		ek.evaluate();

	}

	/**
	 * Returns a set<DirectedEdge> of matches
	 * @return
	 */
	public Set getMatching() {
		Set retval = new HashSet();
		Set flowEdges = ek.getFlowGraph().getEdges();
		for (Iterator ite = flowEdges.iterator(); ite.hasNext();) {
			DirectedEdge e = (DirectedEdge) ite.next();
			Number flow = (Number) e.getUserDatum("FLOW");
			if (flow.intValue() == 1) {
				if (!e.getSource().equals(source) && !e.getDest().equals(sink)) {
					// System.out.println(e+":"+flow);
					retval.add(e);
				}
			}
		}
		return retval;
	}

}
