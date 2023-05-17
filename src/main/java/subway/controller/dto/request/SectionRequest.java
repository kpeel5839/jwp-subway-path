package subway.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class SectionRequest {

    @NotBlank
    @NotEmpty
    @NotNull
    private final String lineName;
    @NotBlank
    @NotEmpty
    @NotNull
    private final String direction;
    @NotBlank
    @NotEmpty
    @NotNull
    private final String standardStationName;
    @NotBlank
    @NotEmpty
    @NotNull
    private final String additionalStationName;
    @Positive
    private final Integer distance;

    public SectionRequest(final String lineName, final String direction, final String standardStationName,
                          final String additionalStationName, final Integer distance) {
        this.lineName = lineName;
        this.direction = direction;
        this.standardStationName = standardStationName;
        this.additionalStationName = additionalStationName;
        this.distance = distance;
    }

    public String getLineName() {
        return lineName;
    }

    public String getDirection() {
        return direction;
    }

    public String getStandardStationName() {
        return standardStationName;
    }

    public String getAdditionalStationName() {
        return additionalStationName;
    }

    public Integer getDistance() {
        return distance;
    }

}
