package org.troy.capstone.search_engine.query;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

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

        System.out.println("Indexing data and building search engine...");
        /**
         * BM25 used intead of default TF-IDF for better relevance scoring,
         * saturation effect, length normalization, better ranking quality,
         * and widely used in modern search engines.
         */
        config.setSimilarity(new BM25Similarity());
        Table table = TableUtils.readCleanedAttributedData();
        try(IndexWriter writer = new IndexWriter(directory, config)) {
            for(Row row : table)
                addDoc(writer, 
                    row.getString(TableColumnName.ID.getColumnName()),
                    row.getString(TableColumnName.NAME.getColumnName()),
                    row.getString(TableColumnName.DESCRIPTION.getColumnName())
                );
        }
        System.out.println("Indexing complete. You can now enter search queries.");

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

        Scanner scan = new Scanner(System.in);
        while (true) {
            System.out.print("Enter search query (or 'exit' to quit): ");
            String userQuery = scan.nextLine();
            if( userQuery.trim().equals("exit") )
                break;
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
            List<String> names =
            Arrays.stream(results.scoreDocs).map(scoreDoc -> {
                Document doc = null;
                try {
                    doc = storedFields.document(scoreDoc.doc);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return doc.get("name");
            }).toList();
            System.out.println("Matching Document Names: ");
            names.forEach(System.out::println);
        }
    }

    // Helper method to automatically add fuzzy search to terms
    private static String makeFuzzyQuery(String originalQuery) {
        // Split query into words and add ~2 to each word (allows up to 2 character edits)
        String[] words = originalQuery.trim().split("\\s+");
        StringBuilder fuzzyQuery = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            // Don't make very short words fuzzy (less than 4 characters)
            fuzzyQuery.append(word);
            if (word.length() >= 4) 
                fuzzyQuery.append("~4");
            
            if (i < words.length - 1)
                fuzzyQuery.append(" ");
        }
        
        return fuzzyQuery.toString();
    }

    private static void addDoc(IndexWriter w, String id, String name, String desc) throws Exception {
        Document doc = new Document();
        doc.add(new StoredField("id", id)); // ID field - stored but not indexed for searching
        doc.add(new TextField("name", name, Field.Store.YES));    // Searchable and stored. Will be removed prior to use in main program
        doc.add(new TextField("description", desc, Field.Store.NO)); // Searchable but not stored  
        w.addDocument(doc);
    }
}
