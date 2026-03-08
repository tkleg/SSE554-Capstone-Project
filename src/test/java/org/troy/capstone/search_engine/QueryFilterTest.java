package org.troy.capstone.search_engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.index.IndexWriter;
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
import org.mockito.InjectMocks;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import org.mockito.Spy;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.TableUtils;

public class QueryFilterTest {
    private QueryFilter queryFilter;
    private static List<Item> items;

    @BeforeAll
    public static void setup() {
        items = TableUtils.readCleanedAttributedData().stream().map(Item::fromRow).collect(Collectors.toList());
    }

    @Test
    @DisplayName("Test that the constructor does not throw an error")
    public void testConstructorNotThrow() {
        assertDoesNotThrow(() -> {
            queryFilter = new QueryFilter(items.stream().collect(Collectors.toSet()));
        });
    }

    @Test
    @DisplayName("Test that the constructor returns null when an exception is thrown")
    public void testConstructorThrow(){
        assertThrows(RuntimeException.class, () -> {
            queryFilter = new QueryFilter(null);
        });
        assertNull(queryFilter);
    }

    @Test
    @DisplayName("Test the SearchResult class")
    public void testSearchResult() {
        QueryFilter.SearchResult searchResult = new QueryFilter.SearchResult("1", 0.5f);
        assertEquals("1", searchResult.getId());
        assertEquals(0.5f, searchResult.getScore());
    }

    @Nested
    @DisplayName("Tests needing a pre-instantiated QueryFilter instance")
    class PreInstantiatedQueryFilterTests {
        
        @Spy
        List<QueryFilter.SearchResult> resultsList;

        @InjectMocks
        private static QueryFilter goodQueryFilter;

        @BeforeAll
        public static void setup() {
            goodQueryFilter = new QueryFilter(items.stream().collect(Collectors.toSet()));
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
            List<QueryFilter.SearchResult> results = goodQueryFilter.search(query);
            assertEquals(expectedSize, results.size()); //Expecting results based on the query
        }

        @Test
        @DisplayName("Test that the search method exits with some items when an exception is thrown part way through adding results")
        public void testSearchResultsException() {
            //First get some normal results to identify an ID to target
            List<QueryFilter.SearchResult> normalResults = goodQueryFilter.search("elec");
            assertTrue(normalResults.size() > 3, "Need more than 3 results for this test");
            
            //Get the ID of the 3rd result to use as our trigger
            String targetId = normalResults.get(2).getId();
            
            //Use mockConstruction to intercept SearchResult constructor calls
            try (MockedConstruction<QueryFilter.SearchResult> mockedConstruction = 
                 mockConstruction(QueryFilter.SearchResult.class, 
                     (mock, context) -> {
                         String id = (String) context.arguments().get(0);
                         
                         //Throw exception if this is our target ID
                         if (targetId.equals(id))
                             throw new RuntimeException("Simulated exception for ID: " + id);
                })) {
                
                // Execute the search - should get partial results due to constructor exception
                List<QueryFilter.SearchResult> partialResults = goodQueryFilter.search("elec");
                
                // Verify we got fewer results than normal (should stop at the exception point)
                assertTrue(partialResults.size() < normalResults.size(), 
                    "Should have fewer results due to exception. Expected < " + normalResults.size() + 
                    " but got " + partialResults.size());
                
                // Verify we got at least some results before the exception
                assertTrue(partialResults.size() >= 2, 
                    "Should have at least 2 results before exception occurred");
                
                //Verify the constructed mocks were actually called
                assertTrue(mockedConstruction.constructed().size() >= 2, 
                    "Should have attempted to construct at least 2 SearchResult objects");
            }
        }

        @Test
        @DisplayName("Test that the search method returns an empty list when the query is null")
        public void testSearchNullQuery() {
            List<QueryFilter.SearchResult> results = goodQueryFilter.search(null);
            assertTrue(results.isEmpty(), "Expected empty results for null query");
        }

        @Test
        @DisplayName("Test that the createNGramAnalyzer method sets the nGramAnalyzer properly when an exception is thrown")
        public void testCreateNGramAnalyzer() throws Exception {
            //Forcing CustomAnalyzer.builder().build() to throw an IOException to test the fallback logic in createNgramAnalyzer
            try(MockedStatic<CustomAnalyzer> mocked = mockStatic(CustomAnalyzer.class)){

                //Create a mock builder that will throw IOException on build()
                CustomAnalyzer.Builder mockBuilder = mock(CustomAnalyzer.Builder.class);
                
                //Mock the static builder() method to return our mock builder
                mocked.when(() -> CustomAnalyzer.builder()).thenReturn(mockBuilder);
                
                //Mock withTokenizer to throw IOException when called with any parameters
                when(mockBuilder.withTokenizer(any(Class.class), any(String.class), any(String.class), any(String.class), any(String.class)))
                    .thenThrow(new IOException("Simulated IOException in CustomAnalyzer withTokenizer"));
                
                //Use reflection to call the private createNgramAnalyzer method
                Method createNgramAnalyzerMethod = QueryFilter.class.getDeclaredMethod("createNgramAnalyzer");
                createNgramAnalyzerMethod.setAccessible(true);
                createNgramAnalyzerMethod.invoke(goodQueryFilter);

                //Use reflection to access the private ngramAnalyzer field and verify it was set to a StopAnalyzer
                Field ngramAnalyzerField = QueryFilter.class.getDeclaredField("ngramAnalyzer");
                ngramAnalyzerField.setAccessible(true);
                Object ngramAnalyzerValue = ngramAnalyzerField.get(goodQueryFilter);
                
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
            QueryFilter queryFilter = new QueryFilter(items.stream().collect(Collectors.toSet()));
            
            //Create a mock IndexWriter that throws IOException on addDocument
            IndexWriter mockWriter = mock(IndexWriter.class);
            String expectedExceptionMessage = "Simulated IO Exception";
            doThrow(new IOException(expectedExceptionMessage)).when(mockWriter).addDocument(any());
            
            //Set the mock IndexWriter in our QueryFilter instance
            Field writerField = QueryFilter.class.getDeclaredField("writer");
            writerField.setAccessible(true);
            writerField.set(queryFilter, mockWriter);

            //Mock the method
            Method addDocMethod = QueryFilter.class.getDeclaredMethod("addDoc", Item.class);
            addDocMethod.setAccessible(true);
   
            //Call addDoc on queryFilter with a valid item - should trigger the IOException and print the error message
            addDocMethod.invoke(queryFilter, items.get(0));

            //Verify the expected error message was printed
            String output = outContent.toString();
            assertEquals("Error adding document to index: " + expectedExceptionMessage + System.lineSeparator(), output);
            
        } finally {
            //Restore original System.out
            System.setOut(originalOut);
        }
    }
}
