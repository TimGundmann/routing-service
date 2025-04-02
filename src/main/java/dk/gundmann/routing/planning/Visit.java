package dk.gundmann.routing.planning;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.entity.PlanningPin;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.CascadingUpdateShadowVariable;
import ai.timefold.solver.core.api.domain.variable.InverseRelationShadowVariable;
import ai.timefold.solver.core.api.domain.variable.PlanningVariable;
import ai.timefold.solver.core.api.domain.variable.PreviousElementShadowVariable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dk.gundmann.routing.company.Vehicle;
import dk.gundmann.routing.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Getter
@Setter
@Entity
@PlanningEntity
public class Visit {

    @PlanningId
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Id
    private UUID id = UUID.randomUUID();

    private LocalDateTime minStartTime;

    private LocalDateTime maxEndTime;

    @Schema(type = "string", pattern = "^\\d{2}:\\d{2}$", example = "01:30")
    @JsonSerialize(using = DurationSerializer.class)
    @JsonDeserialize(using = DurationDeserializer.class)
    private Duration serviceDuration;

    @Embedded
    @Schema(description = "Pickup location")
    private Location location;

    @Schema(description = "How much spase do the parcel occupies", example = "2")
    private int demand;

    @PlanningPin
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private boolean pinned;

    @PlanningVariable
    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @InverseRelationShadowVariable(sourceVariableName = "visits")
    @JsonIgnore
    private Vehicle vehicle;

    @PreviousElementShadowVariable(sourceVariableName = "visits")
    @ManyToOne
    @JoinColumn(name = "previous_vehicle_id")
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIgnore
    private Visit previousVisit;

    @CascadingUpdateShadowVariable(targetMethodName = "updateArrivalTime")
    private LocalDateTime arrivalTime;

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

}
