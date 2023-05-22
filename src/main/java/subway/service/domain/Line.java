package subway.service.domain;

import subway.exception.SectionDuplicateException;
import subway.exception.SectionNotFoundException;

import java.util.List;
import java.util.Optional;

public class Line {

    private final LineProperty lineProperty;
    private final Sections sections;

    public Line(LineProperty lineProperty, Sections sections) {
        this.lineProperty = lineProperty;
        this.sections = sections;
    }

    public List<Section> findSectionByStation(Station station) {
        return sections.findContainsThisStation(station);
    }

    public Optional<Section> findSectionByDirectionAndStation(Direction direction,
                                                              Station standardStation,
                                                              Station additionalStation) {
        validateSection(standardStation, additionalStation);

        if (Direction.UP == direction) {
            return sections.findPreviousStationThisStation(standardStation);
        }

        return sections.findNextStationThisStation(standardStation);
    }

    private void validateSection(Station firstStation, Station secondStation) {
        if (allContainsTwoStation(firstStation, secondStation)) {
            throw new SectionDuplicateException("이미 포함하고 있는 간선 정보입니다.");
        }

        if (noContainsTwoStation(firstStation, secondStation)) {
            throw new SectionNotFoundException("해당 노선은 두 역 모두 포함하지 않습니다.");
        }
    }

    private boolean allContainsTwoStation(Station firstStation, Station secondStation) {
        return sections.isContainsThisStation(firstStation)
                && sections.isContainsThisStation(secondStation);
    }


    private boolean noContainsTwoStation(Station firstStation, Station secondStation) {
        return !sections.isContainsThisStation(firstStation)
                && !sections.isContainsThisStation(secondStation);
    }

    public RouteMapInLine getLineMap() {
        return sections.createMap(lineProperty);
    }

    public LineProperty getLineProperty() {
        return lineProperty;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

    @Override
    public String toString() {
        return "Line{" +
                "lineProperty=" + lineProperty +
                ", sections=" + sections +
                '}';
    }
}
