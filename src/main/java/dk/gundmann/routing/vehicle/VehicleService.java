package dk.gundmann.routing.vehicle;

import dk.gundmann.routing.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final LocationService locationService;

    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getResetedVehicles() {
        return vehicleRepository.findAll().stream()
                .peek(Vehicle::reset)
                .toList();
    }

    public void add(Vehicle vehicle) {
        vehicle.getHomeAddress().setLocation(locationService.findLocation(vehicle.getHomeAddress()));
        vehicleRepository.save(vehicle);
    }

    public void saveAll(List<Vehicle> vehicles) {
        vehicleRepository.saveAll(vehicles);
    }

    public void delete(UUID id) {
        vehicleRepository.deleteById(id);
    }

    public void deleteAll() {
        vehicleRepository.deleteAll();
    }

    public Vehicle get(UUID id) {
        return vehicleRepository.findById(id).orElseThrow();
    }
}
