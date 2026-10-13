package team2.race_service.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Schema(description = "Representation of a Formula 1 driver")
public class Driver {

    @NotNull(message = "Driver ID is required")
    @Schema(description = "Unique identifier from Driver Microservice", example = "1")
    private Long id;

    @NotBlank(message = "First name is required")
    @Schema(description = "Driver's first name", example = "Charles")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Schema(description = "Driver's last name", example = "Leclerc")
    private String lastName;

}