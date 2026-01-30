package com.leanderli.weather;

/**
 * Simple utility to verify the user-agent configuration.
 */
public class VerifyUserAgent {
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("USER-AGENT VERIFICATION");
        System.out.println("=".repeat(80));
        
        String userAgent = OsmApiClient.getUserAgent();
        System.out.println("\nConfigured User-Agent:");
        System.out.println(userAgent);
        
        System.out.println("\nVerifying requirements:");
        
        // Check 1: Contains project name
        boolean hasProjectName = userAgent.contains("WeatherServiceAggregateProject");
        System.out.println("✓ Contains project name: " + hasProjectName);
        
        // Check 2: Contains GitHub project URL
        boolean hasProjectUrl = userAgent.contains("https://github.com/leanderli/weather-service-aggregate-project");
        System.out.println("✓ Contains project URL: " + hasProjectUrl);
        
        // Check 3: Has version number
        boolean hasVersion = userAgent.matches(".*\\d+\\.\\d+.*");
        System.out.println("✓ Contains version: " + hasVersion);
        
        System.out.println("\nPurpose:");
        System.out.println("This user-agent header is included in all OSM API requests to allow");
        System.out.println("the API maintainers to contact us if there are any issues with API usage.");
        
        System.out.println("\nFeedback:");
        System.out.println("If you encounter any issues, please submit them at:");
        System.out.println("https://github.com/leanderli/weather-service-aggregate-project/issues");
        
        System.out.println("\n" + "=".repeat(80));
        
        if (hasProjectName && hasProjectUrl && hasVersion) {
            System.out.println("✓ All requirements met!");
        } else {
            System.out.println("✗ Some requirements not met");
            System.exit(1);
        }
    }
}
