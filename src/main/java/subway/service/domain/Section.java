package subway.service.domain;

import subway.service.domain.vo.Distance;

public class Section {

    private long id;
    private final Station previousStation;
    private final Station nextStation;
    private final Distance distance;

    public Section(final Station previousStation,
                   final Station nextStation,
                   final Distance distance) {
        this.previousStation = previousStation;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    public Section(final long id,
                   final Station previousStation,
                   final Station nextStation,
                   final Distance distance) {
        this.id = id;
        this.previousStation = previousStation;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    public boolean isContainsStation(Station station) {
        return previousStation.equals(station)
                || nextStation.equals(station);
    }

    public boolean isPreviousStationThisStation(Station station) {
        return previousStation.equals(station);
    }

    public boolean isNextStationThisStation(Station station) {
        return nextStation.equals(station);
    }

    public long getId() {
        return id;
    }

    public Station getPreviousStation() {
        return previousStation;
    }

    public Station getNextStation() {
        return nextStation;
    }

    public int getDistance() {
        return distance.getValue();
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", previousStation=" + previousStation +
                ", nextStation=" + nextStation +
                ", distance=" + distance +
                '}';
    }

}
