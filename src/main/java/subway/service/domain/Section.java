package subway.service.domain;

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
