package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.service.converter.SectionConverter;
import subway.service.domain.Section;
import subway.service.domain.Station;
import subway.service.domain.Stations;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.dao.rowmapper.SectionDetail;
import subway.entity.SectionEntity;
import subway.service.converter.LineConverter;
import subway.service.dto.LineDto;
import subway.service.dto.SectionCreateDto;
import subway.entity.StationEntity;
import subway.controller.dto.request.LineRequest;
import subway.entity.LineEntity;
import subway.controller.dto.response.SingleLineDetailResponse;
import subway.service.domain.Direction;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class LineService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public LineService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    @Transactional
    public long save(final LineDto lineDto, final SectionCreateDto sectionCreateDto) {
        final Long lineId = lineDao.insert(LineConverter.toEntity(lineDto));
        final StationEntity previousStation = stationDao.findByName(sectionCreateDto.getPreviousStation());
        final StationEntity nextStation = stationDao.findByName(sectionCreateDto.getNextStation());
        sectionDao.insert(new SectionEntity.Builder()
                .lineId(lineId)
                .distance(sectionCreateDto.getDistance())
                .previousStationId(previousStation.getId())
                .nextStationId(nextStation.getId())
                .build());
        return lineId;
    }

    public List<SingleLineDetailResponse> getAllLine() {
        return sectionDao.findSectionDetail().stream()
                .collect(Collectors.groupingBy(SectionDetail::getLineId))
                .values().stream()
                .map(this::convert)
                .collect(Collectors.toUnmodifiableList());
    }

    public SingleLineDetailResponse getLine(final Long lineId) {
        return convert(sectionDao.findSectionDetailByLineId(lineId));
    }

    // TODO: 리팩토링
    private SingleLineDetailResponse convert(final List<SectionDetail> sectionDetails) {
        if (sectionDetails.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
        }

        List<Section> sections = SectionConverter.queryResultToDomains(sectionDetails);

        Map<Station, List<Object[]>> map = new HashMap<>();

        for (Section section : sections) {
            map.put(section.getPreviousStation(), new ArrayList<>());
            map.put(section.getNextStation(), new ArrayList<>());
        }

        for (Section section : sections) {
            map.get(section.getPreviousStation()).add(new Object[] {Direction.UP, section.getNextStation(), section.getDistance()});
            map.get(section.getNextStation()).add(new Object[] {Direction.DOWN, section.getPreviousStation(), section.getDistance()});
        }

        Deque<Station> deque = new LinkedList<>();
        Set<Station> visited = new HashSet<>();
        moveStation(sections.get(0).getPreviousStation(), deque, map, visited, Direction.UP);

        return new SingleLineDetailResponse(sectionDetails.get(0).getLineId(), sections.get(0).getLine().getName(), sections.get(0).getLine().getColor(), new Stations(new ArrayList<>(deque)));
    }

    public void moveStation(Station station, Deque<Station> deque, Map<Station, List<Object[]>> map, Set<Station> visited, Direction direction) {
        visited.add(station);

        getDirection(station, deque, direction);

        for (Object[] object: map.get(station)) {
            moveNextStation(deque, map, visited, object);
        }
    }

    private void getDirection(final Station station, final Deque<Station> deque, final Direction direction) {
        if (direction == Direction.UP) {
            deque.addLast(station);
            return;
        }
        deque.addFirst(station);
    }

    private void moveNextStation(final Deque<Station> deque, final Map<Station, List<Object[]>> map, final Set<Station> visited, final Object[] object) {
        if (visited.contains(object[1])) {
            return;
        }

        moveStation((Station) object[1], deque, map, visited, (Direction) object[0]);
    }

    public void updateLine(Long id, LineRequest lineUpdateRequest) {
        lineDao.update(new LineEntity(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
    }

    public void deleteLineById(Long id) {
        lineDao.deleteById(id);
    }

}