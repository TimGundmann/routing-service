package dk.gundmann.routing.planning;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import ai.timefold.solver.core.api.solver.SolverStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.gundmann.routing.vehicle.Vehicle;
import dk.gundmann.routing.visit.Visit;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@PlanningSolution
public class RoutePlan {

    @PlanningEntityCollectionProperty
    private List<Vehicle> vehicles;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider
    private List<Visit> visits;

    @PlanningScore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private HardSoftLongScore score;

    @Setter
    private SolverStatus solverStatus;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Setter
    private String scoreExplanation;

    @Builder
    public RoutePlan(List<Vehicle> vehicles, List<Visit> visits) {
        this.vehicles = vehicles;
        this.visits = visits;
    }

    public RoutePlan() {
    }
}
