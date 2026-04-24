package org.troy.capstone.data_structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import org.troy.capstone.Config;
import org.troy.capstone.annotations.Generated;
import org.troy.capstone.constants.MiscValues;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.data_structures.item_table.ItemHashMap;
import org.troy.capstone.entities.Item;
import org.troy.capstone.ui_components.items.SearchedItemPanel;
import org.troy.capstone.utils.UIUtils;

import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import tech.tablesaw.api.Table;

/** Graph data structure to represent similar items. Uses an adjacency list representation. */
public class SimilarItemsGraph {
    /** Number of items in the graph. */
    protected int numItems;
    /** HashMap to store items. Iterated over when filling the graph. */
    private final ItemHashMap itemHashMap;
    /** The original table containing the items, used for retrieving items by index. */
    private final Table table;
    /** Adjacency list to represent edges between items. */
    protected List<Edge>[] adjacencyList;

    /** Inner class to represent an edge in the graph. */
    public static class Edge{
        /** The index of the destination item that this edge points to. */
        int destIndex;
        /** The weight of the edge, representing the similarity score between the source and destination items. */
        float weight;
        /** Constructor for the Edge class. Initializes the destination index and weight of the edge.
         * @param destIndex The index of the destination item that this edge points to.
         * @param weight The weight of the edge, representing the similarity score between the source and destination items.
         * @pre destIndex should be a valid item index corresponding to an item in the graph. weight should be a non-negative float.
         * @post An Edge instance is created with the specified destination index and weight, representing an edge in the graph between two items with a certain similarity score.
         */
        public Edge(int destIndex, float weight) {
            this.destIndex = destIndex;
            this.weight = weight;
        }

        /** Returns a string representation of the edge, including the destination index and weight. 
         * @return A string representation of the edge, including the destination index and weight.
        */
        @Override
        public String toString() {
            return "Index: " + destIndex + ", Weight: " + weight;
        }

        /**
         * Returns the destination index of this edge.
         * @return The destination index of this edge.
         */
        int getDestIndex() {
            return destIndex;
        }
    }

    /** Default constructor. Only used to allow for subclassing in the SimilarItemsGraphTest class. All fields are initialized to null. */
    public SimilarItemsGraph(){
        adjacencyList = null;
        itemHashMap = null;
        table = null;
    }


    /** Finds similar items for the given item ID.
     * @param itemId The ID of the item for which to find similar items.
     * @return A list of VBox panels representing the similar items.
     * @throws RuntimeException if the item is not found in the hash map.
     */
    public List<VBox> findSimilarItems(String itemId) {
        int itemIndex = itemHashMap.getItem(itemId)
            .orElseThrow(() -> new RuntimeException("Item not found in hash map for similar items graph."))
            .getIndex();
        System.out.println("Item index for item ID " + itemId + ": " + itemIndex);
        List<Edge> similarItems = dijkstra(itemIndex);
        List<Item> similarItemsList = similarItems.stream()
            .map(Edge::getDestIndex)
            .map(index -> itemHashMap.getItem(table.row(index).getString(TableColumnName.ID.getColumnName())).get())
            .toList();
        System.out.println("Similar items retrieved from item hash map: " + similarItemsList.size());
        similarItemsList.forEach(item -> System.out.println("Similar item: \"" + item.getName() + "\" (ID: " + item.getId() + ")") );

        List<VBox> panels = similarItemsList.stream()
            .map(SearchedItemPanel::makeRightPanel)
            .toList();
        panels.forEach(panel -> UIUtils.setLineBorder(panel, 3, 2));
        panels.forEach(panel -> panel.setPadding(new Insets(5)));
        return panels;
    }

    /** Constructor for SimilarItemsGraph. Initializes the graph with the given ItemHashMap.
     * @param itemHashMap The ItemHashMap containing the items to be added to the graph.
     * @param table The original table containing the items, used for retrieving items by index when filling the graph.
     * @param buildGraph A Boolean value indicating whether to build the graph. If null, the value from the configuration will be used.
     */
    @SuppressWarnings("unchecked")
    public SimilarItemsGraph(ItemHashMap itemHashMap, Table table, Boolean buildGraph) {
                
        this.numItems = itemHashMap.size();
        this.itemHashMap = itemHashMap;
        adjacencyList = new List[numItems];
        this.table = table;

        if( !canBuild(buildGraph) )
            return;

        System.out.println("Filling graph with " + numItems + " items...");
        fillGraph();
        System.out.println("Graph filled.");
    }

    /** Determines whether the graph can be built based on the provided buildGraph value or the configuration.
     * @param buildGraph The buildGraph value to check. If null, the configuration value is used.
     * @return true if the graph can be built, false otherwise.
     */
    @Generated
    private boolean canBuild(Boolean buildGraph) {
        if( buildGraph == null )
            return Config.graphBuildingEnabled;
        return buildGraph;
    }



    /** Adds an undirected edge between two items in the graph with the given weight.
     * @param sourceItemIndex The index of the source item.
     * @param destItemIndex The index of the destination item.
     * @param weight The weight of the edge, representing the similarity score between the two items.
     * @pre sourceItemIndex and destItemIndex should be valid item indices corresponding to items in the graph. weight should be a non-negative float representing the similarity score between the two items.
     * @post An undirected edge is added between the source and destination items in the graph with the specified weight. The adjacency list is updated to reflect the new edge. Nothing is done if the weight is below the minimum similarity score threshold defined in MiscValues.
     */
    private void addEdge(int sourceItemIndex, int destItemIndex, float weight) {
        if (weight < MiscValues.MIN_SIMILARITY_SCORE.getValue())
            return;

        if( adjacencyList[sourceItemIndex] == null)
            adjacencyList[sourceItemIndex] = new ArrayList<>();
        if( adjacencyList[destItemIndex] == null)
            adjacencyList[destItemIndex] = new ArrayList<>();

        adjacencyList[sourceItemIndex].add(new Edge(destItemIndex, weight));
        adjacencyList[destItemIndex].add(new Edge(sourceItemIndex, weight));
    }

    /** Fills the graph with edges based on the similarity scores between items in the item hash map.
     * @pre itemHashMap should be properly initialized and contain valid items with a defined similarity
     * method to calculate similarity scores between items. The number of items in the item hash map should match the expected number of items in the graph.
     * @post The graph is filled with undirected edges between items based on their similarity scores
     */
    private void fillGraph() {
        List<Item> items = itemHashMap.values().stream().toList();
        for (int i = 0; i < numItems; i++) {
            for (int j = i + 1; j < numItems; j++) {
                float similarity = items.get(i).similarity(items.get(j));
                addEdge(i, j, similarity);
            }
        }
    }

    /** Basic algorithm taken from https://www.geeksforgeeks.org/dsa/dijkstras-shortest-path-algorithm-greedy-algo-7/. Computes the shortest paths from the start index to all other items in the graph. 
     * @param startIndex The index of the starting item from which to compute the shortest paths to all other items in the graph.
     * @pre startIndex should be a valid item index corresponding to an item in the graph. The graph should be properly initialized with edges representing the similarity scores between items.
     * @post The method returns a list of integer arrays, where each array contains the index of a similar item and its corresponding similarity score (converted to an integer). The list is sorted in descending order of similarity scores and limited to the number of similar items to display as defined in MiscValues. Items that are not reachable from the start index (i.e., have a distance of Float.MAX_VALUE) are excluded from the returned list.
     * @return A list of integer arrays, where each array contains the index of a similar item and its corresponding similarity score (converted to an integer). The list is sorted in descending order of similarity scores and limited to the number of similar items to display as defined in MiscValues.
     */
    private List<Edge> dijkstra(int startIndex) {
        PriorityQueue<Edge> pq = new PriorityQueue<>((a, b) -> Float.compare(b.weight, a.weight));

        //Key is index, value is distance from start index (similarity score)
        Map<Integer, Float> distances = new HashMap<>();

        distances.put(startIndex, 0f);
        pq.offer(new Edge(startIndex, 0f));

        while (!pq.isEmpty()) {
            Edge current = pq.poll();
            int currentIndex = current.destIndex;

            List<Edge> neighbors = adjacencyList[currentIndex];
            if(neighbors == null)//Handles vertices with no edges
                continue;
            for (Edge edge : neighbors) {
                float edgeDist = edge.weight;
                int destIndex = edge.destIndex;
                if( distances.getOrDefault(currentIndex, Float.MAX_VALUE) + edgeDist < distances.getOrDefault(destIndex, Float.MAX_VALUE)){
                    distances.put(destIndex, distances.get(currentIndex) + edgeDist);
                    pq.offer(new Edge(destIndex, distances.get(destIndex)));
                }
            }
        }
        
        return distances.entrySet().stream()
            .filter(entry -> entry.getKey() != startIndex)
            .sorted((a, b) -> Float.compare(b.getValue(), a.getValue()))
            .limit((long) MiscValues.NUM_SIMILAR_ITEMS_TO_DISPLAY.getValue())
            .map(entry -> new Edge(entry.getKey(), entry.getValue()))
            .toList();
    }

}
