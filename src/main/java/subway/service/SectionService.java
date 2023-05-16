package subway.service;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.stereotype.Service;
import subway.controller.dto.response.LineResponse;
import subway.entity.SectionEntity;
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

        Optional<Section> deleteSection = line.getSectionByDirectionAndStation(
                sectionInsertDto.getDirection(),
                standardStation,
                additionalStation
        );

        return deleteSection.map(section -> saveSectionByDirection(sectionInsertDto, line, additionalStation, section))
                .orElseGet(() -> saveSectionWhenLastStation(sectionInsertDto, line, standardStation, additionalStation));
    }

    private LineResponse saveSectionByDirection(SectionInsertDto sectionInsertDto, Line line, Station additionalStation, Section section) {
        if (sectionInsertDto.getDirection() == Direction.UP) {
            return saveSectionWhenUpper(sectionInsertDto, line, additionalStation, section);
        }

        return saveSectionWhenDown(sectionInsertDto, line, additionalStation, section);
    }

    private LineResponse saveSectionWhenUpper(SectionInsertDto sectionInsertDto, Line line, Station additionalStation, Section section) {
        Line saveLine = new Line(
                line.getLineProperty(),
                getSectionsWhenUpper(sectionInsertDto, additionalStation, section)
        );
        return LineResponse.from(lineRepository.save(saveLine));
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
        Line saveLine = new Line(
                line.getLineProperty(),
                getSectionsWhenDown(sectionInsertDto, additionalStation, section)
        );

        return LineResponse.from(lineRepository.save(saveLine));
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
        Line saveLine = new Line(line.getLineProperty(), createSectionByDirection(sectionInsertDto, standardStation, additionalStation));
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

        if (sections.isEmpty()) {
            throw new IllegalArgumentException("해당 노선에서 해당 역을 찾을 수 없습니다.");
        }

        if (sections.size() == 2) {
            Section section = line.deleteSectionAndNewSection(sections, station);
            lineRepository.save(new Line(line.getLineProperty(), section));
        }

        sections.forEach(section -> sectionRepository.deleteById(section.getId()));
    }

}
