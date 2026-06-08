SELECT DISTINCT status
FROM orders_import;

SELECT
    o.id,
    o.articul,
    o.order_date,
    o.delivery_date,
    pp.name AS pickup_point,
    o.client_fio,
    o.pickup_code,
    os.name AS status
FROM orders o
JOIN pickup_points pp ON o.pickup_point = pp.id
JOIN order_statuses os ON o.order_status = os.id
ORDER BY o.id;

SELECT
    oi.id,
    oi.articul,
    to_date(oi.order_date, 'DD.MM.YYYY'),
    CASE
        WHEN oi.delivery_date IS NULL OR oi.delivery_date = '' THEN NULL
        ELSE to_date(oi.delivery_date, 'DD.MM.YYYY')
    END,
    pp.id,
    oi.client_fio,
    NULLIF(TRIM(oi.pickup_code), '')::INT,
    os.id
FROM orders_import oi
JOIN pickup_points pp ON pp.name = oi.pickup_point
JOIN order_statuses os ON os.name = oi.status;

----
SELECT setval(
    pg_get_serial_sequence('orders', 'id'),
    COALESCE((SELECT MAX(id) FROM orders), 1),
    true
);