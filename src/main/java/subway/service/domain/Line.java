package subway.service.domain;

import java.util.List;

public class Line {

    private final LineProperty lineProperty;
    private final List<Section> sections;

    public Line(LineProperty lineProperty, List<Section> sections) {
        this.lineProperty = lineProperty;
        this.sections = sections;
    }

    public LineProperty getLineProperty() {
        return lineProperty;
    }

    public List<Section> getSections() {
        return sections;
    }

}
