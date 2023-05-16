package subway.entity;

import java.util.Objects;

public class LineEntity {
    private Long id;
    private String name;
    private String color;

    public LineEntity() {
    }

    public LineEntity(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public LineEntity(Long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public static class Builder {

        private Long id;
        private String name;
        private String color;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder color(String color) {
            this.color = color;
            return this;
        }

        public LineEntity build() {
            return new LineEntity(id, name, color);
        }

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineEntity line = (LineEntity) o;
        return Objects.equals(id, line.id) && Objects.equals(name, line.name) && Objects.equals(color, line.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}
