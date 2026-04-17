package org.troy.capstone.search_engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.troy.capstone.TestDataHolder;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class QueryFilterTest {
    private QueryFilter queryFilter;
    private static final Table table = TestDataHolder.getTableCopy();


    @Test
    @DisplayName("Test that the constructor does not throw an error")
    public void testConstructorNotThrow() {
        assertDoesNotThrow(() -> {
            queryFilter = new QueryFilter(table);
        });
    }

    @Test
    @DisplayName("Test that the constructor returns null when an exception is thrown")
    public void testConstructorThrow(){
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            queryFilter = new QueryFilter(null);
        });
        // Verify the exception was thrown
        assertTrue(exception.getMessage().contains("Failed to initialize QueryFilter"));
        assertNull(queryFilter);
    }

    @Nested
    @DisplayName("Tests needing a pre-instantiated QueryFilter instance")
    @SuppressWarnings("unused")
    class PreInstantiatedQueryFilterTests {
        
        private static QueryFilter goodQueryFilter;

        @BeforeAll
        public static void setup() {
            goodQueryFilter = new QueryFilter(table);
        }
        
        @ParameterizedTest
        @DisplayName("Test the search method with a valid query")
        @CsvSource({
            "elec, 304",
            "hello, 255",
            "world, 0",
            "., 0",
        })
        public void testSearchValidQuery(String query, int expectedSize) {
            Map<String, Float> results = goodQueryFilter.search(query);
            assertEquals(expectedSize, results.size()); //Expecting results based on the query
        }

        @Test
        @DisplayName("Test that the search method returns an empty list when the query is null")
        public void testSearchNullQuery() {
            Map<String, Float> results = goodQueryFilter.search(null);
            assertTrue(results.isEmpty(), "Expected empty results for null query");
        }

        @Test
        @DisplayName("Test that the search method returns an empty list when the query is empty")
        public void testSearchEmptyQuery() {
            Map<String, Float> results = goodQueryFilter.search("   ");
            assertTrue(results.isEmpty(), "Expected empty results for empty query");
        }

        @Test
        @DisplayName("Test that the search method returns an empty list when all docs are below the minimum score threshold (break branch)")
        @SuppressWarnings("ConvertToTryWithResources")
        public void testSearchNoResultsAboveMinScore() throws ReflectiveOperationException, IOException {
            MockedStatic<QueryFilter> mockedQueryFilter = mockStatic(QueryFilter.class);
            mockedQueryFilter.when(() -> QueryFilter.getScoreDocs(any(TopDocs.class))).thenReturn(new ScoreDoc[0]);
            Map<String, Float> searchResults = goodQueryFilter.search("elec");
            assertTrue(searchResults.isEmpty(), "Expected no results when all docs are below the minimum score threshold");
            mockedQueryFilter.close();
        }

        @SuppressWarnings("unchecked")
        @Test
        @DisplayName("Test that the createNGramAnalyzer method sets the nGramAnalyzer properly when an exception is thrown")
        public void testCreateNGramAnalyzer() throws Exception {
            // Create a fresh QueryFilter instance for this test to avoid interference
            QueryFilter testQueryFilter = new QueryFilter(table);
            
            //Forcing CustomAnalyzer.builder().build() to throw an IOException to test the fallback logic in createNgramAnalyzer
            try(MockedStatic<CustomAnalyzer> mocked = mockStatic(CustomAnalyzer.class)){

                //Create a mock builder that will throw IOException on build()
                CustomAnalyzer.Builder mockBuilder = mock(CustomAnalyzer.Builder.class);
                
                //Mock the static builder() method to return our mock builder
                mocked.when(CustomAnalyzer::builder).thenReturn(mockBuilder);
                
                //Mock withTokenizer to throw IOException when called with any parameters
                when(mockBuilder.withTokenizer(any(Class.class), any(String.class), any(String.class), any(String.class), any(String.class)))
                    .thenThrow(new IOException("Simulated IOException in CustomAnalyzer withTokenizer"));
                
                //Use reflection to call the private createNgramAnalyzer method
                Method createNgramAnalyzerMethod = QueryFilter.class.getDeclaredMethod("createNgramAnalyzer");
                createNgramAnalyzerMethod.setAccessible(true);
                createNgramAnalyzerMethod.invoke(testQueryFilter);

                //Use reflection to access the private ngramAnalyzer field and verify it was set to a StopAnalyzer
                Field ngramAnalyzerField = QueryFilter.class.getDeclaredField("ngramAnalyzer");
                ngramAnalyzerField.setAccessible(true);
                Object ngramAnalyzerValue = ngramAnalyzerField.get(testQueryFilter);
                
                assertTrue(ngramAnalyzerValue instanceof StopAnalyzer, 
                    "Expected ngramAnalyzer to be an instance of StopAnalyzer when CustomAnalyzer creation fails");
            }
        }

    }

    @Test
    @DisplayName("Test the appropriate message is printed when an IOException is thrown in addDoc")
    public void testAddDocIOException() throws Exception {
        // Capture System.out to verify error message
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        try {
            QueryFilter testQueryFilter = new QueryFilter(table);
            
            //Create a mock IndexWriter that throws IOException on addDocument
            IndexWriter mockWriter = mock(IndexWriter.class);
            String expectedExceptionMessage = "Simulated IO Exception";
            doThrow(new IOException(expectedExceptionMessage)).when(mockWriter).addDocument(any());
            
            //Set the mock IndexWriter in our QueryFilter instance
            Field writerField = QueryFilter.class.getDeclaredField("writer");
            writerField.setAccessible(true);
            writerField.set(testQueryFilter, mockWriter);

            //Mock the method
            Method addDocMethod = QueryFilter.class.getDeclaredMethod("addDoc", Row.class);
            addDocMethod.setAccessible(true);
   
            //Call addDoc on queryFilter with a valid item - should trigger the IOException and print the error message
            addDocMethod.invoke(testQueryFilter, table.row(0));

            //Verify the expected error message was printed
            String output = outContent.toString();
            assertEquals("Error adding document to index: " + expectedExceptionMessage + System.lineSeparator(), output);
            
        } finally {
            //Restore original System.out
            System.setOut(originalOut);
        }
    }
}
