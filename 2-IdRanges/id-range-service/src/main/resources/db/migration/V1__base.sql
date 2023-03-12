CREATE SCHEMA id_range_allocator;
CREATE TABLE id_range_allocator.id_range_allocation(
    server_id VARCHAR(50) NOT NULL,
    min_value BIGINT NOT NULL,
    max_value BIGINT NOT NULL,
    CONSTRAINT uq_id_range_allocation_per_server UNIQUE(server_id, min_value, max_value),
    CONSTRAINT uq_id_range_min_value UNIQUE(min_value),
    CONSTRAINT uq_id_range_max_value UNIQUE(max_value)
);