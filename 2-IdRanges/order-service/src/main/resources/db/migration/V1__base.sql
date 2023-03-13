CREATE SCHEMA order_service;
CREATE TABLE order_service.orders(
  id BIGINT NOT NULL,
  customer VARCHAR(50) NOT NULL,
  restaurant VARCHAR(50) NOT NULL,
  line_items VARCHAR(250) NOT NULL,
  CONSTRAINT pk_order_id PRIMARY KEY(id)
);

CREATE TABLE order_service.unique_id_sequences(
    -- The name of the sequence
    name VARCHAR(100) NOT NULL,
    -- The min value (inclusive) of the sequence
    min_value BIGINT NOT NULL,
    -- The max value (inclusive) of the sequence
    max_value BIGINT NOT NULL,
    -- The value at which a new sequence needs to be created
    prefetch_value BIGINT NOT NULL,
    -- Whether all the values in this sequence have been used (as ids).
    is_depleted BOOLEAN NOT NULL DEFAULT FALSE
);