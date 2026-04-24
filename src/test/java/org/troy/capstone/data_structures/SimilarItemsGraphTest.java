package org.troy.capstone.data_structures;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.troy.capstone.data_structures.SimilarItemsGraph.Edge;

public class SimilarItemsGraphTest{
    
    private static final List<TestableEdge> TEST_EDGES = List.of(
        new TestableEdge(0, 3, 5),
        new TestableEdge(0, 1, 2),
        new TestableEdge(0, 2, 1),
        new TestableEdge(0, 4, 3),
        new TestableEdge(1, 3, 7),
        new TestableEdge(1, 5, 7),
        new TestableEdge(1, 6, 7),
        new TestableEdge(1, 2, 9),
        new TestableEdge(6, 4, 2),
        new TestableEdge(8, 9, 99)
    );

    private static final Map<Integer, List<Edge>> EXPECTED_EDGES = Map.of(
        0, List.of(new Edge(5, 9), new Edge(3, 5), new Edge(6, 5), new Edge(4, 3), new Edge(1, 2), new Edge(2, 1)),
        1, List.of(new Edge(3, 7), new Edge(5, 7), new Edge(6, 7), new Edge(4, 5), new Edge(2, 3), new Edge(0, 2)),
        2, List.of(new Edge(5, 10), new Edge(3, 6), new Edge(6, 6), new Edge(4, 4), new Edge(1, 3), new Edge(0, 1)),
        3, List.of(new Edge(5, 14), new Edge(6, 10), new Edge(4, 8), new Edge(1, 7), new Edge(2, 6), new Edge(0, 5)),
        4, List.of(new Edge(5, 12), new Edge(3, 8), new Edge(1, 5), new Edge(2, 4), new Edge(0, 3), new Edge(6, 2)),
        5, List.of(new Edge(3, 14), new Edge(6, 14), new Edge(4, 12), new Edge(2, 10), new Edge(0, 9), new Edge(1, 7)),
        6, List.of(new Edge(5, 14), new Edge(3, 10), new Edge(1, 7), new Edge(2, 6), new Edge(0, 5), new Edge(4, 2)),
        7, List.of(),
        8, List.of(new Edge(9, 99)),
        9, List.of(new Edge(8, 99))
    );

    private static final SimilarItemsGraph GRAPH = new TestableSimilarItemsGraph(10, TEST_EDGES);

    private static Method dijkstraMethod;
    
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
                adjacencyList[edge.sourceIndex].add(edge);
                adjacencyList[edge.destIndex].add(edge.flip());
            }
        }
    }
    

    @SuppressWarnings("unused")
    private static IntStream range() {
        return IntStream.range(0, EXPECTED_EDGES.size());
    }

    @BeforeAll
    public static void setup() throws ReflectiveOperationException {
        dijkstraMethod = SimilarItemsGraph.class.getDeclaredMethod("dijkstra", int.class);
        dijkstraMethod.setAccessible(true);
    }

    @ParameterizedTest
    @MethodSource("range")
    public void testDijkstraSimple(int startIndex) throws ReflectiveOperationException {

        @SuppressWarnings("unchecked")
        List<Edge> similarItems = (List<Edge>) dijkstraMethod.invoke(GRAPH, startIndex);
        System.out.println("\nCase " + startIndex + ": " + similarItems+"\n");
        List<Edge> expectedSimilarItems = EXPECTED_EDGES.get(startIndex);

        assert similarItems.size() == expectedSimilarItems.size() : "Expected " + expectedSimilarItems.size() + " similar items, but got " + similarItems.size();
        for (int i = 0; i < similarItems.size(); i++) {
            Edge actual = similarItems.get(i);
            Edge expected = expectedSimilarItems.get(i);
            assert actual.destIndex == expected.destIndex : "Expected destIndex " + expected.destIndex + " at index " + i + ", but got " + actual.destIndex;
            assert actual.weight == expected.weight : "Expected weight " + expected.weight + " at index " + i + ", but got " + actual.weight;
        }

    }

}
