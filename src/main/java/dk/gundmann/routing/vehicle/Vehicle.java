package dk.gundmann.routing.vehicle;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.gundmann.routing.location.Location;
import dk.gundmann.routing.visit.Address;
import dk.gundmann.routing.visit.Visit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@PlanningEntity
public class Vehicle {

    @EqualsAndHashCode.Include
    @PlanningId
    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private UUID id = UUID.randomUUID();

    @Schema(example = "Lorry 1")
    private String name;

    @Schema(example = "#FF0000")
    private String routeColor;

    @Schema(description = "How much spase in the vehicle", example = "20")
    private int capacity;

    @OneToOne(cascade = CascadeType.ALL)
    private Address homeAddress;

    @Schema(type = "string", pattern = "^\\d{2}:\\d{2}$", example = "08:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;

    @PlanningListVariable
    @OneToMany(fetch = FetchType.EAGER)
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @JsonIdentityReference(alwaysAsId = true)
    private List<Visit> visits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    public int getTotalDemand() {
        int totalDemand = 0;
        for (Visit visit : visits) {
            totalDemand += visit.getDemand();
        }
        return totalDemand;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public long getTotalDrivingTimeSeconds() {
        if (visits.isEmpty()) {
            return 0;
        }

        long totalDrivingTime = 0;
        Location previousLocation = homeAddress.getLocation();

        for (Visit visit : visits) {
            totalDrivingTime += visit.getDrivingTimeSeconds().apply(previousLocation, visit.getLocationAddress().getLocation()).duration();
            previousLocation = visit.getLocationAddress().getLocation();
        }
        totalDrivingTime += visits.getFirst().getDrivingTimeSeconds().apply(previousLocation, homeAddress.getLocation()).duration();

        return totalDrivingTime;
    }

    public void reset() {
        visits.clear();
    }

}
