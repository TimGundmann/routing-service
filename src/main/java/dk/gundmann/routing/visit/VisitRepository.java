package dk.gundmann.routing.visit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VisitRepository extends JpaRepository<Visit, UUID> {


}
