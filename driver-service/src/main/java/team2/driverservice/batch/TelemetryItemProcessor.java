package team2.driverservice.batch;

import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;
import team2.driverservice.model.DriverTelemetry;

@Component
public class TelemetryItemProcessor implements ItemProcessor<DriverTelemetry, DriverTelemetry> {

    @Override
    public DriverTelemetry process(DriverTelemetry item) {
        item.setProcessed(true);
        return item;
    }
}
