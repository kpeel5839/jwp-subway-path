package subway.ui.dto.request;

public class SectionRemoveRequest {

    private final Long lineId;
    private final Long stationId;

    public SectionRemoveRequest(Long lineId, Long stationId) {
        this.lineId = lineId;
        this.stationId = stationId;
    }

    public Long getLineId() {
        return lineId;
    }

    public Long getStationId() {
        return stationId;
    }

}
