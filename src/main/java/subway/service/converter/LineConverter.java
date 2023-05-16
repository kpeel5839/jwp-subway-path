package subway.service.converter;

import subway.service.domain.Line;
import subway.service.domain.LineProperty;
import subway.service.dto.LineDto;
import subway.entity.LineEntity;
import subway.controller.dto.response.LineResponse;

import java.util.ArrayList;

public class LineConverter {

    public static LineEntity toEntity(LineDto lineDto) {
        return new LineEntity(lineDto.getName(), lineDto.getColor());
    }

    public static LineResponse domainToResponseDto(final Line line) {
        return new LineResponse(
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
