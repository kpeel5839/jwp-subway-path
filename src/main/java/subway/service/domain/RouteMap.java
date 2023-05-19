package subway.service.domain;

import org.springframework.beans.factory.ListableBeanFactoryExtensionsKt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

public class RouteMap {

    private final Map<Station, List<Path>> map;

    public RouteMap(Map<Station, List<Path>> map) {
        this.map = map;
    }

    public Map<Station, List<Path>> getMap() {
        return map;
    }

    public void merge(RouteMap insertMap) {
        map.putAll(insertMap.getMap());
    }

    public void bfs(Station start, Station end) {
        PriorityQueue<Object[]> q = new PriorityQueue<>(Comparator.comparingInt(o -> (Integer) o[1]));
        Map<Station, Object[]> m = new HashMap<>();
        q.add(new Object[] {start, 0});
        m.put(start, new Object[] {null, 0});

        while (!q.isEmpty()) {
            Object[] poll = q.poll();

            if (((Station) poll[0]).equals(end)) {
                break;
            }

            if ((Integer) m.get(poll[0])[1] < (Integer) poll[1]) {
                continue;
            }

            for (Path path : map.get(poll[0])) {
                Object[] objects = m.get(path.getNextStation());

                if ((Integer) poll[1] + path.getDistance() < (Integer) objects[1]) {
                    q.add(new Object[] {path.getNextStation(), (Integer) poll[1] + path.getDistance()});
                    m.put(path.getNextStation(), new Object[] {poll[0], (Integer) poll[1] + path.getDistance()});
                }
            }
        }

        List<Station> stations = pathReverse(end, start, m);
        System.out.println(m.get(end)[1]);
        System.out.println(stations);
    }

    public List<Station> pathReverse(Station start, Station end, Map<Station, Object[]> m) {
        Stack<Station> stack = new Stack<>();
        List<Station> result = new ArrayList<>();
        Station c = start;

        while (true) {
            stack.add(c);

            if (c.equals(end)) {
                break;
            }

            c = (Station) m.get(c)[0];
        }

        while (!stack.isEmpty()) {
            result.add(stack.pop());
        }

        return result;
    }

    public List<Station> getStationsOnLine() {
        return map.entrySet()
                .stream()
                .findFirst()
                .map(stationEntry -> createSingleLine(stationEntry.getKey()))
                .orElseGet(Collections::emptyList);
    }

    private List<Station> createSingleLine(Station startStation) {
        Set<Station> visitedStation = new HashSet<>();
        Queue<Station> nowStations = new LinkedList<>();
        Deque<Station> singleLine = new LinkedList<>();
        initForCreateSingleLine(startStation, visitedStation, nowStations, singleLine);
        searchLine(visitedStation, nowStations, singleLine);
        return new ArrayList<>(singleLine);
    }

    private void searchLine(Set<Station> visitedStation,
                            Queue<Station> nowStations,
                            Deque<Station> singleLine) {
        while (!nowStations.isEmpty()) {
            Station nowStation = nowStations.poll();
            map.get(nowStation).forEach(path -> selectNextStation(visitedStation, nowStations, singleLine, path));
        }
    }

    private void selectNextStation(Set<Station> visitedStation,
                                   Queue<Station> nowStations,
                                   Deque<Station> singleLine,
                                   Path path) {
        if (visitedStation.contains(path.getNextStation())) {
            return;
        }
        if (Direction.UP == path.getDirection()) {
            singleLine.addLast(path.getNextStation());
        }
        if (Direction.DOWN == path.getDirection()) {
            singleLine.addFirst(path.getNextStation());
        }
        addNextStation(visitedStation, nowStations, path);
    }

    private void addNextStation(Set<Station> visitedStation,
                                Queue<Station> queue,
                                Path path) {
        visitedStation.add(path.getNextStation());
        queue.add(path.getNextStation());
    }

    private void initForCreateSingleLine(Station startStation,
                                         Set<Station> visitedStation,
                                         Queue<Station> queue,
                                         Deque<Station> singleLine) {
        queue.add(startStation);
        visitedStation.add(startStation);
        singleLine.add(startStation);
    }

    @Override
    public String toString() {
        return "RouteMap{" +
                "map=" + map +
                '}';
    }

}
