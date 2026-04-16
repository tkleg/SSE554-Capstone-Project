package org.troy.capstone.data_structures;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.troy.capstone.data_structures.SimilarItemsGraph.Edge;

public class SimilarItemsGraphTest{
    
    static class TestableEdge extends SimilarItemsGraph.Edge {
        int sourceIndex;
        TestableEdge(int sourceIndex, int destIndex, float weight) {
            super(destIndex, weight);
            this.sourceIndex = sourceIndex;
        }

        TestableEdge flip() {
            return new TestableEdge(destIndex, sourceIndex, weight);
        }
    }
    
    static class TestableSimilarItemsGraph extends SimilarItemsGraph {

        @SuppressWarnings("unchecked")
        TestableSimilarItemsGraph(int numVerts, List<TestableEdge> edges) {
            this.numItems = numVerts;
            this.adjacencyList = new List[numItems];
            fillGraph(edges);
        }

        private void fillGraph(List<TestableEdge> edges) {
            for(TestableEdge edge : edges) {
                if( adjacencyList[edge.sourceIndex] == null)
                    adjacencyList[edge.sourceIndex] = new ArrayList<>();
                if( adjacencyList[edge.destIndex] == null)
                    adjacencyList[edge.destIndex] = new ArrayList<>();
                adjacencyList[edge.sourceIndex].add((Edge) edge);
                adjacencyList[edge.destIndex].add(edge.flip());
            }
        }
    }

    @Test
    public void testGraphConstruction() {
        List<TestableEdge> edges = List.of(
            new TestableEdge(0, 3, 5),
            new TestableEdge(0, 1, 2),
            new TestableEdge(0, 2, 1),
            new TestableEdge(0, 4, 3),
            new TestableEdge(1, 3, 7),
            new TestableEdge(1, 5, 7),
            new TestableEdge(1, 6, 7),
            new TestableEdge(1, 2, 9),
            new TestableEdge(6, 4, 2)
        );

        SimilarItemsGraph graph = new TestableSimilarItemsGraph(8, edges);

        List<Edge> similarItems = graph.dijkstra(4);
        System.out.println(similarItems);
        //graph.writeToDotFile("similar_graph.dot");
        List<Edge> expectedSimilarItems = List.of(
            new Edge(5, 12),
            new Edge(3, 8),
            new Edge(1, 5),
            new Edge(2, 4),
            new Edge(0, 3),
            new Edge(6, 2)
        );

        assert similarItems.size() == expectedSimilarItems.size() : "Expected " + expectedSimilarItems.size() + " similar items, but got " + similarItems.size();
        for (int i = 0; i < similarItems.size(); i++) {
            Edge actual = similarItems.get(i);
            Edge expected = expectedSimilarItems.get(i);
            assert actual.destIndex == expected.destIndex : "Expected destIndex " + expected.destIndex + " at index " + i + ", but got " + actual.destIndex;
            assert actual.weight == expected.weight : "Expected weight " + expected.weight + " at index " + i + ", but got " + actual.weight;
        }

    }
    

}
