package subway.service.domain;

import subway.service.domain.vo.Direction;
import subway.service.domain.vo.Distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

        return sections.findNextStationThisStation(additionalStation);
    }

    public void validateSection(Station firstStation, Station secondStation) {
        if (allContainsTwoStation(firstStation, secondStation)
                || noContainsTwoStation(firstStation, secondStation)) {
            throw new IllegalArgumentException("이미 포함하고 있는 간선 정보입니다.");
        }
    }

    public boolean allContainsTwoStation(Station firstStation, Station secondStation) {
        return sections.isContainsThisStation(firstStation)
                && sections.isContainsThisStation(secondStation);
    }


    public boolean noContainsTwoStation(Station firstStation, Station secondStation) {
        return !sections.isContainsThisStation(firstStation)
                && !sections.isContainsThisStation(secondStation);
    }

    public RouteMap getLineMap() {
        Map<Station, List<Path>> lineMap = new HashMap<>();

        for (Section section : sections.getSections()) {
            putIfNotContains(lineMap, section);
            lineMap.get(section.getPreviousStation()).add(createPath(Direction.UP, section));
            lineMap.get(section.getNextStation()).add(createPath(Direction.DOWN, section));
        }

        return new RouteMap(lineMap);
    }

    private Path createPath(Direction direction, Section section) {
        return new Path(
                direction,
                section.getPreviousStation(),
                Distance.from(section.getDistance())
        );
    }

    private void putIfNotContains(Map<Station, List<Path>> lineMap, Section section) {
        if (!lineMap.containsKey(section.getPreviousStation())) {
            lineMap.put(section.getPreviousStation(), new ArrayList<>());
        }

        if (!lineMap.containsKey(section.getNextStation())) {
            lineMap.put(section.getNextStation(), new ArrayList<>());
        }
    }

    public LineProperty getLineProperty() {
        return lineProperty;
    }

    public List<Section> getSections() {
        return sections.getSections();
    }

}
