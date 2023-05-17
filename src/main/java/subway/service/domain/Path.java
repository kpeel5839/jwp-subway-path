package subway.service.domain;

import subway.service.domain.vo.Direction;
import subway.service.domain.vo.Distance;

public class Path {

    private final Direction direction;
    private final Station nextStation;
    private final Distance distance;

    public Path(Direction direction, Station nextStation, Distance distance) {
        this.direction = direction;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    public Direction getDirection() {
        return direction;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public Integer getDistance() {
        return distance.getValue();
    }

}
