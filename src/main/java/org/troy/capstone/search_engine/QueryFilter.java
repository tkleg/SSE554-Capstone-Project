package org.troy.capstone.search_engine;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ngram.NGramTokenizerFactory;
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
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.troy.capstone.annotations.TestExclusionGenerated;
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.utils.TableUtils;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

public class QueryFilter {

    private static final float NAME_FIELD_BOOST = 3.0f; //Boost for name field
    private static final float DESCRIPTION_FIELD_BOOST = 1.0f; //Boost for description field

    private static final int MIN_NGRAM_SIZE = 2;
    private static final int MAX_NGRAM_SIZE = 5;

    private static final double SCORE_THRESHOLD_FACTOR = 0.15; //Only include results with scores at least 15% of the top score

    private static final float DEFAULT_NORMALIZATION_FACTOR = 1.0f; //Default normalization factor for BM25, can be tuned based on dataset characteristics
    private static final float SELECTED_SATURATION_PARAMETER = 1.75f; //Selected saturation parameter for BM25, can be tuned based on dataset characteristics

    private Map<String, Float> filteredItems;
    private Analyzer ngramAnalyzer;
    private Directory directory;
    private IndexWriterConfig config;
    private IndexWriter writer;
    private IndexReader reader;
    private IndexSearcher searcher;
    private String[] searchedFields = {TableColumnName.NAME.getColumnName(), TableColumnName.DESCRIPTION.getColumnName()}; // Fields to search on
    private Map<String, Float> fieldBoosts = Map.of(
        TableColumnName.NAME.getColumnName(), NAME_FIELD_BOOST, //Boost name field higher for better relevance
        TableColumnName.DESCRIPTION.getColumnName(), DESCRIPTION_FIELD_BOOST
    );
    private MultiFieldQueryParser parser;
    private Table table;

    @TestExclusionGenerated
    public static void main(String[] args) {
        
        Table table = TableUtils.readCleanedAttributedData();
        QueryFilter queryFilter = new QueryFilter(table);

        try (Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter search query (or 'exit' to quit): ");
                String userQuery = scan.nextLine().trim();
                if (userQuery.equalsIgnoreCase("exit"))
                    break;
                Map<String, Float> results = queryFilter.search(userQuery);
                for(Map.Entry<String, Float> entry : results.entrySet())
                    System.out.println("ID: " + entry.getKey() + ", Score: " + entry.getValue());
            }
        }
    }

    public QueryFilter(Table table){
        try{
        this.table = table;
        createNgramAnalyzer();

        /**
         *c ByteBuffersDirectory chosen to keep data in RAM for speed and simplicity
         * All data in RAM
         */
        directory = new ByteBuffersDirectory();

        config = new IndexWriterConfig(ngramAnalyzer);

        /**
         * BM25 used intead of default TF-IDF for better relevance scoring,
         * saturation effect, length normalization, better ranking quality,
         * and widely used in modern search engines.
         */
        config.setSimilarity(new BM25Similarity(SELECTED_SATURATION_PARAMETER, DEFAULT_NORMALIZATION_FACTOR));

        writer = new IndexWriter(directory, config);

        table.stream().forEach(this::addDoc);

        reader = DirectoryReader.open(writer);
        searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity(SELECTED_SATURATION_PARAMETER, DEFAULT_NORMALIZATION_FACTOR));
        
        parser = new MultiFieldQueryParser(searchedFields, ngramAnalyzer, fieldBoosts);
        //Set default operator to OR for better recall, so that if any of the terms match, it will be included in results
        parser.setDefaultOperator(MultiFieldQueryParser.Operator.OR);
        }catch (Exception e){
            System.out.println("Error initializing QueryFilter: " + e.getMessage());
            throw new RuntimeException("Failed to initialize QueryFilter", e);
        }
        
    }

    public Map<String, Float> search(String userQuery){
        try{
            // Handle null or empty query
            if (userQuery == null || userQuery.trim().isEmpty()) {
                System.out.println("Empty or null query provided, returning no results");
                return new HashMap<>();
            }
            
            filteredItems = new HashMap<>();

            Query query = parser.parse(userQuery.trim());
            TopDocs results = searcher.search(query, 1000);

            double minimumAllowableScore = results.scoreDocs.length > 0 ?
                results.scoreDocs[0].score * SCORE_THRESHOLD_FACTOR :
                0.0;

            System.out.println("Total hits: " + results.totalHits.value() + ", Minimum score for inclusion: " + minimumAllowableScore);
            
            if( results.totalHits.value() == 0){
                System.out.println("No results found for query: " + userQuery);
                return filteredItems;
            }

            StoredFields storedFields = searcher.getIndexReader().leaves().get(0).reader().storedFields();

            for (int i = 0; i < results.scoreDocs.length; i++) {
                ScoreDoc scoreDoc = results.scoreDocs[i];
                if (scoreDoc.score < minimumAllowableScore)
                    break;
                Document doc = storedFields.document(scoreDoc.doc);
                String id = doc.get("id");
                float score = scoreDoc.score;
                filteredItems.put(id, score);
            }
            System.out.println("Search completed with " + filteredItems.size() + " results above score threshold.");
            return filteredItems;
        }catch (Exception e){
            System.out.println("Error executing search: " + e.getMessage());
            return new HashMap<>();
        }
    }

    private void addDoc(Row row){
        try{
            Document doc = new Document();
            doc.add(new StoredField(TableColumnName.ID.getColumnName(), row.getString(TableColumnName.ID.getColumnName()))); //ID field - stored but not indexed for searching
            doc.add(new TextField(TableColumnName.NAME.getColumnName(), row.getString(TableColumnName.NAME.getColumnName()), Field.Store.YES));    //Searchable and stored. Will be removed prior to use in main program
            doc.add(new TextField(TableColumnName.DESCRIPTION.getColumnName(), row.getString(TableColumnName.DESCRIPTION.getColumnName()), Field.Store.NO)); //Searchable but not stored  
            writer.addDocument(doc);
        }catch (IOException e){
            System.out.println("Error adding document to index: " + e.getMessage());
        }
    }

    private void createNgramAnalyzer(){
        try{
            //NgramTokenizer tokenizes to get tokens to be all substrings of length 2-5, gives better typo tolerance and partial matching
            //StopFilter Filters out common english words, also splits on non-letter cars and sets all tokens to lowercase
            ngramAnalyzer = CustomAnalyzer.builder()
                .withTokenizer(NGramTokenizerFactory.class, "minGramSize", "" + MIN_NGRAM_SIZE, "maxGramSize",  "" + MAX_NGRAM_SIZE)
                .addTokenFilter(StopFilterFactory.class)
                .build();
        }catch (IOException e){
            System.out.println("Error creating ngram analyzer: " + e.getMessage());
            System.out.println("Falling back to Stop analyzer with basic English stop words.");
            ngramAnalyzer = new StopAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        }
    }
}
