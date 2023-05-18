package subway.service;

import org.springframework.stereotype.Service;
import subway.controller.dto.response.LineResponse;
import subway.exception.LineDuplicateException;
import subway.repository.LineRepository;
import subway.repository.StationRepository;
import subway.service.domain.Distance;
import subway.service.domain.Line;
import subway.service.domain.LineProperty;
import subway.service.domain.Section;
import subway.service.domain.Sections;
import subway.service.domain.Station;
import subway.service.dto.LineDto;
import subway.service.dto.SectionCreateDto;

import java.util.List;

@Service
public class LineService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;


    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public LineResponse save(final LineDto lineDto, final SectionCreateDto sectionCreateDto) { // 결국 Line 에서 Section 을 만들어주는 역할을 진행해야함.
        if (lineRepository.existsByName(lineDto.getName())) {
            throw new LineDuplicateException(lineDto.getName() + "은 이미 존재하는 노선입니다.");
        }
        LineProperty lineProperty = new LineProperty(lineDto.getName(), lineDto.getColor());
        Station previousStation = stationRepository.findByName(sectionCreateDto.getPreviousStation());
        Station nextStation = stationRepository.findByName(sectionCreateDto.getNextStation());
        Line line = new Line(lineProperty, new Sections(List.of(
                createSection(sectionCreateDto, previousStation, nextStation)
        )));
        return LineResponse.from(lineRepository.saveLine(line));
    }

    private Section createSection(SectionCreateDto sectionCreateDto, Station previousStation, Station nextStation) {
        return new Section(
                previousStation,
                nextStation,
                Distance.from(sectionCreateDto.getDistance())
        );
    }

}
