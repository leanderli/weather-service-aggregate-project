package com.leanderli.weather;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for OsmApiClient to ensure proper user-agent header configuration.
 */
public class OsmApiClientTest {
    
    @Test
    public void testUserAgentContainsProjectUrl() {
        String userAgent = OsmApiClient.getUserAgent();
        
        assertNotNull(userAgent, "User-Agent should not be null");
        assertTrue(userAgent.contains("https://github.com/leanderli/weather-service-aggregate-project"),
                "User-Agent should contain the project URL");
        assertTrue(userAgent.contains("WeatherServiceAggregateProject"),
                "User-Agent should contain the project name");
    }
    
    @Test
    public void testLocationResultNotFound() {
        OsmApiClient.LocationResult result = new OsmApiClient.LocationResult(null, null, null, "test query");
        
        assertFalse(result.isFound(), "Result should not be found");
        assertEquals("test query", result.getQuery());
        assertNull(result.getLatitude());
        assertNull(result.getLongitude());
        assertNull(result.getDisplayName());
    }
    
    @Test
    public void testLocationResultFound() {
        OsmApiClient.LocationResult result = new OsmApiClient.LocationResult(
                "39.9042", "116.4074", "Beijing, China", "Beijing"
        );
        
        assertTrue(result.isFound(), "Result should be found");
        assertEquals("39.9042", result.getLatitude());
        assertEquals("116.4074", result.getLongitude());
        assertEquals("Beijing, China", result.getDisplayName());
        assertEquals("Beijing", result.getQuery());
    }
    
    @Test
    public void testLocationResultToString() {
        OsmApiClient.LocationResult foundResult = new OsmApiClient.LocationResult(
                "39.9042", "116.4074", "Beijing, China", "Beijing"
        );
        String foundString = foundResult.toString();
        assertTrue(foundString.contains("Beijing, China"));
        assertTrue(foundString.contains("39.9042"));
        assertTrue(foundString.contains("116.4074"));
        
        OsmApiClient.LocationResult notFoundResult = new OsmApiClient.LocationResult(
                null, null, null, "unknown place"
        );
        String notFoundString = notFoundResult.toString();
        assertTrue(notFoundString.contains("not found"));
        assertTrue(notFoundString.contains("unknown place"));
    }
}
