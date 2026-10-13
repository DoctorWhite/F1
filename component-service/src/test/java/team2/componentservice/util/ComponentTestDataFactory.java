package team2.componentservice.util;

import team2.componentservice.api.ComponentViewDto;
import team2.componentservice.data.enums.ComponentType;

public final class ComponentTestDataFactory {

    public static final ComponentViewDto COMPONENT_VIEW = new ComponentViewDto(
            1L,
            "V6 Turbo",
            ComponentType.ENGINE,
            "2.1",
            95.0,
            90.0,
            "Powertrain"
    );

    private ComponentTestDataFactory() {
    }
}
