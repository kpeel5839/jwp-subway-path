package subway.service.domain;

import java.util.List;

public class ShortestPath {

    private final Integer distance;
    private final Stations stationsInPath;
    private final Fare fare;

    public ShortestPath(Integer distance,
                        Stations stationsInPath,
                        Fare fare) {
        this.distance = distance;
        this.stationsInPath = stationsInPath;
        this.fare = fare;
    }

    public Integer getDistance() {
        return distance;
    }

    public List<Station> getStationsInPath() {
        return stationsInPath.getStations();
    }

    public Integer getFare() {
        return fare.getValue();
    }

}
