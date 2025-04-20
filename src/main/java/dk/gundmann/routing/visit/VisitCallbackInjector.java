package dk.gundmann.routing.visit;

import dk.gundmann.routing.location.LocationService;
import org.springframework.stereotype.Component;

@Component
public class VisitCallbackInjector {

    public VisitCallbackInjector(LocationService locationService) {
        VisitCallback.setDrivingTimeSeconds(locationService::distanceBetweenLocation);
    }

}
