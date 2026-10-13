package team2.componentservice.data.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Type of car component")
public enum ComponentType {
    ENGINE,
    SUSPENSION,
    AERO,
    GEARBOX,
    ELECTRONICS
}
