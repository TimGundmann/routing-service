package dk.gundmann.routing.company;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

    private final VehicleService vehicleService;

    @GetMapping("/vehicles")
    public List<Vehicle> getVehicles() {
        return vehicleService.getVehicles();
    }

    @PutMapping("/vehicles")
    public void addVehicle(@RequestBody Vehicle vehicle) {
        vehicleService.addVehicle(vehicle);
    }

    @DeleteMapping("/vehicles")
    public void deleteVehicle(@PathVariable UUID id) {
        vehicleService.deleteVehicle(id);
    }

    @DeleteMapping(path = "/vehicles/deleteAll")
    public void deleteAllVehicles() {
        vehicleService.deleteAllVehicles();
    }

}
