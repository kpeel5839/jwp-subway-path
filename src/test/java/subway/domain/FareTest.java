package subway.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import subway.service.domain.Distance;
import subway.service.domain.Fare;
import subway.service.domain.Line;
import subway.service.domain.LineProperty;
import subway.service.domain.Section;
import subway.service.domain.Sections;
import subway.service.domain.ShortestPath;
import subway.service.domain.Station;
import subway.service.domain.Subway;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static subway.service.domain.Fare.from;

class FareTest {

    public static final LineProperty LINE_PROPERTY_1 = new LineProperty("경의중앙선", "청록");
    public static final LineProperty LINE_PROPERTY_2 = new LineProperty("3호선", "주황");
    public static final LineProperty LINE_PROPERTY_3 = new LineProperty("2호선", "초록");

    public static final Station STATION_1 = new Station("용산");
    public static final Station STATION_2 = new Station("이촌");
    public static final Station STATION_3 = new Station("서빙고");
    public static final Station STATION_4 = new Station("한남");
    public static final Station STATION_5 = new Station("옥수");
    public static final Station STATION_6 = new Station("응봉");
    public static final Station STATION_7 = new Station("왕십리");
    public static final Station STATION_8 = new Station("압구정");
    public static final Station STATION_9 = new Station("신사");
    public static final Station STATION_10 = new Station("잠원");
    public static final Station STATION_11 = new Station("고속터미널");
    public static final Station STATION_12 = new Station("교대");
    public static final Station STATION_13 = new Station("강남");
    public static final Station STATION_14 = new Station("선릉");
    public static final Station STATION_15 = new Station("종합운동장");
    public static final Station STATION_16 = new Station("한양대");
    public static final Station STATION_17 = new Station("성수");
    public static final Station STATION_18 = new Station("건대입구");
    public static final Station STATION_19 = new Station("잠실");

    public static final Section SECTION_1 = new Section(STATION_1, STATION_2, Distance.from(5));
    public static final Section SECTION_2 = new Section(STATION_2, STATION_3, Distance.from(8));
    public static final Section SECTION_3 = new Section(STATION_3, STATION_4, Distance.from(10));
    public static final Section SECTION_4 = new Section(STATION_4, STATION_5, Distance.from(7));
    public static final Section SECTION_5 = new Section(STATION_5, STATION_6, Distance.from(10));
    public static final Section SECTION_6 = new Section(STATION_6, STATION_7, Distance.from(1));
    public static final Line LINE_1 = new Line(LINE_PROPERTY_1, new Sections(List.of(SECTION_1, SECTION_2, SECTION_3, SECTION_4, SECTION_5, SECTION_6)));
    public static final Section SECTION_7 = new Section(STATION_5, STATION_8, Distance.from(10));
    public static final Section SECTION_8 = new Section(STATION_8, STATION_9, Distance.from(15));
    public static final Section SECTION_9 = new Section(STATION_9, STATION_10, Distance.from(12));
    public static final Section SECTION_10 = new Section(STATION_10, STATION_11, Distance.from(10));
    public static final Section SECTION_11 = new Section(STATION_11, STATION_12, Distance.from(3));
    public static final Line LINE_2 = new Line(LINE_PROPERTY_2, new Sections(List.of(SECTION_7, SECTION_8, SECTION_9, SECTION_10, SECTION_11)));
    public static final Section SECTION_12 = new Section(STATION_12, STATION_13, Distance.from(2));
    public static final Section SECTION_13 = new Section(STATION_13, STATION_14, Distance.from(7));
    public static final Section SECTION_14 = new Section(STATION_14, STATION_15, Distance.from(10));
    public static final Section SECTION_15 = new Section(STATION_15, STATION_19, Distance.from(1));
    public static final Section SECTION_16 = new Section(STATION_19, STATION_18, Distance.from(10));
    public static final Section SECTION_17 = new Section(STATION_18, STATION_17, Distance.from(9));
    public static final Section SECTION_18 = new Section(STATION_17, STATION_16, Distance.from(2));
    public static final Section SECTION_19 = new Section(STATION_16, STATION_7, Distance.from(10));
    public static final Line LINE_3 = new Line(LINE_PROPERTY_3, new Sections(List.of(SECTION_12, SECTION_13, SECTION_14, SECTION_15, SECTION_16, SECTION_17, SECTION_18, SECTION_19)));

    @Test
    @DisplayName("요금")
    void createFare() {
        assertThat(from(9).getValue()).isEqualTo(1250);
        assertThat(from(10).getValue()).isEqualTo(1250);
        assertThat(from(16).getValue()).isEqualTo(1450);
        assertThat(from(15).getValue()).isEqualTo(1350);
        assertThat(from(58).getValue()).isEqualTo(2150);
        assertThat(from(50).getValue()).isEqualTo(2050);
    }

    @Test
    @DisplayName("test")
    void test() {
        Subway subway = new Subway(List.of(LINE_1, LINE_2, LINE_3));
        ShortestPath shortestPath = subway.findShortestPath(STATION_2, STATION_6);

        assertThat(shortestPath.getDistance()).isEqualTo(35);
        assertThat(shortestPath.getFare()).isEqualTo(1750);
        assertThat(shortestPath.getStationsInPath()).isEqualTo(new ArrayList<>(List.of(STATION_2, STATION_3, STATION_4, STATION_5, STATION_6)));
    }

    @DisplayName("여러 노선을 고려해 최단 경로를 구한다 (이촌 - 잠실)")
    @Test
    void getShortestPathInMultiLines() {
        Subway subway = new Subway(List.of(LINE_1, LINE_2, LINE_3));
        ShortestPath shortestPath = subway.findShortestPath(STATION_2, STATION_19);

        assertThat(shortestPath.getDistance()).isEqualTo(67);
        assertThat(shortestPath.getFare()).isEqualTo(2350);
        assertThat(shortestPath.getStationsInPath()).isEqualTo(new ArrayList<>(
                List.of(STATION_2, STATION_3, STATION_4, STATION_5, STATION_6, STATION_7, STATION_16, STATION_17, STATION_18, STATION_19)));
    }

    @DisplayName("최단경로가 존재하지 않을 때 예외를 발생한다") // 이건 해야함
    @Test
    void notExistShortestPath() {
        //given
        Line line_1 = new Line(LINE_PROPERTY_1, new Sections(List.of(SECTION_1, SECTION_2)));
        Line line_2 = new Line(LINE_PROPERTY_2, new Sections(List.of(SECTION_7)));
        Subway subway = new Subway(List.of(line_1, line_2));

        assertThatThrownBy(() -> subway.findShortestPath(STATION_1, STATION_8))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("최단 경로를 찾을 수 없습니다");
    }

}
