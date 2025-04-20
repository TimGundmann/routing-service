package dk.gundmann.routing.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface VehicleRepository extends JpaRepository<Vehicle, UUID> {


}
