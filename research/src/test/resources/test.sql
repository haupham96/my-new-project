create table if not exists category
(
    id   bigserial
        primary key,
    name varchar(255)
);

create table if not exists product
(
    product_id          uuid not null
        primary key,
    product_name        varchar(255),
    product_price       numeric,
    product_description text,
    category_id         integer
        references test.category,
    product_type        varchar(255)
);