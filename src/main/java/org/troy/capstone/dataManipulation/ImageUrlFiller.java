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
import org.troy.capstone.constants.TableColumnName;
import org.troy.capstone.utils.TableUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

public class ImageUrlFiller {
    @TestExclusionGenerated
    public static void main(String[] args) {
        //Load csv file
        Table productData = TableUtils.readCleanedData();

        StringColumn imageUrlColumn = productData.stringColumn(TableColumnName.IMAGE_URL.getColumnName());
        StringColumn nameColumn = productData.stringColumn(TableColumnName.NAME.getColumnName());
        StringColumn photoAuthorColumn = productData.stringColumn(TableColumnName.PHOTO_AUTHOR.getColumnName());
        StringColumn photoAuthorUrlColumn = productData.stringColumn(TableColumnName.PHOTO_AUTHOR_URL.getColumnName());

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
                
                // Extract the first photo's data from search results
                JsonNode results = rootNode.get("results");
                if (results.isArray() && results.size() > 0) {
                    JsonNode photo = results.get(0);
                    if (photo != null && photo.has("urls") && photo.has("user")
                        && photo.get("urls").has("regular") && photo.get("user").has("name")
                        && photo.get("user").has("links") && photo.get("user").get("links").has("html")) {

                        String photoUrl = photo.get("urls").get("regular").asText() + "?utm_source=sse554_capstone&utm_medium=referral";
                        imageUrlColumn.set(i, photoUrl); //update the URL in the table
                        
                        JsonNode user = photo.get("user");
                        String authorName = user.get("name").asText();
                        photoAuthorColumn.set(i, authorName); //update the photo author

                        String authorProfileUrl = user.get("links").get("html").asText() + "?utm_source=sse554_capstone&utm_medium=referral";
                        photoAuthorUrlColumn.set(i, authorProfileUrl); //update the photo author URL
                        
                        System.out.println("Updated row " + i + ": " + query + " with photo by " + authorName);
                    } else {
                        System.out.println("Bad photo data found for: " + query);
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
        TableUtils.writeAttributedData(productData);
    }
}
