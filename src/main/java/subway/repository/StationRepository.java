package subway.repository;

import org.springframework.stereotype.Repository;
import subway.dao.StationDao;
import subway.entity.StationEntity;
import subway.service.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class StationRepository {

    private final StationDao stationDao;

    public StationRepository(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public Station save(Station station) {
        StationEntity stationEntity = new StationEntity(station.getName());
        StationEntity stationEntityHasId = stationDao.insert(stationEntity);
        return new Station(stationEntityHasId.getId(), station.getName());
    }

    public Station findById(Long id) {
        StationEntity stationEntity = stationDao.findById(id);
        return new Station(stationEntity.getId(), stationEntity.getName());
    }

    public Station findByName(String name) {
        StationEntity stationEntity = stationDao.findByName(name);
        return new Station(stationEntity.getId(), stationEntity.getName());
    }

    public List<Station> findAll() { // Entity -> Station 의 역할을 어디가 맡아놓으면 좋을텐데. 그것이 Converter 의 역할일까?
        List<StationEntity> stationEntities = stationDao.findAll();

        return stationEntities.stream()
                .map(stationEntity -> new Station(stationEntity.getId(), stationEntity.getName()))
                .collect(Collectors.toList());
    }

    public int update(Station station) { // 이것은 없는 ID? 굳이 Domain 을 넘겨줄 필요가 있을까? update 가 안됐는데? 절대 없음
        StationEntity stationEntity = new StationEntity(station.getId(), station.getName());
        return stationDao.update(stationEntity);
    }

    public int deleteById(Long id) { // 이것은 없는 ID?
        return stationDao.deleteById(id);
    }

}
