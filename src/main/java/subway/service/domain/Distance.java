package subway.service.domain;

import java.util.Objects;

public class Distance {

    private static final int LOWER_BOUND_INTEGER_INCLUSIVE = 0;

    private final Integer value;

    private Distance(Integer value) {
        this.value = value;
    }

    public static Distance from(Integer value) {
        validateDistance(value);

        return new Distance(value);
    }

    private static void validateDistance(int value) {
        if (value <= LOWER_BOUND_INTEGER_INCLUSIVE) {
            throw new IllegalArgumentException("거리는 정수만 가능합니다.");
        }
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Distance distance = (Distance) o;
        return value == distance.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "Distance{" +
                "value=" + value +
                '}';
    }

}
