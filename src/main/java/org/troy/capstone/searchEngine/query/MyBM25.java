package org.troy.capstone.searchEngine.query;

import java.util.Arrays;
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
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

public class MyBM25 {
    public static void main() throws Exception{
        //See https://www.baeldung.com/lucene-analyzers for details on analyzers
        //Hardcoded CharArraySet for custom stop words
        CharArraySet customStopWords = new CharArraySet(
            Arrays.asList("the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by"), 
            true  //ignoreCase = true
        );
        Analyzer analyzer = new StopAnalyzer(customStopWords);
        Directory directory = new ByteBuffersDirectory();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        config.setSimilarity(new BM25Similarity());
        IndexWriter writer = new IndexWriter(directory, config);

        addDoc(writer, "101", "Apple iPhone 15 Pro Max", "Latest flagship smartphone from Apple");
        addDoc(writer, "102", "Samsung Galaxy S23 Ultra", "High-end Android phone with stylus");
        addDoc(writer, "103", "Google Pixel 8", "Google's latest smartphone with AI features");
        writer.close();

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        
        // Create field boosts - name field gets 2x weight compared to description
        String[] fields = {"name", "description"};
        Map<String, Float> boosts = Map.ofEntries(
            Map.entry("name", 2.0f),
            Map.entry("description", 1.0f)
        );
        
        MultiFieldQueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);
        
        // Make parsing more generous
        parser.setDefaultOperator(MultiFieldQueryParser.Operator.OR); // OR instead of AND (default)
        parser.setFuzzyMinSim(0.7f); // Allow fuzzy matching for typos (70% similarity)
        parser.setAllowLeadingWildcard(true); // Allow wildcards at start of terms

        // Simple user search string - this is what you'd get from user input
        String userQuery = "Apple smartphone";
        
        // Parse the user query to search across both name and description fields
        Query query = parser.parse(userQuery);

        // Increase result limit and search more generously
        TopDocs results = searcher.search(query, 50); // Increased from 10 to 50
        System.out.println("Total Hits: " + results.totalHits + " for search term: '" + userQuery + "'");
        for (int i = 0; i < results.scoreDocs.length; i++) {
            Document doc = searcher.storedFields().document(results.scoreDocs[i].doc);
            System.out.println((i + 1) + ". ID: " + doc.get("id") + " | " + doc.get("name") + " - " + doc.get("description") + " (Score: " + results.scoreDocs[i].score + ")");
        }
    }

    private static void addDoc(IndexWriter w, String id, String name, String desc) throws Exception {
        Document doc = new Document();
        doc.add(new StoredField("id", id)); // ID field - stored but not indexed for searching
        doc.add(new TextField("name", name, Field.Store.YES));
        doc.add(new TextField("description", desc, Field.Store.YES));
        w.addDocument(doc);
    }
}
