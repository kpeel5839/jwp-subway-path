package subway.repository;

import org.springframework.stereotype.Repository;
import subway.dao.SectionDao;

@Repository
public class SectionRepository {

    private final SectionDao sectionDao;

    public SectionRepository(SectionDao sectionDao) {
        this.sectionDao = sectionDao;
    }

    public void deleteById(long id) {
        int removeCount = sectionDao.deleteById(id);

        if (removeCount == 0) {
            throw new IllegalArgumentException("해당 구간이 삭제되지 않습니다.");
        }
    }

}
