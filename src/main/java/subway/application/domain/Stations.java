package subway.application.domain;

import subway.application.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class Stations {

    private final List<Station> stations;

    public Stations(final List<Station> stations) {
        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations;
    }

}
