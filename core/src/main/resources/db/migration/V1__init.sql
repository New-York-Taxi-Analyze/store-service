CREATE TABLE taxi_trip
(
    id                    SERIAL PRIMARY KEY,
    vendor_id             INTEGER,
    tpep_pickup_datetime  TIMESTAMP WITHOUT TIME ZONE,
    tpep_dropoff_datetime TIMESTAMP WITHOUT TIME ZONE,
    passenger_count       INTEGER,
    trip_distance         NUMERIC(19, 2),
    rate_code_id          INTEGER,
    store_and_fwd_flag    VARCHAR(255),
    pu_location_id        INTEGER,
    do_location_id        INTEGER,
    payment_type          INTEGER,
    fare_amount           NUMERIC(19, 2),
    extra                 NUMERIC(19, 2),
    mta_tax               NUMERIC(19, 2),
    tip_amount            NUMERIC(19, 2),
    tolls_amount          NUMERIC(19, 2),
    improvement_surcharge NUMERIC(19, 2),
    total_amount          NUMERIC(19, 2)
);
