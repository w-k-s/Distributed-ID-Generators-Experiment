CREATE SCHEMA order_service;
CREATE TABLE order_service.orders
(
    uuid         UUID       NOT NULL,
    customer   VARCHAR(50)  NOT NULL,
    meal VARCHAR(250) NOT NULL,
    created_by VARCHAR(50) NOT NULL ,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT pk_order_id PRIMARY KEY (uuid)
);

