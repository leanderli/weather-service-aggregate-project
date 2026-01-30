# Weather Service Aggregate Project

这是一个用于接收API调用方反馈的项目，当前主要使用了OSM API，并在 user-agent 中标明了项目地址，如有问题可以在该项目下提交 issue。

This is a project for receiving feedback from API callers. It currently mainly uses OSM (OpenStreetMap) API and marks the project address in the user-agent header. If there are any issues, you can submit an issue under this project.

## Features

- Integration with OpenStreetMap (OSM) Nominatim API for geocoding services
- Proper user-agent headers including project URL for API feedback
- Location search functionality
- Reverse geocoding support
- Built with Java and Maven

## User-Agent Header

As per OpenStreetMap API usage policy, this project includes the project URL in the user-agent header:

```
WeatherServiceAggregateProject/1.0 (https://github.com/leanderli/weather-service-aggregate-project)
```

This allows the OSM API maintainers to contact us if there are any issues with our API usage.

## Feedback and Issues

If you encounter any issues with this project or have questions about the API usage:

1. Visit our GitHub Issues page: https://github.com/leanderli/weather-service-aggregate-project/issues
2. Create a new issue describing the problem
3. Include relevant details such as error messages, logs, or API responses

## Building the Project

This project uses Maven for dependency management and building.

### Prerequisites

- Java 11 or higher
- Maven 3.6 or higher

### Build Commands

```bash
# Compile the project
mvn clean compile

# Run tests
mvn test

# Package the application
mvn clean package

# Run the example application
mvn exec:java -Dexec.mainClass="com.leanderli.weather.WeatherServiceApp"
```

## Usage Example

```java
import com.leanderli.weather.OsmApiClient;

public class Example {
    public static void main(String[] args) throws Exception {
        try (OsmApiClient osmClient = new OsmApiClient()) {
            // Search for a location
            OsmApiClient.LocationResult result = osmClient.searchLocation("Beijing, China");
            
            if (result.isFound()) {
                System.out.println("Location: " + result.getDisplayName());
                System.out.println("Latitude: " + result.getLatitude());
                System.out.println("Longitude: " + result.getLongitude());
            }
            
            // IMPORTANT: Wait at least 1 second between requests to comply with OSM usage policy
            Thread.sleep(1000);
            
            // Make another request...
        }
    }
}
```

## Important: Rate Limiting

**The OSM Nominatim API has a usage policy that requires a maximum of 1 request per second.**

When using this client, you MUST:
- Add delays of at least 1 second between consecutive API calls
- Implement proper rate limiting in production applications
- Consider caching results to minimize API calls
- For high-volume usage, set up your own Nominatim instance

Example of proper rate limiting:
```java
osmClient.searchLocation("Location 1");
Thread.sleep(1000); // Wait 1 second
osmClient.searchLocation("Location 2");
```

## API Usage Policy

This project follows the OpenStreetMap Nominatim usage policy:
- https://operations.osmfoundation.org/policies/nominatim/

Key points:
- We include a valid user-agent header with contact information
- We respect rate limits
- We cache results when appropriate
- For high-volume usage, consider setting up your own Nominatim instance

## License

This project is for educational and demonstration purposes.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.