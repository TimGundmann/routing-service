package dk.gundmann.routing.location;

import dk.gundmann.routing.visit.Address;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final Map<Location, Map<Location, RouteData>> drivingTimeSeconds = new HashMap<>();

    private final GeocodingService geocodingService;

    public Location findLocation(Address address) {
        return geocodingService.addressToLocation(address.toSerachString());
    }

    public RouteData distanceBetweenLocation(Location from, Location to) {
        return drivingTimeSeconds.computeIfAbsent(from, k -> new HashMap<>())
                .computeIfAbsent(to, k -> geocodingService.distanceBetweenLocation(from, to));
    }

}
