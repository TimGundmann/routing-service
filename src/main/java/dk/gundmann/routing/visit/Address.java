package dk.gundmann.routing.visit;

import dk.gundmann.routing.location.Location;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    @Id
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    @Builder.Default
    private UUID id = UUID.randomUUID();

    @NotNull
    @Schema(example = "Ålekistevej 207")
    private String addressLine1;
    private String addressLine2;
    @Schema(example = "Vanløse")
    private String city;
    private String subdivision;
    @NotNull
    @Schema(example = "2720")
    private String postalCode;
    @Schema(example = "DK")
    @Builder.Default
    private Country country = Country.DK;

    @Embedded
    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Location location;

    public String toSerachString() {
        return addressLine1 + "," + city + "," + postalCode + "," + country.getCountryName();
    }
}
