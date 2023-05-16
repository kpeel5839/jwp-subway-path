package subway.repository;

import org.springframework.stereotype.Repository;
import subway.controller.dto.response.LineResponse;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.entity.LineEntity;
import subway.entity.SectionEntity;
import subway.service.domain.Line;
import subway.service.domain.Section;

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


    public LineResponse save(Section section) {
        // 일단 Section 에 저장되어 있는 Line 을 저장
        // 그리고 Section 에 Entity 를 가지고 굳
        Line line = section.getLine();
        LineEntity lineEntity = new LineEntity(line.getName(), line.getColor());
        lineDao.insert(lineEntity);

        // 이제 Section 에 저장되어 있는 id 를 가지고 진행을 해줘야함?
        // 아니면 Section 에 있는 것들을 가지고 그냥 넣었어도 됐나?
        // 일단 Section 을 저장해주자.

        SectionEntity sectionEntity = new SectionEntity.Builder()
                .lineId(line.getId())
                .distance(section.getDistance())
                .previousStationId(section.getPreviousStation().getId())
                .nextStationId(section.getNextStation().getId())
                .build();
        sectionDao.insert(sectionEntity);

        // 여기서는 ID 가 차서나옴
    }

    // Line -> LineEntity -> Line 으로 변경해주는 역할정도는 있어야 할 것 같음
}
