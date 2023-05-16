package subway.service.domain;

public class Section {

    private long id;
    private final Line line; // TODO : 과연 line 이 있을 필요가 있을까? 짜피 도메인 구조에서 Line 내에 Section 이 들어있는데?
    private final Station previousStation;
    private final Station nextStation;
    private final Distance distance;

    public Section(final Line line,
                   final Station previousStation,
                   final Station nextStation,
                   final Distance distance) {
        this.line = line;
        this.previousStation = previousStation;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    public Section(final long id,
                   final Line line,
                   final Station previousStation,
                   final Station nextStation,
                   final Distance distance) {
        this.id = id;
        this.line = line;
        this.previousStation = previousStation;
        this.nextStation = nextStation;
        this.distance = distance;
    }

    public long getId() {
        return id;
    }

    public Line getLine() {
        return line;
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
                ", line=" + line +
                ", previousStation=" + previousStation +
                ", nextStation=" + nextStation +
                ", distance=" + distance +
                '}';
    }

}
