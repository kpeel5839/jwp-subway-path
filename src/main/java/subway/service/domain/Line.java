package subway.service.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Line {

    private final LineProperty lineProperty;
    private final List<Section> sections;

    public Line(LineProperty lineProperty, Section section) {
        this.lineProperty = lineProperty;
        sections = new ArrayList<>(List.of(section));
    }

    public Line(LineProperty lineProperty, List<Section> sections) {
        this.lineProperty = lineProperty;
        this.sections = sections;
    }

//    public void validateSection(Direction direction, Station standardStation, Station additionalStation) {
//        List<Section> collect = getConnectSectionWithStation(standardStation, additionalStation);
//
//        if (direction == Direction.UP) {
//            collect.stream()
//                    .filter(section -> section.getPreviousStation().equals(standardStation))
//                    .findFirst()
//                    .ifPresent(section -> upperValidate(section.getNextStation(), additionalStation));
//        }
//
//        collect.stream()
//                .filter(section -> section.getNextStation().equals(standardStation))
//                .findFirst()
//                .ifPresent(section -> downValidate(section.getPreviousStation(), additionalStation));
//    }
//
//    private List<Section> getConnectSectionWithStation(Station standardStation, Station additionalStation) {
//        List<Section> sectionsByStandardStation = sections.stream()
//                .filter(section -> section.isContainsStation(standardStation))
//                .collect(Collectors.toList());
//
//        if (isContainsAdditionalStation(additionalStation, sectionsByStandardStation)) {
//            throw new IllegalArgumentException(INVALID_SECTION_ERROR_MESSAGE);
//        }
//
//        return sectionsByStandardStation;
//    }
//
//    private boolean isContainsAdditionalStation(Station additionalStation, List<Section> sectionsByStandardStation) {
//        return sectionsByStandardStation.stream()
//                .anyMatch(section -> section.isContainsStation(additionalStation));
//    }
//
//    private void upperValidate(Station station, Station additionalStation) {
//        sections.stream()
//                .filter(section -> section.isPreviousStationThisStation(station))
//                .findFirst()
//                .ifPresent(section -> validDuplicate(section.getNextStation(), additionalStation));
//    }
//
//    private void downValidate(Station station, Station additionalStation) {
//        sections.stream()
//                .filter(section -> section.isNextStationThisStation(station))
//                .findFirst()
//                .ifPresent(section -> validDuplicate(section.getPreviousStation(), additionalStation));
//    }
//
//    private void validDuplicate(Station station, Station additionalStation) {
//        if (station.equals(additionalStation)) {
//            throw new IllegalArgumentException(INVALID_SECTION_ERROR_MESSAGE);
//        }
//    }

    public Optional<Section> getDeleteSection(Direction direction,
                                              Station standardStation,
                                              Station additionalStation) {
        validateSection(standardStation, additionalStation);

        if (Direction.UP == direction) {
            return sections.stream()
                    .filter(section -> section.isPreviousStationThisStation(standardStation))
                    .findFirst();
        }

        return sections.stream()
                .filter(section -> section.isNextStationThisStation(standardStation))
                .findFirst();
    }

    public void validateSection(Station firstStation, Station secondStation) {
        if (allContainsTwoStation(firstStation, secondStation)
                || noContainsTwoStation(firstStation, secondStation)) {
            throw new IllegalArgumentException("이미 포함하고 있는 간선 정보입니다.");
        }
    }

    public boolean allContainsTwoStation(Station firstStation, Station secondStation) {
        return containsStation(firstStation) && containsStation(secondStation);
    }


    public boolean noContainsTwoStation(Station firstStation, Station secondStation) {
        return !containsStation(firstStation) && containsStation(secondStation);
    }

    private boolean containsStation(Station station) {
        return sections.stream()
                .anyMatch(section -> section.isContainsStation(station));
    }

    public LineProperty getLineProperty() {
        return lineProperty;
    }

    public List<Section> getSections() {
        return sections;
    }

}
