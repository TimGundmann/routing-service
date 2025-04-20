package dk.gundmann.routing.visit;

import dk.gundmann.routing.vehicle.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
class VisitController {

    private final VisitService visitService;
    private final VehicleService vehicleService;

    @GetMapping
    public List<Visit> getVisits() {
        return visitService.getAll();
    }

    @GetMapping("/{id}")
    public Visit getVisit(@PathVariable UUID id) {
        return visitService.get(id);
    }

    @PutMapping
    public Visit addVisit(@RequestBody Visit visit) {
        return visitService.add(visit);
    }

    @DeleteMapping("/{id}")
    public void deleteVisit(@PathVariable UUID id) {
        vehicleService.saveAll(vehicleService.getResetedVehicles());
        visitService.saveAll(visitService.getRestedVisits());
        visitService.delete(id);
    }

    @DeleteMapping("/deleteAll")
    public void deleteAllVisit() {
        visitService.deleteAll();
    }
}
