CREATE SCHEMA order_service;
CREATE TABLE order_service.orders
(
    id         BIGINT       NOT NULL,
    customer   VARCHAR(50)  NOT NULL,
    restaurant VARCHAR(50)  NOT NULL,
    line_items VARCHAR(250) NOT NULL,
    CONSTRAINT pk_order_id PRIMARY KEY (id)
);

CREATE SEQUENCE order_service.seq_unique_id_a;
CREATE SEQUENCE order_service.seq_unique_id_b;

