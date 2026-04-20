CREATE SCHEMA IF NOT EXISTS ms_capacity;

CREATE TABLE IF NOT EXISTS ms_capacity.capacity
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(90) NOT NULL
);

CREATE TABLE IF NOT EXISTS ms_capacity.capacity_technology
(
    capacity_id BIGINT NOT NULL REFERENCES ms_capacity.capacity(id),
    technology_id BIGINT NOT NULL,

    PRIMARY KEY(capacity_id, technology_id)
);