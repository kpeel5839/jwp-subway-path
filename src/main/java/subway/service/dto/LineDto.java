package subway.service.dto;

import subway.service.domain.Line;

public class LineDto {

    private final String name;
    private final String color;

    public LineDto(final String name, final String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Line toDomain() {
        return new Line(name, color);
    }

}
