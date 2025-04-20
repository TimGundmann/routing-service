package dk.gundmann.routing.location;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Service
class GeocodingService {

    public Location addressToLocation(String address) {
        String url = "https://nominatim.openstreetmap.org/search?q=" +
                URLEncoder.encode(address, StandardCharsets.UTF_8) + "&format=json&limit=1";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "routing-service/1.0") // Nominatim requires this
                .build();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JSONArray results = new JSONArray(response.body());
            if (results.isEmpty()) {
                throw new RuntimeException("No results found for: " + address);
            }

            JSONObject location = results.getJSONObject(0);

            return Location.builder()
                    .latitude(Double.parseDouble(location.getString("lat")))
                    .longitude(Double.parseDouble(location.getString("lon")))
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public RouteData distanceBetweenLocation(Location from, Location to) {
        WebClient client = WebClient.create("http://router.project-osrm.org");

        String response = client.get()
                .uri("/route/v1/driving/%s;%s?overview=full&geometries=geojson".formatted(from, to))
                .retrieve()
                .bodyToMono(String.class)
                .block();

        System.out.println(response);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            var data = root.path("routes").get(0);
            return new RouteData((long) data.path("duration").asDouble(), data.path("geometry").toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
