package subway.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import subway.application.dto.SectionInsertDto;
import subway.dao.LineDao;
import subway.dao.SectionDao;
import subway.dao.StationDao;
import subway.entity.SectionEntity;
import subway.ui.query_option.SubwayDirection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SectionServiceTest {

    @Mock
    private LineDao lineDao;
    @Mock
    private StationDao stationDao;
    @Mock
    private SectionDao sectionDao;
    @InjectMocks
    private SectionService sectionService;

    @BeforeEach
    void setUp() {
        sectionService = new SectionService(lineDao, stationDao, sectionDao);
    }

    @Test
    @DisplayName("생성 성공")
    void save_success() {
        // given
        final String lineName = "2호선";
        final String standardStationName = "잠실";
        final String additionalStationName = "송파";
        final int distance = 3;

        given(lineDao.findIdByName("2호선")).willReturn(1L);
        given(stationDao.findIdByName(standardStationName)).willReturn(1L);
        given(stationDao.findIdByName(additionalStationName)).willReturn(5L);
        given(sectionDao.insert(new SectionEntity.Builder()
                .lineId(1L)
                .previousStationId(1L)
                .nextStationId(5L)
                .distance(distance)
                .build())).willReturn(1L);

        // when
        final SectionInsertDto sectionInsertDto = new SectionInsertDto(
                lineName, SubwayDirection.UP, standardStationName, additionalStationName, distance
        );
        long id = sectionService.save(sectionInsertDto);


        // then
        assertThat(id).isEqualTo(1L);;
    }
}
