package team2.race_service.facade;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import team2.race_service.api.RaceResultViewDto;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.mapper.RaceResultMapper;
import team2.race_service.service.RaceResultService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceResultFacadeImpl implements RaceResultFacade {

    private final RaceResultService raceResultService;
    private final RaceResultMapper raceResultMapper;

    @Override
    public RaceResultViewDto findById(Long id) {
        RaceResult raceResultById = raceResultService.getRaceResultById(id);
        return raceResultMapper.toViewDto(raceResultById);
    }

    @Override
    public Page<RaceResultViewDto> findAll(Pageable pageable) {
        List<RaceResult> raceResults = raceResultService.getAllRaceResults();

        return convertEntitiesToPageableDtos(pageable, raceResults);
    }

    @Override
    public Page<RaceResultViewDto> findRaceResultsForFastestLapDriverId(Pageable pageable, Long driverId) {
        List<RaceResult> raceResults = raceResultService.getRaceResultsForFastestLapDriverId(driverId);

        return convertEntitiesToPageableDtos(pageable, raceResults);
    }

    private @NonNull Page<RaceResultViewDto> convertEntitiesToPageableDtos(Pageable pageable, List<RaceResult> raceResults) {
        List<RaceResultViewDto> dtos = raceResults.stream()
                .map(raceResultMapper::toViewDto)
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), dtos.size());

        return new PageImpl<>(dtos.subList(start, end), pageable, dtos.size());
    }
}
