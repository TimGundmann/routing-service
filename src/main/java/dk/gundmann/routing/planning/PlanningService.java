package dk.gundmann.routing.planning;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlanningService {

//    private final SolverManager<RoutePlan, String> solverManager;
//    private final SolutionManager<RoutePlan, HardSoftScore> solutionManager;
    private final VisitRepository visitRepository;

    public List<Visit> getVisits() {
        return visitRepository.findAll();
    }

    public Visit addVisit(Visit visit) {
        return visitRepository.save(visit);
    }

    public void deleteVisit(UUID id) {
        visitRepository.deleteById(id);
    }

    public String solve(RoutePlan plan) {
        String jobId = UUID.randomUUID().toString();
/*
        solverManager.solveBuilder()
                .withProblemId(jobId)
                .withProblemFinder(id -> jobIdToJob.get(jobId).schedule)
                .withBestSolutionConsumer(solution -> jobIdToJob.put(jobId, Job.ofSchedule(solution)))
                .withExceptionHandler((id, exception) -> {
                    jobIdToJob.put(id, Job.ofException(exception));
                    LOGGER.error("Failed solving jobId ({}).", id, exception);
                })
                .run();
        cleanJobs();
*/
        return jobId;
    }


}
