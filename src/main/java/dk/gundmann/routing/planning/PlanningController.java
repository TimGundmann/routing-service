package dk.gundmann.routing.planning;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/planning")
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningService planningService;

    @PostMapping
    public UUID solv() {
        return planningService.solve();
    }

    @DeleteMapping(path = "/{jobId}")
    public RoutePlan terminateSolving(@PathVariable UUID jobId) {
        return planningService.terminate(jobId);
    }

}
