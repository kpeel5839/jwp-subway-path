create table if not exists STATION
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    primary key(id)
);

create table if not exists LINE
(
    id bigint auto_increment not null,
    name varchar(255) not null unique,
    color varchar(20) not null,
    primary key(id)
);

create table if not exists SECTION
(
    id bigint auto_increment not null,
    line_id bigint not null,
    distance int not null,
    previous_station_id bigint not null,
    next_station_id bigint not null,
    primary key(id)
);

create table if not exists fare_policy
(
    id bigint auto_increment not null,
    line_id bigint not null,
    additional_fare bigint not null,
    primary key(id)
);
