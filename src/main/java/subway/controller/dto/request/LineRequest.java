package subway.controller.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public class LineRequest {

    @NotNull
    @NotEmpty
    @NotBlank
    private final String name;
    @NotNull
    @NotEmpty
    @NotBlank
    private final String color;
    @Positive
    private final Integer distance;
    @NotNull
    @NotEmpty
    @NotBlank
    private final String firstStation;
    @NotNull
    @NotEmpty
    @NotBlank
    private final String secondStation;

    public LineRequest(final String name, final String color, final Integer distance,
                       final String firstStation, final String secondStation) {
        this.name = name;
        this.color = color;
        this.distance = distance;
        this.firstStation = firstStation;
        this.secondStation = secondStation;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public Integer getDistance() {
        return distance;
    }

    public String getFirstStation() {
        return firstStation;
    }

    public String getSecondStation() {
        return secondStation;
    }

}
