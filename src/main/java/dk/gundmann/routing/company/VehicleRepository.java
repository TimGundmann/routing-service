package dk.gundmann.routing.company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface VehicleRepository extends JpaRepository<Vehicle, UUID> {


}
