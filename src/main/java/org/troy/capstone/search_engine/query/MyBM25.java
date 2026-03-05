package org.troy.capstone.search_engine.query;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class MyBM25 {
    public static void main() throws Exception{
        //Lucene https://www.baeldung.com/lucene
        //Highly optimized, better than if I implemented the algorithms myself due to optimizations, data structures
        //See https://www.baeldung.com/lucene-analyzers for details on analyzers
        //Base code source is https://medium.com/@dhruvsharma2600/integrating-search-in-your-application-with-apache-lucene-d11c6fb84ab4
        //Stop used to remove common words

        //Hardcoded CharArraySet for custom stop words
        CharArraySet customStopWords = new CharArraySet(
            Arrays.asList("the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by"), 
            true  //ignoreCase = true
        );
        Analyzer analyzer = new StopAnalyzer(customStopWords);

        /**
         * ByteBuffersDirectory chosen to keep data in RAM for speed and simplicicty
         * All data in RAM
         */
        Directory directory = new ByteBuffersDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        /**
         * BM25 used intead of default TF-IDF for better relevance scoring,
         * saturation effect, length normalization, better ranking quality,
         * and widely used in modern search engines.
         */
        config.setSimilarity(new BM25Similarity());
        try(IndexWriter writer = new IndexWriter(directory, config)) {
            addDoc(writer, "101", "Apple iPhone 15 Pro Max", "Latest flagship smartphone from Apple");
            addDoc(writer, "102", "Samsung Galaxy S23 Ultra", "High-end Android phone with stylus");
            addDoc(writer, "103", "Google Pixel 8", "Google's latest smartphone with AI features");
        }

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        
        // Create field boosts - name field gets 2x weight compared to description
        String[] fields = {"name", "description"};
        Map<String, Float> boosts = Map.of(
            "name", 2.0f,
            "description", 1.0f
        );
        
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);

        // Make parsing more generous
        parser.setDefaultOperator(MultiFieldQueryParser.Operator.OR); // OR instead of AND (default)
        parser.setFuzzyMinSim(0.8f); // Allow fuzzy matching for typos (80% similarity - more lenient)

        // Simple user search string - this is what you'd get from user input
        // For fuzzy search, append ~ to terms with potential typos
        String userQuery = "galxy smartphone"; // Test with typo
        
        // Option 1: Manual fuzzy query (as shown above)
        // String manualFuzzyQuery = "galxy~2 smartphone";
        
        // Option 2: Automatic fuzzy query conversion
        String autoFuzzyQuery = makeFuzzyQuery(userQuery);
        System.out.println("Original query: '" + userQuery + "'");
        System.out.println("Auto fuzzy query: '" + autoFuzzyQuery + "'");
        
        // Parse the user query to search across both name and description fields
        Query query = parser.parse(autoFuzzyQuery); // Use the fuzzy version

        TopDocs results = searcher.search(query, 50);
        
        System.out.println("Total Hits: " + results.totalHits + " for search term: '" + userQuery + "' (fuzzy: '" + autoFuzzyQuery + "')");
        
        // All results already meet minimum score - no manual filtering needed
        StoredFields storedFields = searcher.storedFields();
        List<String> ids =
        Arrays.stream(results.scoreDocs).map(scoreDoc -> {
            Document doc = null;
            try {
                doc = storedFields.document(scoreDoc.doc);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return doc.get("id");
        }).toList();
        System.out.println("Matching Document IDs: " + ids);
    }

    // Helper method to automatically add fuzzy search to terms
    private static String makeFuzzyQuery(String originalQuery) {
        // Split query into words and add ~2 to each word (allows up to 2 character edits)
        String[] words = originalQuery.trim().split("\\s+");
        StringBuilder fuzzyQuery = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            // Don't make very short words fuzzy (less than 4 characters)
            if (word.length() >= 4) {
                fuzzyQuery.append(word).append("~4");
            } else {
                fuzzyQuery.append(word);
            }
            
            if (i < words.length - 1) {
                fuzzyQuery.append(" ");
            }
        }
        
        return fuzzyQuery.toString();
    }

    private static void addDoc(IndexWriter w, String id, String name, String desc) throws Exception {
        Document doc = new Document();
        doc.add(new StoredField("id", id)); // ID field - stored but not indexed for searching
        doc.add(new TextField("name", name, Field.Store.YES));    // Searchable and stored
        doc.add(new TextField("description", desc, Field.Store.YES)); // Searchable and stored  
        w.addDocument(doc);
    }
}
