package jach.msthesis.scheduler.util;



import jach.msthesis.scheduler.MaximumBipartiteMatching;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import edu.uci.ics.jung.algorithms.flows.EdmondsKarpMaxFlow;
import edu.uci.ics.jung.graph.DirectedEdge;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.Indexer;
import edu.uci.ics.jung.graph.decorators.NumericDecorator;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;
import edu.uci.ics.jung.utils.GraphUtils;
import edu.uci.ics.jung.utils.MutableInteger;
import edu.uci.ics.jung.utils.UserData;


public class TestBipartiteMatching {
	public static void main(String args[]) {
		DirectedSparseGraph graph = new DirectedSparseGraph();
		GraphUtils.addVertices(graph, 11);

		String capacityKey = "Capacity";
		Indexer id = Indexer.getIndexer(graph);
		StringLabeller lb = StringLabeller.getLabeller(graph);
		
		for (int i=0;i<11;i++){
			try{
				lb.setLabel((Vertex)id.getVertex(i), i+"");
			}catch(Exception e){}
		}
		

		Edge edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(1));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(2));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(3));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(4));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(0), (Vertex)id.getVertex(5));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(1), (Vertex)id.getVertex(6));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(2), (Vertex)id.getVertex(6));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(7));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(8));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(3), (Vertex)id.getVertex(9));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(4), (Vertex)id.getVertex(8));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);

		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(5), (Vertex)id.getVertex(8));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(6), (Vertex)id.getVertex(10));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(7), (Vertex)id.getVertex(10));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(8), (Vertex)id.getVertex(10));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		edge = GraphUtils.addEdge(graph, (Vertex)id.getVertex(9), (Vertex)id.getVertex(10));
		edge.setUserDatum(capacityKey, new MutableInteger(1), UserData.SHARED);
		
		MaximumBipartiteMatching mbm = new MaximumBipartiteMatching(graph,
				(Vertex)id.getVertex(0), (Vertex)id.getVertex(10));

		//System.out.println(graph);
		//System.out.println((lb.getLabel((Vertex)id.getVertex(9))));
		
		for (Iterator ite=mbm.getMatching().iterator();ite.hasNext();){
			DirectedEdge e=(DirectedEdge)ite.next();
			System.out.println(lb.getLabel(e.getSource())+","+lb.getLabel(e.getDest()));
		}
        
	}

}
