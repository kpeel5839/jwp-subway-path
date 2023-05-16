package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.StationDao;
import subway.controller.dto.request.StationRequest;
import subway.controller.dto.response.StationResponse;
import subway.entity.StationEntity;
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
        Station station = new Station(stationRequest.getName()); // Domain 생성
        Station stationResponse = stationRepository.save(station);
        return StationResponse.of(stationResponse);
    }

    public StationResponse findStationResponseById(Long id) {
        Station station = stationRepository.findById(id);
        return StationResponse.of(station);
    }

    public List<StationResponse> findAllStationResponses() {
        List<Station> stationEntities = stationRepository.findAll();

        return stationEntities.stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
    }

    public void updateStation(Long id, StationRequest stationRequest) {
        int updateCount = stationRepository.update(new Station(id, stationRequest.getName()));

        if (updateCount == 0) {
            throw new IllegalArgumentException("Station 이 수정되지 않았습니다.");
        } // 일관성을 유지하기 위해 이름을 출력하지 않음
    }

    public void deleteStationById(Long id) { // 이 경우 조금 애매함
        int removeCount = stationRepository.deleteById(id);

        if (removeCount == 0) {
            throw new IllegalArgumentException("Station 이 삭제되지 않았습니다.");
        }
    }

}
