CREATE TABLE ourusers
(	id			bigserial,
	login		VARCHAR(32) NOT NULL UNIQUE,
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
	('user2',	'$2a$12$c4HYjryn7vo1bYQfSzkUDe8jPhYIpInbUKZmv5lGnmcyrQPLIWnVu',	'user2@post.ru');	-- пароль 100
-- ----------------------------------------------------------------------
CREATE TABLE roles
(	id			serial,
	name		VARCHAR(64) NOT NULL UNIQUE,
	created_at	TIMESTAMP DEFAULT current_timestamp,
	updated_at	TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO roles (name) VALUES	('ROLE_SUPERADMIN'),('ROLE_ADMIN'),('ROLE_USER');
-- ----------------------------------------------------------------------
CREATE TABLE ourusers_roles
(	ouruser_id		bigint	NOT NULL,
	role_id		INT		NOT NULL,
	PRIMARY KEY (ouruser_id, role_id),
	FOREIGN KEY (ouruser_id) REFERENCES ourusers (id),
	FOREIGN KEY (role_id) REFERENCES roles (id)
);
INSERT INTO ourusers_roles (ouruser_id, role_id) VALUES
	(1, 1), -- super	ROLE_SUPERADMIN
	(2, 2), -- admin	ROLE_ADMIN
	(3, 3),	-- user1	ROLE_USER
	(4, 3); -- user2	ROLE_USER
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
CREATE TABLE products
(	id			bigserial,	-- flyway не знает слово IDENTITY (наверное, не поддерживает h2)
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
	state			VARCHAR(16)	NOT NULL,
	friendly_name	VARCHAR(64)	NOT NULL,
	PRIMARY KEY (id)
);
INSERT INTO orderstates (state, friendly_name) VALUES
	('NONE','(Нет статуса)'),('PENDING','Ожидает подтверждения'),('SERVING','Выполняется'),
	('PAYED','Оплачен'),('CANCELED','Отменён');
-- ----------------------------------------------------------------------
CREATE TABLE orders
(	id				bigserial,
	ouruser_id		bigint	NOT NULL,
	phone			VARCHAR(16)	NOT NULL,
	address			VARCHAR(255)	NOT NULL,
--	orderstate_id	INT NOT NULL,
--	comment			VARCHAR(1024),
	created_at		TIMESTAMP DEFAULT current_timestamp,
	updated_at		TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id),
--	FOREIGN KEY (orderstate_id) REFERENCES orderstates (id),
	FOREIGN KEY (ouruser_id) REFERENCES ourusers (id)
);
-- ----------------------------------------------------------------------
CREATE TABLE orderitems
(	id			bigserial,
	order_id    bigint	NOT NULL,
	product_id  bigint	NOT NULL,
	bying_price DECIMAL(10,2),
	quantity	INT,
	PRIMARY KEY (id),
	FOREIGN KEY (order_id) REFERENCES orders (id),
	FOREIGN KEY (product_id) REFERENCES products (id)
);
-- ----------------------------------------------------------------------
--carts (ouruser_id, product_id, quantity)
--	(1, 1, 1),
--	(1, 1, 1),
--	(1, 1, 1);
--CREATE TABLE ordered_products
--(
--	id			bigserial,		-- TODO: временный id создан, чтобы сузить круг при поиске ошибок
--	order_id	,
--	product_id	bigint	NOT NULL,
--	price		INT,
--	quantity	INT,
----	PRIMARY KEY (order_id, product_id),
--	PRIMARY KEY (id),
--	FOREIGN KEY (ouruser_id) REFERENCES ourusers (id)
--	product_id	,
--	quantity	INT,
--
--
--);
