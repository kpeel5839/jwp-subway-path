package subway.service.domain;

import org.springframework.util.function.SingletonSupplier;
import subway.service.domain.vo.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class Subway {

    private final List<Line> lines;

    public Subway(List<Line> lines) {
        this.lines = lines;
    }

    public List<SingleLine> getAllLine() {
        return lines.stream()
                .map(this::createSingleLine)
                .collect(Collectors.toList());
    }

    public SingleLine getSingleLine(Long lineId) {
        return lines.stream()
                .filter(line -> line.getLineProperty().getId() == lineId)
                .map(this::createSingleLine)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("조회하려는 노선이 존재하지 않습니다."));
    }

    private SingleLine createSingleLine(Line line) {
        Map<Station, List<Path>> lineMap = line.getLineMap();
        return lineMap.entrySet()
                .stream()
                .findFirst()
                .map(entry -> SingleLine.of(
                        line.getLineProperty(),
                        createSingleLine(lineMap, entry.getKey())
                )).orElseGet(() -> SingleLine.of(line.getLineProperty(), Collections.emptyList()));
    }

    private List<Station> createSingleLine(Map<Station, List<Path>> lineMap, Station startStation) {
        Station station = startStation;
        Set<Station> visited = new HashSet<>();
        Queue<Station> q = new LinkedList<>();
        Deque<Station> deque = new LinkedList<>();

        q.add(station);
        visited.add(station);
        deque.add(station);

        while (!q.isEmpty()) {
            Station station1 = q.poll();

            for (Path path : lineMap.get(station1)) {
                if (visited.contains(path.getNextStation())) {
                    continue;
                }

                if (Direction.UP == path.getDirection()) {
                    deque.addLast(path.getNextStation());
                }

                if (Direction.DOWN == path.getDirection()) {
                    deque.addFirst(path.getNextStation());
                }

                visited.add(path.getNextStation());
                q.add(path.getNextStation());
            }
        }

        return new ArrayList<>(deque);
    }


}
