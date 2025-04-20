package dk.gundmann.routing.visit;

import dk.gundmann.routing.location.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitService {

    private final VisitRepository visitRepository;
    private final LocationService locationService;

    public List<Visit> getAll() {
        return visitRepository.findAll();
    }

    public List<Visit> getRestedVisits() {
        return getAll().stream()
                .peek(Visit::reset)
                .toList();
    }

    public Visit add(Visit visit) {
        visit.getLocationAddress().setLocation(locationService.findLocation(visit.getLocationAddress()));
        visit.setDrivingTimeSeconds(locationService::distanceBetweenLocation);
        return visitRepository.save(visit);
    }

    public void delete(UUID id) {
        visitRepository.deleteById(id);
    }

    public void deleteAll() {
        visitRepository.deleteAll();
    }

    public void saveAll(List<Visit> vistis) {
        visitRepository.saveAll(vistis);
    }

    public Visit get(UUID id) {
        return visitRepository.findById(id).orElseThrow(() -> new RuntimeException("Not found"));
    }
}
