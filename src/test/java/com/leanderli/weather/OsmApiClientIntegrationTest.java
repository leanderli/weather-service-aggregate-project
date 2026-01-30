package com.leanderli.weather;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

/**
 * Integration tests for OSM API client.
 * These tests verify the client structure without making actual API calls.
 */
public class OsmApiClientIntegrationTest {
    
    @Test
    public void testClientCanBeCreatedAndClosed() throws IOException {
        // Test that the client implements AutoCloseable correctly
        try (OsmApiClient client = new OsmApiClient()) {
            assertNotNull(client);
        }
        // If we reach here, close() was called successfully
    }
    
    @Test
    public void testClientProvidesUserAgent() {
        String userAgent = OsmApiClient.getUserAgent();
        
        // Verify user agent format
        assertNotNull(userAgent);
        assertFalse(userAgent.isEmpty());
        
        // Verify it follows the pattern: ProjectName/Version (URL)
        assertTrue(userAgent.matches(".*/.* \\(https://.*\\)"));
    }
    
    @Test
    public void testLocationResultHandlesNotFound() {
        OsmApiClient.LocationResult result = new OsmApiClient.LocationResult(
            null, null, null, "unknown location"
        );
        
        assertFalse(result.isFound());
        assertNotNull(result.toString());
        assertTrue(result.toString().toLowerCase().contains("not found"));
    }
    
    @Test
    public void testLocationResultHandlesFoundLocation() {
        OsmApiClient.LocationResult result = new OsmApiClient.LocationResult(
            "40.7128", "-74.0060", "New York, NY, USA", "New York"
        );
        
        assertTrue(result.isFound());
        assertEquals("40.7128", result.getLatitude());
        assertEquals("-74.0060", result.getLongitude());
        assertEquals("New York, NY, USA", result.getDisplayName());
        assertEquals("New York", result.getQuery());
        
        String str = result.toString();
        assertTrue(str.contains("New York"));
        assertTrue(str.contains("40.7128"));
        assertTrue(str.contains("-74.0060"));
    }
}
