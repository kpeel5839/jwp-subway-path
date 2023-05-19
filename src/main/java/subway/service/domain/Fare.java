package subway.service.domain;

public class Fare {

    private final Integer value;

    private Fare(Integer value) {
        this.value = value;
    }

    public static Fare from(Integer totalDistance) {
        return new Fare(calculate(totalDistance));
    }

    private static Integer calculate(Integer totalDistance) {
        return 1250 + chargeMoreForEveryFiveLargerThanTenAndSmallerThanFifty(totalDistance)
                + chargeMoreForEveryEightLargerThanFifty(totalDistance);
    }

    private static int chargeMoreForEveryFiveLargerThanTenAndSmallerThanFifty(int totalDistance) {
        totalDistance = Math.min(40, totalDistance - 10);
        int chargeFare = (int) Math.ceil(totalDistance / 5d) * 100;
        return Math.max(chargeFare, 0);
    }

    private static Integer chargeMoreForEveryEightLargerThanFifty(int totalDistance) {
        totalDistance -= 50;
        int chargeFare = (int) Math.ceil(totalDistance / 8d) * 100;
        return Math.max(chargeFare, 0);
    }

    public Integer getValue() {
        return value;
    }

}
