package dk.gundmann.routing.location;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class Location {

    @Schema(description = "North south point", example = "55.6761")
    private double latitude;

    @Schema(description = "East west point", example = "12.5683")
    private double longitude;

}
