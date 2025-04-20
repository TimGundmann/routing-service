package dk.gundmann.routing.visit;

import dk.gundmann.routing.location.Location;
import dk.gundmann.routing.location.RouteData;
import jakarta.persistence.PostLoad;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.BiFunction;

public class VisitCallback {

    @Setter
    @Autowired
    private static BiFunction<Location, Location, RouteData> drivingTimeSeconds;

    @PostLoad
    public void onLoad(Visit entity) {
        entity.setDrivingTimeSeconds(drivingTimeSeconds);
    }

}
