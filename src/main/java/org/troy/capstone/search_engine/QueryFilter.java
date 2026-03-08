package org.troy.capstone.search_engine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

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
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.entities.Item;
import org.troy.capstone.utils.TableUtils;

public class QueryFilter {

    private static final float NAME_FIELD_BOOST = 3.0f; //Boost for name field
    private static final float DESCRIPTION_FIELD_BOOST = 1.0f; //Boost for description field

    private static final int MIN_NGRAM_SIZE = 2;
    private static final int MAX_NGRAM_SIZE = 5;

    private static final double SCORE_THRESHOLD_FACTOR = 0.15; //Only include results with scores at least 15% of the top score

    private static final float DEFAULT_NORMALIZATION_FACTOR = 1.0f; //Default normalization factor for BM25, can be tuned based on dataset characteristics
    private static final float SELECTED_SATURATION_PARAMETER = 1.75f; //Selected saturation parameter for BM25, can be tuned based on dataset characteristics

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

    public static void main(String[] args) {
        
        Set<Item> items = TableUtils.readCleanedAttributedData().stream().map(Item::fromRow).collect(Collectors.toSet());
        QueryFilter queryFilter = new QueryFilter(items);

        try (Scanner scan = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter search query (or 'exit' to quit): ");
                String userQuery = scan.nextLine().trim();
                if (userQuery.equalsIgnoreCase("exit"))
                    break;
                List<SearchResult> results = queryFilter.search(userQuery);
                results.forEach(result -> System.out.println("ID: " + result.getId() + ", Score: " + result.getScore()));
            }
        }
    }

    public QueryFilter(Set<Item> items){
        try{
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

        items.forEach(this::addDoc);

        reader = DirectoryReader.open(writer);
        searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new BM25Similarity(SELECTED_SATURATION_PARAMETER, DEFAULT_NORMALIZATION_FACTOR));
        
        parser = new MultiFieldQueryParser(searchedFields, ngramAnalyzer, fieldBoosts);
        //Set default operator to OR for better recall, so that if any of the terms match, it will be included in results
        parser.setDefaultOperator(MultiFieldQueryParser.Operator.OR);
        }catch (Exception e){
            System.out.println("Error initializing QueryFilter: " + e.getMessage());
        }
    }

    public class SearchResult {
        private String id;
        private float score;

        public SearchResult(String id, float score) {
            this.id = id;
            this.score = score;
        }

        public String getId() {
            return id;
        }

        public float getScore() {
            return score;
        }
    }

    public List<SearchResult> search(String userQuery){
        try{
            List<SearchResult> resultsList = new ArrayList<>();

            Query query = parser.parse(userQuery.trim());
            TopDocs results = searcher.search(query, 1000);

            double minimumAllowableScore = results.scoreDocs.length > 0 ?
                results.scoreDocs[0].score * SCORE_THRESHOLD_FACTOR :
                0.0;

            StoredFields storedFields = searcher.getIndexReader().leaves().get(0).reader().storedFields();

            for (int i = 0; i < results.scoreDocs.length; i++) {
                try {
                    ScoreDoc scoreDoc = results.scoreDocs[i];
                    if (scoreDoc.score < minimumAllowableScore)
                        break;
                    Document doc = storedFields.document(scoreDoc.doc);
                    String id = doc.get("id");
                    float score = scoreDoc.score;
                    resultsList.add(new SearchResult(id, score));
                } catch (Exception e) {
                    System.out.println("Error retrieving document for result " + i + ": " + e.getMessage());
                    return resultsList; //Return results found so far if there's an error retrieving a document
                }
            }
            
            return resultsList;
        }catch (Exception e){
            System.out.println("Error executing search: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private void addDoc(Item item){
        try{
            Document doc = new Document();
            doc.add(new StoredField("id", item.getId())); // ID field - stored but not indexed for searching
            doc.add(new TextField("name", item.getName(), Field.Store.YES));    // Searchable and stored. Will be removed prior to use in main program
            doc.add(new TextField("description", item.getDescription(), Field.Store.NO)); // Searchable but not stored  
            writer.addDocument(doc);
        }catch (Exception e){
            System.out.println("Error adding document with ID " + item.getId() + " to index: " + e.getMessage());
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
        }catch (Exception e){
            System.out.println("Error creating ngram analyzer: " + e.getMessage());
            System.out.println("Falling back to Stop analyzer with basic English stop words.");
            ngramAnalyzer = new StopAnalyzer(EnglishAnalyzer.ENGLISH_STOP_WORDS_SET);
        }
    }
}
