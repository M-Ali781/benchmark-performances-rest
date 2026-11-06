const fs = require('fs');
const path = require('path');

const NUM_CATEGORIES = 2000;
const NUM_ITEMS = 100000;

const benchmarkDir = path.join(__dirname);
const jmeterDataDir = path.join(__dirname, '..', 'jmeter', 'data');

if (!fs.existsSync(jmeterDataDir)) {
    fs.mkdirSync(jmeterDataDir, { recursive: true });
}

console.log('Generating seed files and benchmark test datasets...');

// 1. Generate SQL Schema & seed file (PostgreSQL)
const sqlFile = path.join(benchmarkDir, 'seed.sql');
const sqlStream = fs.createWriteStream(sqlFile);

sqlStream.write(`-- Benchmark Schema for PostgreSQL
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
FROM generate_series(1, ${NUM_CATEGORIES}) s;

-- Populate 100,000 Items
INSERT INTO item (sku, name, price, stock, category_id, description, updated_at)
SELECT 
    'SKU-' || LPAD(s::text, 6, '0'),
    'Item Product ' || s,
    (10.0 + (s % 500) * 1.5)::numeric(10,2),
    (50 + (s % 200)),
    (1 + (s % ${NUM_CATEGORIES})),
    'Detailed benchmark description for product ' || s || ' with standard test payload properties.',
    NOW()
FROM generate_series(1, ${NUM_ITEMS}) s;
`);

sqlStream.end(() => {
    console.log(`Generated ${sqlFile} (2,000 categories, 100,000 items)`);
});

// 2. Generate JMeter CSV data sets
const catCsvPath = path.join(jmeterDataDir, 'categories.csv');
const catCsvStream = fs.createWriteStream(catCsvPath);
catCsvStream.write('id,code,name\n');
for (let i = 1; i <= Math.min(NUM_CATEGORIES, 5000); i++) {
    catCsvStream.write(`${i},CAT-${String(i).padStart(4, '0')},Category ${i}\n`);
}
catCsvStream.end();

const itemCsvPath = path.join(jmeterDataDir, 'items.csv');
const itemCsvStream = fs.createWriteStream(itemCsvPath);
itemCsvStream.write('id,sku,category_id\n');
for (let i = 1; i <= 10000; i++) {
    const catId = 1 + (i % NUM_CATEGORIES);
    itemCsvStream.write(`${i},SKU-${String(i).padStart(6, '0')},${catId}\n`);
}
itemCsvStream.end();

// 3. Generate Payloads (Light: 1 KB, Heavy: 5 KB)
const lightPayload = {
    sku: "SKU-BENCH-LIGHT-01",
    name: "Lightweight Benchmark Item",
    price: 199.99,
    stock: 50,
    categoryId: 1,
    description: "Standard light payload 1KB ".repeat(30).substring(0, 850)
};
fs.writeFileSync(path.join(jmeterDataDir, 'payload_light.json'), JSON.stringify(lightPayload, null, 2));

const heavyPayload = {
    sku: "SKU-BENCH-HEAVY-01",
    name: "Heavyweight Benchmark Item with 5KB Body",
    price: 499.99,
    stock: 25,
    categoryId: 1,
    description: "Extensive enterprise description payload for stress-testing REST serialization: ".repeat(60).substring(0, 4800)
};
fs.writeFileSync(path.join(jmeterDataDir, 'payload_heavy.json'), JSON.stringify(heavyPayload, null, 2));

console.log('JMeter CSV datasets and payloads generated in jmeter/data/');
