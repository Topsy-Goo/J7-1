CREATE TABLE ourusers
(	id			bigserial,
	login		VARCHAR(36) NOT NULL UNIQUE,	-- 36 — чтобы в cartKeyByLogin() прошёл uuid (36 символов)
	password	VARCHAR(64) NOT NULL,	-- размер 64 не для пароля юзера, а для хэша (хэш, похоже, всегда занимает 60 символов. Даже для пароля длиннее в 128 символов)
	email		VARCHAR(64) NOT NULL UNIQUE,
	created_at	TIMESTAMP DEFAULT current_timestamp,
	updated_at	TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO ourusers (login, password, email) VALUES
	('super',	'$2a$12$c4HYjryn7vo1bYQfSzkUDe8jPhYIpInbUKZmv5lGnmcyrQPLIWnVu',	'super@post.ru'),	-- пароль 100
	('admin',	'$2a$12$c4HYjryn7vo1bYQfSzkUDe8jPhYIpInbUKZmv5lGnmcyrQPLIWnVu',	'admin@post.ru'),	-- пароль 100
	('user1',	'$2a$12$c4HYjryn7vo1bYQfSzkUDe8jPhYIpInbUKZmv5lGnmcyrQPLIWnVu',	'user1@post.ru'),	-- пароль 100
	('user2',	'$2a$12$c4HYjryn7vo1bYQfSzkUDe8jPhYIpInbUKZmv5lGnmcyrQPLIWnVu',	'user2@post.ru'),	-- пароль 100
	('manager',	'$2a$12$c4HYjryn7vo1bYQfSzkUDe8jPhYIpInbUKZmv5lGnmcyrQPLIWnVu',	'manager@post.ru');	-- пароль 100
-- ----------------------------------------------------------------------
CREATE TABLE roles
(	id			serial,
	name		VARCHAR(64) NOT NULL UNIQUE,
	created_at	TIMESTAMP DEFAULT current_timestamp,
	updated_at	TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO roles (name) VALUES	('ROLE_SUPERADMIN'),('ROLE_ADMIN'),('ROLE_USER'),('ROLE_MANAGER');
-- ----------------------------------------------------------------------
CREATE TABLE ourusers_roles
(	ouruser_id	bigint	NOT NULL,
	role_id		INT		NOT NULL,
	PRIMARY KEY (ouruser_id, role_id),
	FOREIGN KEY (ouruser_id) REFERENCES ourusers (id),
	FOREIGN KEY (role_id) REFERENCES roles (id)
);
INSERT INTO ourusers_roles (ouruser_id, role_id) VALUES
	(1, 1), -- super	- ROLE_SUPERADMIN
	(2, 2), -- admin	- ROLE_ADMIN
	(3, 3),	-- user1	- ROLE_USER
	(4, 3), -- user2	- ROLE_USER
	(5, 4);	-- manager	- ROLE_MANAGER
-- ----------------------------------------------------------------------
CREATE TABLE ourpermissions
(	id			serial,
	name		VARCHAR(64) NOT NULL UNIQUE,
	created_at	TIMESTAMP DEFAULT current_timestamp,
	updated_at	TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO ourpermissions (name) VALUES	('EDIT_PRODUCTS'),('SIMLE_SHOPPING');
-- ----------------------------------------------------------------------
CREATE TABLE ourusers_ourpermissions
(	ouruser_id		bigint	NOT NULL,
	ourpermission_id	INT		NOT NULL,
	PRIMARY KEY (ouruser_id, ourpermission_id),
	FOREIGN KEY (ouruser_id) REFERENCES ourusers (id),
	FOREIGN KEY (ourpermission_id) REFERENCES ourpermissions (id)
);
INSERT INTO ourusers_ourpermissions (ouruser_id, ourpermission_id) VALUES
	(1, 1),(1, 2),	-- super: EDIT_PRODUCTS + SIMLE_SHOPPING
	(2, 1),(2, 2),	-- admin: EDIT_PRODUCTS + SIMLE_SHOPPING
	(3, 2),			-- user1: SIMLE_SHOPPING
	(4, 2),			-- user2: SIMLE_SHOPPING
	(5, 1),(5, 2);	-- manager: EDIT_PRODUCTS + SIMLE_SHOPPING
-- ----------------------------------------------------------------------
CREATE TABLE categories
(	id			serial,
	name		VARCHAR(64) NOT NULL UNIQUE,
	created_at	TIMESTAMP DEFAULT current_timestamp,
	updated_at	TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO categories (name) VALUES	('A'),	('B'),	('C'),	('D');
-- ----------------------------------------------------------------------
CREATE TABLE products					-- TODO: помни о SOAP.
(	id			bigserial,
	title		VARCHAR(255)	NOT NULL,
	price		DECIMAL(10,2),
	rest		INT,
	category_id	INT				NOT NULL,
	created_at	TIMESTAMP DEFAULT current_timestamp,
	updated_at	TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id),
	FOREIGN KEY (category_id) REFERENCES categories (id)
);
INSERT INTO products (title, price, rest, category_id) VALUES
	('Товар№01',  10.0, 20, 1),	('Товар№02',  20.0, 20, 2),	('Товар№03',  30.0, 20, 1),
	('Товар№04',  40.0, 20, 2),	('Товар№05',  50.0, 20, 1),	('Товар№06',  60.0, 20, 2),
	('Товар№07',  70.0, 20, 1),	('Товар№08',  80.0, 20, 2),	('Товар№09',  90.0, 20, 1),
	('Товар№10', 100.0, 20, 2),	('Товар№11', 110.0, 20, 3),	('Товар№12', 120.0, 20, 4),
	('Товар№13', 130.0, 20, 3),	('Товар№14', 140.0, 20, 4),	('Товар№15', 150.0, 20, 3),
	('Товар№16', 160.0, 20, 4),	('Товар№17', 170.0, 20, 3),	('Товар№18', 180.0, 20, 4),
	('Товар№19', 190.0, 20, 3),	('Товар№20', 200.0, 20, 4);
-- ----------------------------------------------------------------------
CREATE TABLE orderstates
(	id				serial,
	short_name		VARCHAR(16)	NOT NULL,	-- фактически, короткое имя, которое удобно помнить
	friendly_name	VARCHAR(64)	NOT NULL,	-- расшифровка короткого имени, для демонстрации пользователю
	created_at		TIMESTAMP DEFAULT current_timestamp,
	updated_at		TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO orderstates (short_name, friendly_name) VALUES
	('NONE','(Нет статуса)'),	('PENDING','Ожидает подтверждения'),	('SERVING','Выполняется'),
	('PAYED','Оплачен'),		('CANCELED','Отменён');
-- ----------------------------------------------------------------------
CREATE TABLE delivery_types
(
	id				bigserial,
	friendly_name	VARCHAR(64)	NOT NULL,
	cost			DECIMAL(10,2),
	created_at		TIMESTAMP DEFAULT current_timestamp,
	updated_at		TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO delivery_types (friendly_name, cost) VALUES
	('Самовывоз',0.0),		('Курьерская доставка', 300.0),
	('Почта России',300.0),	('Доставка до терминала',100.0);
-- ----------------------------------------------------------------------
CREATE TABLE orders
(	id				bigserial,
	ouruser_id		bigint	NOT NULL,
	phone			VARCHAR(16) NOT NULL,	-- +7(800)600-40-50
	address			VARCHAR(255) NOT NULL,	-- 123456789_123456789_
	cost			DECIMAL(10,2),
	orderstate_id	INT NOT NULL,
--	comment			VARCHAR(512),
	created_at		TIMESTAMP DEFAULT current_timestamp,
	updated_at		TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id),
	FOREIGN KEY (orderstate_id) REFERENCES orderstates (id),
	FOREIGN KEY (ouruser_id) REFERENCES ourusers (id)
);
INSERT INTO orders (ouruser_id, phone, address, cost, orderstate_id) VALUES
	(2, '+78006004050', 'г.Китеж, ул.Алхимиков, 17-3-12', 140.0, 4),
	(2,  '84957772211', 'г.Китеж, ул.Алхимиков, 17-3-12', 300.0, 5);
-- ----------------------------------------------------------------------
CREATE TABLE orderitems
(	id				bigserial,
	order_id    	bigint	NOT NULL,
	product_id  	bigint	NOT NULL,
	buying_price	DECIMAL(10,2),
	quantity		INT,
	created_at		TIMESTAMP DEFAULT current_timestamp,
	updated_at		TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id),
	FOREIGN KEY (order_id) REFERENCES orders (id),
	FOREIGN KEY (product_id) REFERENCES products (id)
);
INSERT INTO orderitems (order_id, product_id, buying_price, quantity) VALUES
	(1,  1,  10.0, 1),	(1,  2,  20.0, 2),	(1,  3,  30.0, 3),
	(2, 18, 180.0, 1),	(2, 12, 120.0, 1);
-- ----------------------------------------------------------------------
-- Оказывается, какой-то гений придумал «camelCase» в названиях таблиц и столбцов заменять на camel_case при составлении запросов…
-- ----------------------------------------------------------------------
