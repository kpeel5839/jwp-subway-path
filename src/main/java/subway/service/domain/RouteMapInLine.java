package subway.service.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class RouteMapInLine {

    private final Map<Station, List<Path>> map;

    public RouteMapInLine(Map<Station, List<Path>> map) {
        this.map = map;
    }

    public Map<Station, List<Path>> getMap() {
        return map;
    }

    public void merge(RouteMapInLine insertMap) {
        Map<Station, List<Path>> insert = insertMap.getMap();
        insert.forEach(this::putPaths);
    }

    private void putPaths(Station stations, List<Path> additionalPath) {
        map.computeIfAbsent(stations, ignored -> new ArrayList<>());
        List<Path> paths = map.get(stations);
        paths.addAll(additionalPath);
    }

    public ShortestPath getShortestPath(Station source,
                                        Station destination,
                                        FarePolicies farePolicy,
                                        Age age) {
        PriorityQueue<PathElement> stationsInPath = new PriorityQueue<>(this::comparePathElement);
        Map<Station, StationRouteInfo> stationRouteMap = new HashMap<>();
        initDijkstra(source, stationsInPath, stationRouteMap);
        dijkstra(destination, stationsInPath, stationRouteMap);
        isShortestPathNotFound(destination, stationRouteMap);
        ShortestPathInfo shortestPathInfo = pathReverse(destination, source, stationRouteMap);
        return new ShortestPath(shortestPathInfo, Fare.of(shortestPathInfo, farePolicy, age));
    }

    private void isShortestPathNotFound(Station end, Map<Station, StationRouteInfo> stationRouteMap) {
        Integer totalDistance = stationRouteMap.get(end).getDistance();

        if (totalDistance == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("최단 경로를 찾을 수 없습니다");
        }
    }

    private void initDijkstra(Station start, PriorityQueue<PathElement> stationsInPath, Map<Station, StationRouteInfo> stationRouteMap) {
        stationsInPath.add(new PathElement(start, 0));
        map.forEach((key, value) ->
                stationRouteMap.put(key, new StationRouteInfo(Integer.MAX_VALUE))
        );
        stationRouteMap.put(start, new StationRouteInfo(0));
    }

    private void dijkstra(Station destination,
                          PriorityQueue<PathElement> stationsInPath,
                          Map<Station, StationRouteInfo> stationRouteMap) {
        boolean isArrivedDestination = false;
        while (!stationsInPath.isEmpty() && !isArrivedDestination) {
            PathElement pathElement = stationsInPath.poll();
            Station nowStation = pathElement.getStation();
            Integer nowDistance = pathElement.getDistance();
            isArrivedDestination = nowStation.equals(destination);
            searchConnectedStation(stationsInPath, stationRouteMap, isArrivedDestination, nowStation, nowDistance);
        }
    }

    private void searchConnectedStation(PriorityQueue<PathElement> stationsInPath,
                                        Map<Station, StationRouteInfo> stationRouteMap,
                                        boolean isArrivedDestination,
                                        Station nowStation,
                                        Integer nowDistance) {
        if (isNotIncludedNowStationInShortestPath(stationRouteMap, isArrivedDestination, nowStation, nowDistance)) {
            searchConnectedNowStation(stationsInPath, stationRouteMap, nowStation, nowDistance);
        }
    }

    private boolean isNotIncludedNowStationInShortestPath(Map<Station, StationRouteInfo> stationRouteMap, boolean isArrivedDestination, Station nowStation, Integer nowDistance) {
        return isArrivedDestination
                || stationRouteMap.get(nowStation).getDistance() < nowDistance;
    }

    private void searchConnectedNowStation(PriorityQueue<PathElement> stationsInPath,
                                        Map<Station, StationRouteInfo> stationRouteMap,
                                        Station nowStation,
                                        Integer nowDistance) {
        for (Path path : map.get(nowStation)) {
            StationRouteInfo stationRouteInfo = stationRouteMap.get(path.getNextStation());
            int newDistance = nowDistance + path.getDistance();
            findNextStationInShortestPath(stationsInPath, stationRouteMap, nowStation, path, stationRouteInfo, newDistance);
        }
    }

    private void findNextStationInShortestPath(PriorityQueue<PathElement> stationsInPath,
                                        Map<Station, StationRouteInfo> stationRouteMap,
                                        Station nowStation,
                                        Path path,
                                        StationRouteInfo stationRouteInfo,
                                        int newDistance) {
        if (newDistance < stationRouteInfo.getDistance()) {
            stationsInPath.add(new PathElement(path.getNextStation(), newDistance));
            stationRouteMap.put(
                    path.getNextStation(),
                    new StationRouteInfo(nowStation, path.getLineProperty(), newDistance)
            );
        }
    }

    private Integer comparePathElement(PathElement firstElement, PathElement secondElement) {
        return firstElement.getDistance() - secondElement.getDistance();
    }

    public ShortestPathInfo pathReverse(Station source,
                                        Station destination,
                                        Map<Station, StationRouteInfo> stationRouteMap) {
        List<Station> stationsInLine = new ArrayList<>();
        Set<LineProperty> usedLines = new HashSet<>();
        stationsInLine.add(source);
        usedLines.add(stationRouteMap.get(source).getUsedLine());

        while (!stationsInLine.get(stationsInLine.size() - 1).equals(destination)) {
            StationRouteInfo stationRouteInfo = stationRouteMap.get(stationsInLine.get(stationsInLine.size() - 1));
            stationsInLine.add(stationRouteInfo.getPreviousStation());
            usedLines.add(stationRouteInfo.getUsedLine());
        }

        Collections.reverse(stationsInLine);
        return new ShortestPathInfo(stationRouteMap.get(source).getDistance(), usedLines, new Stations(stationsInLine));
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
