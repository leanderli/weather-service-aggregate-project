package com.leanderli.weather;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * OSM API client for geocoding services.
 * 
 * This client includes the project URL in the user-agent header as required by OSM API usage policy.
 * 
 * IMPORTANT: OSM Nominatim usage policy requires a maximum of 1 request per second.
 * Users of this client MUST implement rate limiting to ensure compliance with this policy.
 * 
 * If you encounter any issues with API usage, please submit an issue at:
 * https://github.com/leanderli/weather-service-aggregate-project/issues
 */
public class OsmApiClient implements AutoCloseable {
    
    private static final Logger logger = LoggerFactory.getLogger(OsmApiClient.class);
    
    // OSM Nominatim API endpoint
    private static final String NOMINATIM_API_URL = "https://nominatim.openstreetmap.org";
    
    // User-Agent header with project information for API feedback
    private static final String USER_AGENT = "WeatherServiceAggregateProject/1.0 (https://github.com/leanderli/weather-service-aggregate-project)";
    
    private final CloseableHttpClient httpClient;
    private final Gson gson;
    
    public OsmApiClient() {
        this.httpClient = HttpClients.createDefault();
        this.gson = new Gson();
    }
    
    /**
     * Search for a location using OSM Nominatim API.
     * 
     * @param query the location query string
     * @return LocationResult containing the search results
     * @throws IOException if the API call fails
     * @throws ParseException if response parsing fails
     */
    public LocationResult searchLocation(String query) throws IOException, ParseException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
        String url = String.format("%s/search?q=%s&format=json&limit=1", NOMINATIM_API_URL, encodedQuery);
        
        HttpGet request = new HttpGet(url);
        // Set user-agent header with project information
        request.setHeader("User-Agent", USER_AGENT);
        
        logger.info("Searching for location: {}", query);
        logger.debug("Using User-Agent: {}", USER_AGENT);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            
            if (response.getCode() != 200) {
                logger.error("API request failed with status code: {}", response.getCode());
                throw new IOException("API request failed with status code: " + response.getCode());
            }
            
            JsonArray results = gson.fromJson(responseBody, JsonArray.class);
            
            if (results == null || results.isEmpty()) {
                logger.warn("No results found for query: {}", query);
                return new LocationResult(null, null, null, query);
            }
            
            JsonObject firstResult = results.get(0).getAsJsonObject();
            
            // Safely extract fields with null checks
            if (!firstResult.has("lat") || !firstResult.has("lon") || !firstResult.has("display_name")) {
                logger.warn("Incomplete result data for query: {}", query);
                return new LocationResult(null, null, null, query);
            }
            
            String latitude = firstResult.get("lat").getAsString();
            String longitude = firstResult.get("lon").getAsString();
            String displayName = firstResult.get("display_name").getAsString();
            
            logger.info("Found location: {} at ({}, {})", displayName, latitude, longitude);
            
            return new LocationResult(latitude, longitude, displayName, query);
        }
    }
    
    /**
     * Reverse geocode coordinates to get location information.
     * 
     * @param latitude the latitude coordinate
     * @param longitude the longitude coordinate
     * @return LocationResult containing the location information
     * @throws IOException if the API call fails
     * @throws ParseException if response parsing fails
     */
    public LocationResult reverseGeocode(double latitude, double longitude) throws IOException, ParseException {
        String url = String.format("%s/reverse?lat=%f&lon=%f&format=json", 
                NOMINATIM_API_URL, latitude, longitude);
        
        HttpGet request = new HttpGet(url);
        // Set user-agent header with project information
        request.setHeader("User-Agent", USER_AGENT);
        
        logger.info("Reverse geocoding coordinates: ({}, {})", latitude, longitude);
        logger.debug("Using User-Agent: {}", USER_AGENT);
        
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            String responseBody = EntityUtils.toString(response.getEntity());
            
            if (response.getCode() != 200) {
                logger.error("API request failed with status code: {}", response.getCode());
                throw new IOException("API request failed with status code: " + response.getCode());
            }
            
            JsonObject result = gson.fromJson(responseBody, JsonObject.class);
            
            // Check if the result contains display_name field
            if (result == null || !result.has("display_name")) {
                logger.warn("No location found for coordinates: ({}, {})", latitude, longitude);
                return new LocationResult(
                    String.valueOf(latitude), 
                    String.valueOf(longitude), 
                    null, 
                    null
                );
            }
            
            String displayName = result.get("display_name").getAsString();
            
            logger.info("Found location: {}", displayName);
            
            return new LocationResult(
                String.valueOf(latitude), 
                String.valueOf(longitude), 
                displayName, 
                null
            );
        }
    }
    
    /**
     * Get the user-agent string used for API calls.
     * This includes the project URL for feedback purposes.
     * 
     * @return the user-agent string
     */
    public static String getUserAgent() {
        return USER_AGENT;
    }
    
    /**
     * Close the HTTP client and release resources.
     */
    @Override
    public void close() throws IOException {
        if (httpClient != null) {
            httpClient.close();
        }
    }
    
    /**
     * Result object for location searches.
     */
    public static class LocationResult {
        private final String latitude;
        private final String longitude;
        private final String displayName;
        private final String query;
        
        public LocationResult(String latitude, String longitude, String displayName, String query) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.displayName = displayName;
            this.query = query;
        }
        
        public String getLatitude() {
            return latitude;
        }
        
        public String getLongitude() {
            return longitude;
        }
        
        public String getDisplayName() {
            return displayName;
        }
        
        public String getQuery() {
            return query;
        }
        
        public boolean isFound() {
            return latitude != null && longitude != null && displayName != null;
        }
        
        @Override
        public String toString() {
            if (!isFound()) {
                return "Location not found for query: " + query;
            }
            return String.format("Location: %s (lat: %s, lon: %s)", displayName, latitude, longitude);
        }
    }
}
