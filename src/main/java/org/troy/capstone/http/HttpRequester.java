package org.troy.capstone.http;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.troy.capstone.ENV;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpRequester {
    public static void main(String[] args) throws Exception {
        // Define your query terms
        String[] queries = {"nature", "city", "ocean", "mountains"};
        
        HttpClient client = HttpClient.newHttpClient();
        ObjectMapper mapper = new ObjectMapper();
        
        System.out.println("Searching for photos that match query terms:");
        JsonNode rootNode = null;
        // Search for each query term to get relevant photos
        for (String query : queries) {
            try {
                // Use search API to get photos that actually match the query
                URI uri = URI.create("https://api.unsplash.com/search/photos?query=" + query + "&per_page=1");
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(uri)
                        .header("Authorization", "Client-ID " + ENV.UNSPLASH_ACCESS_KEY)
                        .header("Accept-Version", "v1")
                        .build();
                
                // Send request and capture full response
                HttpResponse<String> httpResponse = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());
                int responseCode = httpResponse.statusCode();
                String responseBody = httpResponse.body();
                
                System.out.println("Response code for " + query + ": " + responseCode);
                
                rootNode = mapper.readTree(responseBody);
                
                // Extract the first photo's URL and description
                JsonNode results = rootNode.path("results");
                if (results.isArray() && results.size() > 0) {
                    JsonNode firstPhoto = results.get(0);
                    String photoUrl = firstPhoto.path("urls").path("regular").asText();
                    String description = firstPhoto.path("description").asText("");
                    System.out.println("- " + query + ": " + description + " (" + photoUrl + ")");
                } else {
                    System.out.println("- " + query + ": No results found.");
                }
                
            } catch (Exception e) {
                System.err.println("Error searching for " + query + ": " + e.getMessage());
            }
        }
        
        // Still save the full response
        String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        File file = new File("response.json");
        PrintWriter writer = new PrintWriter(file); 
        writer.write(prettyJson);
        writer.close();
    }
}
