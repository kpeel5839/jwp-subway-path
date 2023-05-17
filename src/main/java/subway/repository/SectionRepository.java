package subway.repository;

import org.springframework.stereotype.Repository;
import subway.dao.SectionDao;
import subway.entity.SectionEntity;
import subway.service.domain.vo.Distance;
import subway.service.domain.Section;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;

    public SectionRepository(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public Section save(Long lineId, Section section) {
        SectionEntity sectionEntity = domainToEntity(lineId, section);
        Long sectionId = sectionDao.insert(sectionEntity);
        return entityToDomain(sectionId, section);
    }

    private SectionEntity domainToEntity(Long lineId, Section section) {
        return new SectionEntity(
                lineId,
                section.getDistance(),
                section.getPreviousStation().getId(),
                section.getNextStation().getId()
        );
    }

    private Section entityToDomain(Long id, Section section) {
        return new Section(
                id,
                section.getPreviousStation(),
                section.getNextStation(),
                Distance.from(section.getDistance())
        );
    }

    public void deleteById(long id) {
        int removeCount = sectionDao.deleteById(id);

        if (removeCount == 0) {
            throw new IllegalArgumentException("해당 구간이 삭제되지 않습니다.");
        }
    }

}
