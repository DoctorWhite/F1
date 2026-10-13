package team2.driverservice.mapper;

import org.springframework.stereotype.Component;
import team2.driverservice.dto.DriverCreateRequestDto;
import team2.driverservice.dto.DriverRankingEntryDto;
import team2.driverservice.dto.DriverResponseDto;
import team2.driverservice.dto.DriverUpdateRequestDto;
import team2.driverservice.model.Driver;
import team2.driverservice.model.RankedDriver;

@Component
public class DriverMapper {

    public Driver toDriver(DriverCreateRequestDto request) {
        return Driver.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .nationality(request.nationality())
                .role(request.role())
                .wetSkill(request.wetSkill())
                .aggressiveness(request.aggressiveness())
                .consistency(request.consistency())
                .feedbackQuality(request.feedbackQuality())
                .build();
    }

    public Driver toDriver(Long id, DriverUpdateRequestDto request) {
        return Driver.builder()
                .id(id)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .nationality(request.nationality())
                .role(request.role())
                .wetSkill(request.wetSkill())
                .aggressiveness(request.aggressiveness())
                .consistency(request.consistency())
                .feedbackQuality(request.feedbackQuality())
                .build();
    }

    public DriverResponseDto toResponseDto(Driver driver) {
        return new DriverResponseDto(
                driver.getId(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getNationality(),
                driver.getRole(),
                driver.getWetSkill(),
                driver.getAggressiveness(),
                driver.getConsistency(),
                driver.getFeedbackQuality()
        );
    }

    public DriverRankingEntryDto toRankingEntry(RankedDriver rankedDriver, int position) {
        return new DriverRankingEntryDto(
                this.toResponseDto(rankedDriver.driver()),
                rankedDriver.score(),
                position
        );
    }
}
