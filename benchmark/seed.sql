-- Benchmark Schema for PostgreSQL
DROP TABLE IF EXISTS item CASCADE;
DROP TABLE IF EXISTS category CASCADE;

CREATE TABLE category (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE item (
    id BIGSERIAL PRIMARY KEY,
    sku VARCHAR(64) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL,
    price NUMERIC(10,2) NOT NULL,
    stock INT NOT NULL,
    category_id BIGINT NOT NULL REFERENCES category(id),
    description TEXT,
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_item_category ON item(category_id);
CREATE INDEX idx_item_updated_at ON item(updated_at);
CREATE INDEX idx_item_sku ON item(sku);

-- Populate 2,000 Categories
INSERT INTO category (code, name, updated_at)
SELECT 
    'CAT-' || LPAD(s::text, 4, '0'),
    'Category ' || s,
    NOW()
FROM generate_series(1, 2000) s;

-- Populate 100,000 Items
INSERT INTO item (sku, name, price, stock, category_id, description, updated_at)
SELECT 
    'SKU-' || LPAD(s::text, 6, '0'),
    'Item Product ' || s,
    (10.0 + (s % 500) * 1.5)::numeric(10,2),
    (50 + (s % 200)),
    (1 + (s % 2000)),
    'Detailed benchmark description for product ' || s || ' with standard test payload properties.',
    NOW()
FROM generate_series(1, 100000) s;
