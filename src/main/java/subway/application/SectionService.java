package subway.application;

import org.springframework.stereotype.Service;
import subway.application.dto.SectionInsertDto;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.entity.SectionEntity;
import subway.entity.StationEntity;
import subway.ui.query_option.SubwayDirection;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final LineDao lineDao;
    private final StationDao stationDao;
    private final SectionDao sectionDao;

    public SectionService(final LineDao lineDao, final StationDao stationDao, final SectionDao sectionDao) {
        this.lineDao = lineDao;
        this.stationDao = stationDao;
        this.sectionDao = sectionDao;
    }

    public List<Long> save(final SectionInsertDto sectionInsertDto) {
        final Long lineId = lineDao.findIdByName(sectionInsertDto.getLineName());
        final StationEntity standardStation = stationDao.findByName(sectionInsertDto.getStandardStationName());
//        final Station stadardStation = new Station(standardStation, )
        final StationEntity additionStation = stationDao.findByName(sectionInsertDto.getAdditionalStationName());

        if (sectionInsertDto.getDirection() == SubwayDirection.UP) {
            return saveUpperSection(lineId, standardStation.getId(), additionStation.getId(), sectionInsertDto.getDistance())
                    .stream()
                    .map(SectionEntity::getId)
                    .collect(Collectors.toList());
        }

        return saveDownSection(lineId, standardStation.getId(), additionStation.getId(), sectionInsertDto.getDistance())
                .stream()
                .map(SectionEntity::getId)
                .collect(Collectors.toList()); // 일단, 이렇게 호출을 해야한다.
    }

    private List<SectionEntity> saveUpperSection(final Long lineId, final Long previousStationId,
                                                 final Long additionalStationId, final int distance) {
        final List<SectionEntity> sections = sectionDao.findByLineIdAndPreviousStationId(lineId, previousStationId);

        if (sections.isEmpty()) {
            System.out.println("fuck you");
            return saveLastStation(lineId, previousStationId, additionalStationId, distance);
        }

        System.out.println("fuck you");
        return saveBetweenStationWhenUpper(sections, lineId, previousStationId, additionalStationId, distance);
    }

    private List<SectionEntity> saveDownSection(final Long lineId,
                                                final Long nextStationId,
                                                final Long additionalStationId,
                                                final int distance) { // save down section 의 스토리
        final List<SectionEntity> sections = sectionDao.findByLineIdAndNextStationId(lineId, nextStationId);

        if (sections.isEmpty()) {
            System.out.println("fuck you");
            return saveLastStation(lineId, additionalStationId, nextStationId, distance);
        }

        System.out.println("fuck you");
        return saveBetweenStationWhenDown(sections, lineId, additionalStationId, nextStationId, distance);
    }

    private List<SectionEntity> saveLastStation(final long lineId, final long previousStationId,
                                                final long nextStationId, final int distance) {
        SectionEntity sectionEntity = insertSection(lineId, previousStationId, nextStationId, distance);
        return List.of(sectionEntity);
    }

    private List<SectionEntity> saveBetweenStationWhenUpper(final List<SectionEntity> sections,
                                                            final Long lineId,
                                                            final Long previousStationId,
                                                            final Long nextStationId,
                                                            final int distance) {
        if (sections.size() > 1) {
            throw new RuntimeException("중복된 경로가 검색됩니다.");
        }

        final SectionEntity originalSection = sections.get(0);
        final int nextDistance = getNewDistance(distance, originalSection);
        sectionDao.delete(originalSection);

        return List.of(insertSection(lineId, previousStationId, nextStationId, distance),
                insertSection(lineId, nextStationId, originalSection.getNextStationId(), nextDistance));
    }

    private List<SectionEntity> saveBetweenStationWhenDown(final List<SectionEntity> sections,
                                                            final Long lineId,
                                                            final Long previousStationId,
                                                            final Long nextStationId,
                                                            final int distance) {
        if (sections.size() > 1) {
            throw new RuntimeException("중복된 경로가 검색됩니다.");
        }

        final SectionEntity originalSection = sections.get(0);
        final int nextDistance = getNewDistance(distance, originalSection);
        sectionDao.delete(originalSection);

        return List.of(insertSection(lineId, previousStationId, nextStationId, distance),
                insertSection(lineId, originalSection.getPreviousStationId(), previousStationId, nextDistance));
    }

    private static int getNewDistance(final int distance, final SectionEntity originalSection) {
        int originalSectionDistance = originalSection.getDistance();

        if (originalSectionDistance <= distance) {
            throw new IllegalArgumentException("길이 정보가 잘못되었습니다.");
        }

        return originalSectionDistance - distance;
    }

    private SectionEntity insertSection(final long lineId, final long previousStationId, final long nextStationId, final int distance) {
        return sectionDao.insert(new SectionEntity.Builder()
                .lineId(lineId)
                .previousStationId(previousStationId)
                .nextStationId(nextStationId)
                .distance(distance)
                .build());
    }

    public void remove(Long lineId, Long stationId) {
        List<SectionEntity> byLineIdAndPreviousStationIdOrNextStationId = sectionDao.findByLineIdAndPreviousStationIdOrNextStationId(lineId, stationId);
        if (byLineIdAndPreviousStationIdOrNextStationId.size() == 1) {
            sectionDao.delete(byLineIdAndPreviousStationIdOrNextStationId.get(0));
            return;
        }

        if (byLineIdAndPreviousStationIdOrNextStationId.size() > 2) {
            throw new RuntimeException("디투디투디투");
        }
        removeStationInLineWhenTwoSection(lineId, stationId, byLineIdAndPreviousStationIdOrNextStationId);
    }

    private void removeStationInLineWhenTwoSection(Long lineId, Long stationId, List<SectionEntity> byLineIdAndPreviousStationIdOrNextStationId) {
        SectionEntity nextSection = byLineIdAndPreviousStationIdOrNextStationId.stream().filter(v -> v.getPreviousStationId() == stationId).findFirst().orElseThrow(() -> new IllegalArgumentException("디투디투"));
        SectionEntity previousSection = byLineIdAndPreviousStationIdOrNextStationId.stream().filter(v -> v.getNextStationId() == stationId).findFirst().orElseThrow(() -> new IllegalArgumentException("디투디투"));
        int distance = nextSection.getDistance() + previousSection.getDistance();
        insertSection(lineId, previousSection.getPreviousStationId(), nextSection.getNextStationId(), distance);
        byLineIdAndPreviousStationIdOrNextStationId.forEach(sectionDao::delete);
    }

}
