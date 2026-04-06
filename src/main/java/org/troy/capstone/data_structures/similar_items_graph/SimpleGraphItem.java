package org.troy.capstone.data_structures.similar_items_graph;

import java.util.ArrayList;
import java.util.List;

import org.troy.capstone.entities.Item;


public class SimpleGraphItem extends Item {

    static List<SimpleGraphItem> generateTestItems() {
        List<SimpleGraphItem> items = new ArrayList<>();
        for (short i = 0; i < 5; i++)
            items.add(new SimpleGraphItem(i));
        return items;
    }
    
    SimpleGraphItem(short index) {
        super();
        setIndex(index);
    }

    @Override
    public float similarity(Item other) {
        switch (getIndex()) {
            case 0 -> {
                return switch (other.getIndex()) {
                    case 1 -> 4.0f;
                    case 2 -> 8.0f;
                    default -> Float.MAX_VALUE;
                };
            }case 1 -> {
                return switch (other.getIndex()) {
                    case 0 -> 4.0f;
                    case 2 -> 3.0f;
                    case 4 -> 6.0f;
                    default -> Float.MAX_VALUE;
                };
            }case 2 -> {
                return switch (other.getIndex()) {
                    case 0 -> 8.0f;
                    case 1 -> 3.0f;
                    case 3 -> 2.0f;
                    default -> Float.MAX_VALUE;
                };
            }case 3 -> {
                return switch (other.getIndex()) {
                    case 2 -> 2.0f;
                    case 4 -> 10.0f;
                    default -> Float.MAX_VALUE;
                };
            }case 4 -> {
                return switch (other.getIndex()) {
                    case 1 -> 6.0f;
                    case 3 -> 10.0f;
                    default -> Float.MAX_VALUE;
                };
            }default -> { return Float.MAX_VALUE; }
        }
    }
}
