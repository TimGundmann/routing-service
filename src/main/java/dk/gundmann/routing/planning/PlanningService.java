package dk.gundmann.routing.planning;

import ai.timefold.solver.core.api.score.buildin.hardsoft.HardSoftScore;
import ai.timefold.solver.core.api.solver.SolutionManager;
import ai.timefold.solver.core.api.solver.SolverManager;
import ai.timefold.solver.core.api.solver.SolverStatus;
import dk.gundmann.routing.vehicle.VehicleService;
import dk.gundmann.routing.visit.VisitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlanningService {

    private final SolverManager<RoutePlan, String> solverManager;
    private final SolutionManager<RoutePlan, HardSoftScore> solutionManager;
    private final VisitService visitService;
    private final VehicleService vehicleService;

    private final ConcurrentMap<UUID, Job> jobIdToJob = new ConcurrentHashMap<>();

    public UUID solve() {
        RoutePlan plan = RoutePlan.builder()
                .vehicles(vehicleService.getResetedVehicles())
                .visits(visitService.getRestedVisits())
                .build();

        UUID jobId = UUID.randomUUID();
        jobIdToJob.put(jobId, Job.ofRoutePlan(plan));

        solverManager.solveBuilder()
                .withProblemId(jobId.toString())
                .withProblemFinder(id -> jobIdToJob.get(jobId).routePlan)
                .withBestSolutionConsumer(solution -> {
                    jobIdToJob.put(jobId, Job.ofRoutePlan(solution));
                    log.info("Best solution");
                })
                .withExceptionHandler((id, exception) -> {
                    jobIdToJob.put(UUID.fromString(id), Job.ofException(exception));
                    log.error("Failed solving jobId ({}).", id, exception);
                })
                .run();

        return jobId;
    }

    public RoutePlan terminate(UUID jobId) {
        solverManager.terminateEarly(jobId.toString());
        return getRoutePlan(jobId);
    }

    public RoutePlan getRoutePlan(UUID jobId) {
        RoutePlan routePlan = getRoutePlanAndCheckForExceptions(jobId);
        SolverStatus solverStatus = solverManager.getSolverStatus(jobId.toString());
        String scoreExplanation = solutionManager.explain(routePlan).getSummary();
        routePlan.setSolverStatus(solverStatus);
        routePlan.setScoreExplanation(scoreExplanation);
        visitService.saveAll(routePlan.getVisits());
        vehicleService.saveAll(routePlan.getVehicles());
        return routePlan;
    }

    private RoutePlan getRoutePlanAndCheckForExceptions(UUID jobId) {
        Job job = jobIdToJob.get(jobId);
        if (job == null) {
            throw new RuntimeException("No route plan found.");
        }
        if (job.exception != null) {
            throw new RuntimeException(job.exception);
        }
        return job.routePlan;
    }

    private record Job(RoutePlan routePlan, Throwable exception) {

        static Job ofRoutePlan(RoutePlan routePlan) {
            return new Job(routePlan, null);
        }

        static Job ofException(Throwable exception) {
            return new Job(null, exception);
        }

    }

}
