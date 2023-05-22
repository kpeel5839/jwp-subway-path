package subway.service.domain;

import java.util.List;

public class ShortestPath {

    private ShortestPathInfo shortestPathInfo;
    private final Fare fare;

    public ShortestPath(ShortestPathInfo shortestPathInfo,
                        Fare fare) {
        this.shortestPathInfo = shortestPathInfo;
        this.fare = fare;
    }

    public ShortestPathInfo getShortestPathInfo() {
        return shortestPathInfo;
    }

    public Fare getFare() {
        return fare;
    }

}
