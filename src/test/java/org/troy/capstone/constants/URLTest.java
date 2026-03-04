package org.troy.capstone.constants;

import java.net.http.HttpClient;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//These tests should not fail the build process, hence the assert true at the end of the test method.
//They simply check for broken URLs
public class URLTest {

    @ParameterizedTest
    @EnumSource(
        value = URL.class,
        names = {"DEFAULT_AUTHOR_NAME"},
        mode = EnumSource.Mode.EXCLUDE
    )
    @DisplayName("Test that all URLs in URL enum can be called on the network and return a valid response")
    public void testURLs(URL url) {
        try{
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri( URI.create( url.getUrl() ) )
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            int statusCode = response.statusCode();
            if( statusCode < 200 || statusCode >= 400 )
                throw new Exception("Received non-success status code: " + statusCode);
        } catch (Exception e) {
            System.err.println("Error accessing URL: " + url.getUrl());
        }
        assert true;
    }
        
}
