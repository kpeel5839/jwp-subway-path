package subway.service.converter;

import subway.service.domain.Line;
import subway.service.domain.LineProperty;
import subway.service.dto.LineDto;
import subway.entity.LineEntity;
import subway.controller.dto.response.LinePropertyResponse;

import java.util.ArrayList;

public class LineConverter {

    public static LineEntity toEntity(LineDto lineDto) {
        return new LineEntity(lineDto.getName(), lineDto.getColor());
    }

    public static LinePropertyResponse domainToResponseDto(final Line line) {
        return new LinePropertyResponse(
                line.getLineProperty().getId(),
                line.getLineProperty().getName(),
                line.getLineProperty().getColor()
        );
    }

    public static Line entityToDomain(LineEntity lineEntity) {
        return new Line(
                new LineProperty(
                        lineEntity.getId(),
                        lineEntity.getName(),
                        lineEntity.getColor()
                ),
                new ArrayList<>());
    }

}
