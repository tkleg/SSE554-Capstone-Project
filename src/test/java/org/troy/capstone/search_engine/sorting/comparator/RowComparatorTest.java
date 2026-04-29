package org.troy.capstone.search_engine.sorting.comparator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

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

    @Test
    public void testCompareWithNullSortType(){
        RowComparator comparator = new RowComparator((RowComparator.SortType) null);

        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        int result = comparator.compare(null, null);
        assert result == 0 : "compare should return 0 when SortType is null, even if rows are null";
        assert outContent.toString().contains("Invalid SortType for RowComparator: null") : "compare should print an error message when SortType is null";
        System.setOut(originalOut);
    }

    @Test
    @SuppressWarnings("ObjectEqualsNull")
    public void testEqualsWithNullAndDifferentClass(){
        RowComparator comparator = new RowComparator(RowComparator.SortType.PRICE_ASCENDING);
        assert !comparator.equals(null) : "RowComparator should not be equal to null";
        assert !comparator.equals(new Object()) : "RowComparator should not be equal to an object of a different class";
    }

}
