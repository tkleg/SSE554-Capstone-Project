package org.troy.capstone.search_engine.sorting.comparator;

import org.junit.jupiter.api.Test;

public class RowComparatorTest {

    @Test
    public void testStringBasedConstructor(){
        RowComparator[] comparators = RowComparator.getComparators();
        for( RowComparator comparator : comparators ){
            String comparatorString = comparator.toString();
            RowComparator newComparator = new RowComparator(comparatorString);
            assert newComparator != null : "fromString should not return null for valid input";
            assert newComparator.getSortType() == comparator.getSortType() : "fromString should return a RowComparator with the same SortType as the original";
        }
    }

}
