INSERT INTO order_statuses (name)
SELECT DISTINCT status
FROM orders_import
WHERE status IS NOT NULL
  AND status <> '';
ON CONFLICT (name) DO NOTHING;

INSERT INTO users (login, password, fio, role_id)
SELECT
    ui.login,
    ui.password,
    ui.fio,
    r.id
FROM users_import ui
JOIN roles r ON r.name = ui.role_name;

insert into orders
select
	oi.id,
	oi.articul,
	to_date(oi.order_date, 'MM-DD-YY'),
	to_date(oi.delivery_date, 'MM-DD-YY'),
	oi.pickup_point::integer,
	oi.client_fio,
	oi.code::integer,
	os.id
from orders_import oi
join order_statuses os on os.name = oi.status;

INSERT INTO roles (name) VALUES
	('Администратор'),
	('Менеджер'),
	('Авторизированный клиент')
;

INSERT INTO users VALUES
	(1, 'user', '123', 'Иванов Иван Иванович', 3)
;

INSERT INTO users (login, password, fio, role_id)
VALUES (
    'admin',
    'admin',
    'Иванов Иван',
    (SELECT id FROM roles WHERE name = 'Администратор')
);



\copy orders_import(id, articul, order_date, delivery_date, pickup_point, client_fio, pickup_code, status)
FROM 'C:/exam/import/orders.csv'
WITH (
    FORMAT csv,
    HEADER true,
    DELIMITER ',',
    QUOTE '"'
);

INSERT INTO orders (
    id,
    articul,
    order_date,
    delivery_date,
    pickup_point,
    client_fio,
    pickup_code,
    order_status
)

----
to_date(order_date, 'DD.MM.YYYY')
'123'::INT
REPLACE(price, ',', '.')::NUMERIC

DELETE FROM products
WHERE id = 1;

UPDATE products
SET
    name = 'Новое название',
    price = 5000
WHERE id = 1;