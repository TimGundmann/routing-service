package dk.gundmann.routing.planning;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/planning")
@RequiredArgsConstructor
public class RouteController {

    private final PlanningService planningService;

    @GetMapping("/visits")
    public List<Visit> getVisits() {
        return planningService.getVisits();
    }

    @PutMapping("/visits")
    public Visit addVisit(@RequestBody Visit visit) {
        return planningService.addVisit(visit);
    }

    @DeleteMapping("/visits")
    public void deleteVisit(@PathVariable UUID id) {
        planningService.deleteVisit(id);
    }

}
