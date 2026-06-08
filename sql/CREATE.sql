CREATE TABLE roles (
	id SERIAL PRIMARY KEY,
	name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
	id SERIAL PRIMARY KEY,
	login VARCHAR(100) NOT NULL UNIQUE,
	password VARCHAR(100) NOT NULL,
	fio VARCHAR(300) NOT NULL,
	role_id INTEGER NOT NULL REFERENCES roles(id)
);

CREATE TABLE product_names(
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE suppliers(
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE manufacturers(
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE categories(
	id SERIAL PRIMARY KEY,
	name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE products (
	id SERIAL PRIMARY KEY,
	articul VARCHAR(50) NOT NULL UNIQUE,
	product_name_id INTEGER NOT NULL REFERENCES product_names(id),
	measure_item VARCHAR(50) NOT NULL,
	price NUMERIC(10, 2) NOT NULL,
	supplier_id INTEGER NOT NULL REFERENCES suppliers(id),
	manufacturer_id INTEGER NOT NULL REFERENCES manufacturers(id),
	category_id INTEGER NOT NULL REFERENCES categories(id),
	sale INTEGER NOT NULL,
	quantity INTEGER NOT NULL,
	description TEXT,
	photo_path VARCHAR(255)
);

CREATE TABLE pickup_points (
	id SERIAL PRIMARY KEY,
	name VARCHAR(200) NOT NULL UNIQUE
)

CREATE TABLE order_status (
	id SERIAL PRIMARY KEY,
	articul VARCHAR(100) NOT NULL,
	order_date DATE NOT NULL,
	delivery_date DATE NOT NULL,
	pickip_point INTEGER NOT NULL REFERENCES pickup_points(id),
	client_fio VARCHAR(10)
)
drop table users;
drop table roles;
drop table products;
