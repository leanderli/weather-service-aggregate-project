package com.leanderli.weather;

import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Example application demonstrating the use of OSM API with proper user-agent headers.
 * 
 * This project uses the OpenStreetMap Nominatim API for geocoding services.
 * As per OSM usage policy, the user-agent header includes the project URL:
 * https://github.com/leanderli/weather-service-aggregate-project
 * 
 * If you encounter any issues with the API usage, please submit an issue at the project URL.
 */
public class WeatherServiceApp {
    
    private static final Logger logger = LoggerFactory.getLogger(WeatherServiceApp.class);
    
    public static void main(String[] args) {
        logger.info("Weather Service Aggregate Project - OSM API Integration Demo");
        logger.info("User-Agent: {}", OsmApiClient.getUserAgent());
        logger.info("For issues or feedback, visit: https://github.com/leanderli/weather-service-aggregate-project/issues");
        
        try (OsmApiClient osmClient = new OsmApiClient()) {
            // Example 1: Search for a location
            logger.info("\n=== Example 1: Location Search ===");
            OsmApiClient.LocationResult result1 = osmClient.searchLocation("Beijing, China");
            logger.info(result1.toString());
            
            // Wait 1 second between requests to comply with OSM usage policy
            Thread.sleep(1000);
            
            // Example 2: Search for another location
            logger.info("\n=== Example 2: Location Search ===");
            OsmApiClient.LocationResult result2 = osmClient.searchLocation("New York, USA");
            logger.info(result2.toString());
            
            // Wait 1 second between requests to comply with OSM usage policy
            Thread.sleep(1000);
            
            // Example 3: Reverse geocode
            if (result1.isFound()) {
                logger.info("\n=== Example 3: Reverse Geocoding ===");
                try {
                    double lat = Double.parseDouble(result1.getLatitude());
                    double lon = Double.parseDouble(result1.getLongitude());
                    OsmApiClient.LocationResult result3 = osmClient.reverseGeocode(lat, lon);
                    logger.info(result3.toString());
                } catch (NumberFormatException e) {
                    logger.error("Invalid coordinate format: {}", e.getMessage());
                }
            }
            
            logger.info("\n=== Demo completed successfully ===");
            logger.info("Note: Delays were added between API calls to comply with OSM usage policy (1 request per second)");
            
        } catch (IOException | ParseException e) {
            logger.error("Error occurred while calling OSM API", e);
            logger.error("If this is an API-related issue, please report it at:");
            logger.error("https://github.com/leanderli/weather-service-aggregate-project/issues");
            System.exit(1);
        } catch (InterruptedException e) {
            logger.error("Demo was interrupted", e);
            Thread.currentThread().interrupt();
            System.exit(1);
        }
    }
}
