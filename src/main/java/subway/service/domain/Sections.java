package subway.service.domain;

import subway.service.domain.vo.Direction;
import subway.service.domain.vo.Distance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Sections {

    private final List<Section> sections;

    public Sections(List<Section> sections) {
        this.sections = sections;
    }

    public List<Section> findContainsThisStation(Station station) {
        return sections.stream()
                .filter(section -> section.isContainsStation(station))
                .collect(Collectors.toList());
    }

    public boolean isContainsThisStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isContainsStation(station));
    }

    public Optional<Section> findPreviousStationThisStation(Station station) {
        return sections.stream()
                .filter(section -> section.isPreviousStationThisStation(station))
                .findFirst();
    }

    public Optional<Section> findNextStationThisStation(Station station) {
        return sections.stream()
                .filter(section -> section.isNextStationThisStation(station))
                .findFirst();
    }

    public RouteMap createMap() {
        Map<Station, List<Path>> lineMap = new HashMap<>();

        for (Section section : sections) {
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

    public List<Section> getSections() {
        return sections;
    }

}
