package subway.repository;

import org.springframework.stereotype.Repository;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.entity.LineEntity;
import subway.entity.SectionEntity;
import subway.entity.StationEntity;
import subway.exception.LineNotFoundException;
import subway.exception.StationNotFoundException;
import subway.service.domain.Distance;
import subway.service.domain.Line;
import subway.service.domain.LineProperty;
import subway.service.domain.Section;
import subway.service.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class LineRepository {

    private final LineDao lineDao;
    private final SectionDao sectionDao;
    private final StationDao stationDao;

    public LineRepository(LineDao lineDao, SectionDao sectionDao, StationDao stationDao) {
        this.lineDao = lineDao;
        this.sectionDao = sectionDao;
        this.stationDao = stationDao;
    }

    public Line save(Line line) {
        LineProperty lineProperty = saveLineProperty(line.getLineProperty());
        List<Section> sections = saveSections(lineProperty, line.getSections());
        return new Line(lineProperty, sections);
    }

    public LineProperty saveLineProperty(LineProperty lineProperty) { // 그냥 Line 만 생성할 수도 있잖아?
        LineEntity lineEntity = new LineEntity(lineProperty.getName(), lineProperty.getColor());
        Long id = lineDao.insert(lineEntity);
        return new LineProperty(id, lineProperty.getName(), lineProperty.getColor());
    }

    private List<Section> saveSections(LineProperty lineProperty, List<Section> sections) {
        return sections.stream()
                .map(section -> saveSection(lineProperty.getId(), section))
                .collect(Collectors.toList());
    }

    private Section saveSection(Long insert, Section section) {
        SectionEntity sectionEntity = new SectionEntity(
                insert,
                section.getDistance(),
                section.getPreviousStation().getId(),
                section.getNextStation().getId()
        );
        Long id = sectionDao.insert(sectionEntity);
        return new Section(
                id,
                section.getPreviousStation(),
                section.getNextStation(),
                Distance.from(section.getDistance())
        );
    }

    public boolean existsById(Long id) {
        return lineDao.findById(id).size() != 0;
    }

    public boolean existsByName(String name) {
        return lineDao.findByName(name).size() != 0;
    }

    public Line findById(Long id) {
        List<LineEntity> lines = lineDao.findById(id);

        if (lines.isEmpty()) {
            throw new LineNotFoundException("해당 노선은 존재하지 않습니다.");
        }

        return toLine(lines.get(0));
    }

    public List<Line> findAll() {
        List<LineEntity> lineEntities = lineDao.findAll();
        return lineEntities.stream()
                .map(this::toLine)
                .collect(Collectors.toList());
    }

    private Line toLine(LineEntity lineEntity) {
        LineProperty lineProperty = new LineProperty(
                lineEntity.getId(),
                lineEntity.getName(),
                lineEntity.getColor()
        );
        List<Section> sectionsByLineId = getSectionsByLineId(lineEntity.getId());
        return new Line(lineProperty, sectionsByLineId);
    }

    private List<Section> getSectionsByLineId(Long id) {
        return sectionDao.findByLineId(id)
                .stream()
                .map(sectionEntity -> new Section(
                        sectionEntity.getId(),
                        getStationById(sectionEntity.getPreviousStationId()),
                        getStationById(sectionEntity.getNextStationId()),
                        Distance.from(sectionEntity.getDistance())
                ))
                .collect(Collectors.toList());
    }

    private Station getStationById(Long id) {
        List<StationEntity> stationEntities = stationDao.findById(id);

        if (stationEntities.isEmpty()) {
            throw new StationNotFoundException("해당하는 역이 존재하지 않습니다.");
        }

        StationEntity stationEntity = stationEntities.get(0);
        return new Station(stationEntity.getId(), stationEntity.getName());
    }

}
