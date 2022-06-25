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
(	ouruser_id			bigint	NOT NULL,
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
	name		VARCHAR(128) NOT NULL UNIQUE,
	created_at	TIMESTAMP DEFAULT current_timestamp,
	updated_at	TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO categories (name) VALUES
	('A'),	('B'),	('C'),	('D'),	('E');
-- ----------------------------------------------------------------------
CREATE TABLE measures
(	id			serial,
	name		VARCHAR(128) NOT NULL UNIQUE,
	created_at	TIMESTAMP DEFAULT current_timestamp,
	updated_at	TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO measures (name) VALUES
('штука'), ('комплект'), ('килограмм'), ('грамм'), ('упаковка'), ('пакет'),
('банка'), ('бутылка'), ('литр');
-- ----------------------------------------------------------------------
CREATE TABLE products					-- TODO: помни о SOAP.
(	id			bigserial,
	title		VARCHAR(255)	NOT NULL,
	price		NUMERIC(10,2)	NOT NULL,
	rest		INT				NOT NULL,
	measure_id	INT				NOT NULL,
	category_id	INT				NOT NULL,
	created_at	TIMESTAMP DEFAULT current_timestamp,
	updated_at	TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id),
	FOREIGN KEY (measure_id) REFERENCES measures (id),
	FOREIGN KEY (category_id) REFERENCES categories (id)
);
INSERT INTO products
(title,			price,		rest,	measure_id, category_id) VALUES
('Товар№01',	 10.0,		20,		1,			1),
('Товар№02',	 20.0,		20,		3,			2),
('Товар№03',	 30.0,		20,		2,			1),
('Товар№04',	 40.0,		20,		3,			2),
('Товар№05',	 50.0,		20,		2,			1),
('Товар№06',	 60.0,		20,		4,			2),
('Товар№07',	 70.0,		20,		1,			1),
('Товар№08',	 80.0,		20,		4,			2),
('Товар№09',	 90.0,		20,		1,			1),
('Товар№10',	100.0,		20,		3,			2),
('Товар№11',	110.0,		20,		6,			3),
('Товар№12',	120.0,		20,		7,			4),
('Товар№13',	130.0,		20,		6,			3),
('Товар№14',	140.0,		20,		8,			4),
('Товар№15',	150.0,		20,		5,			3),
('Товар№16',	160.0,		20,		9,			4),
('Товар№17',	170.0,		20,		6,			3),
('Товар№18',	180.0,		20,		8,			4),
('Товар№19',	190.0,		20,		5,			3),
('Товар№20',	200.0,		20,		7,			4);
-- ----------------------------------------------------------------------
CREATE TABLE orderstates
(	id				serial,
	short_name		VARCHAR(16)	NOT NULL UNIQUE,	-- фактически, короткое имя, которое удобно помнить
	friendly_name	VARCHAR(64)	NOT NULL UNIQUE,	-- расшифровка короткого имени, для демонстрации пользователю
	created_at		TIMESTAMP DEFAULT current_timestamp,
	updated_at		TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO orderstates
(short_name,	friendly_name) VALUES
('NONE',		'(Нет статуса)'),
('PENDING',		'Ожидает подтверждения'),
('SERVING',		'Выполняется'),
('PAYED',		'Оплачен'),
('CANCELED',	'Отменён');
-- ----------------------------------------------------------------------
--CREATE TABLE delivery_types
--(
--	id				serial,
--	short_name		VARCHAR(16)		NOT NULL UNIQUE,
--	friendly_name	VARCHAR(64)		NOT NULL UNIQUE,
--	cost			NUMERIC(10,2)	NOT NULL,
--	created_at		TIMESTAMP DEFAULT current_timestamp,
--	updated_at		TIMESTAMP DEFAULT current_timestamp,
--	PRIMARY KEY (id)
--);
--INSERT INTO delivery_types
--(short_name,	friendly_name,				cost) VALUES
--('SELFY',		'Самовывоз',				0.0),
--('RF_POST',	'Почта России',				300.0),
--('COURIER',	'Курьерская доставка', 		300.0),
--('TERMINAL',	'Доставка до терминала',	100.0);
-- ----------------------------------------------------------------------
CREATE TABLE shipping_info
(
	id					bigserial,	  --				   PayPal
	country_code		VARCHAR(2),   -- country_code		2
	postal_code			VARCHAR(6),   -- postal_code		6
	region				VARCHAR(60),  -- admin_area_1		0…100
	town_village		VARCHAR(100), -- admin_area_2		1…100
	street_house		VARCHAR(100), -- address_line_1		1…100
	apartment			VARCHAR(20),  -- address_line_2		0…100
	phone				VARCHAR(20)	NOT NULL, -- +7 (800) 600-40-50
	created_at		TIMESTAMP DEFAULT current_timestamp,
	updated_at		TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id)
);
INSERT INTO shipping_info
(country_code, postal_code,	region,			town_village, street_house,	  	  apartment, phone) VALUES
('RU', 	'200870', 	'Муромская обл.',		'г.Китеж',	  'ул.Алхимиков, 17-3', '12',	 '+78006004050'),
('RU', 	'200870', 	'Муромская обл.',		'г.Китеж',	  'ул.Алхимиков, 17-3', '12',	 '84957772211'),
('RU', 	'456842', 	'Чукотский авт.округ',	'пос.Мирный', 'ул.Чоппера 33/7',	'',		 '31415'),
('RU', 	'125402',	'',						'Москва',	  'Юнатов 22-180',	    '',		 '988-480-77-12');
-- ----------------------------------------------------------------------
CREATE TABLE orders
(	id					bigserial,
	ouruser_id			bigint			NOT NULL,
	shipping_info_id	bigint			NOT NULL,
	all_items_cost		NUMERIC(10,2)	NOT NULL,
	orderstate_id		INT				NOT NULL,
	created_at			TIMESTAMP DEFAULT current_timestamp,
	updated_at			TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id),
	FOREIGN KEY (orderstate_id) REFERENCES orderstates (id),
	FOREIGN KEY (shipping_info_id) REFERENCES shipping_info (id),
	FOREIGN KEY (ouruser_id) REFERENCES ourusers (id)
);
INSERT INTO orders
(ouruser_id, shipping_info_id,  all_items_cost,	orderstate_id) VALUES
(2, 		 1,				 	140.0,			4),	-- admin	Китеж	почта
(2, 		 2,				 	300.0,			5),	-- admin	Китеж	почта
(3, 		 3,				 	500.0,			4),	-- user1	Мирный	почта
(3, 		 3,				 	580.0,			4),	-- user1	Мирный	почта
(4, 		 4,				 	370.0,			3);	-- user2	Москва	самовывоз
-- ----------------------------------------------------------------------
CREATE TABLE orderitems
(	id				bigserial,
	order_id    	bigint			NOT NULL,
	product_id  	bigint			NOT NULL,
	buying_price	NUMERIC(10,2)	NOT NULL,
	quantity		INT				NOT NULL,
	created_at		TIMESTAMP DEFAULT current_timestamp,
	updated_at		TIMESTAMP DEFAULT current_timestamp,
	PRIMARY KEY (id),
	FOREIGN KEY (order_id) REFERENCES orders (id),
	FOREIGN KEY (product_id) REFERENCES products (id)
);
INSERT INTO orderitems
(order_id, product_id, buying_price, quantity) VALUES
(1,			 1,			 10.0,		1),	-- admin
(1,			 2,			 20.0,		2),
(1,			 3,			 30.0,		3),
(2,			18,			180.0,		1),	-- admin
(2,			12,			120.0,		1),
(3,			10,			100.0,		1),	-- user1
(3,			20,			200.0,		2),
(4,			10,			100.0,		1),	-- user1
(4,			15,			150.0,		1),
(4,			16,			160.0,		1),
(4,			17,			170.0,		1),
(5,			 6,			 60.0,		1),	-- user2
(5,			11,			110.0,		2),
(5,			 9,			 90.0,		1);
-- ----------------------------------------------------------------------
CREATE TABLE productreviews
(	id				bigserial,
	text			VARCHAR(2048)	NOT NULL,
	ouruser_id		bigint			NOT NULL,
	product_id  	bigint			NOT NULL,
	created_at		TIMESTAMP DEFAULT current_timestamp,
	updated_at		TIMESTAMP DEFAULT current_timestamp,
	FOREIGN KEY (ouruser_id) REFERENCES ourusers (id),
	FOREIGN KEY (product_id) REFERENCES products (id),
	PRIMARY KEY (id)
);
INSERT INTO productreviews (ouruser_id, product_id, text) VALUES
	(1, 3, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nisl tincidunt eget nullam non. Quis hendrerit dolor magna eget est lorem ipsum dolor sit. Volutpat odio facilisis mauris sit amet massa. Commodo odio aenean sed adipiscing diam donec adipiscing tristique. Mi eget mauris pharetra et. Elementum nibh tellus molestie nunc. Non tellus orci ac auctor augue.'),--
	(2, 2, 'Et malesuada fames ac turpis egestas sed. Sit amet nisl suscipit adipiscing bibendum est ultricies. Arcu ac tortor dignissim convallis aenean et tortor at. Pretium viverra suspendisse potenti nullam ac tortor vitae purus. Eros donec ac odio tempor orci dapibus ultrices. Et magnis dis parturient montes nascetur. Est placerat in egestas erat imperdiet.'),--
	(3, 1, 'Sit amet nulla facilisi morbi tempus. Nulla facilisi cras fermentum odio eu. Etiam erat velit scelerisque in dictum non consectetur a erat. Enim nulla aliquet porttitor lacus luctus accumsan tortor posuere. Ut sem nulla pharetra diam. Fames ac turpis egestas maecenas. Bibendum neque egestas congue quisque egestas diam. Laoreet id donec ultrices tincidunt arcu non sodales neque. Eget felis eget nunc lobortis mattis aliquam faucibus purus. Faucibus interdum posuere lorem ipsum dolor sit.'),--
	(2, 3, 'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Nisl tincidunt eget nullam non. Quis hendrerit dolor magna eget est lorem ipsum dolor sit. Volutpat odio facilisis mauris sit amet massa. Commodo odio aenean sed adipiscing diam donec adipiscing tristique. Mi eget mauris pharetra et. Elementum nibh tellus molestie nunc. Non tellus orci ac auctor augue.'),--
	(3, 2, 'Et malesuada fames ac turpis egestas sed. Sit amet nisl suscipit adipiscing bibendum est ultricies. Arcu ac tortor dignissim convallis aenean et tortor at. Pretium viverra suspendisse potenti nullam ac tortor vitae purus. Eros donec ac odio tempor orci dapibus ultrices. Et magnis dis parturient montes nascetur. Est placerat in egestas erat imperdiet.'),--
	(1, 1, 'Sit amet nulla facilisi morbi tempus. Nulla facilisi cras fermentum odio eu. Etiam erat velit scelerisque in dictum non consectetur a erat. Enim nulla aliquet porttitor lacus luctus accumsan tortor posuere. Ut sem nulla pharetra diam. Fames ac turpis egestas maecenas. Bibendum neque egestas congue quisque egestas diam. Laoreet id donec ultrices tincidunt arcu non sodales neque. Eget felis eget nunc lobortis mattis aliquam faucibus purus. Faucibus interdum posuere lorem ipsum dolor sit.');--
-- ----------------------------------------------------------------------
-- Оказывается, какой-то гений придумал «camelCase» в названиях таблиц и столбцов заменять на camel_case при составлении запросов…
-- ----------------------------------------------------------------------
