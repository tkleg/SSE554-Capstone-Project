package org.troy.capstone.dataManipulation;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.troy.capstone.ENV;
import org.troy.capstone.annotations.TestExclusionGenerated;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class ImageUrlFiller {
    @TestExclusionGenerated
    public static void main(String[] args) {
        //Load csv file
        Table productData = Table.read().csv("data\\1000_items_catalog_v2.csv");

        StringColumn imageUrlColumn = productData.stringColumn("image_url");
        StringColumn nameColumn = productData.stringColumn("name");

        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        
        List<Integer> badRowIndexes = new ArrayList<>();

        //iterate over image_url column
        for (int i = 0; i < imageUrlColumn.size(); i++) {
            String oldUrl = imageUrlColumn.get(i);
            
            if( !oldUrl.startsWith("https://example.com") )
                continue; //skip already filled URLs
            
            String query = nameColumn.get(i);//use product name as query
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            
            //System.out.println("Processing row " + i + ": " + query);
            
            try {
                // Use search API to get photos that actually match the query
                URI uri = URI.create("https://api.unsplash.com/search/photos?query=" + encodedQuery + "&per_page=1");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Authorization", "Client-ID " + ENV.UNSPLASH_ACCESS_KEY)
                        .header("Accept-Version", "v1")
                        .build();
                
                // Send request and capture full response
                HttpResponse<String> httpResponse = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                
                int responseCode = httpResponse.statusCode();
                if( responseCode != 200 ){
                    System.err.println("Response Code Error: Row " + i + " (" + query + "): Received response code " + responseCode);
                    badRowIndexes.add(i); //mark this row index as bad
                    continue; //skip to next iteration
                }

                String responseBody = httpResponse.body();
                JsonNode rootNode = mapper.readTree(responseBody);
                
                // Extract the first photo's URL from search results
                JsonNode results = rootNode.get("results");
                if (results != null && results.isArray() && results.size() > 0) {
                    JsonNode photo = results.get(0);
                    if (photo != null && photo.has("urls")) {
                        String photoUrl = photo.get("urls").get("regular").asText();
                        imageUrlColumn.set(i, photoUrl); //update the URL in the table
                        //System.out.println("Updated row " + i + " (" + query + "): " + photoUrl);
                    } else {
                        System.out.println("No photo data found for: " + query);
                        badRowIndexes.add(i);
                    }
                } else {
                    System.out.println("No search results found for: " + query);
                    badRowIndexes.add(i);
                }
                
            } catch (IOException | InterruptedException e) {
                System.err.println("Exception Caught: Row " + i + " (" + query + "): " + e.getMessage());
                badRowIndexes.add(i); //mark this row index as bad
            }
        }

        //remove bad rows
        productData = productData.dropWhere(
            Selection.with(
                badRowIndexes.stream().mapToInt(Integer::intValue).toArray()
            )
        );
        System.out.println("Removed " + badRowIndexes.size() + " rows due to errors.\nRows with errors were at indices: " + badRowIndexes);
        System.out.println("Final dataset size after cleanup: " + productData.rowCount() + " rows.");

        //Save updated table to new CSV
        productData.write().csv("data\\shopping_dataset_500_items_filled.csv");
    }
}
