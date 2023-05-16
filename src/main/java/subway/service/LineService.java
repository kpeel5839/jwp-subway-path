package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.controller.dto.response.LineResponse;
import subway.repository.LineRepository;
import subway.repository.StationRepository;
import subway.service.converter.SectionConverter;
import subway.service.domain.Distance;
import subway.service.domain.Line;
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

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse save(final LineDto lineDto, final SectionCreateDto sectionCreateDto) { // 결국 Line 에서 Section 을 만들어주는 역할을 진행해야함.
        // 일단 Line 과 함께 Section 이 저장되어야 한다는 사실은 어떻게 보면 뻔하다.
        // 그러면 Section 을 저장할 때 Line 도 같이 넣어야하는 것일까?
        // lineRepository 가 존재하는 이유는 그것일 뿐일 수도 있다.
        // 그러면 Section 에 Station 으로 바꿔주는 역할을 LineRepository 의 역할일까?
        // 일단 Domain 을 만드는 행위를 구성해보자.
        Station previousStation = stationRepository.findByName(sectionCreateDto.getPreviousStation());
        Station nextStation = stationRepository.findByName(sectionCreateDto.getNextStation());
        Section section = new Section(
                lineDto.toDomain(),
                previousStation,
                nextStation,
                Distance.from(sectionCreateDto.getDistance())
        );

        return lineRepository.save(section);
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
