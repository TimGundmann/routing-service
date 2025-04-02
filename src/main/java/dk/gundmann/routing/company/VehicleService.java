package dk.gundmann.routing.company;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public List<Vehicle> getVehicles() {
        return vehicleRepository.findAll();
    }


    public void addVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(UUID id) {
        vehicleRepository.deleteById(id);
    }

    public void deleteAllVehicles() {
        vehicleRepository.deleteAll();
    }
}
