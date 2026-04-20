
CREATE TABLE IF NOT EXISTS public.customers (
    customer_id SERIAL PRIMARY KEY,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(50)  NOT NULL,
    balance     NUMERIC(10,2) DEFAULT 0,
    role        VARCHAR(20)   DEFAULT 'user'
);

CREATE TABLE IF NOT EXISTS public.bottoms (
    bottom_id SERIAL PRIMARY KEY,
    name      VARCHAR(50)   NOT NULL,
    price     NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS public.toppings (
    topping_id SERIAL PRIMARY KEY,
    name       VARCHAR(50)   NOT NULL,
    price      NUMERIC(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS public.orders (
    order_id    SERIAL PRIMARY KEY,
    customer_id INTEGER REFERENCES public.customers(customer_id),
    total_price NUMERIC(10,2),
    status      VARCHAR(50) DEFAULT 'behandles'
);

CREATE TABLE IF NOT EXISTS public.order_lines (
    order_line_id SERIAL PRIMARY KEY,
    order_id      INTEGER REFERENCES public.orders(order_id),
    bottom_id     INTEGER REFERENCES public.bottoms(bottom_id),
    topping_id    INTEGER REFERENCES public.toppings(topping_id),
    quantity      INTEGER NOT NULL
);


INSERT INTO public.bottoms (name, price) VALUES
    ('Chokolade',   5.00),
    ('Vanilje',     5.00),
    ('Citron',      5.50),
    ('Rødbede',     6.00),
    ('Jordnøddesmør', 6.50);

INSERT INTO public.toppings (name, price) VALUES
    ('Chokolade frosting',  4.00),
    ('Vanilje frosting',    4.00),
    ('Jordbær frosting',    4.50),
    ('Karamel frosting',    4.50),
    ('Cream cheese frosting', 5.00);

INSERT INTO public.customers (email, password, balance, role) VALUES
    ('admin@cupcake.dk',  'admin123',  500.00, 'admin'),
    ('kunde1@mail.dk',    'pas123',    200.00, 'user'),
    ('kunde2@mail.dk',    'pas456',    150.00, 'user');

INSERT INTO public.orders (customer_id, total_price, status) VALUES
    (2, 19.00, 'behandles'),
    (3, 28.50, 'afsendt'),
    (2, 10.50, 'afhentet');

INSERT INTO public.order_lines (order_id, bottom_id, topping_id, quantity) VALUES
    (1, 1, 1, 2),
    (1, 2, 3, 1),
    (2, 3, 5, 3),
    (3, 4, 2, 1);
