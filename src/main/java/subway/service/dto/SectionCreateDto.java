package subway.service.dto;

public class SectionCreateDto {

    private final Integer distance;
    private final String previousStation;
    private final String nextStation;

    public SectionCreateDto(Integer distance, String previousStation, String nextStation) {
        this.distance = distance;
        this.previousStation = previousStation;
        this.nextStation = nextStation;
    }

    public Integer getDistance() {
        return distance;
    }

    public String getPreviousStation() {
        return previousStation;
    }

    public String getNextStation() {
        return nextStation;
    }

}
