package subway.service;

import org.springframework.stereotype.Service;
import subway.controller.dto.response.LineResponse;
import subway.repository.LineRepository;
import subway.repository.SectionRepository;
import subway.repository.StationRepository;
import subway.service.domain.Direction;
import subway.service.domain.Distance;
import subway.service.domain.Line;
import subway.service.domain.Section;
import subway.service.domain.Station;
import subway.service.dto.SectionInsertDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class SectionService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final SectionRepository sectionRepository;

    public SectionService(LineRepository lineRepository, StationRepository stationRepository, SectionRepository sectionRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.sectionRepository = sectionRepository;
    }

    public LineResponse save(final SectionInsertDto sectionInsertDto) {
        final Line line = lineRepository.findByName(sectionInsertDto.getLineName());
        final Station standardStation = stationRepository.findByName(sectionInsertDto.getStandardStationName());
        final Station additionalStation = stationRepository.findByName(sectionInsertDto.getAdditionalStationName());

        Optional<Section> sectionByDirectionAndStation = line.getSectionByDirectionAndStation(
                sectionInsertDto.getDirection(),
                standardStation,
                additionalStation
        );

        deleteSection(sectionByDirectionAndStation);

        return sectionByDirectionAndStation
                .map(section -> saveSectionByDirection(sectionInsertDto, line, additionalStation, section))
                .orElseGet(() -> saveSectionWhenLastStation(sectionInsertDto, line, standardStation, additionalStation));
    }

    private void deleteSection(Optional<Section> deleteSection) {
        deleteSection.ifPresent(
                section -> sectionRepository.deleteById(section.getId())
        );
    }

    private LineResponse saveSectionByDirection(SectionInsertDto sectionInsertDto, Line line, Station additionalStation, Section section) {
        if (sectionInsertDto.getDirection() == Direction.UP) {
            return saveSectionWhenUpper(sectionInsertDto, line, additionalStation, section);
        }

        return saveSectionWhenDown(sectionInsertDto, line, additionalStation, section);
    }

    private LineResponse saveSectionWhenUpper(SectionInsertDto sectionInsertDto, Line line, Station additionalStation, Section section) {
        List<Section> sectionsWhenUpper = getSectionsWhenUpper(sectionInsertDto, additionalStation, section);

        Line saveLine = new Line(
                line.getLineProperty(),
                saveSections(line.getLineProperty().getId(), sectionsWhenUpper)
        );

        return LineResponse.from(saveLine);
    }

    private List<Section> saveSections(Long lineId, List<Section> sections) {
        return sections.stream()
                .map(section -> sectionRepository.save(lineId, section))
                .collect(Collectors.toList());
    }

    private List<Section> getSectionsWhenUpper(SectionInsertDto sectionInsertDto,
                                               Station additionalStation,
                                               Section section) {
        return new ArrayList<>(List.of(
                new Section(
                        section.getPreviousStation(),
                        additionalStation,
                        Distance.from(sectionInsertDto.getDistance())
                ), new Section(
                        additionalStation,
                        section.getNextStation(),
                        Distance.from(section.getDistance() - sectionInsertDto.getDistance())
                )
        ));
    }

    private LineResponse saveSectionWhenDown(SectionInsertDto sectionInsertDto, Line line, Station additionalStation, Section section) {
        List<Section> sectionsWhenDown = getSectionsWhenDown(sectionInsertDto, additionalStation, section);

        Line saveLine = new Line(
                line.getLineProperty(),
                saveSections(line.getLineProperty().getId(), sectionsWhenDown)
        );

        return LineResponse.from(saveLine);
    }

    private List<Section> getSectionsWhenDown(SectionInsertDto sectionInsertDto,
                                              Station additionalStation,
                                              Section section) {
        return new ArrayList<>(List.of(
                new Section(
                        section.getPreviousStation(),
                        additionalStation,
                        Distance.from(section.getDistance() - sectionInsertDto.getDistance())
                ), new Section(
                        additionalStation,
                        section.getNextStation(),
                        Distance.from(sectionInsertDto.getDistance())
                )
        ));
    }

    private LineResponse saveSectionWhenLastStation(SectionInsertDto sectionInsertDto, Line line, Station standardStation, Station additionalStation) {
        Line saveLine = new Line(
                line.getLineProperty(),
                createSectionByDirection(sectionInsertDto, standardStation, additionalStation)
        );
        Line afterSaveLine = lineRepository.save(saveLine);
        return LineResponse.from(afterSaveLine);
    }

    private Section createSectionByDirection(SectionInsertDto sectionInsertDto, Station standardStation, Station additionalStation) {
        if (Direction.UP == sectionInsertDto.getDirection()) {
            return new Section(standardStation, additionalStation, Distance.from(sectionInsertDto.getDistance()));
        }

        return new Section(additionalStation, standardStation, Distance.from(sectionInsertDto.getDistance()));
    }

    public void remove(final Long lineId, final Long stationId) {
        Line line = lineRepository.findById(lineId);
        Station station = stationRepository.findById(stationId);
        List<Section> sections = line.getSectionByStation(station);
        saveNewSection(lineId, station, sections);
        sections.forEach(section -> sectionRepository.deleteById(section.getId()));
    }

    private void saveNewSection(Long lineId, Station station, List<Section> sections) {
        if (sections.isEmpty()) {
            throw new IllegalArgumentException("해당 노선에서 해당하는 역을 찾을 수 없습니다.");
        }

        if (sections.size() == 2) {
            Section section = addSection(sections, station);
            sectionRepository.save(lineId, section);
        }
    }

    private Section addSection(List<Section> sections, Station standardStation) {
        Section nextSection = getSectionFromStation(
                sections,
                section -> section.isPreviousStationThisStation(standardStation)
        );
        Section previousSection = getSectionFromStation(
                sections,
                section -> section.isNextStationThisStation(standardStation)
        );

        return new Section(
                previousSection.getPreviousStation(),
                nextSection.getNextStation(),
                Distance.from(nextSection.getDistance() + previousSection.getDistance())
        );
    }

    private Section getSectionFromStation(List<Section> sections, Predicate<Section> sectionSelect) {
        return sections.stream()
                .filter(sectionSelect)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 구간을 찾을 수 없습니다."));
    }

}
