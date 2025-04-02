package dk.gundmann.routing.planning;

import ai.timefold.solver.core.api.domain.solution.PlanningEntityCollectionProperty;
import ai.timefold.solver.core.api.domain.solution.PlanningScore;
import ai.timefold.solver.core.api.domain.solution.PlanningSolution;
import ai.timefold.solver.core.api.domain.solution.ProblemFactCollectionProperty;
import ai.timefold.solver.core.api.domain.valuerange.ValueRangeProvider;
import ai.timefold.solver.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import ai.timefold.solver.core.api.solver.SolverStatus;
import dk.gundmann.routing.company.Vehicle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Getter
@PlanningSolution
@RequiredArgsConstructor
public class RoutePlan {

    @ProblemFactCollectionProperty
    @ValueRangeProvider
    private final Set<Vehicle> vehicles;

    @PlanningEntityCollectionProperty
    private final Set<Visit> visits;

    @PlanningScore
    private HardSoftLongScore score;

    private SolverStatus solverStatus;

}
