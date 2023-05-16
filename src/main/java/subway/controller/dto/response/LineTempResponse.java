package subway.controller.dto.response;

import subway.service.domain.Line;

import java.util.List;
import java.util.stream.Collectors;

public class LineTempResponse {

    private final Long id;
    private final String name;
    private final String color;
    private final List<SectionResponseInLine> sections;

    private LineTempResponse(Long id,
                             String name,
                             String color,
                             List<SectionResponseInLine> sections) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.sections = sections;
    }
    
    public static LineTempResponse from(Line line) {
        return new LineTempResponse(
                line.getLineProperty().getId(),
                line.getLineProperty().getName(),
                line.getLineProperty().getColor(),
                line.getSections().
                        stream()
                        .map(SectionResponseInLine::from)
                        .collect(Collectors.toList())
        );
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<SectionResponseInLine> getSections() {
        return sections;
    }

}
