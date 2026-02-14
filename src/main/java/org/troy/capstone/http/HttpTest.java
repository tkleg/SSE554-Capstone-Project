package org.troy.capstone.http;

import java.io.File;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import org.troy.capstone.ENV;

import com.fasterxml.jackson.databind.ObjectMapper;

public class HttpTest {
    public static void main(String[] args) throws Exception {
        URL url = URI.create("https://api.unsplash.com/photos/").toURL();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url.toURI())
                .header("Authorization", "Client-ID " + ENV.UNSPLASH_ACCESS_KEY)
                .header("Accept-Version", "v1")
                .build();
        HttpClient client = HttpClient.newHttpClient();
        String response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString()).body();
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(response, Object.class);
        String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(json);
        File file = new File("response.json");
        PrintWriter writer = new PrintWriter(file); 
        writer.write(prettyJson);
        writer.close();
    }
}
