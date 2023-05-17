package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.controller.dto.request.StationRequest;
import subway.controller.dto.response.StationResponse;
import subway.repository.StationRepository;
import subway.service.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public StationResponse saveStation(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        Station stationResponse = stationRepository.save(station);
        return StationResponse.from(stationResponse);
    }

    public StationResponse findStationResponseById(Long id) {
        Station station = stationRepository.findById(id);
        return StationResponse.from(station);
    }

    public List<StationResponse> findAllStationResponses() {
        List<Station> stationEntities = stationRepository.findAll();

        return stationEntities.stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
    }

    public void updateStation(Long id, StationRequest stationRequest) {
        int updateCount = stationRepository.update(new Station(id, stationRequest.getName()));

        if (updateCount == 0) {
            throw new IllegalArgumentException("역이 수정되지 않았습니다.");
        }
    }

    public void deleteStationById(Long id) {
        int removeCount = stationRepository.deleteById(id);

        if (removeCount == 0) {
            throw new IllegalArgumentException("역이 삭제되지 않았습니다.");
        }
    }

}
