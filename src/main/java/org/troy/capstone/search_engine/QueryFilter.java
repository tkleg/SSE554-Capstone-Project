package org.troy.capstone.search_engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.troy.capstone.constants.TableColumnName;

import tech.tablesaw.api.Row;
import tech.tablesaw.api.Table;

/**
 * The QueryFilter class is responsible for filtering search results based on a user query. It uses Apache Lucene to create an index of the item data and perform searches on it. Code was sourced from https://medium.com/@dhruvsharma2600/integrating-search-in-your-application-with-apache-lucene-d11c6fb84ab4 and very heavily modified to fit my case.
 */
public class QueryFilter {

    /** Boost factor for the name field in the search index, giving it higher importance in relevance scoring. */
    private static final float NAME_FIELD_BOOST = 3.0f;
    /** Boost factor for the description field in the search index, giving it lower importance than the name field in relevance scoring. */
    private static final float DESCRIPTION_FIELD_BOOST = 1.0f;

    /** Minimum n-gram size for the custom analyzer, allowing for better typo tolerance and partial matching in search queries. */
    private static final int MIN_NGRAM_SIZE = 2;
    /** Maximum n-gram size for the custom analyzer, allowing for better typo tolerance and partial matching in search queries. */
    private static final int MAX_NGRAM_SIZE = 5;

    /** Score threshold factor for filtering search results, only including results with scores at least this factor times the top score to ensure relevance while allowing for some variation in scoring. */
    private static final double SCORE_THRESHOLD_FACTOR = 0.15;

    /** Default normalization factor for BM25 similarity, can be tuned based on dataset characteristics to adjust the length normalization effect in relevance scoring. */
    private static final float DEFAULT_NORMALIZATION_FACTOR = 1.0f;
    /** Selected saturation parameter for BM25 similarity, can be tuned based on dataset characteristics to adjust the term frequency saturation effect in relevance scoring. */
    private static final float SELECTED_SATURATION_PARAMETER = 1.75f;

    /** A map of item IDs to their corresponding relevance scores for the current search results. */
    private Map<String, Float> filteredItems;
    /** The analyzer used for indexing and searching, configured to use n-grams for better typo tolerance and partial matching. */
    private Analyzer ngramAnalyzer;
    /** The Lucene Directory where the search index is stored, using ByteBuffersDirectory to keep the index in RAM for speed and simplicity. */
    private Directory directory;
    /** The Lucene IndexWriter used to create the search index from the item data. */
    private IndexWriterConfig config;
    /** The Lucene IndexWriter used to create the search index from the item data. */
    private IndexWriter writer;
    /** The Lucene IndexReader used to read the search index for performing searches. */
    private IndexReader reader;
    /** The Lucene IndexSearcher used to perform search queries on the index and retrieve relevant results. */
    private IndexSearcher searcher;
    /** The fields to be searched in the index, including name and description. */
    private String[] searchedFields = {TableColumnName.NAME.getColumnName(), TableColumnName.DESCRIPTION.getColumnName()};
    /** A map of field names to their corresponding boost factors for relevance scoring. */
    private Map<String, Float> fieldBoosts = Map.of(
        TableColumnName.NAME.getColumnName(), NAME_FIELD_BOOST, //Boost name field higher for better relevance
        TableColumnName.DESCRIPTION.getColumnName(), DESCRIPTION_FIELD_BOOST
    );
    /** The MultiFieldQueryParser used to parse user queries across multiple fields with the specified boosts and analyzer. */
    private MultiFieldQueryParser parser;

    //Main in EntryPoints.java to stop Javadocs from including it

    /**
     * Creates a QueryFilter from a Table. The constructor initializes the Lucene index with the item data from the table, using a custom n-gram analyzer for better search performance and relevance scoring.
     * 
     * @pre table is not null and contains the expected columns for creating Items (ID, Name, Description, etc.).
     * 
     * @param table A tablesaw Table containing the item data, with each row representing an item and containing columns for ID, Name, Description, etc. The constructor will create a Lucene index from this data for performing search queries.
     */
    public QueryFilter(Table table){
        try{
        if( table == null )
            throw new IOException("Input tablenput table cannot be null");
        createNgramAnalyzer();

        /**
         * ByteBuffersDirectory chosen to keep data in RAM for speed and simplicity.
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
        }catch (IOException e){
            System.out.println("Error initializing QueryFilter: " + e.getMessage());
            throw new RuntimeException("Failed to initialize QueryFilter", e);
        }
        
    }

    /**
     * Searches the index for items matching the user query and returns a map of item IDs to their relevance scores for the results that meet the score threshold.
     * Logs any errors during search execution and returns an empty map in case of errors.
     * 
     * @pre userQuery is not null or empty, and is a valid query string that can be parsed by the MultiFieldQueryParser.
     * 
     * @param userQuery The search query input by the user.
     * @return A map of item IDs to their relevance scores for the search results that meet the score threshold. If no results are found or if there is an error during search execution, an empty map is returned.
     */
    public Map<String, Float> search(String userQuery){
        try{

            //Handle Null Query
            if (userQuery == null) {
                System.out.println("Null query provided. Returning empty results.");
                return new HashMap<>();
            }

            //Handle Empty Query
            if (userQuery.trim().isEmpty()) {
                System.out.println("Empty query provided. Returning empty results.");
                return new HashMap<>();
            }
            
            filteredItems = new HashMap<>();

            Query query = parser.parse(userQuery.trim());
            TopDocs results = searcher.search(query, 1000);

            double minimumAllowableScore = results.scoreDocs.length > 0 ?
                results.scoreDocs[0].score * SCORE_THRESHOLD_FACTOR :
                0.0;

            System.out.println("Total hits: " + results.totalHits.value() + ", Minimum score for inclusion: " + minimumAllowableScore);

            StoredFields storedFields = searcher.getIndexReader().leaves().get(0).reader().storedFields();

            for (ScoreDoc scoreDoc : getScoreDocs(results)) {
                if (scoreDoc.score < minimumAllowableScore)
                    break;
                Document doc = storedFields.document(scoreDoc.doc);
                String id = doc.get("id");
                float score = scoreDoc.score;
                filteredItems.put(id, score);
            }
            System.out.println("Search completed with " + filteredItems.size() + " results above score threshold.");
            return filteredItems;
        }catch (IOException | ParseException e){
            System.out.println("Error executing search: " + e.getMessage());
            return new HashMap<>();
        }
    }


    /** Returns the array of ScoreDoc objects from the given TopDocs object. Only necessary to allow mocking for test purposes.
     * @pre results is not null and contains valid ScoreDoc objects.
     * @param results The TopDocs object containing the search results from which to extract the ScoreDoc array.
     * @return The array of ScoreDoc objects from the given TopDocs object.
    */
    static ScoreDoc[] getScoreDocs(TopDocs results) {
        return results.scoreDocs;
    }

    /** Adds a document to the Lucene index based on a tablesaw Row of item data. The document includes the item ID, name, and description, with the name and description fields being indexed for searching and the ID field being stored for retrieval. Logs any errors during document addition. 
     * 
     * @pre row is not null and contains the expected columns for creating a Document (ID, Name, Description, etc.).
     * @post A Document is created from the provided Row and added to the Lucene index, with the ID field stored and the name and description fields indexed for searching. If there is an error during document creation or addition, it is logged and the method continues without adding the document.
    *
     * @param row A Row from a tablesaw Table containing item info to be added to the Lucene index as a Document
     */
    private void addDoc(Row row){
        try{
            Document doc = new Document();
            doc.add(new StoredField(TableColumnName.ID.getColumnName(), row.getString(TableColumnName.ID.getColumnName()))); //ID field - stored but not indexed for searching
            doc.add(new TextField(TableColumnName.NAME.getColumnName(), row.getString(TableColumnName.NAME.getColumnName()), Field.Store.YES));    //Searchable and stored
            doc.add(new TextField(TableColumnName.DESCRIPTION.getColumnName(), row.getString(TableColumnName.DESCRIPTION.getColumnName()), Field.Store.NO)); //Searchable but not stored  
            writer.addDocument(doc);
        }catch (IOException e){
            System.out.println("Error adding document to index: " + e.getMessage());
        }
    }

    /**
     * Creates a custom n-gram analyzer for indexing and searching, configured to use n-grams of the specified sizes for better typo tolerance and partial matching in search queries. If there is an error creating the custom analyzer, it falls back to a standard StopAnalyzer with English stop words.
     */
    private void createNgramAnalyzer(){
        try{
            //NgramTokenizer tokenizes to get tokens to be all substrings of length 2-5, gives better typo tolerance and partial matching.
            //StopFilter Filters out common english words, also splits on non-letter cars and sets all tokens to lowercase.
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
