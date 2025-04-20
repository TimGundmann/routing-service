package dk.gundmann.routing.vehicle;

import dk.gundmann.routing.visit.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private final VisitService visitService;

    @GetMapping()
    public List<Vehicle> getVehicles() {
        return vehicleService.getAll();
    }

    @GetMapping("/{id}")
    public Vehicle getVehicle(@PathVariable UUID id) {
        return vehicleService.get(id);
    }

    @PutMapping()
    public void addVehicle(@RequestBody Vehicle vehicle) {
        vehicleService.add(vehicle);
    }

    @DeleteMapping("/{id}")
    public void deleteVehicle(@PathVariable UUID id) {
        vehicleService.delete(id);
    }

    @DeleteMapping(path = "/deleteAll")
    public void deleteAllVehicles() {
        vehicleService.saveAll(vehicleService.getResetedVehicles());
        visitService.saveAll(visitService.getRestedVisits());
        vehicleService.deleteAll();
    }

}
