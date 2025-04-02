package dk.gundmann.routing.company;

import ai.timefold.solver.core.api.domain.entity.PlanningEntity;
import ai.timefold.solver.core.api.domain.lookup.PlanningId;
import ai.timefold.solver.core.api.domain.variable.PlanningListVariable;
import com.fasterxml.jackson.annotation.JsonFormat;
import dk.gundmann.routing.location.Location;
import dk.gundmann.routing.planning.Visit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @Schema(description = "How much spase in the vehicle", example = "20")
    private int capacity;

    @Embedded
    private Location homeLocation;

    @Schema(type = "string", pattern = "^\\d{2}:\\d{2}$", example = "08:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime departureTime;

    @PlanningListVariable
    @OneToMany
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private List<Visit> visits;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    public int getTotalDemand() {
        int totalDemand = 0;
        for (Visit visit : visits) {
            totalDemand += visit.getDemand();
        }
        return totalDemand;
    }

}
