package team2.race_service.data.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "Weather conditions affecting race performance and strategy")
public enum Weather {

    @Schema(description = "Dry track, high temperatures")
    SUNNY("Dry tires recommended"),

    @Schema(description = "Moderate temperatures, lower grip than sunny")
    CLOUDY("Monitor track temperature"),

    @Schema(description = "Wet track, requires inters or full wets")
    RAINY("High risk of accidents");

    private final String strategyNote;
}