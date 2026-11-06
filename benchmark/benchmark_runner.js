const http = require('http');

const PORT = 8085;
const HOST = 'localhost';

const VARIANTS = [
    { name: 'A : Jersey', basePath: '/api/jersey' },
    { name: 'C : @RestController', basePath: '/api/mvc' },
    { name: 'D : Spring Data REST', basePath: '/api/data-rest' }
];

function makeRequest(method, path, body = null) {
    return new Promise((resolve) => {
        const start = process.hrtime.bigint();
        const options = {
            hostname: HOST,
            port: PORT,
            path: path,
            method: method,
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            }
        };

        const req = http.request(options, (res) => {
            let data = '';
            res.on('data', chunk => { data += chunk; });
            res.on('end', () => {
                const end = process.hrtime.bigint();
                const latencyMs = Number(end - start) / 1e6;
                const ok = res.statusCode >= 200 && res.statusCode < 400;
                resolve({ latencyMs, ok, status: res.statusCode });
            });
        });

        req.on('error', () => {
            const end = process.hrtime.bigint();
            resolve({ latencyMs: Number(end - start) / 1e6, ok: false, status: 500 });
        });

        if (body) {
            req.write(typeof body === 'string' ? body : JSON.stringify(body));
        }
        req.end();
    });
}

async function runScenario(variant, name, requestGenerators, totalRequests = 100, concurrency = 10) {
    const latencies = [];
    let errors = 0;
    const startWall = Date.now();

    for (let i = 0; i < totalRequests; i += concurrency) {
        const batch = [];
        for (let j = 0; j < concurrency && (i + j) < totalRequests; j++) {
            const gen = requestGenerators[(i + j) % requestGenerators.length];
            const { method, path, body } = gen(variant.basePath);
            batch.push(makeRequest(method, path, body));
        }
        const results = await Promise.all(batch);
        for (const r of results) {
            latencies.push(r.latencyMs);
            if (!r.ok) errors++;
        }
    }

    const durationSec = (Date.now() - startWall) / 1000;
    latencies.sort((a, b) => a - b);

    const rps = (totalRequests / durationSec).toFixed(1);
    const p50 = latencies[Math.floor(latencies.length * 0.50)].toFixed(2);
    const p95 = latencies[Math.floor(latencies.length * 0.95)].toFixed(2);
    const p99 = latencies[Math.floor(latencies.length * 0.99)].toFixed(2);
    const errRate = ((errors / totalRequests) * 100).toFixed(2);

    return { variant: variant.name, scenario: name, rps, p50, p95, p99, errRate };
}

async function main() {
    console.log('========================================================================');
    console.log(' BENCHMARK REST PERFORMANCE : JERSEY vs SPRING MVC vs SPRING DATA REST');
    console.log('========================================================================\n');

    // Scenario 1: READ-heavy
    const readHeavyGens = [
        (base) => ({ method: 'GET', path: `${base}/items?page=0&size=50` }),
        (base) => ({ method: 'GET', path: `${base}/items?categoryId=1&page=0&size=20` }),
        (base) => ({ method: 'GET', path: `${base}/categories/1/items` }),
        (base) => ({ method: 'GET', path: `${base}/categories?page=0&size=20` })
    ];

    // Scenario 2: JOIN-filter
    const joinFilterGens = [
        (base) => ({ method: 'GET', path: `${base}/items?categoryId=1&page=0&size=20` }),
        (base) => ({ method: 'GET', path: `${base}/items/1` })
    ];

    // Scenario 3: MIXED
    const mixedGens = [
        (base) => ({ method: 'GET', path: `${base}/items?page=0&size=20` }),
        (base) => ({ method: 'POST', path: `${base}/items`, body: { sku: 'SKU-TMP-' + Math.random(), name: 'Test Item', price: 99.9, stock: 10, categoryId: 1 } }),
        (base) => ({ method: 'PUT', path: `${base}/items/1`, body: { sku: 'SKU-000001', name: 'Item Product 1', price: 11.5, stock: 100, categoryId: 1 } })
    ];

    // Scenario 4: HEAVY-body
    const heavyDesc = "Bench Payload ".repeat(150);
    const heavyGens = [
        (base) => ({ method: 'POST', path: `${base}/items`, body: { sku: 'SKU-HVY-' + Math.random(), name: 'Heavy Item', price: 299.9, stock: 5, categoryId: 1, description: heavyDesc } }),
        (base) => ({ method: 'PUT', path: `${base}/items/1`, body: { sku: 'SKU-000001', name: 'Item Product 1', price: 11.5, stock: 100, categoryId: 1, description: heavyDesc } })
    ];

    const allResults = [];

    for (const v of VARIANTS) {
        console.log(`Running benchmarks for ${v.name}...`);
        const r1 = await runScenario(v, 'READ-heavy', readHeavyGens, 100, 10);
        const r2 = await runScenario(v, 'JOIN-filter', joinFilterGens, 100, 10);
        const r3 = await runScenario(v, 'MIXED', mixedGens, 60, 6);
        const r4 = await runScenario(v, 'HEAVY-body', heavyGens, 40, 4);
        allResults.push(r1, r2, r3, r4);
    }

    console.log('\n--- T2 : Resultats de performance comparatifs ---');
    console.table(allResults);
}

main().catch(err => console.error(err));
