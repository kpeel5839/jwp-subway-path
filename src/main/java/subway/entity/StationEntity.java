package subway.entity;

import java.util.Objects;

public class StationEntity {
    private Long id;
    private String name;

    public StationEntity() {
    }

    public StationEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public StationEntity(String name) {
        this.name = name;
    }

    public static class Builder {

        private Long id;
        private String name;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public StationEntity build() {
            return new StationEntity(id, name);
        }

    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StationEntity stationEntity = (StationEntity) o;
        return id.equals(stationEntity.id) && name.equals(stationEntity.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "StationEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
