package team2.race_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.race_service.data.entity.RaceResult;
import team2.race_service.data.repository.RaceResultRepository;
import team2.race_service.exception.RaceResultNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RaceResultServiceImpl implements RaceResultService {

    private final RaceResultRepository raceResultRepository;

    @Override
    public RaceResult getRaceResultById(Long id) {
        return raceResultRepository.findById(id)
                .orElseThrow(() -> new RaceResultNotFoundException("Race result not found with id: " + id));
    }

    @Override
    public List<RaceResult> getAllRaceResults() {
        return raceResultRepository.findAll();
    }

    @Override
    public List<RaceResult> getRaceResultsForFastestLapDriverId(Long driverId) {
        return raceResultRepository.findByFastestLapDriverId(driverId);
    }
}
