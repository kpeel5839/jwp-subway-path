package subway.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.controller.dto.response.LineResponse;
import subway.exception.LineDuplicateException;
import subway.repository.LineRepository;
import subway.repository.StationRepository;
import subway.service.domain.Distance;
import subway.service.domain.Line;
import subway.service.domain.LineProperty;
import subway.service.domain.Section;
import subway.service.domain.Station;
import subway.service.dto.LineDto;
import subway.service.dto.SectionCreateDto;

@Service
@Transactional
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;


    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(final LineDto lineDto, final SectionCreateDto sectionCreateDto) { // 결국 Line 에서 Section 을 만들어주는 역할을 진행해야함.
        if (lineRepository.existsByName(lineDto.getName())) {
            throw new LineDuplicateException(lineDto.getName() + "은 이미 존재하는 노선입니다.");
        }

        LineProperty lineProperty = new LineProperty(lineDto.getName(), lineDto.getColor());
        Station previousStation = stationRepository.findByName(sectionCreateDto.getPreviousStation());
        Station nextStation = stationRepository.findByName(sectionCreateDto.getNextStation());
        Line line = new Line(
                lineProperty,
                new Section(
                        previousStation,
                        nextStation,
                        Distance.from(sectionCreateDto.getDistance())
                )
        );
        return LineResponse.from(lineRepository.save(line));
    }
//
//    public List<SingleLineDetailResponse> getAllLine() {
//        return sectionDao.findSectionDetail().stream()
//                .collect(Collectors.groupingBy(SectionDetail::getLineId))
//                .values().stream()
//                .map(this::convert)
//                .collect(Collectors.toUnmodifiableList());
//    }
//
//    public SingleLineDetailResponse getLine(final Long lineId) {
//        return convert(sectionDao.findSectionDetailByLineId(lineId));
//    }
//
//    // TODO: 리팩토링
//    private SingleLineDetailResponse convert(final List<SectionDetail> sectionDetails) {
//        if (sectionDetails.isEmpty()) {
//            throw new IllegalArgumentException("존재하지 않는 노선입니다.");
//        }
//
//        List<Section> sections = SectionConverter.queryResultToDomains(sectionDetails);
//
//        Map<Station, List<Object[]>> map = new HashMap<>();
//
//        for (Section section : sections) {
//            map.put(section.getPreviousStation(), new ArrayList<>());
//            map.put(section.getNextStation(), new ArrayList<>());
//        }
//
//        for (Section section : sections) {
//            map.get(section.getPreviousStation()).add(new Object[] {Direction.UP, section.getNextStation(), section.getDistance()});
//            map.get(section.getNextStation()).add(new Object[] {Direction.DOWN, section.getPreviousStation(), section.getDistance()});
//        }
//
//        Deque<Station> deque = new LinkedList<>();
//        Set<Station> visited = new HashSet<>();
//        moveStation(sections.get(0).getPreviousStation(), deque, map, visited, Direction.UP);
//
//        return new SingleLineDetailResponse(sectionDetails.get(0).getLineId(), sections.get(0).getLine().getName(), sections.get(0).getLine().getColor(), new Stations(new ArrayList<>(deque)));
//    }
//
//    public void moveStation(Station station, Deque<Station> deque, Map<Station, List<Object[]>> map, Set<Station> visited, Direction direction) {
//        visited.add(station);
//
//        getDirection(station, deque, direction);
//
//        for (Object[] object: map.get(station)) {
//            moveNextStation(deque, map, visited, object);
//        }
//    }
//
//    private void getDirection(final Station station, final Deque<Station> deque, final Direction direction) {
//        if (direction == Direction.UP) {
//            deque.addLast(station);
//            return;
//        }
//        deque.addFirst(station);
//    }
//
//    private void moveNextStation(final Deque<Station> deque, final Map<Station, List<Object[]>> map, final Set<Station> visited, final Object[] object) {
//        if (visited.contains(object[1])) {
//            return;
//        }
//
//        moveStation((Station) object[1], deque, map, visited, (Direction) object[0]);
//    }
//
//    public void updateLine(Long id, LineRequest lineUpdateRequest) {
//        lineDao.update(new LineEntity(id, lineUpdateRequest.getName(), lineUpdateRequest.getColor()));
//    }
//
//    public void deleteLineById(Long id) {
//        lineDao.deleteById(id);
//    }

}
