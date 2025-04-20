package dk.gundmann.routing.location;

import dk.gundmann.routing.visit.Address;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    GeocodingService geocodingService;

    @InjectMocks
    LocationService locationService;

    @Test
    void shouldFindLocation() {
        // given
        when(geocodingService.addressToLocation(anyString())).thenReturn(Location.builder().build());

        // when then
        assertNotNull(locationService.findLocation(Address.builder().build()));
    }

    @Test
    void shouldGetDistanceTimeFromGeocodingService() {
        // given
        when(geocodingService.distanceBetweenLocation(any(Location.class), any(Location.class))).thenReturn(1L);

        // when
        locationService.distanceBetweenLocation(Location.builder().build(), Location.builder().build());

        // then
        verify(geocodingService).distanceBetweenLocation(any(Location.class), any(Location.class));
    }

    @Test
    void shouldGetDistanceTimeFromCache() {
        // given
        var from = Location.builder()
                .latitude(1.0)
                .longitude(2.0)
                .build();

        var to = Location.builder()
                .latitude(3.0)
                .longitude(5.0)
                .build();

        when(geocodingService.distanceBetweenLocation(any(Location.class), any(Location.class))).thenReturn(2L);
        locationService.distanceBetweenLocation(from, to);

        // when
        locationService.distanceBetweenLocation(from, to);

        // then
        verify(geocodingService, times(1)).distanceBetweenLocation(any(Location.class), any(Location.class));
    }
}