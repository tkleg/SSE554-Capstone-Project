package org.troy.capstone.data_structures.similar_items_graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.troy.capstone.constants.MiscValues;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.entities.Item;

public class SimilarItemsGraph {
    private final int numItems;
    private final ItemHashMap itemHashMap;
    private final Map<Integer, List<Edge>> adjacencyList;

    class Edge{
        int destIndex;
        float weight;
        public Edge(int destIndex, float weight) {
            this.destIndex = destIndex;
            this.weight = weight;
        }
    }

    /**
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SimilarItemsGraph() {
        this.numItems = 5;
        this.itemHashMap = null; // Not needed for this test
        adjacencyList = new HashMap<>();
        for (int i = 0; i < numItems; i++)
            adjacencyList.put(i, new ArrayList<>());
        // Manually add edges as per your test case
        addEdge(0, 1, 4);
        addEdge(0, 2, 8);
        addEdge(1, 2, 3);
        addEdge(1, 4, 6);
        addEdge(2, 3, 2);
        addEdge(3, 4, 10);
    }
    */

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public SimilarItemsGraph() {
        this.numItems = 6;
        this.itemHashMap = null; // Not needed for this test
        adjacencyList = new HashMap<>();
        for (int i = 0; i < numItems; i++)
            adjacencyList.put(i, new ArrayList<>());
        // Manually add edges as per your test case
        addEdge('S', 'A', 6);
        addEdge('S', 'D', 8);
        addEdge('S', 'E', 7);
        addEdge('A', 'B', 9);
        addEdge('B', 'C', 12);
        addEdge('C', 'D', 3);
        addEdge('C', 'E', 5);
        addEdge('D', 'E', 10);
    }

    public SimilarItemsGraph(ItemHashMap itemHashMap) {
        this.numItems = itemHashMap.size();
        this.itemHashMap = itemHashMap;
        adjacencyList = new HashMap<>();
        for (int i = 0; i < numItems; i++)
            adjacencyList.put(i, new ArrayList<>());
        System.out.println("Filling graph with " + numItems + " items...");
        fillGraph();
        System.out.println("Graph filled.");
    }

    public void addEdge(char sourceItemId, char destItemId, float weight) {
        int sourceItemIndex = sourceItemId - 'A';
        int destItemIndex = destItemId - 'A';
        addEdge(sourceItemIndex, destItemIndex, weight);
        addEdge(destItemIndex, sourceItemIndex, weight);
    }

    public void addEdge(int sourceItemIndex, int destItemIndex, float weight) {
        if (weight < MiscValues.MIN_SIMILARITY_SCORE.getValue())
            return;

        adjacencyList.computeIfAbsent(sourceItemIndex, k -> new ArrayList<>())
            .add(new Edge(destItemIndex, weight));
        adjacencyList.computeIfAbsent(destItemIndex, k -> new ArrayList<>())
            .add(new Edge(sourceItemIndex, weight));
    }

    private void fillGraph() {
        List<Item> items = itemHashMap.values().stream().toList();
        for (int i = 0; i < numItems; i++) {
            for (int j = i + 1; j < numItems; j++) {
                float similarity = items.get(i).similarity(items.get(j));
                addEdge(i, j, similarity);
            }
        }
    }

    public List<int[]> dijkstra(char startItemId) {
        int startIndex = startItemId - 'A';
        return dijkstra(startIndex);
    }

    /** Basic algorithm taken from https://www.geeksforgeeks.org/dsa/dijkstras-shortest-path-algorithm-greedy-algo-7/ */
    public List<int[]> dijkstra(int startIndex) {
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> Float.compare(b.weight, a.weight));

        //Key is item index, value is distance from start index.
        Map<Integer,Float> distances = new HashMap<>();
        for (int i = 0; i < numItems; i++)
            distances.put(i, Float.MAX_VALUE);

        distances.put(startIndex, 0f);
        pq.offer(new Edge(startIndex, 0));

        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            int currentIndex = current.destIndex;
            float currentDist = distances.get(currentIndex);

            if(currentDist > distances.get(currentIndex))
                continue;

            for (Edge edge : adjacencyList.get(currentIndex)) {
                if(distances.get(edge.destIndex) > edge.weight + currentDist) {
                    distances.put(edge.destIndex, edge.weight + currentDist);
                    pq.offer(new Edge(edge.destIndex, distances.get(edge.destIndex)));
                }
            }
        }
        
        return distances.entrySet().stream()
            .filter(entry -> entry.getValue() < Float.MAX_VALUE && entry.getKey() != startIndex)
            .sorted(Map.Entry.<Integer, Float>comparingByValue().reversed())
            .limit(MiscValues.NUM_SIMILAR_ITEMS_TO_DISPLAY.getIntValue())
            .map(entry -> new int[]{entry.getKey(), entry.getValue().intValue()})
            .toList();
            
    }

    public static void main(String[] args) {
        SimilarItemsGraph graph = new SimilarItemsGraph();
        List<int[]> similarItems = graph.dijkstra('S');
        for (int[] pair : similarItems) {
            System.out.println("Item " + pair[0] + " with similarity score " + pair[1]);
        }
    }

}
