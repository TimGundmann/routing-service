package dk.gundmann.routing.visit;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.entity.PlanningPin;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.CascadingUpdateShadowVariable;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PreviousElementShadowVariable;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dk.gundmann.routing.location.Location;
import dk.gundmann.routing.location.RouteData;
import dk.gundmann.routing.vehicle.Vehicle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.function.BiFunction;

@Getter
@Setter
@Entity
@PlanningEntity
@EntityListeners(VisitCallback.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Visit {

    @PlanningId
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id = UUID.randomUUID();

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Lob
    private String geometry;

    private LocalDateTime minStartTime;

    private LocalDateTime maxEndTime;

    @Schema(type = "string", pattern = "^\\d{2}:\\d{2}$", example = "01:30")
    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration serviceDuration;

    @OneToOne(cascade = CascadeType.ALL)
    private Address locationAddress;

    @Schema(description = "How much spase do the parcel occupies", example = "2")
    private int demand;

    @PlanningPin
    @JsonIgnore
    private boolean pinned;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @InverseRelationShadowVariable(sourceVariableName = "visits")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIdentityReference(alwaysAsId = true)
    private Vehicle vehicle;

    @PreviousElementShadowVariable(sourceVariableName = "visits")
    @ManyToOne
    @JoinColumn(name = "previous_visit_id")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIdentityReference(alwaysAsId = true)
    private Visit previousVisit;

    @CascadingUpdateShadowVariable(targetMethodName = "updateArrivalTime")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime arrivalTime;

    @Transient
    @JsonIgnore
    private BiFunction<Location, Location, RouteData> drivingTimeSeconds;

    public boolean isServiceFinishedAfterMaxEndTime() {
        return arrivalTime != null
                && arrivalTime.plus(serviceDuration).isAfter(maxEndTime);
    }

    @JsonIgnore
    public long getServiceFinishedDelayInMinutes() {
        if (arrivalTime == null) {
            return 0;
        }
        return roundDurationToNextOrEqualMinutes(Duration.between(maxEndTime, arrivalTime.plus(serviceDuration)));
    }

    private static long roundDurationToNextOrEqualMinutes(Duration duration) {
        var remainder = duration.minus(duration.truncatedTo(ChronoUnit.MINUTES));
        var minutes = duration.toMinutes();
        if (remainder.equals(Duration.ZERO)) {
            return minutes;
        }
        return minutes + 1;
    }

    private void updateArrivalTime() {
        if (previousVisit == null && vehicle == null) {
            arrivalTime = null;
            return;
        }
        LocalTime departureTime = previousVisit == null ? vehicle.getDepartureTime() : previousVisit.getDepartureTime().toLocalTime();
        if (departureTime == null) {
            arrivalTime = null;
        } else {
            arrivalTime = minStartTime.with(departureTime.plusSeconds(getDrivingTimeSecondsFromPreviousStandstill()));
        }
    }

    @JsonIgnore
    public long getDrivingTimeSecondsFromPreviousStandstill() {
        if (vehicle == null) {
            throw new IllegalStateException(
                    "This method must not be called when the shadow variables are not initialized yet.");
        }
        RouteData routeData;

        if (previousVisit == null) {
            routeData = drivingTimeSeconds.apply(vehicle.getHomeAddress().getLocation(), getLocationAddress().getLocation());
        } else {
            routeData = drivingTimeSeconds.apply(previousVisit.getLocationAddress().getLocation(), getLocationAddress().getLocation());
        }
        geometry = routeData.geometry();
        return routeData.duration();
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public LocalDateTime getDepartureTime() {
        if (arrivalTime == null) {
            return null;
        }
        return getStartServiceTime().plus(serviceDuration);
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public LocalDateTime getStartServiceTime() {
        if (arrivalTime == null) {
            return null;
        }
        return arrivalTime.isBefore(minStartTime) ? minStartTime : arrivalTime;
    }

    public void reset() {
        arrivalTime = null;
        pinned = false;
        previousVisit = null;
        vehicle = null;
        geometry = null;
    }

}
