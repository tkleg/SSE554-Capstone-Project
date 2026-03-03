package org.troy.capstone.searchEngine.query;

import java.util.Arrays;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.FuzzyQuery;
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

        addDoc(writer, "Apple iPhone 15 Pro Max", "Latest flagship smartphone from Apple");
        addDoc(writer, "Samsung Galaxy S23 Ultra", "High-end Android phone with stylus");
        addDoc(writer, "Google Pixel 8", "Google's latest smartphone with AI features");
        writer.close();

        IndexReader reader = DirectoryReader.open(directory);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity());
        
        MultiFieldQueryParser parser = new MultiFieldQueryParser(
            new String[] {"name", "description"}, 
            analyzer
        );

        Query nameQuery = new FuzzyQuery(new Term("name", "gogle"));
        BoostQuery boostedNameQuery = new BoostQuery(nameQuery, 2.0f); //Boost the name field query

        Query descriptionQuery = new FuzzyQuery(new Term("description", "flgship"));
        BooleanQuery combinedQuery = new BooleanQuery.Builder()
            .add(boostedNameQuery, BooleanClause.Occur.SHOULD)
            .add(descriptionQuery, BooleanClause.Occur.SHOULD)
            .build();

        TopDocs results = searcher.search(combinedQuery, 10);
        System.out.println("Total Hits: " + results.totalHits + " for search term " + nameQuery.toString() + " OR " + descriptionQuery.toString());
        for (int i = 0; i < results.scoreDocs.length; i++) {
            Document doc = searcher.storedFields().document(results.scoreDocs[i].doc);
            System.out.println((i + 1) + ". " + doc.get("name") + " - " + doc.get("description") + " (Score: " + results.scoreDocs[i].score + ")");
        }
    }

    private static void addDoc(IndexWriter w, String name, String desc) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("name", name, Field.Store.YES));
        doc.add(new TextField("description", desc, Field.Store.YES));
        w.addDocument(doc);
    }
}
